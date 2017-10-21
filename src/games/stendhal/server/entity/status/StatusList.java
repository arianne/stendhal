/***************************************************************************
 *                   (C) Copyright 2013 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.status;

import static games.stendhal.common.constants.Actions.AUTOWALK;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;

/**
 * handles a list of status for an entity
 *
 * @author hendrik
 */
public class StatusList {
	private WeakReference<RPEntity> entityRef;

	/** Container for statuses inflicted on entity */
	private List<Status> statuses;

	/** Immunites to statuses */
	private EnumSet<StatusType> immunities;

	/**
	 * StatusList for an entity
	 *
	 * @param entity RPEntity which has the statuses managed by this list.
	 */
	public StatusList(RPEntity entity) {
		this.entityRef = new WeakReference<RPEntity>(entity);
		immunities = EnumSet.noneOf(StatusType.class);
		statuses = new LinkedList<Status>();
	}

	/**
	 * Get statuses that are currently inflicted on the entity
	 *
	 * @return List of statuses
	 */
	public List<Status> getStatuses() {
		return statuses;
	}

	/**
	 * Count how many occurances of a status are inflicted on the entity
	 *
	 * @param statusType type of status being checked
	 * @return number of times status is found
	 */
	public int countStatusByType(StatusType statusType) {
		int count = 0;
		for (Status status : statuses) {
			if (status.getStatusType() == statusType) {
				count += 1;
			}
		}
		return count;
	}

	/**
	 * gets the first status of the specified status subclass
	 *
	 * @param statusClass status subclass
	 * @return Status or <code>null</code>
	 */
	<T extends Status> T getFirstStatusByClass(Class<T> statusClass) {
		for (Status status : statuses) {
			if (status.getClass().equals(statusClass)) {
				return statusClass.cast(status);
			}
		}
		return null;
	}

	/**
	 * gets all statuses of the specified status subclass
	 *
	 * @param statusClass status subclass
	 * @return Status or <code>null</code>
	 */
	<T extends Status> List<T> getAllStatusByClass(Class<T> statusClass) {
		List<T> res = new LinkedList<T>();
		for (Status status : statuses) {
			if (status.getClass().equals(statusClass)) {
				res.add(statusClass.cast(status));
			}
		}
		return res;
	}

	/**
	 * removes all statuses of this class
	 *
	 * @param statusClass status class
	 */
	public <T extends Status> void removeAll(Class<T> statusClass) {
		List<T> interestingStatuses = getAllStatusByClass(statusClass);
		for (Status status : interestingStatuses) {
			remove(status);
		}
	}


	/**
	 * removes all statuses of this type
	 *
	 * @param statusType status type
	 */
	public void removeAll(StatusType statusType) {
		for (Status status : new LinkedList<Status>(statuses)) {
			if (status.getStatusType() == statusType) {
				remove(status);
			}
		}
	}

	/**
	 * removes all statuses (e. g. on death)
	 */
	public void removeAll() {
		for (Status status : new LinkedList<Status>(statuses)) {
			remove(status);
		}
	}

	/**
	 * Find if the entity has a specified status
	 *
	 * @param statusType the status type to check for
	 * @return true, if the entity has status; false otherwise
	 */
	public boolean hasStatus(StatusType statusType) {
		for (Status status : statuses) {
			if (status.getStatusType() == statusType) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add status effect to entity
	 *
	 * @param status
	 *            Status to be added
	 * @param attacker
	 *            Entity that is inflicting status
	 * @return true, if the was inflected; false if the RPEntity is immune.
	 */
	public boolean inflictStatus(final Status status, final Entity attacker) {
		if (isImmune(status.getStatusType())) {
			return false;
		}
		status.getStatusType().getStatusHandler().inflict(status, this, attacker);
		return true;
	}

	/**
	 * Check if entity is immune to specified status attack.
	 *
	 * @param statusType type of status
	 * @return Entity is immune
	 */
	public boolean isImmune(final StatusType statusType) {
		return immunities.contains(statusType);
	}

	/**
	 * Remove any immunity of specified status effect from entity.
	 *
	 * @param statusType type of status
	 */
	public void removeImmunity(StatusType statusType) {
		RPEntity entity = entityRef.get();
		if (entity == null) {
			return;
		}
		immunities.remove(statusType);
		entity.sendPrivateText("You are not immune to being " + statusType.getName() + " anymore.");
	}

	/**
	 * Make entity immune to a specified status attack.
	 *
	 * @param statusType Status type
	 */
	public void setImmune(final StatusType statusType) {
		immunities.add(statusType);
		removeAll(statusType);
	}

	/**
	 * activates a status attribute for the client without overriding a potential existing value
	 *
	 * @param attributeName name of attribute
	 */
	void activateStatusAttribute(String attributeName) {
		RPEntity entity = entityRef.get();
		if (entity == null) {
			return;
		}

		// do not override an existing value (e. g. the amount of hp lost by another poison instance)
		if (!entity.has(attributeName)) {
			entity.put(attributeName, 0);
			entity.notifyWorldAboutChanges();
		}
	}

	/**
	 * removes a status
	 *
	 * @param status Status to remove
	 */
	public void remove(Status status) {
		status.getStatusType().getStatusHandler().remove(status, this);
	}

	/**
	 * interally adds a status to the list of statuses
	 *
	 * @param status status to add
	 */
	void addInternal(Status status) {
		statuses.add(status);
		final StatusType stype = status.getStatusType();
		if ((stype == StatusType.POISONED)
				|| (stype == StatusType.CONFUSED)
				|| (stype == StatusType.SHOCKED)) {
			final RPEntity entity = this.getEntity();
			if (entity.has(AUTOWALK)) {
				entity.remove(AUTOWALK);
			}
		}
	}

	/**
	 * internally removes a status from the list of statuses
	 *
	 * @param status status to remove
	 */
	void removeInternal(Status status) {
		statuses.remove(status);
	}

	/**
	 * gets the entity for this StatusList
	 *
	 * @return RPEntity or <code>null</code>
	 */
	RPEntity getEntity() {
		return entityRef.get();
	}

}
