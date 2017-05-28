/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.player;

import games.stendhal.server.entity.creature.BabyDragon;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.PurpleDragon;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class PlayerPetManager {
	private final Player player;

	PlayerPetManager(final Player player) {
		this.player = player;
	}

	public void storePet(final Pet pet) {
		if (!player.hasSlot("#pets")) {
			player.addSlot(new RPSlot("#pets"));
		}

		final RPSlot slot = player.getSlot("#pets");
		slot.clear();

		/*
		 * RPSlot.add() destroys zoneid, so preserve/restore it.
		 *
		 * TODO: Remove if getID()/setID() are made purely virtual.
		 */
		String zoneid;

		if (pet.has("zoneid")) {
			zoneid = pet.get("zoneid");
		} else {
			zoneid = null;
		}

		slot.add(pet);

		if (zoneid != null) {
			pet.put("zoneid", zoneid);
		}

		player.put("pet", pet.getID().getObjectID());
	}

	/**
	 * Recreate a saved pet.
	 *
	 * @return A pet, or <code>null</code> if none.
	 */
	public Pet retrievePet() {
		if (player.hasSlot("#pets")) {
			final RPSlot slot = player.getSlot("#pets");

			if (slot.size() > 0) {
				final RPObject object = slot.getFirst();
				slot.remove(object.getID());

				player.removeSlot("#pets");

				if (object.get("type").equals("cat")) {
					return new Cat(object, player);
				} else if (object.get("type").equals("baby_dragon")) {
					return new BabyDragon(object, player);
				} else if (object.get("type").equals("purple_dragon")) {
					return new PurpleDragon(object, player);
				}
			}
		}

		return null;
	}
}
