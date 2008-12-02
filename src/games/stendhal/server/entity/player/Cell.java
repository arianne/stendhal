package games.stendhal.server.entity.player;

import java.awt.Point;

public class Cell {
	private final Point entry;
	private String inmateName = "";

	public Cell(Point entry) {
		this.entry = entry;
	}

	public boolean remove(String name) {
		if(this.inmateName.equalsIgnoreCase(name)){
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

	public boolean add(String string) {
		if (isEmpty()){
			inmateName = string;
			return true;
		}
		return false;
	}
}
