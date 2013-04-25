/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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
	private String contentSlotName;

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
	 * @param name name of slot
	 * @param contentSlotName name of slot used by the "item may be put in" check
	 */
	public EntitySlot(final String name, final String contentSlotName) {
		super(name);
		this.contentSlotName = contentSlotName;
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		setErrorMessage("The " + getName() + " of " + ((Entity) getOwner()).getDescriptionName(true) + " is too far away.");
		return false;
	}

	@Override
	public boolean isReachableForThrowingThingsIntoBy(final Entity entity) {
		return isReachableForTakingThingsOutOfBy(entity);
	}

	@Override
	public boolean isItemSlot() {
		return true;
	}

	@Override
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
	 * gets the slot name for the item check
	 *
	 * @return slot name
	 */
	public String getContentSlotName() {
		return contentSlotName;
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
