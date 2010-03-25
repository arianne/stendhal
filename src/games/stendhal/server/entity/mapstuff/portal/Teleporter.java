package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.engine.Spot;
import games.stendhal.server.entity.player.Player;

public class Teleporter extends Portal {
	
	private Spot spot;

	public Teleporter(final Spot spot) {
		this.spot = spot;
		
	}

	/**
	 * Use the portal.
	 * 
	 * @param player
	 *            the Player who wants to use this portal
	 * @return <code>true</code> if the portal worked, <code>false</code>
	 *         otherwise.
	 */
	@Override
	protected boolean usePortal(final Player player) {
		if (!nextTo(player)) {
			// Too far to use the portal
			return false;
		}

		if (player.teleport(spot.getZone(), spot.getX(), spot.getY(), null, null)) {
			player.stop();
			
		}
		return true;
	}

}
