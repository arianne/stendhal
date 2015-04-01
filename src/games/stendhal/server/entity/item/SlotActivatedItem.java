package games.stendhal.server.entity.item;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.status.StatusType;

import java.util.List;
import java.util.Map;

/**
 * An item that can be activated by being held in specific slots.
 * 
 * @author AntumDeluge
 */
public abstract class SlotActivatedItem extends Item {
	
	/** List of slots where this item is active when equpped. */
	private List<String> activeSlotList; // Should java.util.Collection be used instead?
	
	/** Entity that is carrying the item. */
	@SuppressWarnings("unused")
	private RPEntity owner;
	
	/** Slot where the item is currently held. */
	private String currentSlot;

	public SlotActivatedItem(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public SlotActivatedItem(Item item) {
		super(item);
	}
	
	@Override
	public boolean onEquipped(final RPEntity owner, final String slot) {
		this.owner = owner;
		this.currentSlot = slot;
		return super.onEquipped(owner, slot);
	}
	
	@Override
	public boolean onUnequipped() {
		this.owner = null;
		this.currentSlot = null;
		return super.onUnequipped();
	}
	
	/**
	 * Activates the item's attributes for the given owner.
	 * 
	 * @param owner
	 * 		The entity that is carrying the item
	 */
	public abstract void activate(RPEntity owner);

	/**
	 * Sets current slot and activates the item for the owner if the slot
	 * name is found in active slot list.
	 * 
	 * @param owner
	 * 		The entity that is carrying the item
	 * @param slot
	 * 		Slot name where item can be activated
	 */
	public void activate(RPEntity owner, String slot) {
		if (this.canActivateInSlot(slot)) {
			this.activate(owner);
		}
	}
	
	/**
	 * Tests whether the item can be activated in specified slot.
	 * 
	 * @param slot
	 * 		Slot to be tested
	 * @return
	 * 		Item can activate
	 */
	protected boolean canActivateInSlot(String slot) {
		if ((activeSlotList != null)
				&& !activeSlotList.isEmpty()) {
			return activeSlotList.contains(slot);
		}
		
		return false;
	}
	
	/**
	 * Action to take when item is activated.
	 * 
	 * @param owner
	 * 		Entity carrying the item
	 */
	protected abstract void onActivate(RPEntity owner);
	
	/**
	 * Action to take when item is deactivated.
	 * 
	 * @param owner
	 * 		Entity carrying the item
	 */
	protected abstract void onDeactivate(RPEntity owner);
	
	/**
	 * Gets the name of the slot where the item is currently held.
	 * 
	 * @return
	 * 		Slot name containing item
	 */
	public String getCurrentSlot() {
		return this.currentSlot;
	}
	
	/**
	 * Clear slot name in case item is placed on ground.
	 */
	public void onRemovedFromSlot() {
		this.currentSlot = null;
	}
	
	/**
	 * Dummy method
	 * 
	 * See: StatusResistantItem.setStatusResistancesActiveSlot(String)
	 */
	public void addStatusResistancesActiveSlot(String slotName) {
		// Do nothing
	}
	
	/**
	 * Dummy method
	 * 
	 * See: StatusResistantItem.setStatusResistanceList(resistanceList)
	 */
	public void setStatusResistanceList(Map<StatusType, Double> resistanceList) {
		// Do nothing
	}
}
