/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package utilities;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Events;
import games.stendhal.common.parser.WordList;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.transformer.PlayerTransformer;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.DressedEntityRPClass;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.PlayerSlot;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import utilities.RPClass.ItemTestHelper;

public abstract class PlayerTestHelper {
	private static Logger logger = Logger.getLogger(PlayerTestHelper.class);

	/**
	 * Create a named player.
	 *
	 * @param name
	 * @return player
	 */
	public static Player createPlayer(final String name) {
		generatePlayerRPClasses();

		final RPObject object = new RPObject();
		object.put("name", name);
		final Player pl = (Player) new PlayerTransformer().transform(object);
		final Iterator<RPEvent> eventsIterator = pl.eventsIterator();
		while(eventsIterator.hasNext()) {
			eventsIterator.next();
			eventsIterator.remove();
		}

		pl.setName(name);

		//addEmptySlots(pl);

		return pl;
	}

	/**
	 * Register a player in rule processor, world and zone.
	 *
	 * @param player
	 * @param zoneName
	 */
	public static void registerPlayer(final Player player, final String zoneName) {
		registerPlayer(player, SingletonRepository.getRPWorld().getZone(zoneName));
	}

	/**
	 * Register a player in rule processor, world and zone.
	 *
	 * @param player
	 * @param zone
	 */
	public static void registerPlayer(final Player player, final StendhalRPZone zone) {
		registerPlayer(player);

		zone.add(player);
	}

	/**
	 * Register a player in rule processor and world.
	 *
	 * @param player
	 */
	public static void registerPlayer(final Player player) {
		MockStendhalRPRuleProcessor.get().addPlayer(player);

		MockStendlRPWorld.get().add(player);
	}

	public static Player createPlayerWithOutFit(final String name) {
		final Player player = createPlayer(name);

		player.setOutfit(1, 1, 1, 0, 0, 0, 1, 0, 0);

		return player;
	}

	/**
	 * Remove a player from rule processor, world and zone.
	 *
	 * @param player
	 * @param zone
	 */
	public static void unregisterPlayer(final Player player, final StendhalRPZone zone) {
		zone.remove(player);
		removePlayer(player);
	}

	/**
	 * Remove a player from rule processor, world and zone.
	 *
	 * @param playerName
	 * @param zone
	 */
	public static void removePlayer(final String playerName, final StendhalRPZone zone) {
		final Player player = MockStendhalRPRuleProcessor.get().getPlayer(playerName);

		if (player != null) {
			unregisterPlayer(player, zone);
		}
	}

	/**
	 * Remove a player from rule processor, world and zone.
	 *
	 * @param playerName
	 * @param zoneName
	 */
	public static void removePlayer(final String playerName, final String zoneName) {
		removePlayer(playerName, MockStendlRPWorld.get().getZone(zoneName));
	}

	/**
	 * Remove a player from world and rule processor.
	 *
	 * @param playerName
	 */
	public static void removePlayer(final String playerName) {
		final Player player = MockStendhalRPRuleProcessor.get().getPlayer(playerName);

		if (player != null) {
			removePlayer(player);
		}
	}

	/**
	 * Remove a player from world and rule processor.
	 *
	 * @param player
	 */
	public static void removePlayer(final Player player) {
		if (player != null) {
			final String name = player.getName();

			MockStendlRPWorld.get().remove(player.getID());
			MockStendhalRPRuleProcessor.get().getOnlinePlayers().remove(player);

			if (name != null) {
				WordList.getInstance().unregisterSubjectName(name);
			}
		}
	}

	/**
	 * Remove all players from world and rule processor.
	 */
	public static void removeAllPlayers() {
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	/**
	 * Equip the player with the given amount of money.
	 *
	 * @param player
	 * @param amount
	 * @return success flag
	 */
	public static boolean equipWithMoney(final Player player, final int amount) {
		return equipWithStackableItem(player, "money", amount);
	}

	/**
	 * Equip the player with the given items.
	 *
	 * @param player
	 * @param clazz
	 * @return success flag
	 */
	public static boolean equipWithItem(final Player player, final String clazz) {
		ItemTestHelper.generateRPClasses();
		final Item item = SingletonRepository.getEntityManager().getItem(clazz);

		return player.equipToInventoryOnly(item);
	}


	/**
	 * Equip the player with the given item and set the given item string.
	 *
	 * @param player
	 * @param clazz
	 * @param info
	 * @return success flag
	 */
	public static boolean equipWithItem(final Player player, final String clazz, final String info) {
		ItemTestHelper.generateRPClasses();
		final Item item = SingletonRepository.getEntityManager().getItem(clazz);
		item.setInfoString(info);

		return player.equipToInventoryOnly(item);
	}

	/**
	 * Equip the player with the given amount of items.
	 *
	 * @param player
	 * @param clazz
	 * @param amount
	 * @return success flag
	 */
	public static boolean equipWithStackableItem(final Player player, final String clazz, final int amount) {
		final StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem(clazz);
		item.setQuantity(amount);

		return player.equipToInventoryOnly(item);
	}

	/**
	 *
	 * @param player
	 * @param clazz
	 * @param slot
	 * @return true if it could be equipped to slot, false otherwise
	 */
	public static boolean equipWithItemToSlot(final Player player, final String clazz, final String slot) {
		ItemTestHelper.generateRPClasses();
		final Item item = SingletonRepository.getEntityManager().getItem(clazz);
		return player.equip(slot, item);
	}

	/**
	 * Reset the conversation state of the NPC.
	 *
	 * @param npc
	 * 		SpeakerNPC
	 */
	public static void resetNPC(final SpeakerNPC npc) {
		if (npc != null) {
			npc.setCurrentState(ConversationStates.IDLE);
		}
	}

	/**
	 * Reset the conversation state of the named NPC.
	 *
	 * @param npcName
	 * 		NPC string name
	 */
	public static void resetNPC(final String npcName) {
		resetNPC(SingletonRepository.getNPCList().get(npcName));
	}

	/**
	 * Remove the named NPC.
	 *
	 * @param npcName
	 */
	public static void removeNPC(final String npcName) {
		SingletonRepository.getNPCList().remove(npcName);
	}

	public static void addEmptySlots(final Player player) {
		//		"bag", "rhand", "lhand", "head", "armor",
		//		"legs", "feet", "finger", "cloak", "keyring"
		player.addSlot(new PlayerSlot("bag"));
		player.addSlot(new PlayerSlot("lhand"));
		player.addSlot(new PlayerSlot("rhand"));
		player.addSlot(new PlayerSlot("armor"));
		player.addSlot(new PlayerSlot("head"));
		player.addSlot(new PlayerSlot("legs"));
		player.addSlot(new PlayerSlot("feet"));
		player.addSlot(new PlayerSlot("finger"));
		player.addSlot(new PlayerSlot("cloak"));
		player.addSlot(new PlayerSlot("keyring"));
		player.addSlot(new RPSlot("!quests"));
		player.getSlot("!quests").add(new RPObject());
		player.addSlot(new RPSlot("!kills"));
		player.getSlot("!kills").add(new RPObject());
		player.addSlot(new RPSlot("!tutorial"));
		player.getSlot("!tutorial").add(new RPObject());
		player.addSlot(new RPSlot("!visited"));
		player.getSlot("!visited").add(new RPObject());
	}

	public static void generateEntityRPClasses() {
		if (!RPClass.hasRPClass("entity")) {
			Entity.generateRPClass();
		}

		if (!RPClass.hasRPClass("active_entity")) {
			ActiveEntity.generateRPClass();
		}

		if (!RPClass.hasRPClass("rpentity")) {
			RPEntity.generateRPClass();
		}

		if (!RPClass.hasRPClass("dressed_entity")) {
			DressedEntityRPClass.generateRPClass();
		}
	}

	public static void generateNPCRPClasses() {
		generateEntityRPClasses();

		if (!RPClass.hasRPClass("npc")) {
			NPC.generateRPClass();
		}
	}

	public static void generatePlayerRPClasses() {
		generateEntityRPClasses();

		if (!RPClass.hasRPClass("player")) {
			Player.generateRPClass();
		}
	}

	public static void generateCreatureRPClasses() {
		generateNPCRPClasses();

		if (!RPClass.hasRPClass("creature")) {
			Creature.generateRPClass();
		}
	}

	public static void dumpQuests(final Player player) {
		final List<String> quests = player.getQuests();
		for (final String quest : quests) {
			logger.info(quest + "=" + player.getQuest(quest));
		}
	}

	/**
	 * Set the (order) time in a quest slot back the specified number of seconds.
	 * @param player
	 * @param questSlot
	 * @param index
	 * @param seconds
	 */
	public static void setPastTime(final Player player, final String questSlot, final int index, final long seconds) {
		final long pastTime = new Date().getTime() - seconds*1000;

		player.setQuest(questSlot, index, Long.toString(pastTime));
	}

	/**
	 * Query the player's events for private messages.
	 * @param player
	 * @return message text
	 */
	public static String getPrivateReply(final Player player) {
		String reply = null;

		for (final RPEvent event : player.events()) {
			if (event.getName().equals(Events.PRIVATE_TEXT)) {
				reply = event.get("text");
			}
		}

		player.clearEvents();

		return reply;
	}
}
