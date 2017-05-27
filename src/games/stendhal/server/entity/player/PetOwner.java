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

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import marauroa.common.game.RPObject;

/**
 * Handles ownership of pets and sheep.
 *
 * @author hendrik
 */
public class PetOwner {


	/**
	 * The pet ID attribute name.
	 */
	protected static final String ATTR_PET = "pet";

	/**
	 * The sheep ID attribute name.
	 */
	protected static final String ATTR_SHEEP = "sheep";

	private static Logger LOGGER = Logger.getLogger(PetOwner.class);

	private final Player player;

	private final PlayerSheepManager playerSheepManager;
	private final PlayerPetManager playerPetManager;


	public PetOwner(final Player player) {
		this.player = player;
		playerSheepManager = new PlayerSheepManager(player);
		playerPetManager = new PlayerPetManager(player);
	}

	public void removeSheep(final Sheep sheep) {
		if (sheep != null) {
			sheep.setOwner(null);
		}

		if (player.has(ATTR_SHEEP)) {
			player.remove(ATTR_SHEEP);
		} else {
			LOGGER.warn("Called removeSheep but player has not sheep: " + this);
		}
	}

	public void removePet(final Pet pet) {
		if (pet != null) {
			pet.setOwner(null);
		}

		if (player.has(ATTR_PET)) {
			player.remove(ATTR_PET);
		} else {
			LOGGER.warn("Called removePet but player has not pet: " + this);
		}
	}

	public boolean hasSheep() {
		return player.has(ATTR_SHEEP);
	}

	public boolean hasPet() {
		return player.has(ATTR_PET);
	}

	/**
	 * Set the player's pet. This will also set the pet's owner.
	 *
	 * @param pet
	 *            The pet.
	 */
	public void setPet(final Pet pet) {
		player.put(ATTR_PET, pet.getID().getObjectID());
		pet.setOwner(player);
	}

	/**
	 * Set the player's sheep. This will also set the sheep's owner.
	 *
	 * @param sheep
	 *            The sheep.
	 */
	public void setSheep(final Sheep sheep) {
		player.put(ATTR_SHEEP, sheep.getID().getObjectID());
		sheep.setOwner(player);
	}

	/**
	 * Get the player's sheep.
	 *
	 * @return The sheep.
	 */
	public Sheep getSheep() {
		if (player.has(ATTR_SHEEP)) {
			try {
				return (Sheep) SingletonRepository.getRPWorld().get(
						new RPObject.ID(player.getInt(ATTR_SHEEP), player.get("zoneid")));
			} catch (final Exception e) {
				LOGGER.error("Pre 1.00 Marauroa sheep bug. (player = "
						+ player.getName() + ")", e);

				if (player.has(ATTR_SHEEP)) {
					player.remove(ATTR_SHEEP);
				}

				if (player.hasSlot("#flock")) {
					player.removeSlot("#flock");
				}

				return null;
			}
		} else {
			return null;
		}
	}

	public Pet getPet() {
		try {
			if (player.has(ATTR_PET)) {
				return (Pet) SingletonRepository.getRPWorld().get(
						new RPObject.ID(player.getInt(ATTR_PET), player.get("zoneid")));
			} else {
				return null;
			}
		} catch (final ClassCastException e) {
			player.remove(ATTR_PET);
			LOGGER.error("removed pets attribute" + e);
			return null;
		}
	}

	public void destroy() {
		final Sheep sheep = player.getSheep();

		if (sheep != null) {
			sheep.getZone().remove(sheep);

			/*
			 * NOTE: Once the sheep is stored there is no more trace of zoneid.
			 */
			playerSheepManager.storeSheep(sheep);
		} else {
			// Bug on pre 0.20 released
			if (player.hasSlot("#flock")) {
				player.removeSlot("#flock");
			}
		}

		final Pet pet = player.getPet();

		if (pet != null) {
			pet.getZone().remove(pet);

			/*
			 * NOTE: Once the pet is stored there is no more trace of zoneid.
			 */
			playerPetManager.storePet(pet);
		}
	}

	public Pet retrievePet() {
		return playerPetManager.retrievePet();
	}

	public Sheep retrieveSheep() {
		return playerSheepManager.retrieveSheep();
	}
}
