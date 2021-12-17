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
package games.stendhal.server.entity.player;

import java.util.Map;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.dbcommand.DeletePendingAchievementDetailsCommand;
import games.stendhal.server.core.engine.dbcommand.ReadPendingAchievementDetailsCommand;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnListenerDecorator;
import games.stendhal.server.core.events.TurnNotifier;
import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

/**
 * Retrieves pending or partial achievement information from the database on login
 * Updates the player object if necessary
 * Deletes pending achievements from database so that they are not reapplied next login
 *
 * @author kymara
 */
public class UpdatePendingAchievementsOnLogin implements LoginListener, TurnListener {

	private ResultHandle handle = new ResultHandle();

	@Override
	public void onLoggedIn(Player player) {
		DBCommand command = new ReadPendingAchievementDetailsCommand(player);
		DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
		TurnNotifier.get().notifyInTurns(1, new TurnListenerDecorator(this));
	}

	@Override
	public void onTurnReached(int currentTurn) {
		ReadPendingAchievementDetailsCommand command = DBCommandQueue.get().getOneResult(ReadPendingAchievementDetailsCommand.class, handle);

		if (command == null) {
			TurnNotifier.get().notifyInTurns(0, new TurnListenerDecorator(this));
			return;
		}
		Player player = command.getPlayer();

		updateElfPrincessAchievement(player, command.getDetails("quest.special.elf_princess.0025"));
		updateKillBlordroughsAchievement(player, command.getDetails("quest.special.kill_blordroughs.5"));
		updateItemLoots(player, command.getDetails("item.set.black"));
		updateItemLoots(player, command.getDetails("item.set.chaos"));
		updateItemLoots(player, command.getDetails("item.set.shadow"));
		updateItemLoots(player, command.getDetails("item.set.golden"));
		updateItemLoots(player, command.getDetails("item.set.red"));
		updateItemLoots(player, command.getDetails("item.set.mainio"));
		updateItemHarvest(player, command.getDetails("obtain.apple"));

		// Could also check for reached achievements here. This is also checked on login but the order may vary due to the async access?

		// delete the entries. We don't need feedback
		DBCommand deletecommand = new DeletePendingAchievementDetailsCommand(player);
		DBCommandQueue.get().enqueue(deletecommand);

	}

	private static void updateElfPrincessAchievement(final Player player, final Map<String, Integer> details) {

		// nothing to update
		if (details == null) {
			return;
		}

		final String QUEST_SLOT = "elf_princess";

		// if player didn't start this quest yet, do nothing (shouldn't be details in this case but check anyway)
		if(!player.hasQuest(QUEST_SLOT)) {
			return;
		}

		// param (key) should be "" for this one, all we need to know is the count
		int missingcount = details.get("");

		if (missingcount > 0) {
			final String[] parts = player.getQuest(QUEST_SLOT).split(";");

			// is quest slot already time stamped?
			if(parts.length>2) {
				int newcount = MathHelper.parseInt(parts[2])+missingcount;
				player.setQuest(QUEST_SLOT, 2, "" + newcount);
				return;
			} else if (parts.length==2) {
				// the count was not stored, so we just store the count we had in the table
				player.setQuest(QUEST_SLOT, 2, "" + missingcount);
			} else {
				// we didn't store a time before. so we just choose the value '1' for a long ago system time
				player.setQuest(QUEST_SLOT, 1, "1");
				// the count was also not stored, so we just store the count we had in the table
				player.setQuest(QUEST_SLOT, 2, "" + missingcount);
			}
		}
	}

	private static void updateKillBlordroughsAchievement(final Player player, final Map<String, Integer> details) {

		// nothing to update
		if (details == null) {
			return;
		}

		String QUEST_SLOT = "kill_blordroughs";

		// if player didn't start this quest yet, do nothing (shouldn't be details in this case but check anyway)
		if (!player.hasQuest(QUEST_SLOT)) {
			return;
		}

		// param (key) should be "" for this one, all we need to know is the count
		int missingCount = details.get("");

		if (missingCount > 0) {
			String slot = player.getQuest(QUEST_SLOT);
			if (slot.indexOf(";completed=") < 0) {
				player.setQuest(QUEST_SLOT, slot + ";completed=" + missingCount);
			} else {
				String slotValue = slot.substring(slot.lastIndexOf('=') + 1);
				if (MathHelper.parseIntDefault(slotValue, 0) < missingCount) {
					player.setQuest(QUEST_SLOT, slot.substring(0, slot.lastIndexOf('=') + 1) + missingCount);
				}
			}
		}
	}

	private static void updateItemLoots(final Player player, final Map<String, Integer> details) {

		// nothing to update
		if (details == null) {
			return;
		}

		// update player loots which have been stored as param (key) = itemname, count (value) = number of loots
		for (Map.Entry<String, Integer> detail : details.entrySet()) {
			player.incLootForItem(detail.getKey(), detail.getValue());
		}
	}


	private static void updateItemHarvest(final Player player, final Map<String, Integer> details) {

		// nothing to update
		if (details == null) {
			return;
		}

		// update player loots which have been stored as param (key) = itemname, count (value) = number of loots
		for (Map.Entry<String, Integer> detail : details.entrySet()) {
			player.incHarvestedForItem(detail.getKey(), detail.getValue());
		}
	}

}
