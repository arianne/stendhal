package games.stendhal.server.entity.item;

import games.stendhal.common.constants.Testing;
import games.stendhal.server.entity.RPEntity;

import java.util.List;
import java.util.Map;

import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * An item that can be activated by being held in specific slots.
 * 
 * @author AntumDeluge
 */
public abstract class SlotActivatedItem extends Item {
	
	/* The logger instance */
	private static final Logger logger = Logger.getLogger(SlotActivatedItem.class);
	
	/* List of slots where this item is active when equipped. */
	// XXX: Should java.util.Collection be used instead?
	private List<String> activeSlotsList = null;
	
	/* The active state of the item initialized as deactivated. */
	private boolean activated = false;
	
	/* Name of slot being transition to/from. Useful for checking previous
	 * slot on un-equipping.
	 * 
	 * FIXME: This slot is not necessary. Can call
	 *        this.getContainerSlot().getName() from onUnequipped().
	 */
	//private String transitionSlot;
	
	
/* XXX --- CONSTRUCTORS --- XXX */
	
	/**
	 * Default constructor.
	 * 
	 * @param name
	 * 		Item's name
	 * @param clazz
	 * 		Item's class or type
	 * @param subclass
	 * 		Item's subclass
	 * @param attributes
	 * 		Attributes available to this item
	 */
	public SlotActivatedItem(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param item
	 * 		Item to be copied
	 */
	public SlotActivatedItem(Item item) {
		super(item);
	}
	
	
/* XXX --- ITEM INITIALIZATION --- XXX */
	
	/**
	 * Create a list of slots in which this item can be activated while
	 * equipped.
	 * 
	 * @param list
	 * 		List of slot active slot names
	 */
	public void initializeActiveSlotsList(final List<String> list) {
		this.activeSlotsList = list;
		
		if (logger.isDebugEnabled() || Testing.DEBUG) {
			logger.info("SlotActivatedItem: Initializing active slots list");
			if (this.activeSlotsList == null) {
				logger.error("Could not initialize active slots list");
			}
		}
	}
	
	
/* XXX --- ITEM MANIPULATION --- XXX */
	
	/**
	 * Fill the list of active slots.
	 * 
	 * @param slotList
	 * 		List of slots in which item can be activated
	 */
	public void initiateActiveSlotsList(final List<String> list) {
		activeSlotsList = list;
		}
	
	/**
	 * Action to take when item is equipped. If successfully equipped item's
	 * activation state is set to <b>true</b>.
	 */
	@Override
	public boolean onEquipped(final RPEntity owner, final String slot) {
		if (logger.isDebugEnabled() || Testing.DEBUG) {
			logger.info(this.getName() + " moved to \"" + slot + "\"");
		}
		
		/* Attempt to activate item's attributes if being transitioned to an
		 * active slot from a non-active one.
		 * 
		 * FIXME: Should also check !this.activated.
		 */
		if (this.isActiveSlot(slot)) {
			if (logger.isDebugEnabled() || Testing.DEBUG) {
				logger.info("SlotActivatedItem: Equipping to active slot \""
						+ slot + "\"");
			}
			
			/* Check and activate item's attribute's for containing slot owner.
			 * 
			 * FIXME: Returning wrong value in either this.onActivate() or
			 *        this.onDeactivate().
			 */
			this.activated = this.onActivate();
		}
		
		return super.onEquipped(owner, slot);
	}
	
	/**
	 * Action to take when item is un-equipped. If successfully un-equipped
	 * item's activation state is set to <b>false</b>.
	 */
	@Override
	public boolean onUnequipped() {
		/* The slot from where the item is being removed. */
		final RPSlot slotObject = this.getContainerSlot();
		
		if (slotObject != null) {
			final String slot = slotObject.getName();
			
			if (logger.isDebugEnabled() || Testing.DEBUG) {
				logger.info(this.getName() + " removed from \"" + slot + "\"");
			}
			
			/* Attempt to deactivate item's attributes if being transitioned from
			 * an active slot to a non-active one.
			 * 
			 * FIXME: Should also check this.activated.
			 */
			if (!this.isActiveSlot(slot)) {
				/* Check and deactive item's attribute's for containing slot
				 * owner.
				 * 
				 * FIXME: Returning wrong value in either this.onActivate() or
				 *        this.onDeactivate().
				 */
				this.activated = this.onDeactivate();
			}
		} else {
			/* Item was picked up from ground or other unknown source. */
			if (logger.isDebugEnabled() || Testing.DEBUG) {
				logger.info(this.getName() + " removed from \"null\"");
			}
		}
		
		return super.onUnequipped();
	}
	
	/**
	 * Sets the item's state of activation.
	 * 
	 * @param state
	 * 		<b>true</b>: Item's attributes are currently applied to owning
	 * 		entity.
	 */
	protected void setActivationState(boolean state) {
		this.activated = state;
	}
	
	
/* XXX --- ITEM CHECKS --- XXX */
	
	/**
	 * Tests whether the item is currently activated.
	 * 
	 * @return
	 * 		Item's activation state
	 */
	public boolean isActivated() {
		return this.activated;
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
		/*if (logger.isDebugEnabled() || Testing.DEBUG) {
			String activeSlotListString = "";
			for (String slotName: this.activeSlotsList) {
				activeSlotListString += " " + slotName;
			}
			
			logger.info("Checking " + this.getName()
					+ " for active slot \"" + slot + "\" in \""
					+ activeSlotListString + "\"");
		}*/
		
		if ((activeSlotsList != null)
				&& !activeSlotsList.isEmpty() && slot != null) {
			return activeSlotsList.contains(slot);
		}
		
		return false;
	}
	
	
/* XXX --- ITEM ACTIVATION --- XXX */
	
	/**
	 * Actions to take when activated and owner is known.
	 *
	 * @return
	 * 		Item activation state
	 */
	protected abstract boolean onActivate();
	
	/**
	 * Actions to take when deactivated and owner is known.
	 * 
	 * @return
	 * 		Item activation state
	 */
	protected abstract boolean onDeactivate();
}
