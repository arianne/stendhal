package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.entity.RPEntity;

/**
 * A OneWayPortalDestination is an invisible point where players are placed when
 * they use a portal that leads there. One cannot interact with
 * OneWayPortalDestinations in any other way.
 */
public class OneWayPortalDestination extends Portal {

	/**
	 * Creates a OneWayPortalDestination.
	 */
	public OneWayPortalDestination() {
		put("hidden", "");
		setResistance(0);
	}

	/**
	 * Cannot be used, as one way portal destinations are only destinations of
	 * other portals.
	 */
	@Override
	public void setDestination(final String zone, final Object number) {
		throw new IllegalArgumentException(
				"One way portal destinations are only destinations of other portals");
	}

	@Override
	public boolean loaded() {
		return true; // Always loaded
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		return false;
	}
}
