/*
 * $Id$
 */
package games.stendhal.server.entity.item.scroll;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Represents a teleport scroll that takes the player to a specified location
 * for a specified time, after which it will teleport the player to given
 * location.
 * <p>
 * infostring attribute in items.xml:<p> 
 * <code> 1_dreamscape 77 35 5400 0_semos_plains_s -1 -1 </code>
 * <p>where <ul><li>1_dreamscape is the target zone name; <li>77 and 35 are the target x
 * and y position; <li>5400 is the number of turns before return; <li>0_semos_plains_s
 * is the return zone; <li>-1 and -1 are the return x and y positions (negative
 * value means a random position)</ul>
 * 
 * TODO: This class isn't fully self-containing as the LoginHandler (that
 * handles the players logging in the target zone) must be implemented
 * elsewhere.
 */
public class TimedTeleportScroll extends TeleportScroll {

	private static final Logger logger = Logger.getLogger(TimedTeleportScroll.class);

	/**
	 * Teleport the player back from the target zone.
	 * 
	 * @param player
	 * @return true if teleport was successful
	 */
	public boolean teleportBack(Player player) {
		String targetZoneName = null;
		String returnZoneName = null;
		int returnX = 0;
		int returnY = 0;
		String infoString = getInfoString();
		if (infoString != null) {
			StringTokenizer st = new StringTokenizer(infoString);
			if (st.countTokens() == 7) {
				targetZoneName = st.nextToken();
				st.nextToken(); /* targetX */
				st.nextToken(); /* targetY */
				st.nextToken(); /* timeInTurns */
				returnZoneName = st.nextToken();
				returnX = Integer.parseInt(st.nextToken());
				returnY = Integer.parseInt(st.nextToken());
			} else {
				throw new IllegalArgumentException(
						"the infostring attribute is malformed");
			}
		}
		int x = returnX;
		int y = returnY;
		if (player == null || player.getZone() == null
				|| targetZoneName == null) {
			return true;
		}

		if (!targetZoneName.equals(player.getZone().getName())) {
			return true; /* player is already away from the target zone */
		}

		StendhalRPZone returnZone = StendhalRPWorld.get().getZone(
				returnZoneName);

		if (x < 0) {
			x = Rand.rand(returnZone.getWidth());
		}
		if (y < 0) {
			y = Rand.rand(returnZone.getHeight());
		}

		boolean result = player.teleport(returnZone, x, y, null, player);

		String afterReturnMessage = getAfterReturnMessage();
		if (afterReturnMessage != null) {
			player.sendPrivateText(afterReturnMessage);
		}

		return result;
	}

	/**
	 * Creates a new timed marked teleport scroll.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public TimedTeleportScroll(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public TimedTeleportScroll(TimedTeleportScroll item) {
		super(item);
	}

	/**
	 * Is invoked when a teleporting scroll is used. Tries to put the player on
	 * the scroll's destination, or near it.
	 * 
	 * @param player
	 *            The player who used the scroll and who will be teleported
	 * @return true iff teleport was successful
	 */
	@Override
	protected boolean useTeleportScroll(Player player) {
		String targetZoneName = null;
		int targetX = 0;
		int targetY = 0;
		int timeInTurns = 0;
		String infoString = getInfoString();
		if (infoString != null) {
			StringTokenizer st = new StringTokenizer(infoString);
			if (st.countTokens() == 7) {
				targetZoneName = st.nextToken();
				targetX = Integer.parseInt(st.nextToken());
				targetY = Integer.parseInt(st.nextToken());
				timeInTurns = Integer.parseInt(st.nextToken());
			} else {
				throw new IllegalArgumentException(
						"the infostring attribute is malformed");
			}
		}

		/* check destination */
		StendhalRPZone targetZone = StendhalRPWorld.get().getZone(
				targetZoneName);

		if (targetZone != null) {
			String beforeReturnMessage = getBeforeReturnMessage();
			if (beforeReturnMessage != null) {
				TurnNotifier.get().notifyInTurns(
						(int) (timeInTurns * 0.9),
						new TimedTeleportWarningTurnListener(player,
								StendhalRPWorld.get().getZone(targetZoneName),
								beforeReturnMessage));
			}
			TurnNotifier.get().notifyInTurns(timeInTurns,
					new TimedTeleportTurnListener(player));

			/*
			 * we use the player as teleporter (last parameter) to give feedback
			 * if something goes wrong
			 */
			return player.teleport(targetZone, targetX, targetY, null, player);
		} else {
			/* invalid zone - only log it */
			logger.warn("Timed teleport scroll to unknown zone: "
					+ targetZoneName);
		}

		return false;
	}

	@Override
	public String describe() {
		String result;
		if (hasDescription()) {
			result = getDescription();
		} else {
			result = "You see " + Grammar.a_noun(getTitle()) + ".";
		}
		return result;
	}

	/**
	 * override this to show a message before teleporting the player back.
	 * 
	 * @return the message to shown or null for no message
	 */
	protected String getBeforeReturnMessage() {
		return null;
	}

	/**
	 * override this to show a message after teleporting the player back.
	 * 
	 * @return the message to shown or null for no message
	 */
	protected String getAfterReturnMessage() {
		return null;
	}

	/**
	 * TimedTeleportTurnListener class is the implementation of the TurnListener
	 * interface for the timed teleport.
	 */
	class TimedTeleportTurnListener implements TurnListener {

		private final Player player;

		TimedTeleportTurnListener(Player player) {
			this.player = player;
		}

		public void onTurnReached(int currentTurn) {
			teleportBack(player);
		}
	}

	/**
	 * TimedTeleportWarningTurnListener class is the implementation of the
	 * TurnListener interface for the timed teleport to send a warning message
	 * to the player before teleporting back.
	 */
	static class TimedTeleportWarningTurnListener implements TurnListener {

		private final Player player;
		private final StendhalRPZone zone;
		private final String warningMessage;

		TimedTeleportWarningTurnListener(Player player, StendhalRPZone zone,
				String warningMessage) {
			this.player = player;
			this.zone = zone;
			this.warningMessage = warningMessage;
		}

		public void onTurnReached(int currentTurn) {
			if (player == null || player.getZone() == null || zone == null) {
				return;
			}
			if (player.getZone().getName().equals(zone.getName())) {
				player.sendPrivateText(warningMessage);
			}
		}
	}
}
