/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import games.stendhal.common.Grammar;
import games.stendhal.common.ItemTools;
import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.EquipListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.slot.LootableSlot;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

public class Corpse extends PassiveEntity implements 
		EquipListener {
	/**
	 * TurnListener to degradate a corpse
	 *   
	 * @author madmetzger
	 */
	private final class CorpseRottingTurnListener implements TurnListener {
		public void onTurnReached(final int currentTurn) {
			Corpse.this.onTurnReached(currentTurn);
		}
	}
	
	/**
	 * TurnListener to release this corpse to get everyone able to be rewarded for looting this corpse
	 * (i.e. for achievements)
	 *  
	 * @author madmetzger
	 */
	private final class CorpseReleaseRewardingForEveryoneTurnListener implements TurnListener {
		public void onTurnReached(final int currentTurn) {
			Corpse.this.everyoneRewardableForLootedItems = true;
		}
	}

	/**
	 * The killer's name attribute name.
	 */
	protected static final String ATTR_KILLER = "killer";

	/**
	 * The name attribute name.
	 */
	protected static final String ATTR_NAME = "name";
	
	/**
	 * The image name
	 */
	private static final String ATTR_IMAGE = "image";

	private static final Logger logger = Logger.getLogger(Corpse.class);

	/** Time (in seconds) until a corpse disappears. */
	private static final int DEGRADATION_TIMEOUT = 15 * MathHelper.SECONDS_IN_ONE_MINUTE;
	
	/** number of degradation steps. */
	private static final int MAX_STAGE = 5; 

	/** time between two degradation steps */
	private static final int DEGRADATION_STEP_TIMEOUT = DEGRADATION_TIMEOUT
			/ MAX_STAGE;
	
	/** Minimum resistance of a single corpse */
	private static final int MIN_RESISTANCE = 5;
	/** Theoretical resistance of a single corpse */
	private static final int MAX_RESISTANCE = 70;
	
	/** Time (in seconds) before everone can be awarded on removal of an item from this corpse */
	private static final int MIN_HOLDING_TIME_FOR_ITEMS = 5;
	
	/** Can everyone be rewarded for looting items from this corpse?*/
	private boolean everyoneRewardableForLootedItems = false;
	
	private int stage;

	private TurnListener corpseDegradator = new CorpseRottingTurnListener();
	private TurnListener itemForRewardsReleaser = new CorpseReleaseRewardingForEveryoneTurnListener();
	
	@Override
	public void onRemoved(final StendhalRPZone zone) {
		SingletonRepository.getTurnNotifier().dontNotify(corpseDegradator);
		SingletonRepository.getTurnNotifier().dontNotify(itemForRewardsReleaser);
		super.onRemoved(zone);
	}

	public static void generateRPClass() {
		final RPClass entity = new RPClass("corpse");
		entity.isA("entity");
		entity.addAttribute("class", Type.STRING);
		entity.addAttribute("stage", Type.BYTE);

		entity.addAttribute(ATTR_NAME, Type.STRING);
		entity.addAttribute(ATTR_KILLER, Type.STRING);
		entity.addAttribute(ATTR_IMAGE, Type.STRING);

		entity.addRPSlot("content", 4);
	}
	
	/**
	 * non rotting corpse.
	 * 
	 * @param clazz
	 * @param x
	 * @param y
	 */
	public Corpse(final String clazz, final int x, final int y) {
		setRPClass("corpse");
		put("type", "corpse");

		setEntityClass(clazz);

		setPosition(x, y);
		stage = 0;
		put("stage", stage);
		// default to player corpse image
		put(ATTR_IMAGE, "player");
		setResistance(calculateResistance());
		
		final RPSlot slot = new LootableSlot(this);
		addSlot(slot);
	}

	/**
	 * Create a corpse.
	 * 
	 * @param victim
	 *            The killed entity.
	 * @param killerName
	 *            The killer name.
	 * 
	 * 
	 */
	public Corpse(final RPEntity victim, final String killerName) {
		setRPClass("corpse");
		put("type", "corpse");

		if (victim.has("class")) {
			setEntityClass(victim.get("class"));
		} else {
			setEntityClass(victim.get("type"));
		}
		
		put(ATTR_IMAGE, victim.getCorpseName());
		setSize(victim.getCorpseWidth(), victim.getCorpseHeight());

		if ((killerName != null)) {
			put(ATTR_NAME, victim.getTitle());
			put(ATTR_KILLER, killerName);
		} else if (has(ATTR_KILLER)) {
			logger.error("Corpse: (" + victim + ") with null killer: ("
					+ killerName + ")");
			remove(ATTR_KILLER);
		}

		final Rectangle2D rect = victim.getArea();

		setPosition(
				(int) (rect.getX() + ((rect.getWidth() - getWidth()) / 2.0)),
				(int) (rect.getY() + ((rect.getHeight() - getHeight()) / 2.0)));

		SingletonRepository.getTurnNotifier().notifyInSeconds(getDegradationStepTimeout(), this.corpseDegradator);
		SingletonRepository.getTurnNotifier().notifyInSeconds(MIN_HOLDING_TIME_FOR_ITEMS, this.itemForRewardsReleaser);
		
		stage = 0;
		put("stage", stage);
		setResistance(calculateResistance());

		final RPSlot slot = new LootableSlot(this);
		addSlot(slot);
	}

	/**
	 * Calculate walking resistance for the corpse.
	 *  
	 * @return resistance value between <code>100 * MIN_RESISTANCE</code> and
	 * <code>100 * MAX_RESISTANCE</code>
	 */
	private int calculateResistance() {
		// Using area would make the resistance grow very fast for large corpses
		double mean = Math.sqrt(getWidth() * getHeight());
		// Get a [0, 1[ value for a corpse size index 
		double normalized = 1 - 1 / Math.max(1.0, mean);
		// Scale between max and min
		return Math.max(MIN_RESISTANCE, (int) (MAX_RESISTANCE * normalized));
	}

	//
	// Corpse
	//

	/**
	 * Get the entity name.
	 * 
	 * @return The entity's name, or <code>null</code> if undefined.
	 */
	public String getName() {
		if (has(ATTR_NAME)) {
			return get(ATTR_NAME);
		} else {
			return null;
		}
	}

	/**
	 * Set the killer name of the corpse.
	 * 
	 * @param killer
	 *            The corpse's killer name.
	 */
	public void setKiller(final String killer) {
		put(ATTR_KILLER, killer);
	}

	/**
	 * Set the name of the corpse.
	 * 
	 * @param name
	 *            The corpse name.
	 */
	public void setName(final String name) {
		put(ATTR_NAME, name);
	}

	private void modify() {
		if (getZone() != null) {
			if (isContained()) {
				SingletonRepository.getRPWorld().modify(getBaseContainer());
			} else {
				notifyWorldAboutChanges();
			}
		}
	}

	/**
	 * @return true iff this corpse is completely rotten
	 */
	private boolean isCompletelyRotten() {
		return stage >= MAX_STAGE;
	}

	/**
	 * degradate this corpse
	 */
	private void degradateCorpse() {
		stage++;
		put("stage", stage);
		modify();
	}

	/**
	 * degradate this corpse and remove it if it is completely rotten
	 */
	public void onTurnReached(final int currentTurn) {
		degradateCorpse();
		if (isCompletelyRotten()) {
				this.getZone().remove(this);
		} else {
			SingletonRepository.getTurnNotifier().notifyInSeconds(getDegradationStepTimeout(), this.corpseDegradator);
		}
	}

	/**
	 * Sets the current degrading state. Set it to MAX_STAGE will remove the
	 * corpse.
	 * 
	 * @param newStage
	 */
	public void setStage(final int newStage) {
		if ((newStage >= 0) && (newStage <= MAX_STAGE)) {
			stage = newStage;
			put("stage", stage);
			modify();
		}
	}

	public void add(final PassiveEntity entity) {
		final RPSlot content = getSlot("content");
		content.add(entity);
	}

	public boolean isFull() {
		return getSlot("content").isFull();
	}

	@Override
	public int size() {
		return getSlot("content").size();
	}

	public Iterator<RPObject> getContent() {
		final RPSlot content = getSlot("content");
		return content.iterator();
	}

	@Override
	public String describe() {
		final String[] stageText = { "new", "fresh", "cold", "slightly rotten",
				"rotten", "very rotten" };

		String text = "You see the " + stageText[stage] + " corpse of ";

		if (hasDescription()) {
			text = getDescription();
		} else if (has(ATTR_NAME)) {
			text += get(ATTR_NAME);

			if (has(ATTR_KILLER)) {
				// only display the killer if it is the corpse of a player
				if (get("class").equals("player")) {
					text += ", killed by " + get(ATTR_KILLER);
				}
			}
		} else {
			text += Grammar.a_noun(ItemTools.itemNameToDisplayName(get("class")));
		}

		text += ". You can #inspect it to see its contents.";

		return (text);
	}

	public boolean canBeEquippedIn(final String slot) {
		return false;
	}

	//
	// Entity
	//

	/**
	 * Returns the name or something that can be used to identify the entity for
	 * the player.
	 * 
	 * @param definite
	 *            <code>true</code> for "the", and <code>false</code> for
	 *            "a/an" in case the entity has no name.
	 * 
	 * @return The description name.
	 */
	@Override
	public String getDescriptionName(final boolean definite) {
		final String name = getName();

		if (name != null) {
			return name;
		} else {
			return super.getDescriptionName(definite);
		}
	}

	/**
	 * Get the nicely formatted entity title/name.
	 * 
	 * @return The title, or <code>null</code> if unknown.
	 */
	@Override
	public String getTitle() {
		final String name = getName();

		if (name != null) {
			return name;
		} else {
			return super.getTitle();
		}
	}
	
	protected int getDegradationStepTimeout() {
		return DEGRADATION_STEP_TIMEOUT;
	}
	
	/**
	 * Checks if the items from this corpse are deserved by everyone for rewarding (i.e. for achievements)
	 * 
	 * @return true if everyone should be rewarded
	 */
	public boolean isItemLootingRewardableForEveryone() {
		return everyoneRewardableForLootedItems && !get("class").equals("player");
	}
	
	/**
	 * gets the killer of this corpse
	 * 
	 * @return the name of the killer
	 */
	public String getKiller() {
		return get(ATTR_KILLER);
	}
}
