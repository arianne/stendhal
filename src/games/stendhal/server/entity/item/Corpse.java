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
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.LootableSlot;
import games.stendhal.server.events.EquipListener;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.Definition.Type;

public class Corpse extends PassiveEntity implements TurnListener,
		EquipListener {

	private static final Logger logger = Log4J.getLogger(Corpse.class);

	/** Time (in seconds) until a corpse disappears. */
	private static final int DEGRADATION_TIMEOUT = 15 * 60; // 3 minutes

	private static final int MAX_STAGE = 5; // number of degradation steps

	private static final int DEGRADATION_STEP_TIMEOUT = DEGRADATION_TIMEOUT
			/ MAX_STAGE;

	private int stage;

	private boolean isDegrading = true;

	public static void generateRPClass() {
		RPClass entity = new RPClass("corpse");
		entity.isA("entity");
		entity.addAttribute("class", Type.STRING);
		entity.addAttribute("stage", Type.BYTE);

		entity.addAttribute("name", Type.STRING);
		entity.addAttribute("killer", Type.STRING);

		entity.addRPSlot("content", 4);
	}

	private void decideSize(String clazz) {
		int width = 1;
		int height = 1;

		if (clazz.equals("giant_animal") || clazz.equals("giant_human")
				|| clazz.equals("huge_animal")) {
			width = 2;
			height = 2;
		} else if (clazz.equals("mythical_animal") || clazz.equals("boss")) {
			width = 6;
			height = 6;
		} else if (clazz.equals("enormous_creature")) {
			width = 16;
			height = 16;
		}

		setWidth(width);
		setHeight(height);
	}

	public Corpse(String clazz, int x, int y) {
		setRPClass("corpse");
		put("type", "corpse");
		put("class", clazz);

		decideSize(clazz);

		setX(x);
		setY(y);
		TurnNotifier.get().notifyInSeconds(DEGRADATION_STEP_TIMEOUT, this);
		stage = 0;
		put("stage", stage);

		RPSlot slot = new LootableSlot(this);

		// BUG: Capacity is set at RPClass.
		// slot.setCapacity(4);
		addSlot(slot);
	}

	/**
	 * Create a corpse.
	 *
	 * @param victim
	 *            The killed entity.
	 * @param killer
	 *            The killer entity.
	 *
	 *
	 */
	public Corpse(RPEntity victim, Entity killer) {
		this(victim, killer.getTitle());
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
	public Corpse(RPEntity victim, String killerName) {
		setRPClass("corpse");
		put("type", "corpse");

		if (victim.has("class")) {
			put("class", victim.get("class"));
		} else {
			put("class", victim.get("type"));
		}

		decideSize(get("class"));

		if ((killerName != null) && (victim instanceof Player)) {
			put("name", victim.getName());
			put("killer", killerName);
		} else if (has("killer")) {
			logger.error("Corpse: (" + victim + ") with null killer: ("
					+ killerName + ")");
			remove("killer");
		}

		// Consider rewriting this section once we get corpses larger
		// than 2x2.
		//
		// TODO: decideSize() has been called, width/height are set.
		// Center corpse area on victim area.
		Rectangle2D rect = victim.getArea();
		setX((int) Math.round(rect.getCenterX() - 1));
		setY((int) Math.round(rect.getCenterY() - 1));

		TurnNotifier.get().notifyInSeconds(DEGRADATION_STEP_TIMEOUT, this);
		stage = 0;
		put("stage", stage);

		RPSlot slot = new LootableSlot(this);
		addSlot(slot);
	}

	//
	// Corpse
	//

	private void modify() {
		if (isContained()) {
			StendhalRPWorld.get().modify(getBase());
		} else {
			notifyWorldAboutChanges();
		}
	}

	private RPObject getBase() {
		RPObject base = getContainer();
		while (base.isContained()) {
			if (base == base.getContainer()) {
				logger.error("A corpse is contained by itself.");
				break;
			}

			base = base.getContainer();
		}
		return base;
	}

	private boolean decDegradation(int aktTurn) {
		stage++;
		put("stage", stage);

		modify();

		return stage <= MAX_STAGE;
	}

	public void onTurnReached(int currentTurn, String message) {
		if (!isDegrading) {
			return;
		}
		if (!decDegradation(currentTurn)) {
			if (isContained()) {
				// We modify the base container if the object change.

				StendhalRPWorld.get().modify(getBase());

				getContainerSlot().remove(this.getID());
			} else {
				StendhalRPWorld.get().remove(getID());
			}

		} else {
			TurnNotifier.get().notifyInSeconds(DEGRADATION_STEP_TIMEOUT, this);
		}
	}

	/**
	 * Set to false to stop degrading. (Some corpse are used in quests).
	 *
	 * @param isDegrading
	 *            true, if degrading, false otherwise
	 */
	public void setDegrading(boolean isDegrading) {
		this.isDegrading = isDegrading;
	}

	/**
	 * Sets the current degrading state. Set it to MAX_STAGE will remove the
	 * corpse.
	 *
	 * @param newStage
	 */
	public void setStage(int newStage) {
		if ((newStage >= 0) && (newStage <= MAX_STAGE)) {
			stage = newStage;
			put("stage", stage);

			// Mark this object as modified if it has been added to the
			// world already.
			if (has("zoneid")) {
				modify();
			}
		}
	}

	public void add(PassiveEntity entity) {
		RPSlot content = getSlot("content");
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
		RPSlot content = getSlot("content");
		return content.iterator();
	}

	@Override
	public String describe() {
		String[] stageText = { "new", "fresh", "cold", "slightly rotten",
				"rotten", "very rotten" };
		String text = "You see the " + stageText[stage] + " corpse of ";
		if (hasDescription()) {
			text = getDescription();
		} else if (!has("name")) {
			text += Grammar.a_noun(get("class")).replace("_", " ");
		} else {
			text += get("name") + ", killed by " + get("killer");
		}
		text = text + ". You can #inspect it to see its contents.";
		return (text);
	}

	public boolean canBeEquippedIn(String slot) {
		return false;
	}

}
