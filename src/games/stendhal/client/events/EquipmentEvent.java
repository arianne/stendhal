package games.stendhal.client.events;

import games.stendhal.client.entity.*;

interface EquipmentEvent 
  {
  // Called when an item is equipped on the entity, you know the slot by calling item.getContainerSlot()   
  public void onEquipItem(Item item);
  // Called when an item equipped is changed on the entity, you know the slot by calling item.getContainerSlot()   
  public void onChangeItem(Item item);
  // Called when entity drops the item or the item is removed from entity 
  public void onDropItem(Item item);
  }
