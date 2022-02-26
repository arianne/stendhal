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
package games.stendhal.server.script;

import static games.stendhal.server.entity.player.PlayerLootedItemsHandler.LOOTED_ITEMS;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.behaviour.journal.ProducerRegister;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * Deep inspects a player and all his/her items.
 *
 * @author hendrik
 */
public class DeepInspect extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);
		if ((args.size() != 2) || (!(args.get(0).equals("character") || args.get(0).equals("username")))) {
			admin.sendPrivateText("usage: {\"character\" | \"username\"} <name>.");
			admin.sendPrivateText("character will do an inspection of an online character.");
			admin.sendPrivateText("username will do an inspection of all characters belonging to that account as they are stored in the database.");
			return;
		}
		if (args.get(0).equals("character")) {
			inspectOnline(admin, args.get(1));
		} else {
			inspectOffline(admin, args.get(1));
		}
	}

	/**
	 * inspect an online character
	 *
	 * @param admin  Inspector
	 * @param charname name of online character to inspect
	 */
	private void inspectOnline(final Player admin, final String charname) {
		Player player = SingletonRepository.getRuleProcessor().getPlayer(charname);
		if (player == null) {
			admin.sendPrivateText(NotificationType.ERROR, "There is no character called " + charname + " online.");
			return;
		}
		inspect(admin, player);
	}

	/**
	 * inspects offline players
	 *
	 * @param admin  Inspector
	 * @param username username who's characters are being inspected
	 */
	private void inspectOffline(final Player admin, final String username) {
		try {
			Map<String, RPObject> characters = DAORegister.get().get(CharacterDAO.class).loadAllActiveCharacters(username);
			int i = 0;
			for (RPObject object : characters.values()) {
				i++;
				TurnNotifier.get().notifyInSeconds(i, new TurnListener() {

					@Override
					public void onTurnReached(int currentTurn) {
						inspect(admin, object);
					}
				});
			}
		} catch (SQLException e) {
			admin.sendPrivateText(NotificationType.ERROR, e.toString());
		} catch (IOException e) {
			admin.sendPrivateText(NotificationType.ERROR, e.toString());
		}
	}

	/**
	 * Inspects a player
	 *
	 * @param admin  Inspector
	 * @param target player being inspected
	 */
	private void inspect(final Player admin, final RPObject target) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Inspecting " + target.get("name") + "\n");

		final List<String> locationSlots = Arrays.asList("zoneid", "x", "y");

		for (final String value : target) {
			// skip attributes used on the location info section below
			if (!locationSlots.contains(value)) {
				sb.append(value + ": " + target.get(value) + "\n");
			}
		}

		// location info
		if (target.has("zoneid") && target.has("x") && target.has("y")) {
			final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(target.get("zoneid"));
			final int zoneX = zone.getX();
			final int zoneY = zone.getY();
			final int zoneZ = zone.getLevel();
			final int X = target.getInt("x");
			final int Y = target.getInt("y");
			final String absolutePos = Integer.toString(zoneX + X) + "," + Integer.toString(zoneY + Y) + "," + Integer.toString(zoneZ);

			sb.append("\nLocation info:\n");
			sb.append("  Zone ID:\t\t" + zone.getName() + "\n");

			sb.append("  Zone coordinates:\t" + Integer.toString(zone.getX()) + "," + Integer.toString(zone.getY()) + "," + Integer.toString(zone.getLevel()) + "\n");
			sb.append("  Relative pos:\t" + target.get("x") + "," + target.get("y") + "\n");
			sb.append("  Absolute pos:\t" + absolutePos + "\n");
		}

		admin.sendPrivateText(sb.toString());
		sb.setLength(0);

		// Inspect slots
		for (final RPSlot slot : target.slots()) {
			// don't return buddy-list for privacy reasons
			if (slot.getName().equals("!buddy")
					|| slot.getName().equals("!ignore")) {
				continue;
			}
			sb.append("\nSlot " + slot.getName() + ": \n");

			// list objects
			for (final RPObject object : slot) {
				sb.append("   " + object + "\n");
			}

			sb.append("\n");
			admin.sendPrivateText(sb.toString());
			sb.setLength(0);
		}

		if (target instanceof Player) {
			Player player = (Player) target;

			// Produced items
			sb.append("Production:\n   ");
			final ProducerRegister producerRegister = SingletonRepository.getProducerRegister();
		    final List<String> produceList = new LinkedList<String>();

		    for (String food : producerRegister.getProducedItemNames("food")) {
		    	produceList.add(food);
		    }
		    for (String drink : producerRegister.getProducedItemNames("drink")) {
		    	produceList.add(drink);
		    }
		    for (String resource : producerRegister.getProducedItemNames("resource")) {
		    	produceList.add(resource);
		    }

			for (String product : produceList) {
				int quant = player.getQuantityOfProducedItems(product);
				if (quant > 0) {
					sb.append("[" + product + "=" + Integer.toString(quant) + "]");
				}
			}

			sb.append("\n");
			admin.sendPrivateText(sb.toString());
			sb.setLength(0);


			Collection<Item> itemList = SingletonRepository.getEntityManager().getItems();

			// all production
			sb.append("All Production (excludes items above):");

			final Map<String, Map<String, String>> allProduced = new TreeMap<>(); // TreeMap keeps items sorted alphabetically
			for (final Entry<String, String> e: player.getMap(LOOTED_ITEMS).entrySet()) {
				String prefix = "misc";
				String itemName = e.getKey();
				final String itemQuantity = e.getValue();

				if (itemName.contains(".")) {
					prefix = itemName.substring(0,itemName.indexOf("."));
					itemName = itemName.replace(prefix + ".", "");
				}

				// exclude items from "Production" section
				if (!produceList.contains(itemName)) {
					final Map<String, String> tmp;
					if (!allProduced.containsKey(prefix)) {
						tmp = new TreeMap<>();
					} else {
						tmp = allProduced.get(prefix);
					}

					tmp.put(itemName, itemQuantity);
					allProduced.put(prefix, tmp);
				}
			}

			for (final String category: allProduced.keySet()) {
				sb.append("\n   " + category + ":\n      ");
				for (final Entry<String, String> e: allProduced.get(category).entrySet()) {
					sb.append("[" + e.getKey() + "=" + e.getValue() + "]");
				}
			}

			sb.append("\n");
			admin.sendPrivateText(sb.toString());
			sb.setLength(0);

			// Looted items
			sb.append("Loots:\n   ");

			int itemCount = 0;
			for (Item item : itemList) {
				itemCount = player.getNumberOfLootsForItem(item.getName());
				if (itemCount > 0) {
					sb.append("[" + item.getName() + "=" + Integer.toString(itemCount) + "]");
				}
			}

			sb.append("\n");
			admin.sendPrivateText(sb.toString());
			sb.setLength(0);


			// Harvested items
			sb.append("Harvested Items (FishSource, FlowerGrower, VegetableGrower):\n   ");
			itemCount = 0;
			for (Item item : itemList) {
				itemCount = player.getQuantityOfHarvestedItems(item.getName());
				if (itemCount > 0) {
					sb.append("[" + item.getName() + "=" + Integer.toString(itemCount) + "]");
				}
			}

			sb.append("\n");
			admin.sendPrivateText(sb.toString());
			sb.setLength(0);

			// commerce: money spent & gained
			sb.append("\nPurchases from NPCs:\n");
			Map<String, String> commerceInfo = player.getMap("npc_purchases");
			boolean addedCInfo = false;
			if (commerceInfo != null) {
				for (final String npcName: commerceInfo.keySet()) {
					if (!addedCInfo) {
						sb.append("    ");
						addedCInfo = true;
					}
					sb.append("[" + npcName + ":" + commerceInfo.get(npcName) + "]");
				}
			}

			sb.append("\n\nSales to NPCs:\n");
			commerceInfo = player.getMap("npc_sales");
			addedCInfo = false;
			if (commerceInfo != null) {
				for (final String npcName: commerceInfo.keySet()) {
					if (!addedCInfo) {
						sb.append("    ");
						addedCInfo = true;
					}
					sb.append("[" + npcName + ":" + commerceInfo.get(npcName) + "]");
				}
			}

			sb.append("\n");
			admin.sendPrivateText(sb.toString());
			sb.setLength(0);
		}
	}
}
