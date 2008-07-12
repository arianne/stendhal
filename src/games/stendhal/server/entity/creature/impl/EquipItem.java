package games.stendhal.server.entity.creature.impl;

public class EquipItem {

	public String slot;

	public String name;

	public int quantity;

	public EquipItem(final String slot, final String name, final int amount) {
		this.slot = slot;
		this.name = name;
		this.quantity = amount;
	}
}
