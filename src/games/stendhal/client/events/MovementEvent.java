package games.stendhal.client.events;

import games.stendhal.common.Direction;

public interface MovementEvent {

	/**
	 * When rpentity moves, it will be called with the data.
	 * @param x
	 * @param y 
	 * @param direction
	 * @param speed 
	 */
	void onMove(int x, int y, Direction direction, double speed);

	/**
	 * When rpentity reachs the [x,y,1,1] area.
	 * @param x
	 * @param y
	 */
	void onEnter(int x, int y);

	/**
	 * When rpentity leaves the [x,y,1,1] area.
	 * @param x
	 * @param y
	 */
	void onLeave(int x, int y);

	/**
	 * Entity has been stopped at Point(x,y)
	 * @param x
	 * @param y
	 */
	void onStop(int x, int y);
}
