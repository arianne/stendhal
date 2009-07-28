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
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.LootableSlot;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.Definition.Type;

import org.apache.log4j.Logger;

public class Corpse extends PassiveEntity implements 
		EquipListener {
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

	private static final int DEGRADATION_STEP_TIMEOUT = DEGRADATION_TIMEOUT
			/ MAX_STAGE;

	private int stage;

	private TurnListener turnlistener = new TurnListener() {

		public void onTurnReached(final int currentTurn) {
			
			Corpse.this.onTurnReached(currentTurn);
			
		}
		
		
	};
	
	@Override
	public void onRemoved(final StendhalRPZone zone) {
		SingletonRepository.getTurnNotifier().dontNotify(turnlistener);
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

		if ((killerName != null) && (victim instanceof Player)) {
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

		SingletonRepository.getTurnNotifier().notifyInSeconds(getDegradationStepTimeout(), this.turnlistener);
		stage = 0;
		put("stage", stage);

		final RPSlot slot = new LootableSlot(this);
		addSlot(slot);
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

	private boolean isCompletelyRotten() {
		stage++;
		put("stage", stage);

		modify();

		return stage >= MAX_STAGE;
	}

	public void onTurnReached(final int currentTurn) {
		if (isCompletelyRotten()) {
				this.getZone().remove(this);
		} else {
			SingletonRepository.getTurnNotifier().notifyInSeconds(getDegradationStepTimeout(), this.turnlistener);
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
				text += ", killed by " + get(ATTR_KILLER);
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
}
