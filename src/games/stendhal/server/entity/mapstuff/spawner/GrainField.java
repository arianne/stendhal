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
 * A grain field can be harvested by players who have a scythe. After that, it
 * will slowly regrow; there are several regrowing steps in which the graphics
 * will change to show the progress.
 * 
 * @author daniel
 */
public class GrainField extends GrowingPassiveEntityRespawnPoint implements
		UseListener {
	/** How many growing steps are needed before one can harvest again. */
	public static final int RIPE = 5;
	
	/** Resistance of fully grown grain */
	private static final int MAX_RESISTANCE = 80;

	private String grainName;

	protected final void setGrainName(final String grainName) {
		this.grainName = grainName;
	}

    protected String getGrainName() {
		return grainName;
    }

	public GrainField(final RPObject object, final String name) {
		super(object, name + "_field", name + " field", "Harvest", RIPE, 1, 1);
		grainName = name;
		updateResistance();
		update();
	}

	public GrainField(final String name) {
		super(name + "_field", name + " field", "Harvest", RIPE, 1, 1);
		grainName = name;
	}
	
	@Override
	protected void setRipeness(final int ripeness) {
		super.setRipeness(ripeness);
		updateResistance();
	}
	
	/**
	 * Set resistance according the current ripeness.
	 */
	private void updateResistance() {
		/*
		 * Ripeness starts from 0. Give it a tiny bit of resistance for
		 * walking over newly ploughed ground.
		 */
		setResistance((getRipeness() + 1) * MAX_RESISTANCE / (RIPE + 1));
	}

	@Override
	public String describe() {
		String text;
		switch (getRipeness()) {
		case 0:
			text = "You see " + grainName + " that has just been harvested.";
			break;
		case RIPE:
			text = "You see ripe " + grainName + ".";
			break;
		default:
			text = "You see unripe " + grainName + ".";
			break;
		}
		return text;
	}

	/**
	 * Is called when a player tries to harvest this grain field.
	 * @param entity the harvesting entity
	 * @return true if successful
	 */
	public boolean onUsed(final RPEntity entity) {
		if (entity.nextTo(this)) {
			if (getRipeness() == RIPE) {
                if ("cane".equals(grainName)) {
                    if (entity.isEquipped("sickle")) {
                        onFruitPicked(null);
                        final Item grain = SingletonRepository.getEntityManager().getItem(
                                grainName);
                        entity.equipOrPutOnGround(grain);
                        return true;
                    } else if (entity instanceof Player) {
                        entity.sendPrivateText("You need a sickle to harvest " + grainName + " fields.");
                        return false;
                    }
                } else {
                    if (entity.isEquipped("old scythe")
                        || entity.isEquipped("scythe") 
                        || entity.isEquipped("black scythe")) {
                        onFruitPicked(null);
                        final Item grain = SingletonRepository.getEntityManager().getItem(
                                grainName);
                        entity.equipOrPutOnGround(grain);
                        return true;
                    } else if (entity instanceof Player) {
                        entity.sendPrivateText("You need a scythe to harvest " + grainName + " fields.");
                        return false;
                    }
                }
			} else if (entity instanceof Player) {
				entity.sendPrivateText("This " + grainName + " is not yet ripe enough to harvest.");
				return false;
			}
		} else if (entity instanceof Player) {
			entity.sendPrivateText("You can't reach that " + grainName + " from here.");
		}
		return false;
	}

}
