/***************************************************************************
 *                   (C) Copyright 2003-2015 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.events;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;

/**
 * manages the tutorial based on events created all over the game.
 *
 * @author hendrik
 */
public class TutorialNotifier {

	/**
	 * If the specified event is unknown, add it to the list and send the text
	 * to the player.
	 *
	 * @param player
	 *            Player
	 * @param type
	 *            EventType
	 */
	private static void process(final Player player, final TutorialEventType type) {
		final String key = type.name().toLowerCase();
		if (player.getKeyedSlot("!tutorial", key) == null) {
			player.setKeyedSlot("!tutorial", key, "1");

			// we must delay this for 1 turn for technical reasons (like zone
			// change)
			// but we delay it for 2 seconds so that the player has some time to
			// recognize the event
			new DelayedPlayerTextSender(player, "Tutorial: " + type.getMessage(), NotificationType.TUTORIAL, 2);

		}
	}

	/**
	 * Login.
	 *
	 * @param player
	 *            Player
	 */
	public static void login(final Player player) {
		process(player, TutorialEventType.FIRST_LOGIN);
	}

	/**
	 * moving.
	 *
	 * @param player
	 *            Player
	 */
	public static void move(final Player player) {
		final StendhalRPZone zone = player.getZone();
		if (zone != null) {
			if (zone.getName().equals("int_semos_guard_house")) {
				process(player, TutorialEventType.FIRST_MOVE);
			}
		}
	}

	/**
	 * Zone changes.
	 *
	 * @param player
	 *            Player
	 * @param sourceZone
	 *            source zone
	 * @param destinationZone
	 *            destination zone
	 */
	public static void zoneChange(final Player player, final String sourceZone,
			final String destinationZone) {
		if (sourceZone.equals("0_semos_village_w") && destinationZone.equals("int_semos_guard_house")) {
			process(player, TutorialEventType.RETURN_GUARDHOUSE);
		} else if (destinationZone.equals("0_semos_city")) {
			process(player, TutorialEventType.VISIT_SEMOS_CITY);
		} else if (destinationZone.equals("int_semos_tavern_0")) {
			process(player, TutorialEventType.VISIT_SEMOS_TAVERN);
		} else if (destinationZone.equals("-1_semos_dungeon")) {
			process(player, TutorialEventType.VISIT_SEMOS_DUNGEON);
		} else if (destinationZone.equals("-2_semos_dungeon")) {
			process(player, TutorialEventType.VISIT_SEMOS_DUNGEON_2);
		} else if (destinationZone.equals("int_afterlife")) {
			process(player, TutorialEventType.FIRST_DEATH);
		} else if (destinationZone.equals("0_semos_plains_n")){
			process(player, TutorialEventType.VISIT_SEMOS_PLAINS);
		}
	}

	/**
	 * player got attacked.
	 *
	 * @param player
	 *            Player
	 */
	public static void attacked(final Player player) {
		process(player, TutorialEventType.FIRST_ATTACKED);
	}

	/**
	 * player killed something.
	 *
	 * @param player
	 *            Player
	 */
	public static void killedSomething(final Player player) {
		process(player, TutorialEventType.FIRST_KILL);
	}

	/**
	 * player killed another player.
	 *
	 * @param player
	 *            Player
	 */
	public static void killedPlayer(final Player player) {
		process(player, TutorialEventType.FIRST_PLAYER_KILL);
	}


	/**
	 * player got poisoned.
	 *
	 * @param player
	 *            Player
	 */
	public static void poisoned(final Player player) {
		process(player, TutorialEventType.FIRST_POISONED);
	}

	/**
	 * a player who stayed another minute in game.
	 *
	 * @param player
	 *            Player
	 * @param age
	 *            playing time
	 */
	public static void aged(final Player player, final int age) {
		if (age >= 60) {
			process(player, TutorialEventType.TIMED_RULES);
		} else if (age >= 45) {
			process(player, TutorialEventType.TIMED_PASSWORD);
		} else if (age >= 30) {
			// this is a new tutorial event, so we check isNew,
			// as our older players don't need to know how to change outfit
			if (player.isNew()) {
				process(player, TutorialEventType.TIMED_OUTFIT);
			}
		} else if (age >= 15) {
			// players less likely to get this event now that they do not start naked
			// but keep it anyway as it's a cute feature to notice that players are naked
			if (player.isNaked()) {
				process(player, TutorialEventType.TIMED_NAKED);
			}
		} else if (age >= 5) {
			process(player, TutorialEventType.TIMED_HELP);
		}
	}
	/**
	 * player > level 2 logged in for new release.
	 *
	 * @param player
	 *            Player
	 */
	public static void newrelease(final Player player) {
		// process(player, TutorialEventType.NEW_RELEASE77);
	}

	/**
	 * player got private messaged
	 *
	 * @param player
	 *            Player
	 */
	public static void messaged(final Player player) {
		process(player, TutorialEventType.FIRST_PRIVATE_MESSAGE);
	}

	/**
	 * player got something given from an equip item action of an npc
	 *
	 * @param player
	 *            Player
	 */
	public static void equippedByNPC(final Player player, final Item item) {
		// do not trigger on Stackable items, as it may be confusing to players
		// that already own an item of that type. It is far less discoverable
		// that the number have increased compared to a new item showing up
		if (!(item instanceof StackableItem)) {
			process(player, TutorialEventType.FIRST_EQUIPPED);
		}
	}
}
