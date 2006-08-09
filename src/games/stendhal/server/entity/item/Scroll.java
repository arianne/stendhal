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
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.events.UseEvent;
import games.stendhal.server.rule.EntityManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Represents a magic scroll.
 */
public class Scroll extends StackableItem implements UseEvent {

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
		if (getItemSubclass().equals("black")) {
			// black scroll can only be stacked if they refer to the same
			// location
			if (has("infostring") && otheri.has("infostring"))
				return (get("infostring").equals(otheri.get("infostring")));
			return (false);
		}
		return getItemClass().equals(otheri.getItemClass())
				&& getItemSubclass().equals(otheri.getItemSubclass());
	}

	public void onUsed(RPEntity user) {
		Player player = (Player) user;
		String name = getName();
		boolean successful;

		if (name.equals("empty_scroll")) {
			successful = onEmptyScroll(player);
		} else if (name.equals("marked_scroll") || name.equals("home_scroll")) {
			successful = onTeleportScroll(player);
		} else if (name.equals("archers_protection_scroll")) {
			successful = onCreatureProtection(player);
		} else if (name.equals("summon_scroll")) {
			successful = onSummon(player);
		} else {
			player.sendPrivateText("I am unable to use this scroll");
			successful = false;
		}
		if (successful) {
			this.removeOne();
			getWorld().modify(player);
		}
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	private boolean onCreatureProtection(Player player) {
		player.sendPrivateText("I am unable to use this scroll");
		return false;
	}

	/**
	 * 
	 * @param player
	 * @return always true
	 */
	private boolean onEmptyScroll(Player player) {
		Item item = getWorld().getRuleManager().getEntityManager().getItem(
				"marked_scroll");
		StendhalRPZone zone = (StendhalRPZone) getWorld().getRPZone(player.get("zoneid"));
		zone.assignRPObjectID(item);
		item.setx(player.getx());
		item.sety(player.gety());
		item.put("infostring", "" + player.getID().getZoneID() + " "
				+ player.getx() + " " + player.gety());
		zone.add(item);
		return true;
	}

	/**
	 * Is invoked when a summon scroll is used.
	 * @param player The player who used the scroll
	 * @return true iff summoning was successful
	 */
	private boolean onSummon(Player player) {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player
				.getID());
		if (zone.isInProtectionArea(player)) {
			player.sendPrivateText("Use of magic is not allowed here.");
			return false;
		}

		int x = player.getInt("x");
		int y = player.getInt("y");

		EntityManager manager = (world)
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

		rp.addNPC(creature);
		return true;
	}

	/**
	 * Is invoked when a teleporting scroll is used. Tries to put the
	 * player on the scroll's destination, or near it. 
	 * @param player The player who used the scroll and who will be teleported
	 * @return true iff summoning was successful
	 */
	private boolean onTeleportScroll(Player player) {
		// init as home_scroll
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone("0_semos_city");
		int x = 30;
		int y = 40;

		// Is it a marked scroll? Marked scrolls have a destination which
		// is stored in the infostring, existing of a zone name and x and y
		// coordinates
		if (has("infostring")) {
			String infostring = get("infostring");
			java.util.StringTokenizer st = new java.util.StringTokenizer(infostring);
			if (st.countTokens() == 3) {
				zone = (StendhalRPZone) world.getRPZone(st.nextToken());
				x = Integer.parseInt(st.nextToken());
				y = Integer.parseInt(st.nextToken());
			}
		}

		// teleport
		if (StendhalRPAction.placeat(zone, player, x, y)) {
			StendhalRPAction.changeZone(player, zone.getID().getID());
			StendhalRPAction.transferContent(player);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String describe() {
		String text = super.describe();
		if (has("infostring") && get("infostring") != null) {
			text += " It is marked with: " + get("infostring");
		}
		return (text);
	}

}