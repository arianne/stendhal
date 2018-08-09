/***************************************************************************
 *                   (C) Copyright 2018 - Stendhal                         *
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

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * Changes kill counts for player.
 *
 * @author AntumDeluge
 */
public class AlterKills extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		final int argc = args.size();

		// Show help
		if (argc < 1 || args.get(0).equals("!help")) {
			showUsage(admin);
			return;
		}

		// Admin did not input correct number of arguments
		if (argc < 4) {
			showUsage(admin);
			admin.sendPrivateText(NotificationType.ERROR, "ERROR: Not enough arguments");
			return;
		}

		final String player_name = args.get(0);
		final Player target = SingletonRepository.getRuleProcessor().getPlayer(player_name);
		final String kill_type = args.get(1).toLowerCase();
		final int kill_count;
		// All remaining arguments are the enemy's name
		final String enemy = String.join(" ", args.subList(3, args.size()));

		// Player does not exist
		if (target == null) {
			admin.sendPrivateText(NotificationType.ERROR, "ERROR: No such player: " + player_name);
			return;
		}

		// Admin tries to change kill count to string value
		try {
			kill_count = Integer.parseInt(args.get(2));
		} catch (NumberFormatException e) {
			showUsage(admin);
			admin.sendPrivateText(NotificationType.ERROR, "ERROR: Argument 3 (count) must be an integer value");
			return;
		}

		if (kill_type.equals("solo")) {
			target.setSoloKillCount(enemy, kill_count);
		} else if (kill_type.equals("shared")) {
			target.setSharedKillCount(enemy, kill_count);
		} else {
			// Admin inputs invalid kill type
			showUsage(admin);
			admin.sendPrivateText(NotificationType.ERROR, "ERROR: Argument 2 (type) must be one of \"solo\" or \"shared\"");
			return;
		}

		// Notify player of changes
		target.sendPrivateText(NotificationType.SUPPORT, "你 " + kill_type + " 杀死 " + enemy + " 的数量改写到 "
				+ Integer.toString(kill_count) + " by " + admin.getTitle());
	}

	/**
	 * Shows help text.
	 *
	 * @param admin Administrator invoking script
	 */
	private void showUsage(final Player admin) {
		List<String> usage = Arrays.asList(
				"\n用法:",
				"    /script AlterKills.class <player> <solo|shared> <count> <enemy>",
				"    /script AlterKills.class !help",
				"Args:",
				"    player:\t改为用户名.",
				"    solo:\t计算单人杀怪数量.",
				"    shared:\t计算共同杀怪数量.",
				"    count:\t杀怪数量.",
				"    enemy:\t敌人名字.");
		admin.sendPrivateText(NotificationType.CLIENT, String.join("\n", usage));
	}
}
