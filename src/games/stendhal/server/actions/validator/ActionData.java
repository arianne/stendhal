/***************************************************************************
 *                   (C) Copyright 2012-2013 Faiumoni                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.validator;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.slot.Slot;

/**
 * data used by actions
 *
 * @author hendrik
 */
public class ActionData {

	private Entity entity = null;
	private Slot slot = null;


	/**
	 * gets entity
	 *
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * gets slot
	 *
	 * @return the slot
	 */
	public Slot getSlot() {
		return slot;
	}

	/**
	 * sets entity
	 *
	 * @param entity the entity to set
	 */
	protected void setEntity(Entity entity) {
		this.entity = entity;
	}

	/**
	 * sets slot
	 *
	 * @param slot the slot to set
	 */
	protected void setSlot(Slot slot) {
		this.slot = slot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((slot == null) ? 0 : slot.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ActionData other = (ActionData) obj;
		if (entity == null) {
			if (other.entity != null) {
				return false;
			}
		} else if (!entity.equals(other.entity)) {
			return false;
		}
		if (slot == null) {
			if (other.slot != null) {
				return false;
			}
		} else if (!slot.equals(other.slot)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ActionData [entity=" + entity + ", slot=" + slot + "]";
	}

}
