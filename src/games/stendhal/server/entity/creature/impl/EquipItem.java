package games.stendhal.server.entity.creature.impl;

public class EquipItem {

	public String slot;

	public String name;

	public int quantity;

	public EquipItem(String slot, String name, int amount) {
		this.slot = slot;
		this.name = name;
		this.quantity = amount;
	}
}
