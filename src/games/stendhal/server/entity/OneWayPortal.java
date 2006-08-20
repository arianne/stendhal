package games.stendhal.server.entity;

public class OneWayPortal extends Portal {
	public OneWayPortal() {
		put("hidden", "");
	}

	@Override
	public void setDestination(String zone, int number) {
		throw new IllegalArgumentException(
				"One way portals are only destination of other portals");
	}

	@Override
	public boolean loaded() {
		return true; // Always loaded
	}

	@Override
	public boolean isObstacle() {
		return false;
	}

	@Override
	public void onUsed(RPEntity user) {
		// Does nothing
	}
}
