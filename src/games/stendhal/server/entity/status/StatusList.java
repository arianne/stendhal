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

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * handles a list of status for an entity
 * 
 * @author hendrik
 */
public class StatusList {
	private static Logger logger = Logger.getLogger(StatusList.class);

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
	 * Find the index of the first occurance of the status effect
	 * 
	 * @param statusName
	 *            Status effect to search for
	 * @return List index of status effect
	 */
	public int getFirstStatusIndex(final String statusName) {
		int index;
		for (index = 0; index < statuses.size(); index++) {
			if (statuses.get(index).getName() == statusName) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Count how many occurances of a status are inflicted on the entity
	 * 
	 * @param statusName
	 *            Name of the status being checked
	 * @return Number of times status is found
	 */
	public int statusOccurrenceCount(final String statusName) {
		int count = 0;
		for (Status status : statuses) {
			if (status.getName().equals(statusName)) {
				count += 1;
			}
		}
		return count;
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
	 * Find if the player has a specified status
	 * 
	 * @param statusName
	 *            The status to check for
	 * @return Entity has status
	 */
	public boolean hasStatus(final String statusName) {
		for (Status status : statuses) {
			if (status.getName().equals(statusName)) {
				return true;
			}
		}
		return false;
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
	 */
	public void inflictStatus(final Status status) {
		inflictStatus(status, null);
	}

	/**
	 * Add status effect to entity
	 * 
	 * @param status
	 *            Status to be added
	 * @param attacker
	 *            Entity that is inflicting status
	 */
	public void inflictStatus(final Status status, final RPEntity attacker) {
		RPEntity entity = entityRef.get();
		if (entity == null) {
			return;
		}

		String statusName = status.getName();

		int count = statusOccurrenceCount(status.getName());
		if ((count < status.allowedOccurrences())
				|| (status.allowedOccurrences() < 0)) {
			statuses.add(status);

			if (!entity.has("status_" + statusName)) {
				entity.put("status_" + statusName, 1);
			}

			if (attacker == null) {
				entity.sendPrivateText(NotificationType.SCENE_SETTING, "You have been afflicted with \"shock\"");
			} else {
				entity.sendPrivateText(NotificationType.SCENE_SETTING, "You have been afflicted with \"" + statusName + "\" by " + attacker.getName());
			}
		} else {
			logger.debug("Entity \"" + entity.getName() + "\" cannot add more occurrences of " + statusName + ". Total occurrences: " + count);
			if (hasStatus(statusName)) {
				// Reset counter for first instance of status
				int index = getFirstStatusIndex(statusName);
				statuses.remove(index);
				statuses.add(index, status);
				if (attacker == null) {
					entity.sendPrivateText(NotificationType.SCENE_SETTING, "You have been afflicted with \"shock\"");
				} else {
					entity.sendPrivateText(NotificationType.SCENE_SETTING, "You have been afflicted with \"" + statusName + "\" by " + attacker.getName());
				}
			}
		}
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
	 * @param attacker
	 *            Status attack type
	 */
	public void setImmune(final StatusAttacker attacker) {
		RPEntity entity = entityRef.get();
		if (entity == null) {
			return;
		}

		final String statusName = attacker.getName();

		// Remove any current instances of the status attribute
		if (entity.has("status_" + statusName)) {
			entity.remove("status_" + statusName);
		}

		// FIXME: should clear any consumable statuses
		attacker.clearConsumables(entity);

		// Add to list of immunities
		immunities.add(attacker.getStatusType());
	}

	/**
	 * Removes a single instance of a status from entity
	 * 
	 * @param statusName
	 *            Status to be removed
	 * @return Entity is still affected by another instance
	 */
	public boolean removeStatus(final String statusName) {
		RPEntity entity = entityRef.get();
		if (entity == null) {
			return false;
		}

		if (hasStatus(statusName)) {
			int index = getFirstStatusIndex(statusName);
			statuses.remove(index);

			if (!hasStatus(statusName)) {
				if (entity instanceof Player && !hasStatus(statusName)) {
					((Player) entity).sendPrivateText(NotificationType.SCENE_SETTING, "\"" + statusName + "\" has worn off.");
				}
				if (entity.has("status_" + statusName)) {
					entity.remove("status_" + statusName);
				}
				return false;
			}
		}
		return true;
	}

	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Disburdens the player from the effect of a poisonous item/creature.
	 */
	public void healPoison() {
		removeAll(PoisonStatus.class);
	}

	/**
	 * Poisons the player with a poisonous item. Note that this method is also
	 * used when a player has been poisoned while fighting against a poisonous
	 * creature.
	 *
	 * @param item
	 *            the poisonous item
	 * @return true iff the poisoning was effective, i.e. iff the player is not
	 *         immune
	 */
	public boolean poison(final ConsumableItem item) {
		RPEntity entity = entityRef.get();
		if (entity == null) {
			return false;
		}
		
		if (isImmune(StatusType.POISONED)) {
			return false;
		}

		// Send the client the new status, but avoid overwriting
		// the real value in case the player was already poisoned.
		if (!entity.has("poisoned")) {
			entity.put("poisoned", "0");
			entity.notifyWorldAboutChanges();
		}
		PoisonStatus status = new PoisonStatus(item.getAmount(), item.getFrecuency(), item.getRegen());
		new PoisonStatusHandler().inflict(status, this);
		if (entity instanceof Player) {
			TutorialNotifier.poisoned((Player) entity);
		}
		return true;
	}

	public void eat(final ConsumableItem item) {
		RPEntity entity = entityRef.get();
		if (entity == null) {
			return;
		}

		// Send the client the new status, but avoid overwriting
		// the real value in case the player was already poisoned.
		if (isChoking()) {
			if (!entity.has("choking")) {
				entity.put("choking", 0);
			}
		} else {
			if (!entity.has("eating")) {
				entity.put("eating", 0);
			}
		}
		entity.notifyWorldAboutChanges();

		EatStatus status = new EatStatus(item.getAmount(), item.getFrecuency(), item.getRegen());
		new EatStatusHandler().inflict(status, this);

		if (item.getName().equals("beer") || item.getName().equals("wine")) {
			DrunkStatus drunkStatus = new DrunkStatus();
			new DrunkStatusHandler().inflict(drunkStatus, this);
		}
	}

	public boolean isFull() {
		return countStatusByType(StatusType.EATING) > 4;
	}

	public boolean isChoking() {
		return countStatusByType(StatusType.EATING) > 5;
	}

	public boolean isChokingToDeath() {
		return countStatusByType(StatusType.EATING) > 8;
	}


	public void clear() {
		// TODO: notify handler
		statuses.clear();
	}

	public void remove(Status status) {
		// TODO: notify handler
		removeInternal(status);
	}

	/**
	 * interally adds a status to the list of statuses
	 *
	 * @param status status to add
	 */
	void addInternal(Status status) {
		statuses.add(status);
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
