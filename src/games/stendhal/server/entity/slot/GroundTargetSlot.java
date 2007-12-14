package games.stendhal.server.entity.slot;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;

/**
 * A pseudo slot representing the ground as target for an item (so that the
 * ground has not to be treated special in equipment code).
 * 
 * @author hendrik
 */
public class GroundTargetSlot implements Slot {
	private StendhalRPZone zone;
	private int x;
	private int y;

	/**
	 * generates a new GroundSlot
	 * 
	 * @param zone
	 *            StendhalRPZone
	 * @param x
	 *            x
	 * @param y
	 *            y
	 */
	public GroundTargetSlot(StendhalRPZone zone, int x, int y) {
		super();
		this.zone = zone;
		this.x = x;
		this.y = y;
	}

	public boolean isItemSlot() {
		return true;
	}

	public boolean isReachableForTakingThingsOutOfBy(Entity entity) {

		// this is only a target slot
		return false;
	}

	public boolean isReachableForThrowingThingsIntoBy(Entity entity) {
		return entity.getZone().equals(zone) && entity.nextTo(x, y, 5.0);
	}

}
