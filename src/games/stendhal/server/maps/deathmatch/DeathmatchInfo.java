package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

/**
 * Stores information about the place of the deathmatch.
 *
 * @author hendrik
 */
public class DeathmatchInfo {

	private final DeathmatchArea arena;

	private final Spot entranceSpot;

	private final StendhalRPZone zone;

	/**
	 * Creates a new DeathmatchInfo.
	 *
	 * @param arena
	 *            combat area
	 * @param zone
	 *            zone
	 */
	public DeathmatchInfo(final Area arena, final StendhalRPZone zone,
			final Spot entrance) {
		super();
		this.arena = new DeathmatchArea(arena);
		this.zone = zone;
		this.entranceSpot = entrance;
	}

	/**
	 * Gets the arena.
	 *
	 * @return combat area
	 */
	public Area getArena() {
		return arena.getArea();
	}

	/**
	 * Gets the zone.
	 *
	 * @return zone
	 */
	public StendhalRPZone getZone() {
		return zone;
	}

	public boolean isInArena(Player player) {
		return arena.contains(player);
	}

	Spot getEntranceSpot() {
		return entranceSpot;
	}

	void startSession(Player player) {
		DeathmatchState deathmatchState = DeathmatchState.createStartState(player.getLevel());
		player.setQuest("deathmatch", deathmatchState.toQuestString());
		DeathmatchEngine dmEngine = new DeathmatchEngine(player, this);
		TurnNotifier.get().notifyInTurns(0, dmEngine);
	}
}
