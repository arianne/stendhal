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

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.behaviour.journal.ProducerRegister;
import games.stendhal.server.entity.player.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
			for (RPObject object : characters.values()) {
				inspect(admin, object);
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

		for (final String value : target) {
			sb.append(value + ": " + target.get(value) + "\n");
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
		}
	}
}
