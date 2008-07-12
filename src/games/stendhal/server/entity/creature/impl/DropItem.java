package games.stendhal.server.entity.creature.impl;

public class DropItem {

	public String name;

	public double probability;

	public int min;

	public int max;

	public DropItem(final String name, final double probability, final int min, final int max) {
		this.name = name;
		this.probability = probability;
		this.min = min;
		this.max = max;
	}

	public DropItem(final String name, final double probability, final int amount) {
		this.name = name;
		this.probability = probability;
		this.min = amount;
		this.max = amount;
	}
}
