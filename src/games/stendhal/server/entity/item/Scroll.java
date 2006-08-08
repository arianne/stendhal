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
import games.stendhal.server.StendhalRPWorld;
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
 * Magic scrolls
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

		if (name.equals("empty_scroll")) {
			onEmptyScroll(player);
		} else if (name.equals("marked_scroll") || name.equals("home_scroll")) {
			onTeleportScroll(player);
		} else if (name.equals("archers_protection_scroll")) {
			onCreatureProtection(player);
		} else if (name.equals("summon_scroll")) {
			onSummon(player);
		} else {
			player.sendPrivateText("I am unable to use this scroll");
		}

		this.removeOne();
		getWorld().modify(player);
	}

	private void onCreatureProtection(Player player) {
		player.sendPrivateText("I am unable to use this scroll");
	}

	private void onEmptyScroll(Player player) {
		Item item = getWorld().getRuleManager().getEntityManager().getItem(
				"marked_scroll");
		StendhalRPZone zone = (StendhalRPZone) getWorld().getRPZone(player.get("zoneid"));
		zone.assignRPObjectID(item);
		item.setx(player.getx());
		item.sety(player.gety());
		item.put("infostring", "" + player.getID().getZoneID() + " "
				+ player.getx() + " " + player.gety());
		zone.add(item);
	}

	private void onSummon(Player player) {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player
				.getID());
		if (zone.isInProtectionArea(player)) {
			player.sendPrivateText("Use of magic is not allowed here.");
			return;
		}

		int x = player.getInt("x");
		int y = player.getInt("y");

		EntityManager manager = ((StendhalRPWorld) world)
				.getRuleManager().getEntityManager();

		// pick it
		Collection<Creature> creatures = manager.getCreatures();
		int magiclevel = 4;
		List<Creature> possibleCreatures = new ArrayList<Creature>();
		for (Creature creature : creatures) {
			if (creature.getLevel() <= magiclevel) {
				possibleCreatures.add(creature);
			}
		}
		
		// String type = "green_dragon";
		// Creature pickedCreature = manager.getCreature(type);
		int pickedIdx = (int) (Math.random() * possibleCreatures.size());
		Creature pickedCreature = possibleCreatures.get(pickedIdx);

		// create it
		AttackableCreature creature = new AttackableCreature(pickedCreature);

		zone.assignRPObjectID(creature);
		StendhalRPAction.placeat(zone, creature, x, y);
		zone.add(creature);

		creature.init();
		creature.setMaster(player);
		creature.clearDropItemList();

		rp.addNPC(creature);
	}

	private void onTeleportScroll(Player player) {

		// init as home_scroll
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone("0_semos_city");
		int x = 30;
		int y = 40;

		// is it a marked scroll?
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