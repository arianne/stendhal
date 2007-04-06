package games.stendhal.client.events;

import games.stendhal.client.entity.Item;

interface EquipmentEvent {

	// Called when an item is equipped on the entity, you know the slot by
	// calling item.getContainerSlot()
	void onEquipItem(Item item);

	// Called when an item equipped is changed on the entity, you know the slot
	// by calling item.getContainerSlot()
	void onChangeItem(Item item);

	// Called when entity drops the item or the item is removed from entity
	void onDropItem(Item item);
}
