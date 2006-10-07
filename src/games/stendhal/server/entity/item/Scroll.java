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

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.events.UseListener;
import games.stendhal.server.rule.EntityManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Represents a magic scroll.
 */
public class Scroll extends StackableItem implements UseListener {
	private static final Logger logger = Logger.getLogger(Scroll.class);

	/**
	 * Creates a new scroll
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Scroll(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	@Override
	public boolean isStackable(Stackable other) {
		StackableItem otheri = (StackableItem) other;
		String name = getName();

		// first step make sure that both are scrolls of the same kind
		if (getItemClass().equals(otheri.getItemClass())
				&& getItemSubclass().equals(otheri.getItemSubclass())) {

			// ok they are of the same kind. But we need a special case for scrolls with infostrings
			if (name.equals("marked_scroll") || name.equals("summon_scroll")) {
				// marked_scroll and summon_scroll can be stacked
				// if they refer to the same location / creature
				if (has("infostring") && otheri.has("infostring")) {
					return (get("infostring").equals(otheri.get("infostring")));
				}

				// summon_scroll without infostring can be stacked as well
				if (name.equals("summon_scroll")) {
					return (!has("infostring") && !otheri.has("infostring"));
				}

				// at least one scroll as an inforstring and it is not equal to the other
				return false;
			}

			// scrolls of the same kind can be stacked (infostring was already dealt with)
			return true;
		}

		// these items are not scrolls of the same kind
		return false;
	}

	public void onUsed(RPEntity user) {
		
		if (this.isContained()) {
			// We modify the base container if the object change.
			RPObject base = this.getContainer();

			while (base.isContained()) {
				base = base.getContainer();
			}

			if (!user.nextTo((Entity) base, 0.25)) {
				logger.debug("Consumable item is too far");
				return;
			}
		} else {
			if (!user.nextTo(this, 0.25)) {
				logger.debug("Consumable item is too far");
				return;
			}
		}
		
		Player player = (Player) user;
		String name = getName();
		boolean successful;

		if (name.equals("empty_scroll")) {
			successful = useEmptyScroll(player);
		} else if (name.equals("marked_scroll") || name.equals("home_scroll")) {
			successful = useTeleportScroll(player);
		} else if (name.equals("summon_scroll")) {
			successful = useSummonScroll(player);
		} else {
			player.sendPrivateText("What a strange scroll! You can't make heads or tails of it.");
			successful = false;
		}
		if (successful) {
			this.removeOne();
			player.notifyWorldAboutChanges();
		}
	}

	/**
	 * 
	 * @param player
	 * @return always true
	 */
	private boolean useEmptyScroll(Player player) {
		Item markedScroll = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
				"marked_scroll");
		markedScroll.put("infostring", "" + player.getID().getZoneID() + " "
				+ player.getX() + " " + player.getY());
		player.equip(markedScroll, true);
		return true;
	}

	/**
	 * Is invoked when a summon scroll is used.
	 * @param player The player who used the scroll
	 * @return true iff summoning was successful
	 */
	private boolean useSummonScroll(Player player) {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player
				.getID());
		if (zone.isInProtectionArea(player)) {
			player.sendPrivateText("The aura of protection in this area prevents the scroll from working!");
			return false;
		}
		
		if (StendhalRPRuleProcessor.get().getNPCs().size() > 100) {
			player.sendPrivateText("Mysteriously, the scroll does not function! Perhaps this area is too crowded...");
			logger.error("too many npcs");
			return false;
		}

		int x = player.getInt("x");
		int y = player.getInt("y");

		EntityManager manager = StendhalRPWorld.get()
				.getRuleManager().getEntityManager();

		Creature pickedCreature = null;
		if (has("infostring")) {

			// scroll for special monster
			String type = get("infostring");
			pickedCreature = manager.getCreature(type);			
		} else {
			
			// pick it randomly
			Collection<Creature> creatures = manager.getCreatures();
			int magiclevel = 4;
			List<Creature> possibleCreatures = new ArrayList<Creature>();
			for (Creature creature : creatures) {
				if (creature.getLevel() <= magiclevel) {
					possibleCreatures.add(creature);
				}
			}
			int pickedIdx = (int) (Math.random() * possibleCreatures.size());
			pickedCreature = possibleCreatures.get(pickedIdx);
		}

		// create it
		AttackableCreature creature = new AttackableCreature(pickedCreature);

		zone.assignRPObjectID(creature);
		StendhalRPAction.placeat(zone, creature, x, y);
		zone.add(creature);

		creature.init();
		creature.setMaster(player);
		creature.clearDropItemList();
		creature.put("title_type", "friend");

		StendhalRPRuleProcessor.get().addNPC(creature);
		return true;
	}

	/**
	 * Is invoked when a teleporting scroll is used. Tries to put the
	 * player on the scroll's destination, or near it. 
	 * @param player The player who used the scroll and who will be teleported
	 * @return true iff summoning was successful
	 */
	private boolean useTeleportScroll(Player player) {
		// init as home_scroll
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("0_semos_city");
		int x = 30;
		int y = 40;

		// Is it a marked scroll? Marked scrolls have a destination which
		// is stored in the infostring, existing of a zone name and x and y
		// coordinates
		if (has("infostring")) {
			String infostring = get("infostring");
			java.util.StringTokenizer st = new java.util.StringTokenizer(infostring);
			if (st.countTokens() == 3) {
				zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(st.nextToken());
				x = Integer.parseInt(st.nextToken());
				y = Integer.parseInt(st.nextToken());
			}
		}
		// we use the player as teleporter (last parameter) to give feedback
		// if something goes wrong.
		return player.teleport(zone, x, y, null, player);
	}

	@Override
	public String describe() {
		String text = super.describe();
		if (has("infostring") && get("infostring") != null) {
			text += " Upon it is written: " + get("infostring");
		}
		return (text);
	}

}
