/*
 * @(#) src/games/stendhal/server/entity/item/scroll/InvitationScroll.java
 *
 * $Id$
 */
package games.stendhal.server.entity.item.scroll;

//
//

import java.util.Map;
import java.util.StringTokenizer;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

/**
 * Represents an teleport scroll that acts as an invitation to an event.
 * The programmatic event name is in the <code>infostring</code> attribute.
 * As they are by invitation, it ignores destination zone anti-teleport rules.
 */
public class InvitationScroll extends TeleportScroll {

	//	private static final Logger logger =
	//				Logger.getLogger(InvitationScroll.class);

	/**
	 * Creates a new invitation teleport scroll.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public InvitationScroll(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Try to teleport to a marked scroll infostring style place.
	 *
	 * @param	where		A location in the form of
	 *				<em>zone x y</em>.
	 * @param	player		The player to teleport.
	 *
	 * @return	<code>true</code> if teleport was successful.
	 */
	protected boolean teleportTo(String where, Player player) {
		StringTokenizer st;
		StendhalRPZone zone;
		int x;
		int y;

		st = new StringTokenizer(where, " ");

		if (!st.hasMoreTokens()) {
			return false;
		}

		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(st.nextToken());

		if (!st.hasMoreTokens()) {
			return false;
		}

		try {
			x = Integer.parseInt(st.nextToken());
		} catch (NumberFormatException ex) {
			return false;
		}

		if (!st.hasMoreTokens()) {
			return false;
		}

		try {
			y = Integer.parseInt(st.nextToken());
		} catch (NumberFormatException ex) {
			return false;
		}

		return player.teleport(zone, x, y, null, player);
	}

	/**
	 * Is invoked when a teleporting scroll is actually used.
	 *
	 * @param	player		The player who used the scroll and who
	 *				will be teleported.
	 *
	 * @return	<code>true</code> if teleport was successful.
	 */
	@Override
	protected boolean useTeleportScroll(Player player) {
		if (!has("infostring")) {
			player.sendPrivateText("This invitation has not been filled in.");

			return false;
		}

		//		String dest = StendhalRPZone.get().getGatheringLocation(
		//			get("infostring"));
		String dest = null;

		// TODO: implement me

		if (dest == null) {
			player.sendPrivateText("You do not recognize the event.");

			return false;
		}

		return teleportTo(dest, player);
	}

	/**
	 * Get a description of the event and it's time.
	 *
	 * @return	A description.
	 */
	@Override
	public String describe() {
		StringBuffer sbuf;
		int secondsTill;

		/*
		 * Base description (set by creator?)
		 */
		if (hasDescription()) {
			sbuf = new StringBuffer(getDescription());
		} else {
			sbuf = new StringBuffer("An invitation to an event.");
		}

		/*
		 * When is it?
		 */
		if (has("infostring")) {
			//			secondsTill = StendhalRPZone.get().getGatheringTime(
			//				get("infostring"));
			secondsTill = Integer.MIN_VALUE;

			if (secondsTill != Integer.MIN_VALUE) {
				sbuf.append(" This event ");

				if (secondsTill < 0) {
					sbuf.append("happened ");
					TimeUtil.approxTimeUntil(-secondsTill);
					sbuf.append(" ago.");
				} else if (secondsTill > 0) {
					sbuf.append("starts in ");
					TimeUtil.approxTimeUntil(secondsTill);
					sbuf.append('.');
				} else {
					sbuf.append("is happening now.");
				}
			}
		}

		return sbuf.toString();
	}
}
