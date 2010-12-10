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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

/**
 * A growing carrot which can be picked.
 * 
 * @author hendrik
 */
public class VegetableGrower extends GrowingPassiveEntityRespawnPoint implements
		UseListener {
	private String vegetableName;

	protected final void setVegetableName(final String vegetableName) {
		this.vegetableName = vegetableName;
	}

    protected String getVegetableName() {
		return vegetableName;
    }


    /**
     * Create a VegetableGrower from an RPObject. Used when restoring growers
     * from the DB.
     * 
     * @param object object to be converted
     * @param name item name
     * @param maxRipeness maximum ripeness of the object
     * @param growthRate average time between growth steps 
     */
	public VegetableGrower(final RPObject object, final String name,
			final int maxRipeness, final int growthRate) {
		super(object, "items/grower/" + name + "_grower", "items/grower/" + name + " grower", "Pick", maxRipeness, growthRate);
		vegetableName = name;
		update();
	}

	/**
	 * Create a new VegetableGrower for an item.
	 * 
	 * @param name item name
	 */
	public VegetableGrower(final String name) {
		super("items/grower/" + name + "_grower", "items/grower/" + name + " grower", "Pick", 1, 1, 1);
		vegetableName = name;
	}

	@Override
	public String describe() {
		String text;
		switch (getRipeness()) {
		case 0:
			text = "You see a planted " + vegetableName + " seed.";
			break;
		case 1:
			text = "You see a ripe " + vegetableName + ".";
			break;
		default:
			text = "You see an unripe " + vegetableName + ".";
			break;
		}
		return text;
	}

	/**
	 * Is called when a player tries to harvest this item.
	 * @param entity that tries to harvest
	 * @return true on success
	 */
	public boolean onUsed(final RPEntity entity) {
		if (entity.nextTo(this)) {
			if (getRipeness() == getMaxRipeness()) {
				onFruitPicked(null);
				final Item grain = SingletonRepository.getEntityManager().getItem(
						vegetableName);
				if(entity instanceof Player) {
					((Player) entity).incHarvestedForItem(vegetableName, 1);
					SingletonRepository.getAchievementNotifier().onObtain((Player) entity);
				}
				entity.equipOrPutOnGround(grain);
				return true;
			} else if (entity instanceof Player) {
				((Player) entity).sendPrivateText("This " + vegetableName
						+ " is not yet ripe enough to pick.");
			}
		} else if (entity instanceof Player) {
		((Player) entity).sendPrivateText("You are too far away from the " + vegetableName
						+ ".");
		}
		return false;
	}

}
