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
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

import java.util.Collections;
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

	// TODO: Use Weak reference
	private RPEntity entity;

	/** Container for statuses inflicted on entity */
	private List<Status> statuses;

	/** Immunites to statuses */
	private EnumSet<StatusType> immunities;

	/** Resistances to statuses */
	private EnumSet<StatusType> resistances;

	/**
	 * Food, drinks etc. that the player wants to consume and has not finished
	 * with.
	 */
	List<ConsumableItem> itemsToConsume;

	/**
	 * Poisonous items that the player still has to consume. This also includes
	 * poison that was the result of fighting against a poisonous creature.
	 */
	List<ConsumableItem> poisonToConsume;

	public StatusList(RPEntity entity) {
		this.entity = entity;
		immunities = EnumSet.noneOf(StatusType.class);
		resistances = EnumSet.noneOf(StatusType.class);
		statuses = new LinkedList<Status>();
		itemsToConsume = new LinkedList<ConsumableItem>();
		poisonToConsume = new LinkedList<ConsumableItem>();
	}

	public void logic() {

		// Statuses to be removed
		List<String> statusesToRemove = new LinkedList<String>();
		if (statuses.size() > 0) {
			// Only use the first instance of a status
			List<String> usedStatuses = new LinkedList<String>();
			String currentStatus;
			for (Status status : statuses) {
				currentStatus = status.getName();
				if (!usedStatuses.contains(currentStatus)) {
					status.affect(entity);
					usedStatuses.add(currentStatus);
				}
				if (status.removeConditionMet()) {
					statusesToRemove.add(status.getName());
				}
			}
		}
		for (String statusName : statusesToRemove) {
			removeStatus(statusName);
		}
		
		consume(SingletonRepository.getRuleProcessor().getTurn());
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
	 * Find if the player has a specified status
	 * 
	 * @param statusType the status type to check for
	 * @return Entity has status
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
	 * @param statusName
	 *            Status attack type
	 * @return Entity is immune
	 */
	public boolean isImmune(final String statusName) {
		return immunities.contains(StatusType.valueOf(statusName.toUpperCase()));
	}

	/**
	 * Status effects that cannot be inflicted on entity
	 * 
	 * @param status
	 *            Immunity to check for
	 * @return Entity is immune to status
	 */
	public boolean isResistantToStatus(final Status status) {
		return resistances.contains(status.getStatusType());
	}

	/**
	 * Remove any immunity of specified status effect from entity.
	 * 
	 * @param statusName Status attack type
	 */
	public void removeImmunity(final String statusName) {
		immunities.remove(StatusType.valueOf(statusName.toUpperCase()));
		entity.sendPrivateText("You are not immune to " + statusName + " anymore.");
	}

	/**
	 * Make entity immune to a specified status attack.
	 * 
	 * @param attack
	 *            Status attack type
	 */
	public void setImmune(final StatusAttacker attack) {
		final String statusName = attack.getName();

		// Remove any current instances of the status attribute
		if (entity.has("status_" + statusName)) {
			entity.remove("status_" + statusName);
		}

		// FIXME: should clear any consumable statuses
		attack.clearConsumables(entity);

		// Add to list of immunities
		immunities.add(StatusType.valueOf(statusName.toUpperCase()));
	}

	/**
	 * Removes a single instance of a status from entity
	 * 
	 * @param statusName
	 *            Status to be removed
	 * @return Entity is still affected by another instance
	 */
	public boolean removeStatus(final String statusName) {
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
	 * Removes all instances of a status from the entity
	 * 
	 * @param statusName Status to be cured
	 */
	public void cureStatus(final String statusName) {
		while (removeStatus(statusName)) {
			// Do nothing. Just let removeStatus() remove all instances
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * 
	 * @param item
	 */
	public void addPoisonToConsume(ConsumableItem item) {
        poisonToConsume.add(item);
	}
	
	/**
	 * 
	 */
	public void clearPoisonToConsume() {
	    poisonToConsume.clear();
	}
	
	/**
	 * Checks whether the player is still suffering from the effect of a
	 * poisonous item/creature or not.
	 * @return true if player still has poisons to consume
	 */
	public boolean isPoisoned() {
		return !(poisonToConsume.size() == 0);
	}

	/**
	 * Disburdens the player from the effect of a poisonous item/creature.
	 */
	public void healPoison() {
		poisonToConsume.clear();
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
		if (isImmune("poison")) {
			return false;
		} else {
			/*
			 * Send the client the new poisoning status, but avoid overwriting
			 * the real value in case the player was already poisoned.
			 */
			if (!entity.has("poisoned")) {
				entity.put("poisoned", "0");
				entity.notifyWorldAboutChanges();
			}
			poisonToConsume.add(item);
			if (entity instanceof Player) {
				TutorialNotifier.poisoned((Player) entity);
			}
			return true;
		}
	}

	public boolean isFull() {
		return itemsToConsume.size() > 4;
	}

	public boolean isChoking() {
		return itemsToConsume.size() > 5;
	}

	public boolean isChokingToDeath() {
		return itemsToConsume.size() > 8;
	}

	public void eat(final ConsumableItem item) {
		if (isChoking()) {
			entity.put("choking", 0);
		} else {
			entity.put("eating", 0);
		}
		itemsToConsume.add(item);
	}

	private void consume(final int turn) {
		Collections.sort(itemsToConsume);
		if (itemsToConsume.size() > 0) {
			final ConsumableItem food = itemsToConsume.get(0);
			if (food.consumed()) {
				itemsToConsume.remove(0);
			} else {
				if (turn % food.getFrecuency() == 0) {
					logger.debug("Consumed item: " + food);
					final int amount = food.consume();
					if (isChoking()) {
						entity.put("choking", amount);
					} else {
						if (entity.has("choking")) {
							entity.remove("choking");
						}
						entity.put("eating", amount);
					}
					if (entity.heal(amount, true) == 0) {
						itemsToConsume.clear();
					}
				}
			}
		} else {
			if (entity.has("eating")) {
				entity.remove("eating");
			}
			if (entity.has("choking")) {
				entity.remove("choking");
			}
		}

		if ((poisonToConsume.size() == 0)) {
			if (entity.has("poisoned")) {
				entity.remove("poisoned");
			}
		} else {
			final List<ConsumableItem> poisonstoRemove = new LinkedList<ConsumableItem>();
			int sum = 0;
			int amount = 0;
			for (final ConsumableItem poison : new LinkedList<ConsumableItem>(
					poisonToConsume)) {
				if (turn % poison.getFrecuency() == 0) {
					if (poison.consumed()) {
						poisonstoRemove.add(poison);
					} else {
						amount = poison.consume();
						entity.damage(-amount, poison);
						sum += amount;
						entity.put("poisoned", sum);
					}
				}

			}
			for (final ConsumableItem poison : poisonstoRemove) {
				poisonToConsume.remove(poison);
			}
		}

		entity.notifyWorldAboutChanges();
	}

	public void clearFoodList() {
		itemsToConsume.clear();
	}

	public void clear() {
		itemsToConsume.clear();
		poisonToConsume.clear();
	}

	public void remove(Status status) {
		// TODO Auto-generated method stub
		
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
	 * @return RPEntity
	 */
	RPEntity getEntity() {
		return entity;
	}

}
