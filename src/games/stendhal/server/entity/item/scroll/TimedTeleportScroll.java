/*
 * $Id$
 */
package games.stendhal.server.entity.item.scroll;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.LoginListener;
import games.stendhal.server.events.LoginNotifier;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.util.Map;
import java.util.StringTokenizer;

import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 * Represents a teleport scroll that takes the player to a specified location for a specified time, after which
 * it will teleport the player to given location.
 */
public class TimedTeleportScroll extends TeleportScroll {

	private static final Logger logger = Log4J.getLogger(TimedTeleportScroll.class);

	/* Note: the values here are for example only, they are overridden with the ones from items.xml infostring.
	 * This class must be extended to call the initHandler method, see RainbowBeansScroll for an example.
	 */

	protected static String targetZoneName = "1_dreamscape";
	protected static int targetX = -1;
	protected static int targetY = -1;

	protected static int timeInTurns = 5400;

	protected static String returnZoneName = "0_semos_plains_s";
	protected static int returnX = -1;
	protected static int returnY = -1;

	/**
	 * initialize teleport parameters and the login notifier to teleport away players logging into the target zone.
	 */
	protected void initHandler() {
		String infoString = getInfoString();
		if (infoString != null) {
			StringTokenizer st = new StringTokenizer(infoString);
			if (st.countTokens() == 7) {
				targetZoneName = st.nextToken();
				targetX = Integer.parseInt(st.nextToken());
				targetY = Integer.parseInt(st.nextToken());
				timeInTurns = Integer.parseInt(st.nextToken());
				returnZoneName = st.nextToken();
				returnX = Integer.parseInt(st.nextToken());
				returnY = Integer.parseInt(st.nextToken());
			} else {
				throw new IllegalArgumentException("the infostring attribute is malformed");
			}
		}
		/* login notifier to teleport away players logging into the dream world */
		LoginNotifier.get().addListener(new LoginListener() {

			public void onLoggedIn(Player player) {
				StendhalRPZone zone = StendhalRPWorld.get().getZone(returnZoneName);
				teleportBack(player, targetZoneName, zone, returnX, returnY);
			}

		});
	}

	/**
	 * Teleport the player back from the dream world.
	 * 
	 * @param player
	 * @param returnZone
	 * @param x
	 * @param y
	 * @return true if teleport was successful
	 */
	public boolean teleportBack(Player player, String targetZoneName, StendhalRPZone returnZone, int x, int y) {
		if (player == null || player.getZone() == null || targetZoneName == null) {
			return true;
		}

		if (!targetZoneName.equals(player.getZone().getName())) {
			return true; /* player is already away from the target zone */
		}

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
	 * Creates a new timed marked teleport scroll
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public TimedTeleportScroll(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor
	 *
	 * @param item item to copy
	 */
	public TimedTeleportScroll(TimedTeleportScroll item) {
		super(item);
	}

	/**
	 * Is invoked when a teleporting scroll is used. Tries to put the
	 * player on the scroll's destination, or near it. 
	 * @param player The player who used the scroll and who will be teleported
	 * @return true iff teleport was successful
	 */
	@Override
	protected boolean useTeleportScroll(Player player) {
		/* check destination */
		StendhalRPZone targetZone = StendhalRPWorld.get().getZone(targetZoneName);

		if (targetZone != null) {
			String beforeReturnMessage = getBeforeReturnMessage();
			if (beforeReturnMessage != null) {
				TurnNotifier.get().notifyInTurns((int)(timeInTurns * 0.9),
						new TimedTeleportWarningTurnListener(player, StendhalRPWorld.get().getZone(targetZoneName),
								beforeReturnMessage));
			}
			TurnNotifier.get().notifyInTurns(timeInTurns,
					new TimedTeleportTurnListener(player, targetZoneName, StendhalRPWorld.get().getZone(returnZoneName),
							returnX,
							returnY));

			/* we use the player as teleporter (last parameter) to give feedback if something goes wrong */
			return player.teleport(targetZone, targetX, targetY, null, player);
		} else {
			/* invalid zone - only log it */
			logger.warn("Timed marked scroll to unknown zone " + targetZoneName + " teleported " + player.getName()
			        + " to Semos instead");
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
	 * @return the message to shown or null for no message
	 */
	protected String getBeforeReturnMessage() {
		return null;
	}

	/**
	 * override this to show a message after teleporting the player back.
	 * @return the message to shown or null for no message
	 */
	protected String getAfterReturnMessage() {
		return null;
	}

	/**
	 * TimedTeleportTurnListener class is the implementation of the TurnListener interface for the timed teleport.
	 */
	class TimedTeleportTurnListener implements TurnListener {

		private final Player player;
		private final String targetZoneName;
		private final StendhalRPZone zone;
		private final int x;
		private final int y;

		TimedTeleportTurnListener(Player player, String targetZoneName, StendhalRPZone returnZone, int x, int y) {
			this.player = player;
			this.targetZoneName = targetZoneName;
			this.zone = returnZone;
			this.x = x;
			this.y = y;
		}

		public void onTurnReached(int currentTurn, String message) {
			teleportBack(player, targetZoneName, zone, x, y);
		}
	}

	/**
	 * TimedTeleportWarningTurnListener class is the implementation of the TurnListener interface for the timed teleport
	 * to send a warning message to the player before teleporting back.
	 */
	class TimedTeleportWarningTurnListener implements TurnListener {

		private final Player player;
		private final StendhalRPZone zone;
		private final String warningMessage;

		TimedTeleportWarningTurnListener(Player player, StendhalRPZone zone, String warningMessage) {
			this.player = player;
			this.zone = zone;
			this.warningMessage = warningMessage;
		}

		public void onTurnReached(int currentTurn, String message) {
			if (player == null || player.getZone() == null || zone == null) {
				return;
			}
			if (player.getZone().getName().equals(zone.getName())) {
				player.sendPrivateText(warningMessage);
			}
		}
	}
}
