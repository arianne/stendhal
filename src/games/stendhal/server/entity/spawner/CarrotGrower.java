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
package games.stendhal.server.entity.spawner;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

/**
 * A growing carrot which can be picked
 * 
 * @author hendrik
 */
public class CarrotGrower extends GrowingPassiveEntityRespawnPoint implements UseListener {

	public CarrotGrower(RPObject object) throws AttributeNotFoundException {
		super(object, "items/grower/carrot_grower", "Pick", 1, 1, 1);
		update();
	}

	public CarrotGrower() throws AttributeNotFoundException {
		super("items/grower/carrot_grower", "Pick", 1, 1, 1);
	}

	@Override
	public String describe() {
		String text;
		switch (getRipeness()) {
			case 0:
				text = "You see a planted carrot seed.";
				break;
			case 1:
				text = "You see a ripe carrot.";
				break;
			default:
				text = "You see an unripe carrot.";
				break;
		}
		return text;
	}

	/**
	 * Is called when a player tries to harvest this item.
	 */
	public void onUsed(RPEntity entity) {
		if (entity.nextTo(this, 0.25)) {
			if (getRipeness() == 1) {
				onFruitPicked(null);
				Item grain = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("carrot");
				entity.equip(grain, true);
			} else if (entity instanceof Player) {
				((Player) entity).sendPrivateText("This carrot is not yet ripe enough to pick.");
			}
		}
	}

}
