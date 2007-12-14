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
package games.stendhal.server.entity.mapstuff.spawner;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;
import marauroa.common.game.RPObject;

/**
 * A grain field can be harvested by players who have a scythe. After that, it
 * will slowly regrow; there are several regrowing steps in which the graphics
 * will change to show the progress.
 * 
 * @author daniel
 */
public class GrainField extends GrowingPassiveEntityRespawnPoint implements
		UseListener {

	/** How many regrowing steps are needed before one can harvest again */
	public static final int RIPE = 5;

	public GrainField(RPObject object) {
		super(object, "grain_field", "Harvest", RIPE, 1, 2);
		setResistance(80);
		update();
	}

	public GrainField() {
		super("grain_field", "Harvest", RIPE, 1, 2);
		setResistance(80);
	}

	@Override
	public String describe() {
		String text;
		switch (getRipeness()) {
		case 0:
			text = "You see a grain field that has just been harvested.";
			break;
		case RIPE:
			text = "You see a ripe grain field.";
			break;
		default:
			text = "You see an unripe grain field.";
			break;
		}
		return text;
	}

	/**
	 * Is called when a player tries to harvest this grain field.
	 */
	public boolean onUsed(RPEntity entity) {
		if (entity.nextTo(this)) {
			if (getRipeness() == RIPE) {
				if (entity.isEquipped("old_scythe")
						|| entity.isEquipped("scythe")) {
					onFruitPicked(null);
					Item grain = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
							"grain");
					entity.equip(grain, true);
					return true;
				} else if (entity instanceof Player) {
					entity.sendPrivateText("You need a scythe to harvest grain fields.");
					return false;
				}
			} else if (entity instanceof Player) {
				entity.sendPrivateText("This grain is not yet ripe enough to harvest.");
				return false;
			}
		}
		return false;
	}

}
