package games.stendhal.server.core.config.zone;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

public class TeleportationRules {
	/** Areas where teleporting out is blocked */
	private List<Rectangle> leavingBarriers = new LinkedList<Rectangle>();
	/** Areas where teleporting in is blocked */
	private List<Rectangle> arrivingBarriers = new LinkedList<Rectangle>();

	/**
	 * Block teleporting to a rectangular area.
	 * 
	 * @param x x coordinate of the blocked area 
	 * @param y y coordinate of the blocked area
	 * @param width width of the blocked area
	 * @param height height of the blocked area
	 */
	public void disallowIn(int x, int y, int width, int height) {
		Rectangle r = new Rectangle(x, y, width, height);
		arrivingBarriers.add(r);
	}
	
	/**
	 * Block teleporting in.
	 */
	public void disallowIn() {
		// Make a rectangle large enough to cover the zone, even if we some
		// day start allowing changeable sizes
		Rectangle r = new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
		arrivingBarriers.add(r);
	}

	/**
	 * Check if teleporting to a location is allowed.
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return <code>true</code> if teleporting to the point is allowed, <code>false</code> otherwise  
	 */
	public boolean isInAllowed(int x, int y) {
		for (Rectangle r : arrivingBarriers) {
			if (r.contains(x, y)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Block teleporting from a rectangular area.
	 * 
	 * @param x x coordinate of the blocked area 
	 * @param y y coordinate of the blocked area
	 * @param width width of the blocked area
	 * @param height height of the blocked area
	 */
	public void disallowOut(int x, int y, int width, int height) {
		Rectangle r = new Rectangle(x, y, width, height);
		leavingBarriers.add(r);
	}
	
	/**
	 * Block teleporting out.
	 */
	public void disallowOut() {
		// Make a rectangle large enough to cover the zone, even if we some
		// day start allowing changeable sizes
		Rectangle r = new Rectangle(0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
		leavingBarriers.add(r);
	}

	/**
	 * Check if teleporting from a location is allowed.
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return <code>true</code> if teleporting to the point is allowed, <code>false</code> otherwise  
	 */
	public boolean isOutAllowed(int x, int y) {
		for (Rectangle r : leavingBarriers) {
			if (r.contains(x, y)) {
				return false;
			}
		}
		
		return true;
	}
}
