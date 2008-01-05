package games.stendhal.server.maps.athor.ship;

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.util.TimeUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * This class simulates a ferry going back and forth between the mainland and
 * the island. Note that, even though this class lies in a maps package, this is
 * not a zone configurator.
 * 
 * NPCs that have to do with the ferry:
 * <li> Eliza - brings players from the mainland docks to the ferry.
 * <li>Jessica - brings players from the island docks to the ferry.
 * <li>Jackie - brings players from the ferry to the docks. Captain - the ship
 * captain.
 * <li>Laura - the ship galley maid.
 * <li>Ramon - offers blackjack on the ship.
 * 
 * @see games.stendhal.server.maps.athor.ship.CaptainNPC
 * @author daniel
 * 
 */
public final class AthorFerry implements TurnListener {

	private Status current;

	public Status getState() {
		return current;
	}

	/** The Singleton instance. */
	private static AthorFerry instance;

	/**
	 * A list of non-player characters that get notice when the ferry arrives or
	 * departs, so that they can react accordingly, e.g. inform nearby players.
	 */
	private List<IFerryListener> listeners;

	/** How much it costs to board the ferry. */
	public static final int PRICE = 25;

	private AthorFerry() {
		listeners = new LinkedList<IFerryListener>();
		current = Status.ANCHORED_AT_MAINLAND;
	}

	/**
	 * @return The Singleton instance.
	 */
	public static AthorFerry get() {
		if (instance == null) {
			instance = new AthorFerry();

			// initiate the turn notification cycle
			TurnNotifier.get().notifyInSeconds(1, instance);

		}
		return instance;
	}

	/**
	 * Gets a textual description of the ferry's status.
	 *
	 * @return A String representation of time remaining till next state.
	 */

	private String getRemainingSeconds() {
		int secondsUntilNextState = TurnNotifier.get()
				.getRemainingSeconds(this);
		return TimeUtil.approxTimeUntil(secondsUntilNextState);
	}

	/**
	 * Is called when the ferry has either arrived at or departed from a harbor.
	 */
	public void onTurnReached(int currentTurn) {
		// cycle to the next state

		current = current.next();
		for (IFerryListener npc : listeners) {
			npc.onNewFerryState(current);
		}
		TurnNotifier.get().notifyInSeconds(current.duration(), this);
	}

	public void addListener(IFerryListener npc) {
		listeners.add(npc);
	}
	
	/**
	 * Auto registers the listener to Athorferry.
	 * deregistration must be implemented if it is used for short living objects
	 * @author astridemma
	 *
	 */
	public abstract static class FerryListener implements IFerryListener {
		public FerryListener() {
			AthorFerry.get().addListener(this);
		}

		public abstract void onNewFerryState(Status current);
	}

	public interface IFerryListener {
		void onNewFerryState(Status current);
	}

	public enum Status {
		ANCHORED_AT_MAINLAND {
			@Override
			Status next() {
				return DRIVING_TO_ISLAND;
			}

			@Override
			int duration() {
				return 2 * 60;
			}

			@Override
			public String toString() {
				return "The ferry is currently anchored at the mainland. It will take off in "
						+ AthorFerry.get().getRemainingSeconds() + ".";
			}
		},
		DRIVING_TO_ISLAND {
			@Override
			Status next() {
				return ANCHORED_AT_ISLAND;
			}

			@Override
			int duration() {
				return 5 * 60;
			}

			@Override
			public String toString() {
				return "The ferry is currently sailing to the island. It will arrive in "
						+ AthorFerry.get().getRemainingSeconds() + ".";
			}

		},
		ANCHORED_AT_ISLAND {
			@Override
			Status next() {
				return DRIVING_TO_MAINLAND;
			}

			@Override
			int duration() {
				return 2 * 60;
			}

			@Override
			public String toString() {
				return "The ferry is currently anchored at the island. It will take off in "
						+ AthorFerry.get().getRemainingSeconds() + ".";
			}

		},
		DRIVING_TO_MAINLAND {
			@Override
			Status next() {
				return ANCHORED_AT_MAINLAND;
			}

			@Override
			int duration() {
				return 5 * 60;
			}

			@Override
			public String toString() {
				return "The ferry is currently sailing to the mainland. It will arrive in "
						+ AthorFerry.get().getRemainingSeconds() + ".";
			}

		};

		/**
		 * gives the following status.
		 * @return the next Status
		 */
		abstract Status next();

		/**
		 * how long will this state last.
		 * @return time in seconds;
		 */
		abstract int duration();
	}

}
