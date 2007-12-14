/*
 * @(#) src/games/stendhal/server/entity/item/scroll/InvitationScroll.java
 *
 * $Id$
 */
package games.stendhal.server.entity.item.scroll;

//
//

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * Represents an teleport scroll that acts as an invitation to an event. The
 * programmatic event name is in the <code>infostring</code> attribute. As
 * they are by invitation, it ignores destination zone anti-teleport rules.
 */
public class InvitationScroll extends TeleportScroll {

	/**
	 * Creates a new invitation teleport scroll.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public InvitationScroll(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor
	 * 
	 * @param item
	 *            item to copy
	 */
	public InvitationScroll(InvitationScroll item) {
		super(item);
	}

	/**
	 * Try to teleport to a marked scroll infostring style place.
	 * 
	 * @param where
	 *            A location in the form of <em>zone x y</em>.
	 * @param player
	 *            The player to teleport.
	 * 
	 * @return <code>true</code> if teleport was successful.
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

		zone = StendhalRPWorld.get().getZone(st.nextToken());

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
	 * @param player
	 *            The player who used the scroll and who will be teleported.
	 * 
	 * @return <code>true</code> if teleport was successful.
	 */
	@Override
	protected boolean useTeleportScroll(Player player) {
		String dest = getInfoString();

		if (dest == null) {
			player.sendPrivateText("This invitation has not been filled in.");
			return false;
		}

		return teleportTo(dest, player);
	}

	/**
	 * Get a description of the event and it's time.
	 * 
	 * @return A description.
	 */
	@Override
	public String describe() {
		/*
		 * Base description (set by creator?)
		 */
		if (hasDescription()) {
			return getDescription();
		} else {
			return "An invitation to an event.";
		}
	}
}
