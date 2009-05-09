package games.stendhal.server.entity.player;

import java.awt.Point;

class Cell {
	private final Point entry;
	private String inmateName = "";

	protected Cell(final Point entry) {
		this.entry = entry;
	}

	protected boolean remove(final String name) {
		if (this.inmateName.equalsIgnoreCase(name)) {
			this.inmateName = "";
			return true;
		}
		return false;
	}

	public boolean isEmpty() {
		return "".equals(inmateName);
		
	}

	public Point getEntry() {
		return entry;
	}

	protected boolean add(final String string) {
		if (isEmpty()) {
			inmateName = string;
			return true;
		}
		return false;
	}
}
