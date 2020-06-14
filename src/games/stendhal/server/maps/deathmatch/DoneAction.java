/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deathmatch;

import static games.stendhal.server.core.rp.achievement.factory.DeathmatchAchievementFactory.HELPER_SLOT;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.dbcommand.WriteHallOfFamePointsCommand;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.command.DBCommandPriority;
import marauroa.server.db.command.DBCommandQueue;

/**
 * Handles player claim of victory by giving reward after verifying the winning.
 */
public class DoneAction implements ChatAction {

	private static final Logger logger = Logger.getLogger(DoneAction.class);

	private final DeathmatchInfo deathmatchInfo;


	public DoneAction(final DeathmatchInfo deathmatchInfo) {
		this.deathmatchInfo = deathmatchInfo;
	};

	/**
	 * Creates the player bound special trophy helmet and equips it.
	 *
	 * @param player Player object
	 * @return Helmet
	 */
	private Item createTrophyHelmet(final Player player) {
		final Item helmet = SingletonRepository.getEntityManager().getItem("trophy helmet");
		helmet.setBoundTo(player.getName());
		helmet.put("def", 1);
		helmet.setInfoString(player.getName());
		helmet.setPersistent(true);
		helmet.setDescription("This is " + player.getName()
		        + "'s grand prize for Deathmatch winners. Wear it with pride.");
		player.equipOrPutOnGround(helmet);
		return helmet;
	}

	/**
	 * Updates the player's points in the hall of fame for deathmatch.
	 *
	 * @param player Player
	 */
	private void updatePoints(final Player player) {
		final DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));
		DBCommandQueue.get().enqueue(new WriteHallOfFamePointsCommand(player.getName(), "D", deathmatchState.getPoints(), true), DBCommandPriority.LOW);
	}

	/**
	 * Tracks helping players & updates achievements related to helping with deathmatch.
	 *
	 * @param aided
	 * 		The player who is being helped.
	 * @param timestamp
	 * 		Time the deathmatch was completed.
	 */
	private void updateHelpers(final Player aided, final long timestamp) {
		for (final Player helper: deathmatchInfo.getArena().getPlayers()) {
			final String helperName = helper.getName();
			// player must have helped kill at least 3 deathmatch creatures to count towards achievement
			final int aidedKills = deathmatchInfo.getAidedKills(helperName);
			if (aidedKills > 2) {
				int helpCount = 0;
				if (helper.hasQuest(HELPER_SLOT)) {
					try {
						helpCount = Integer.parseInt(helper.getQuest(HELPER_SLOT, 0));
					} catch (final NumberFormatException e) {
						logger.error("Deathmatch helper quest slot value not an integer.");
						e.printStackTrace();
					}
				}

				helpCount++;

				helper.setQuest(HELPER_SLOT, 0, Integer.toString(helpCount));
				helper.setQuest(HELPER_SLOT, 1, Long.toString(timestamp));

				SingletonRepository.getAchievementNotifier().onFinishDeathmatch(helper);
			}
		}
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		final DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));
		if (deathmatchState.getLifecycleState() != DeathmatchLifecycle.VICTORY) {
			raiser.say("C'm on, don't lie to me! All you can do now is #bail or win.");
			return;
		}

		updatePoints(player);

		// We assume that the player only carries one trophy helmet.
		final Item helmet = player.getFirstEquipped("trophy helmet");
		if (helmet == null) {
			createTrophyHelmet(player);
			raiser.say("Here is your special trophy helmet. Keep it, as the defense will increase by 1 "
				+ "for every deathmatch you complete. Now, tell me if you want to #leave.");
		} else {
			int defense = 1;
			if (helmet.has("def")) {
				defense = helmet.getInt("def");
			}
			defense++;
			final int maxdefense = 5 + (player.getLevel() / 5);
			if (defense > maxdefense) {
				helmet.put("def", maxdefense);
				raiser.say("I'm sorry to inform you, the maximum defense for your helmet at your current level is "
				                + maxdefense);
			} else {
				helmet.put("def", defense);
				String message;
				if (defense == maxdefense) {
					message = "Your helmet has been magically strengthened to the maximum defense for your level, " + defense;
				} else {
					message = "Your helmet has been magically strengthened to a defense of " + defense;
				}
				raiser.say(message + ". Now, tell me if you want to #leave.");
			}
		}
		player.updateItemAtkDef();
		TurnNotifier.get().notifyInTurns(0, new NotifyPlayerAboutHallOfFamePoints((SpeakerNPC) raiser.getEntity(), player.getName(), "D", "deathmatch_score"));

		new SetQuestAction("deathmatch", 0, "done").fire(player, sentence, raiser);
		// Track the number of wins.
		new IncrementQuestAction("deathmatch", 6, 1).fire(player, sentence, raiser);
		SingletonRepository.getAchievementNotifier().onFinishDeathmatch(player);

		// track helpers
		updateHelpers(player, System.currentTimeMillis());
	}

}
