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

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

public class Corpse extends PassiveEntity implements TurnListener {
	private static final Logger logger = Log4J.getLogger(Corpse.class);
	private static final int DEGRADATION_TIMEOUT = 3000; // 30 minutes at 300 ms
	private static final int MAX_STAGE = 5; // number of degradation steps
	private static final int DEGRADATION_SETP_TIMEOUT = DEGRADATION_TIMEOUT / MAX_STAGE;
	private int stage;
	private boolean isDegrading = true;

	public static void generateRPClass() {
		RPClass entity = new RPClass("corpse");
		entity.isA("entity");
		entity.add("class", RPClass.STRING);
		entity.add("stage", RPClass.BYTE);

		entity.add("name", RPClass.STRING);
		entity.add("killer", RPClass.STRING);

		entity.addRPSlot("content", 4);
	}

	public Corpse(String clazz, int x, int y) throws AttributeNotFoundException {
		put("type", "corpse");
		put("class", clazz);

		setx(x);
		sety(y);
		TurnNotifier.get().notifyInTurns(DEGRADATION_SETP_TIMEOUT, this, null);
		stage = 0;
		put("stage", stage);

		RPSlot slot = new RPSlot("content");
		slot.setCapacity(4);
		addSlot(slot);
	}

	public Corpse(RPEntity entity, Entity killer)
			throws AttributeNotFoundException {
		put("type", "corpse");

		if (entity.has("class")) {
			put("class", entity.get("class"));
		} else {
			put("class", entity.get("type"));
		}

		if (killer != null && entity instanceof Player) {
			put("name", entity.getName());

			if (killer.has("name")) {
				put("killer", killer.get("name"));
			} else if (killer.has("subclass")) {
				put("killer", killer.get("subclass"));
			} else if (killer.has("class")) {
				put("killer", killer.get("class"));
			} else if (killer.has("type")) {
				put("killer", killer.get("type"));
			}
		}

		if (killer == null && has("killer")) {
			logger.error("Corpse: (" + entity + ") with null killer: ("
					+ killer + ")");
			remove("killer");
		}

		// Consider rewriting this section once we get corpses larger
		// than 2x2. 
		Rectangle2D rect = entity.getArea(entity.getx(), entity.gety());
		setx((int) Math.round(rect.getCenterX() - 1));
		sety((int) Math.round(rect.getCenterY() - 1));
		
		TurnNotifier.get().notifyInTurns(DEGRADATION_SETP_TIMEOUT, this, null);
		stage = 0;
		put("stage", stage);

		RPSlot slot = new RPSlot("content");
		slot.setCapacity(4);
		addSlot(slot);
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}
	
	private void modify() {
		if (isContained()) {
			// We modify the base container if the object change.
			RPObject base = getContainer();
			while (base.isContained()) {
				if (base == base.getContainer()) {
					logger.fatal("A corpse is contained by itself.");
					break;
				}

				base = base.getContainer();
			}

			StendhalRPWorld.get().modify(base);
		} else {
			notifyWorldAboutChanges();
		}
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
				RPObject base = getContainer();
				while (base.isContained()) {
					if (base == base.getContainer()) {
						logger.fatal("A corpse is contained by itself.");
						break;
					}

					base = base.getContainer();
				}

				StendhalRPWorld.get().modify(base);

				getContainerSlot().remove(this.getID());
			} else {
				StendhalRPWorld.get().remove(getID());
			}

		} else {
			TurnNotifier.get().notifyInTurns(DEGRADATION_SETP_TIMEOUT, this, null);
		}
	}

	/**
	 * Set to false to stop degrading. (Some corpse are used in quests).
	 *
	 * @param isDegrading true, if degrading, false otherwise
	 */
	public void setDegrading(boolean isDegrading) {
		this.isDegrading = isDegrading;
	} 

	/**
	 * Sets the current degrading state. Set it to MAX_STAGE
	 * will remove the corpse.
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
		content.assignValidID(entity);
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
		String stageText[] = { "new", "fresh", "cold", "slightly rotten",
				"rotten", "very rotten" };
		String text = "You see the " + stageText[stage] + " corpse of ";
		if (hasDescription()) {
			text = getDescription();
		} else if (!has("name")) {
			text += "a " + get("class").replace("_", " ");
		} else {
			text += get("name") + ". It was killed by " + get("killer");
		}
		text = text + ". You can #inspect it to see its contents.";
		return (text);
	}

}
