package games.stendhal.server.entity.item;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Testing;
import games.stendhal.server.entity.RPEntity;

/**
 * An item that can be activated by being held in specific slots.
 *
 * @author AntumDeluge
 */
public abstract class SlotActivatedItem extends Item {

	/** The logger instance */
	private static final Logger logger = Logger.getLogger(SlotActivatedItem.class);

	/** List of slots where this item is active when equipped. */
	private List<String> activeSlotsList = null;

	/** The active state of the item initialized as deactivated. */
	protected boolean activated = false;

	/** Slot to track previous equipped location. */
	private String transitionSlot;


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
	@Override
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
	public void initiateActiveSlotsList(final List<String> slotList) {
		activeSlotsList = slotList;
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

		/* Begin tracking transition slot when the item is equipped. */
		this.transitionSlot = slot;

		/* Attempt to activate item's attributes if being transitioned to an
		 * active slot from a non-active one.
		 *
		 * FIXME: Should also check !this.activated.
		 */
		if (this.isActiveSlot(slot)) {
			/* Check and activate item's attribute's for containing slot owner.
			 * this.onActivate() should return <b>true</b>.
			 */
			this.activated = this.onActivate();

			/* Check if the item has been activated. */
			if (!this.activated) {
				logger.error("Did not activate when equipped to slot \""
						+ slot + "\"");
			}
		}

		return super.onEquipped(owner, slot);
	}

	/**
	 * Action to take when item is un-equipped. If successfully un-equipped
	 * item's activation state is set to <b>false</b>.
	 */
	@Override
	public boolean onUnequipped() {
		/* Use the transition slot to determine previous equipped location. */
		if (this.transitionSlot != null) {
//			if (logger.isDebugEnabled() || Testing.DEBUG) {
//				logger.info(this.getName() + " removed from \"" +
//						this.transitionSlot + "\"");
//			}

			/* Attempt to deactivate item's attributes if being transitioned from
			 * an active slot to a non-active one.
			 *
			 * FIXME: Should also check this.activated.
			 */
			if (this.isActiveSlot(this.transitionSlot)) {
				/* Check and deactivate item's attribute's for containing slot
				 * owner. this.onDeactivate() should return <b>false</b>.
				 */
				this.activated = this.onDeactivate();

				/* We need to clear transitionSlot in case the item is placed
				 * on the ground.
				 */
				this.transitionSlot = null;

				/* Check if the item is still activated. */
				if (this.activated) {
					logger.error("Did not deactivate when removed from slot \""
							+ transitionSlot + "\"");
				}
			}
		} else {
			/* Item was picked up from ground or other unknown source. */
			if (logger.isDebugEnabled() || Testing.DEBUG) {
				logger.info(this.getName() + " removed from \"null\"");
			}
		}

		return super.onUnequipped();
	}


/* XXX --- ITEM CHECKS --- XXX */

	/**
	 * Checks the activation state of the item for activation.
	 *
	 * @return
	 * 		<b>true</b> if item is not currently activated
	 */
	protected boolean canActivate() {
		if (this.activated) {
			return false;
		}
		return true;
	}

	/**
	 * Checks the activation state of the item for deactivation.
	 *
	 * @return
	 * 		<b>true</b> if item is currently activated
	 */
	protected boolean canDeactivate() {
		if (!this.activated) {
			return false;
		}
		return true;
	}

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
		if ((activeSlotsList != null)
				&& !activeSlotsList.isEmpty() && slot != null) {
			return activeSlotsList.contains(slot);
		}

		return false;
	}


/* XXX --- ITEM ACTIVATION --- XXX */

	/**
	 * Actions to take when equipped. Should return <b>true</b> on successful
	 * activation.
	 *
	 * @return
	 * 		Activated state
	 */
	protected boolean onActivate() {
		return canActivate();
	}

	/**
	 * Actions to take when Unequipped. Should return <b>false</b> on
	 * successful deactivation.
	 *
	 * @return
	 * 		Non-activated state
	 */
	protected boolean onDeactivate() {
		return !canDeactivate();
	}

	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append(super.toString());
		if ((activeSlotsList != null) && !activeSlotsList.isEmpty()) {
			st.append("\n\n-- Slot Activated Item --\nItem is activated in slots:\n\t" + activeSlotsList.get(0));
			final int slot_count = activeSlotsList.size();
			if (slot_count > 1) {
				for (final String slot_name : activeSlotsList.subList(1, slot_count)) {
					st.append(", " + slot_name);
				}
			}
		}

		return st.toString();
	}
}
