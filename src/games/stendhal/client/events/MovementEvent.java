package games.stendhal.client.events;

import games.stendhal.common.Direction;

public interface MovementEvent {
	// When rpentity moves, it will be called with the data.
	public void onMove(int x, int y, Direction direction, double speed);

	// When rpentity reachs the [x,y,1,1] area.
	public void onEnter(int x, int y);

	// When rpentity leaves the [x,y,1,1] area.
	public void onLeave(int x, int y);

	// When rpentity stops
	public void onStop();
}
