package games.stendhal.server.entity.item;

import games.stendhal.server.entity.RPEntity;

import java.util.List;
import java.util.Map;

/**
 * An item that can be activated by being held in specific slots.
 * 
 * @author AntumDeluge
 */
public abstract class SlotActivatedItem extends Item {
	
	/** List of slots where this item is active when equipped. */
	// XXX: Should java.util.Collection be used instead?
	private List<String> activeSlotList = null;
	
	/** The active state of the item */
	private boolean activated = false;
	
	
/* XXX --- CONSTRUCTORS --- XXX */
	
	public SlotActivatedItem(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}
	
	public SlotActivatedItem(Item item) {
		super(item);
	}
	
	
/* XXX --- ITEM MANIPULATION --- XXX */
	
	/**
	 * Fill the list of active slots.
	 * 
	 * @param slotList
	 * 		List of slots in which item can be activated
	 */
	public void initiateActiveSlotList(final List<String> slotList) {
		activeSlotList = slotList;
		}
	
	/**
	 * Activates/deactivates the item's attributes if it has an owner.
	 */
	public abstract void setActivation();
	
	/**
	 * Activates/deactivates the item's attributes for the given owner.
	 * 
	 * @param owner
	 * 		The entity that is carrying the item
	 */
	public abstract void setActivation(RPEntity owner);
	
	/**
	 * Action to take when item is equipped.
	 */
	@Override
	public boolean onEquipped(final RPEntity owner, final String slot) {
		this.setActivation();
		
		return super.onEquipped(owner, slot);
	}
	
	/**
	 * Action to take when item is unequipped.
	 */
	@Override
	public boolean onUnequipped() {
		this.setActivation();
		
		return super.onUnequipped();
	}
	
	
/* XXX --- ITEM CHECKS --- XXX */
	
	/**
	 * Tests whether the item can be activated in the current slot.
	 * 
	 * @return
	 * 		<b>true</b> if current slot name is found in active slot list
	 */
	protected boolean isActiveCurrentSlot() {
		if (this.isContained() && (activeSlotList != null)
				&& !activeSlotList.isEmpty()) {
			return activeSlotList.contains(this.getCurrentSlotName());
		}
		
		return false;
	}
	
	/**
	 * Tests whether the item can be activated in specified slot.
	 * 
	 * @param slot
	 * 		Slot to be tested
	 * @return
	 * 		<b>true</b> if slot name is found in active slot list
	 */
	protected boolean isActiveSlot(String slot) {
		if ((activeSlotList != null)
				&& !activeSlotList.isEmpty()) {
			return activeSlotList.contains(slot);
		}
		
		return false;
	}
	
	/**
	 * Tests whether the item is currently activated.
	 * 
	 * @return
	 * 		Item's activation state
	 */
	public boolean activated() {
		return activated;
	}
	
	
/* XXX --- ITEM ACTIVATION --- XXX */
	
	/**
	 * Actions to take when activated and owner is known.
	 */
	protected abstract void onActivate();
	
	/**
	 * Actions to take for specified owner when item is activated.
	 * 
	 * @param owner
	 * 		Entity carrying the item
	 */
	protected abstract void onActivate(RPEntity owner);
	
	/**
	 * Actions to take when deactivated and owner is known.
	 */
	protected abstract void onDeactivate();
	
	/**
	 * Action to take for specified owner when item is deactivated.
	 * 
	 * @param owner
	 * 		Entity carrying the item
	 */
	protected abstract void onDeactivate(RPEntity owner);
}
