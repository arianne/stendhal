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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import games.stendhal.common.Direction;
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
public class Unblock extends ScriptImpl implements TurnListener {
	private static final int CHECK_INTERVAL = 10;
	private static final int GRACE_PERIOD_IN_TURNS = 200;
	private Set<PlayerPositionEntry> playerPositions = new HashSet<PlayerPositionEntry>();
	private Set<KeepFreeArea> keepFreeAreas = new HashSet<KeepFreeArea>();

	/**
	 * records a player position in a specific turn
	 */
	static class PlayerPositionEntry {
		private String playerName;
		private String zoneName;
		private int x;
		private int y;
		private int turn;
		private KeepFreeArea keepFreeArea;

		/**
		 * creates a new PlayerPositionEntry
		 *
		 * @param player Player
		 * @param keepFreeArea area to keep free
		 */
		public PlayerPositionEntry(Player player, KeepFreeArea keepFreeArea) {
			super();
			this.playerName = player.getName();
			this.zoneName = player.getZone().getName();
			this.x = player.getX();
			this.y = player.getY();
			this.turn = SingletonRepository.getRuleProcessor().getTurn();
			this.keepFreeArea = keepFreeArea;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result+ playerName.hashCode();
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + zoneName.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof PlayerPositionEntry)) {
				return false;
			}
			PlayerPositionEntry other = (PlayerPositionEntry) obj;
			if (!playerName.equals(other.playerName)) {
				return false;
			}
			if (x != other.x) {
				return false;
			}
			if (y != other.y) {
				return false;
			}
			if (!zoneName.equals(other.zoneName)) {
				return false;
			}
			return true;
		}

		/**
		 * has the player moved away including leaving the zone or logging out
		 *
		 * @return true if the player moved away, false if he is still in the restricted area
		 */
		public boolean hasPlayerMovedAway() {
			Player player = SingletonRepository.getRuleProcessor().getPlayer(playerName);
			if (player == null) {
				return true;
			}
			if (!player.getZone().getName().equals(zoneName)) {
				return true;
			}
			if (player.getX() != x) {
				return true;
			}
			if (player.getY() != y) {
				return true;
			}
			return false;
		}

		/**
		 * gets the turn this entry was created
		 *
		 * @return turn
		 */
		public int getTurn() {
			return turn;
		}

		/**
		 * gets the KeepFreeArea
		 *
		 * @return KeepFreeArea
		 */
		public KeepFreeArea getKeepFreeArea() {
			return keepFreeArea;
		}

		/**
		 * gets the Player
		 *
		 * @return player or <code>null</code> in case the player logged out
		 */
		public Player getPlayer() {
			return SingletonRepository.getRuleProcessor().getPlayer(playerName);
		}
	}

	/**
	 * an area to keep free with an associated teleportation target spot
	 */
	static class KeepFreeArea {
		private Area area;
		private int x;
		private int y;

		/**
		 * creates a new KeepFreeArea
		 *
		 * @param area area to keep free
		 * @param x teleportation target x
		 * @param y teleportation target y
		 */
		public KeepFreeArea(Area area, int x, int y) {
			super();
			this.area = area;
			this.x = x;
			this.y = y;
		}

		/**
		 * gets a list of players who are within the restricted area
		 *
		 * @return list of players
		 */
		public List<Player> getPlayers() {
			return area.getPlayers();
		}

		/**
		 * gets the teleportation target x
		 *
		 * @return teleportation target x
		 */
		public int getX() {
			return x;
		}

		/**
		 * gets the teleportation target y
		 *
		 * @return teleportation target y
		 */
		public int getY() {
			return y;
		}

		/**
		 * gets the zone
		 *
		 * @return StendhalRPZone
		 */
		public StendhalRPZone getZone() {
			return area.getZone();
		}
	}

	/**
	 * checks
	 */
	@Override
	public void onTurnReached(int currentTurn) {
		TurnNotifier.get().notifyInSeconds(CHECK_INTERVAL, this);
		cleanupList();
		teleportAway(currentTurn);
		record();
	}

	/**
	 * removes players that have moved away from the list
	 */
	private void cleanupList() {
		Iterator<PlayerPositionEntry> itr = playerPositions.iterator();
		while (itr.hasNext()) {
			PlayerPositionEntry entry = itr.next();
			if (entry.hasPlayerMovedAway()) {
				itr.remove();
			}
		}

	}


	/**
	 * teleports players out of the restricted area
	 *
	 * @param turn current turn
	 */
	private void teleportAway(int turn) {
		Iterator<PlayerPositionEntry> itr = playerPositions.iterator();
		while (itr.hasNext()) {
			PlayerPositionEntry entry = itr.next();
			if (entry.getTurn() + GRACE_PERIOD_IN_TURNS < turn) {
				itr.remove();
				KeepFreeArea keepFreeArea = entry.getKeepFreeArea();
				Player player = entry.getPlayer();
				if (player == null) {
					continue;
				}
				player.teleport(keepFreeArea.getZone(), keepFreeArea.getX(), keepFreeArea.getY(), Direction.DOWN, player);
			}
		}
	}

	/**
	 * recors players who are within the restricted zone.
	 */
	private void record() {
		for (KeepFreeArea keepFreeArea : keepFreeAreas) {
			for (Player player : keepFreeArea.getPlayers()) {
				// we do something dirty here with hashCode and equals (turn is ignored)
				PlayerPositionEntry entry = new PlayerPositionEntry(player, keepFreeArea);
				if (!playerPositions.contains(entry)) {
					playerPositions.add(entry);
				}
			}
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

		// bank entrance
		keepFreeAreas.add(new KeepFreeArea(new Area(world.getZone("0_semos_city"), 17, 23, 3, 2), 23, 25));

		// bank exit
		keepFreeAreas.add(new KeepFreeArea(new Area(world.getZone("int_semos_bank"), 8, 28, 11, 29), 24, 27));

		// inn entrance
		keepFreeAreas.add(new KeepFreeArea(new Area(world.getZone("0_semos_city"), 40, 38, 4, 3), 50, 40));

		// inn exit
		keepFreeAreas.add(new KeepFreeArea(new Area(world.getZone("int_semos_tavern_0"), 21, 15, 3, 2), 22, 11));

		TurnNotifier.get().notifyInSeconds(CHECK_INTERVAL, this);
	}

	@Override
	public void unload(Player admin, List<String> args) {
		super.unload(admin, args);
		TurnNotifier.get().dontNotify(this);
	}

}
