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

import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

/**
 * Moves players away that spend to much time in an restricted area
 *
 * @author hendrik
 */
public class UnblockTradeTable extends ScriptImpl implements TurnListener {
	private static final int CHECK_INTERVAL = 10;
	private static final int GRACE_PERIOD_IN_TURNS = 200;

	private static Logger logger = Logger.getLogger(UnblockTradeTable.class);

	private StendhalRPZone zone;
	private Area pathArea;
	private Area tablePathArea;
	private Player player;
	private int firstTurn = -1;

	/**
	 * checks
	 */
	@Override
	public void onTurnReached(int currentTurn) {
		TurnNotifier.get().notifyInSeconds(CHECK_INTERVAL, this);
		if (!isBlockSituation()) {
			cleanup();
			return;
		}

		record();
		if (shouldActionBeTaken()) {
			teleportAway();
			cleanup();
		}
	}

	private boolean isBlockSituation() {
		List<Player> playersOnPath = pathArea.getPlayers();
		List<Player> playersOnTablePath = tablePathArea.getPlayers();

		// is someone on a blocking spot?
		if (playersOnPath.isEmpty()) {
			return false;
		}

		// are there two players around the table?
		if (playersOnTablePath.size() == 2) {
			return false;
		}

		// is there no other player near the table?
		Player bad = getBadPlayer();
		playersOnTablePath.remove(bad);
		if (playersOnTablePath.size() == 0) {
			return true;
		}
		return false;
	}

	private void cleanup() {
		player = null;
		firstTurn = -1;
	}

	private void record() {
		if (firstTurn < 0) {
			firstTurn = TurnNotifier.get().getCurrentTurnForDebugging();
			player = getBadPlayer();
		}
	}

	private Player getBadPlayer() {
		List<Player> playersOnPath = pathArea.getPlayers();
		if (playersOnPath.isEmpty()) {
			return null;
		}
		Player res = playersOnPath.get(0);
		for (Player p : playersOnPath) {
			if (p.getX() > res.getX()) {
				res = p;
			}
		}
		return res;
	}

	private boolean shouldActionBeTaken() {
		int currentTurn = TurnNotifier.get().getCurrentTurnForDebugging();
		return ((firstTurn > -1) && (firstTurn + GRACE_PERIOD_IN_TURNS < currentTurn));
	}

	private void teleportAway() {
		if (player != null) {
			// at the top left corner of the table, one tile to the right
			// So that the player cannot just run down, but close to the left
			// because player tend to put items on the ground.
			logger.info("Teleported " + player.getName()
					+ " away from trading table coordinates " + player.getX() + "," + player.getY());
			player.teleport(zone, 36, 2, Direction.DOWN, player);
			new GameEvent("trade table", "teleport", player.getName(), zone.getName(), "36", "2").raise();
		}
	}

	/**
	 * executes the script
	 */
	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		TurnNotifier.get().dontNotify(this);
		StendhalRPWorld world = SingletonRepository.getRPWorld();
		zone = world.getZone("int_semos_bank");
		pathArea = new Area(zone, 32, 7, 35, 7);
		tablePathArea = new Area(zone, 35, 2, 40, 8);
		TurnNotifier.get().notifyInSeconds(CHECK_INTERVAL, this);
	}

	@Override
	public void unload(Player admin, List<String> args) {
		super.unload(admin, args);
		TurnNotifier.get().dontNotify(this);
	}

	// Spot in question: 32,7 to 35,7, right most player
	// no player in 35, 2 to 40, 8 (with the exception of 35,7
	// upper table 39,4 to 41,4
	// lower table 39,6 to 41,6
	// teleport target 36, 2
}
