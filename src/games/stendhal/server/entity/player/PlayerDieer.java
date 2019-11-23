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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Killer;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.RingOfLife;
import games.stendhal.server.entity.item.SlotActivatedItem;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.slot.Slots;
import marauroa.common.Pair;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Handles death of players.
 *
 * @author hendrik
 */
public class PlayerDieer {
	/** The name of the zone placed in when killed. */
	public static final String DEFAULT_DEAD_AREA = "int_afterlife";

	private static final Logger logger = Logger.getLogger(PlayerDieer.class);
	private final Player player;

	private List<Item> drops;
	// it is not crazy to have a number of drops as well as a drops list
	// because there will be one entry  in the drops list for each item
	// but could be more than one stackable item per entry
	// i need the number of drops for saying it/them at the end.
	private int numberOfDrops;

	public PlayerDieer(final Player player) {
		this.player = player;
	}


	public void onDead(final Killer killer) {
		player.put("dead", "");
		logger.debug("ondeadstart");
		abondonPetsAndSheep();

		player.getStatusList().removeAll();

		double penaltyFactor = 1.0;

		// only lose skills if creature is a spawned creature and not one from /summon or Plague
		if (!(killer instanceof RaidCreature)) {
			logger.debug("noraidcreature");
			logger.debug("player karma is " + player.getKarma());
   			double karma;
			if (player.isBadBoy()) {
			    // don't allow PKers to use good karma to help against death penalty
			    // if they had positive karma, this will return 0 (i.e. no change to the normal death penalty)
				karma = player.useKarma(-100.0, 0.0);
			} else {
				karma = player.useKarma(-100.0, 100.0);
			}
			logger.debug("karma selected: " + karma);
			// scale down to between -1 and 1, as the penalty factor is scaled to 1 (we will also need to scale again before adding to penaltyFactor)
			karma = karma / 100.0;

			final List<RingOfLife> ringList = player.getAllEquippedWorkingRingOfLife();
			// A very unlucky player might drop the ring too
			for (Item item : drops) {
				if (item instanceof RingOfLife) {
					RingOfLife ring = (RingOfLife) item;
					if (!ring.isBroken()) {
						ringList.add((RingOfLife) item);
					}
				}
			}

			logger.debug("ringlist " + ringList);

			if (ringList.isEmpty()) {
			    // if player has positive karma, then they will lose between 0% and 10% skills - less than if karma was ignored
			    // if player has negative karma, they lose between 10% and 20% skills - more than if karma was ignored
			    penaltyFactor = 0.9 + (karma / 10.0);
			    logger.debug("penaltyFactor: " + penaltyFactor);
			} else {
			    // if player has positive karma, then they will lose between 0% and 1% skills - less than if karma ignored
			    // if player has negative karma, they lose between 1% and 2% skills - more than if karma was ignored
				// Use up a random ring
				Rand.rand(ringList).damage();
				penaltyFactor = 0.99 + (karma / 100.0);
			}

			// round to 3 decimal places (i.e as a percentage it will be one decimal place)
			penaltyFactor = (double) Math.round(penaltyFactor * 1000) / 1000;

			// note on karma: players can only hit the maximums of these ranges if they themselves had over 100 Karma, less than -100 karma, respectively.
			// and even then, some chance will mean they are not guaranteed to hit the maximum
			// (just because we call useKarma(-100.0,100.0) doesn't mean that a player with over 100.0 karma will get 100.0 used. He is just more likely to get 100.0 used.)

			// Using subXP() instead of using setXP() directly to get the level
			// checks correctly done. setXP() can not do the magic unlike setAtkXP()
			// & setDEFXP() because it's used by creatures as well
			player.subXP((int) Math.round(player.getXP() * (1 - penaltyFactor)));
			player.setAtkXP((int) Math.round(player.getAtkXP() * penaltyFactor));
			player.setDefXP((int) Math.round(player.getDefXP() * penaltyFactor));
			if (killer instanceof Player) {
								Player playerKiller = (Player) killer;
								handlePlayerKiller(playerKiller);
			}
			player.update();
		}

		// this is for telling the player what % of their old value, the skills are now. so, some loss of precision is ok
		// but we don't want to say it is 100% when it is not.
		final String skillPercentage;
		if (penaltyFactor > 0.99 && penaltyFactor < 1) {
			skillPercentage = String.format("%.1f",penaltyFactor * 100.0);
		} else {
			skillPercentage = String.format("%.0f",penaltyFactor * 100.0);
		}
		player.setHP(player.getBaseHP());

		player.returnToOriginalOutfit();

		// After a tangle with the grim reaper, give some karma,
		// but limit abuse
		if (player.getKarma() < 75.0) {
			player.addKarma(100.0);
		}

		String zoneinfo = player.getZone().describe();
		String locationmsg = "You died " + zoneinfo;
		if(!player.getZone().isInterior()) {
			// only tell the more precise location inside the zone if it's not an interior
			int x = player.getZone().getWidth();
			int y = player.getZone().getHeight();
			int lastx = player.getX();
			int lasty = player.getY();
			String northsouth = (lasty < y/3) ? "north " : ( (lasty > 2*y/3) ? "south " : "");
			String eastwest = (lastx < x/3) ? "west" : ( (lastx > 2*x/3) ? "east" : "");
			String pos = (northsouth + eastwest);
			if (pos.equals("")) {
				pos = "center";
			}
			locationmsg += " in the " + pos + " part";
		}
		respawnInAfterLife();

		player.sendPrivateText(NotificationType.INFORMATION, locationmsg +".");
		if (numberOfDrops > 0) {
			Collection<String> strings = new LinkedList<String>();
			for (Item item : this.drops) {
				if (item instanceof StackableItem) {
					StackableItem si = (StackableItem) item;
					if (si.getQuantity() > 1) {
						StringBuilder sb = new StringBuilder();
						sb.append(si.getQuantity());
						sb.append(" ");
						sb.append(Grammar.plural(si.getName()));
						strings.add(sb.toString());
					}
					if (si.getQuantity() == 1) {
						strings.add(Grammar.a_noun(si.getName()));
					}
				} else {
					strings.add(Grammar.a_noun(item.getName()));
				}
			}
			player.sendPrivateText(NotificationType.NEGATIVE, "Your corpse contains " + Grammar.enumerateCollection(strings) + ", but you may be able to retrieve " + Grammar.itthem(numberOfDrops) + ". Your skills are " + skillPercentage + "% of their old value.");
		} else {
			player.sendPrivateText(NotificationType.POSITIVE, "You were lucky and dropped no items when you died. Your skills are " + skillPercentage + "% of their old value.");
		}
	}

	private void handlePlayerKiller(final Player playerKiller) {
		// Do not punish on suicide. (That happen at least with club of thorns).
		if (playerKiller != player) {
			playerKiller.setLastPlayerKill(System.currentTimeMillis());
		}
	}

	private void respawnInAfterLife() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(DEFAULT_DEAD_AREA);

		if (zone == null) {
			logger.error("Unable to find dead area [" + DEFAULT_DEAD_AREA
					+ "] for player: " + player.getName());
		} else {
			if (!zone.placeObjectAtEntryPoint(player)) {
				logger.error("Unable to place player in zone " + zone + ": "
						+ player.getName());
			}
		}
	}

	private void abondonPetsAndSheep() {
		final Sheep sheep = player.getSheep();

		if (sheep != null) {
			player.removeSheep(sheep);
		}

		final Pet pet = player.getPet();

		if (pet != null) {
			player.removePet(pet);
		}
	}

	protected void dropItemsOn(final Corpse corpse) {
		// drop at least 1 and at most 4 items
		final int maxItemsToDrop = Rand.rand(4);
		final List<Pair<RPObject, RPSlot>> objects = retrieveAllDroppableObjects();
		drops = new LinkedList<Item>();
		numberOfDrops = 0;
		Collections.shuffle(objects);
		for (int i = 0; i < maxItemsToDrop; i++) {
			if (!objects.isEmpty()) {
				final Pair<RPObject, RPSlot> object = objects.remove(0);

				// deactivate slot activated items
				if (object.first() instanceof SlotActivatedItem) {
					SlotActivatedItem slotItem = (SlotActivatedItem) object.first();
					slotItem.onUnequipped();
				}

				if (object.first() instanceof StackableItem) {
					final StackableItem item = (StackableItem) object.first();

					// We won't drop the full quantity, but only a
					// percentage.
					// Get a random percentage between 25 % and 75 % to drop
					final double percentage = (Rand.rand(50) + 25) / 100.0;
					final int quantityToDrop = (int) Math.round(item.getQuantity()
							* percentage);

					if (quantityToDrop > 0) {
						final StackableItem itemToDrop = item.splitOff(quantityToDrop);
						new ItemLogger().splitOff(player, item, itemToDrop, quantityToDrop);
						new ItemLogger().equipAction(player, itemToDrop,
							new String[]{"slot", player.getName(), object.second().getName()},
							new String[]{"slot", player.getName(), "content"});
						corpse.add(itemToDrop);
						numberOfDrops += quantityToDrop;
						drops.add(itemToDrop);
					}
				} else if (object.first() instanceof Item) {
					Item justItem = (Item) object.first();
					object.second().remove(object.first().getID());
					new ItemLogger().equipAction(player, (Entity) object.first(),
									new String[]{"slot", player.getName(), object.second().getName()},
									new String[]{"slot", player.getName(), "content"});

					corpse.add((PassiveEntity) object.first());
					numberOfDrops += 1;
					drops.add(justItem);
				}
			}
		}
	}

	/**
	 *
	 * @return a list of all Items in RPEntity carrying slots that can be dropped
	 */
	private List<Pair<RPObject, RPSlot>> retrieveAllDroppableObjects() {
		final List<Pair<RPObject, RPSlot>> objects = new LinkedList<Pair<RPObject, RPSlot>>();

		for (RPSlot slot : player.slots(Slots.CARRYING)) {

			// a list that will contain the objects that could
			// be dropped.
			for (final RPObject objectInSlot : slot) {
				addDroppableObjects(objectInSlot, objects);
			}
		}
		return objects;
	}

	/**
	 * Add any droppable objects inside an object, including the object itself
	 * if it's droppable and empty. The contents are scanned recursively.
	 *
	 * @param obj
	 * @param list
	 */
	private void addDroppableObjects(RPObject obj, List<Pair<RPObject, RPSlot>> list) {
		boolean droppable = true;
		for (RPSlot slot : obj.slots()) {
			for (RPObject subobj : slot) {
				addDroppableObjects(subobj, list);
				// Don't drop containers, if they are not empty
				droppable = false;
			}
		}
		if (obj instanceof Item) {
			Item item = (Item) obj;
			// don't drop special quest rewards as there is no way to
			// get them again
			if (item.isBound() || item.isUndroppableOnDeath()) {
				droppable = false;
			}
		}
		if (droppable) {
			list.add(new Pair<RPObject, RPSlot>(obj, obj.getContainerSlot()));
		}
	}
}
