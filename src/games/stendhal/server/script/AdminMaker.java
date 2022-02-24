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

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import games.stendhal.common.Level;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.ServerModeUtil;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.core.scripting.ScriptingNPC;
import games.stendhal.server.core.scripting.ScriptingSandbox;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Creates a portable NPC who gives ALL players powerful items, increases their
 * level and makes them admins. This is used on test-systems only. Therefore it
 * is disabled in default install and you have to use this parameter:
 * -Dstendhal.testserver=junk as a vm argument.
 *
 * As admin uses /script AdminMaker.class to summon her right next to him/her.
 * Please unload it with /script -unload AdminMaker.class
 */

public class AdminMaker extends ScriptImpl {

	private static Logger logger = Logger.getLogger(AdminMaker.class);
	private static final String TELE_QUEST_SLOT = "AdminMakerTele";

	private final List<String> itemsSingle = Arrays.asList("rod of the gm",
			"golden shield", "golden armor", "golden cloak",
			"golden helmet", "golden legs", "golden boots",
			"hunter crossbow");

	private final List<String> itemsStack = Arrays.asList("money",
			"greater potion", "greater antidote", "power arrow",
			"deadly poison");


	protected class UpgradeAction implements ChatAction {

		private void xpGain(final Player player) {
			final int level = player.getLevel();

			// increase level by xlevel per execution
			int xlevel = 10;

			// Player should at least be min_level after one execution
			final int min_level = 20;
			if (level + xlevel < min_level) {
				xlevel = min_level - level;
			}

			// Don't give more XP than needed when near/at max
			if (level + xlevel > Level.maxLevel()) {
				xlevel = Level.maxLevel() - level;
			}

			player.addXP(Level.getXP(level + xlevel) - Level.getXP(level));

			// set the atk and def to half the level (is a good rule of thumb)
			final int skills = ((Level.getXP(level) + xlevel) / 2);
			player.setAtkXP(skills);
			player.setDefXP(skills);
			player.incAtkXP();
			player.incDefXP();
		}



		private void equip(final Player player) {

			// Give player all single items from list he/she doesn't have
			for (final String itemName : itemsSingle) {
				if (!player.isEquipped(itemName)) {
					final Item itemObj = sandbox.getItem(itemName);
					player.equipOrPutOnGround(itemObj);
				}
			}

			// Give 5000 of each stack in list, regardless of how many are
			// already there
			for (final String itemName : itemsStack) {
				final Item item = sandbox.getItem(itemName);
				if (item instanceof StackableItem) {
					final StackableItem stackableItem = (StackableItem) item;
					stackableItem.setQuantity(5000);
					player.equipToInventoryOnly(stackableItem);
				}
			}
			// turn on their keyring for them
			player.setFeature("keyring", "2 4");
		}

		private void admin(final Player player) {
			if (player.getAdminLevel() == 0) {
				// can't use destroy/summon/alter/script
				player.setAdminLevel(600);
				player.update();
				player.notifyWorldAboutChanges();
			}
		}

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
			raiser.say("I will give you some items, and adjust your level and skills. Also, your keyring is enabled.");
			xpGain(player);
			equip(player);
			admin(player);
		}
	}

	protected class TeleportAction implements ChatAction {
		private class Destination {
			private final String zone;
			private final int x;
			private final int y;

			Destination(final String zone, final int x, final int y) {
				this.zone = zone;
				this.x = x;
				this.y = y;
			}
		}

		private final List<Destination> DESTINATIONS = Arrays.asList(
			new Destination("0_nalwor_city", 88, 85),
			new Destination("-1_nalwor_drows_tunnel_n", 58, 44),
			new Destination("0_ados_city", 30, 57),
			new Destination("0_orril_forest_e", 107, 7),
			new Destination("0_ados_mountain_n2_w2", 10, 100),
			new Destination("0_semos_mountain_n2_e2", 86, 71),
			new Destination("-2_orril_dungeon", 106, 21),
			new Destination("-2_orril_lich_palace", 67, 118),
			new Destination("-2_orril_dwarf_mine", 50, 40),
			new Destination("-1_semos_mine_nw", 22, 75),
			new Destination("-6_kanmararn_city", 33, 52),
			new Destination("-2_ados_outside_nw", 28, 4),
			new Destination("-2_kotoch_entrance", 20, 111),
			new Destination("1_kikareukin_cave", 18, 97),
			new Destination("0_kalavan_city", 64, 13),
			new Destination("0_kirdneh_city", 63, 26),
			new Destination("0_fado_city", 30, 20),
			new Destination("-1_fado_great_cave_e3", 13, 100),
			new Destination("-1_fado_great_cave_w2", 90, 57),
			new Destination("0_athor_island", 77, 73),
			new Destination("5_kikareukin_cave", 31, 100),
			new Destination("-2_semos_mine_e2", 4, 5),
			new Destination("0_amazon_island_nw", 30, 30),
			new Destination("int_mithrilbourgh_stores", 6, 5)
		);


		private boolean randomTeleport(final Player player) {
			// Destination selection: random for first, then go in order
			// todo: maybe mix in a second kind of random like bunny/santa?

			// pick a Destination
			int i;
			if (player.hasQuest(TELE_QUEST_SLOT)) {
				i = Integer.parseInt(player.getQuest(TELE_QUEST_SLOT));
			} else {
				i = new Random().nextInt(DESTINATIONS.size());
			}
			i++;
			if (i >= DESTINATIONS.size()) {
				i = 0;
			}
			player.setQuest(TELE_QUEST_SLOT, "" + i);
			final Destination picked = DESTINATIONS.get(i);

			// Teleport
			final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(picked.zone);
			if (!player.teleport(zone, picked.x, picked.y, null, player)) {
				logger.error("AdminMaker random teleport failed, "
						+ picked.zone + " " + picked.x + " " + picked.y);
				return false;
			}
			return true;
		}


		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {

			// before we send the player off into the unknown give a marked
			// scroll
			final Item markedScroll = sandbox.getItem("marked scroll");
			markedScroll.setInfoString(player.getID().getZoneID() + " "
					+ player.getX() + " " + player.getY());
			markedScroll.setBoundTo(player.getName());

			if (player.equipToInventoryOnly(markedScroll)) {
				// Teleport
				if (randomTeleport(player)) {
					// todo: priv msg doesn't work
					player.sendPrivateText(player.getTitle()
							+ " use the scroll to come back here. Use /teleport <playername> <zonename> <x> <y> to beam to a different place.");
				} else {
					raiser.say("oops, looks like you found a bug.");
				}
			} else {
				raiser.say("Ask me again when you have room for a scroll.");
			}

		}
	}

	@Override
	public void load(final Player admin, final List<String> args, final ScriptingSandbox sandbox) {
		super.load(admin, args, sandbox);

		// Require parameter -Dstendhal.testserver=junk
		if (!ServerModeUtil.isTestServer()) {
			final String msg = "Server must be started with this vm parameter: -Dstendhal.testserver=junk";
			if (admin != null) {
				admin.sendPrivateText(msg);
				logger.warn("AdminMaker - " + msg + " . Executed by "
						+ admin.getName());
			}
			return;
		}



		// create npc
		ScriptingNPC npc;
		npc = new ScriptingNPC("Admin Maker");
		npc.setEntityClass("tavernbarmaidnpc");

		// Place NPC in int_admin_playground on server start
		final String myZone = "0_semos_city";
		sandbox.setZone(myZone);
		int x = 32;
		int y = 16;

		// if this script is executed by an admin, Admin Maker will be placed
		// next to
		// him/her.
		if (admin != null) {
			sandbox.setZone(sandbox.getZone(admin));
			x = admin.getX() + 1;
			y = admin.getY();
		}

		// Set zone and position
		npc.setPosition(x, y);
		sandbox.add(npc);

		// Create Dialog
		npc.behave("greet", "Hi, how can i #help you?");
		npc.behave("help",
				"Perhaps you would like a free power #upgrade and maybe a #random destination?");
		npc.addGoodbye();
		npc.add(ConversationStates.ATTENDING, "upgrade", null,
				ConversationStates.ATTENDING, null, new UpgradeAction());
		npc.add(ConversationStates.ATTENDING, "random", null,
				ConversationStates.ATTENDING, null, new TeleportAction());

	}

}
