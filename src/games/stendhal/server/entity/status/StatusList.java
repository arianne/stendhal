package games.stendhal.server.entity.status;

import games.stendhal.common.NotificationType;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

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

	private RPEntity entity;

	/** Immunites to statuses */
	private List<String> immunities;

	/** Container for statuses inflicted on entity */
	private List<Status> statuses;

	/** Resistances to statuses */
	private List<String> resistances;

	public StatusList(RPEntity entity) {
		this.entity = entity;
		immunities = new LinkedList<String>();
		statuses = new LinkedList<Status>();
		resistances = new LinkedList<String>();
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
	 * Find if the player has a specified status
	 * 
	 * @param status
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
	 * @param attack
	 *            Status attack type
	 * @return Entity is immune
	 */
	public boolean isImmune(final String statusName) {
		if (immunities.contains(statusName)) {
			return true;
		}
		return false;
	}

	/**
	 * Status effects that cannot be inflicted on entity
	 * 
	 * @param status
	 *            Immunity to check for
	 * @return Entity is immune to status
	 */
	public boolean isResistantToStatus(final Status status) {
		if (resistances.contains(status.getName())) {
			return true;
		}
		return false;
	}

	/**
	 * Remove any immunity of specified status effect from entity.
	 * 
	 * @param attack
	 *            Status attack type
	 */
	public void removeImmunity(final String statusName) {
		if (immunities.contains(statusName)) {
			immunities.remove(statusName);
		}
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
		if (!immunities.contains(statusName)) {
			immunities.add(statusName);
		}
	}

	/**
	 * Removes a single instance of a status from entity
	 * 
	 * @param status
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
	 * @param status Status to be cured
	 */
	public void cureStatus(final String statusName) {
		while (removeStatus(statusName)) {
			// Do nothing. Just let removeStatus() remove all instances
		}
	}
}
