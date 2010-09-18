package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;
import marauroa.common.game.RPSlot;

/**
 * Stendhal specific information about this slot.
 * 
 * @author hendrik
 */
public class EntitySlot extends RPSlot implements Slot {
	private String errorMessage;

	/**
	 * Creates an uninitialized EntitySlot.
	 * 
	 */
	public EntitySlot() {
		super();
	}

	/**
	 * Creates a new EntitySlot.
	 * 
	 * @param name
	 *            name of slot
	 */
	public EntitySlot(final String name) {
		super(name);
	}

	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		setErrorMessage("The " + getName() + " of " + ((Entity) getOwner()).getDescriptionName(true) + " is too far away.");
		return false;
	}

	public boolean isReachableForThrowingThingsIntoBy(final Entity entity) {
		return isReachableForTakingThingsOutOfBy(entity);
	}

	public boolean isItemSlot() {
		return true;
	}

	public RPSlot getWriteableSlot() {
		return this;
	}

	public boolean isTargetBoundCheckRequired() {
		return false;
	}

	/**
	 * sets the error message
	 *
	 * @param errorMessage error message to set
	 */
	protected void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * gets the last error message
	 *
	 * @return error message or <code>null</code>
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * clears the last error message
	 */
	public void clearErrorMessage() {
		errorMessage = null;
	}

	/**
	 * gets the type of the slot ("slot", "ground", "market")
	 *
	 * @return slot type
	 */
	public String getSlotType() {
		return "slot";
	}
}
