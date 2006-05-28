package games.stendhal.server.entity;

public class OneWayPortal extends Portal {
	public OneWayPortal() {
		put("hidden", "");
	}

	public void setDestination(String zone, int number) {
		throw new IllegalArgumentException(
				"One way portals are only destination of other portals");
	}

	public boolean loaded() {
		return true; // Always loaded
	}

	public boolean isCollisionable() {
		return false;
	}

	public void onUsed(RPEntity user) {
		// Does nothing
	}
}
