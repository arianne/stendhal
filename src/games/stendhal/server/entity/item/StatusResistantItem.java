package games.stendhal.server.entity.item;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Testing;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.status.StatusResistancesList;
import games.stendhal.server.entity.status.StatusType;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SlotOwner;

/**
 * An item that is resistant to status attacks when equipped.
 *
 * @author AntumDeluge
 */
public class StatusResistantItem extends SlotActivatedItem {

	/** The logger instance */
	final Logger logger = Logger.getLogger(StatusResistantItem.class);

	/** List of status types that this item is resistant to. */
	private StatusResistancesList resistances;


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
	public StatusResistantItem(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);

		/* Initialize resistances. */
		this.resistances = new StatusResistancesList();
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 * 		Item to copy
	 */
	public StatusResistantItem(final StatusResistantItem item) {
		super(item);
		initializeStatusResistancesList(item.resistances.getMap());
	}


	/**
	 * Create or reset resistances to status types for this item.
	 *
	 * @param list
	 * 		Status types and resistant values
	 */
	@Override
	public void initializeStatusResistancesList(final Map<StatusType, Double> list) {
		if (this.resistances == null) {
			this.resistances = new StatusResistancesList();
		}

		this.resistances.setStatusResistances(list);

		/* Slot that the item is initialized/created in. */
		final RPSlot slotObject = this.getContainerSlot();
		final String slot;
		if (slotObject != null) {
			slot = slotObject.getName();

			if (logger.isDebugEnabled() || Testing.DEBUG) {
				logger.info(this.getName() + " initialized in \"" + slot + "\"");
			}
		}

		if (logger.isDebugEnabled() || Testing.DEBUG){
			logger.info("StatusResistantItem: Initializing status resistances list");
			if (this.resistances == null) {
				logger.error("Could not initialize status resistances list");
			}
		}
	}

	/**
	 * Create or reset resistances to status types for this item.
	 *
	 * @param list
	 * 		Status types and resistant values
	 */
	public void initializeStatusResistancesList(final StatusResistancesList list) {
		this.resistances = list;

		if (logger.isDebugEnabled() || Testing.DEBUG) {
			logger.info("StatusResistantItem: Initializing status resistances");
			if (this.resistances == null) {
				logger.error("Could not initialize status resistances list");
			}
		}
	}


	/**
	 * Applies or removes a status resistance value for the owning entity.
	 *
	 * @param statusType
	 * 		The resisted status effect
	 * @param apply
	 * 		Applies resistance for <b>true</b>, removes for false
	 * @return
	 * 		Resistance value was successfully adjusted
	 */
	private boolean adjustOwnerStatusResistance(final StatusType statusType,
			final boolean apply) {

		SlotOwner slotOwner = this.getContainerBaseOwner();

		/* XXX: Is there any usefulness in casting to RPEntity? Would it be
		 *      better to simply only allow resistances for Player?
		 */
		if (slotOwner instanceof RPEntity) {
			RPEntity owner = (RPEntity)slotOwner;

			final String statusName = statusType.getName().toLowerCase();
			final String resistAttribute = "resist_"
					+ statusName;
			final double currentResistance;
			double newResistance = this.getStatusResistanceValue(statusType);

			if (!apply) {
				/* Invert the new resistance value for removal instead of
				 * application.
				 */
				newResistance *= -1;
			}

			/* Apply current resistance value if applicable. */
			if (owner.has(resistAttribute)) {
				currentResistance = owner.getDouble(resistAttribute);

				/* If for some reason the owner already has the resistance
				 * attribute is 0 or less when trying to remove it.
				 */
				if (!apply && currentResistance <= 0.0) {
					/* Remove the residual attribute. */
					owner.remove(resistAttribute);
					return false;
				}

				newResistance += currentResistance;
			}

			/* Safeguarding. Entity cannot be more than 100% resistant. Do not
			 * need to worry about less than zero because resistance will be
			 * removed in such case.
			 *
			 * FIXME: Removing items will subtract from effective resistance
			 *        even when potential resistance is greater than 1.0.
			 *        Should implement separate variables for effective and
			 *        potential resistance. Will probably need to be calculated
			 *        from owning entity.
			 */
			if (newResistance > 1.0) {
				newResistance = 1.0;
			}

			/* Remove reference if entity is no longer resistant. This can be
			 * changed to allow a less than 0 value for items that cause a
			 * weakness to status effects.
			 */
			if (newResistance <= 0.0) {
				owner.remove(resistAttribute);

				if (logger.isDebugEnabled() || Testing.DEBUG) {
					logger.info(owner.getName() + " new "
							+ statusName + " resistance: 0.0");
				}

				return true;
			} else {
				owner.put(resistAttribute, newResistance);

				if (logger.isDebugEnabled() || Testing.DEBUG) {
					logger.info(owner.getName() + " new "
							+ statusName + " resistance: "
							+ Double.toString(newResistance));
				}

				return true;
			}
		}

		return false;
	}


	/**
	 * Actions to take when activated. Super class sets activationState by
	 * calling this method via onEquipped().
	 *
	 * @return
	 * 		Item activation state
	 */
	@Override
	protected boolean onActivate() {
		boolean active = this.isActivated();

		if (active) {
			/* If the item is already activated do not change the state or
			 * attributes. Active item state = true.
			 */
			return active;
		}

		StatusType statusType;
		if ((resistances != null) && !resistances.isEmpty()) {
			for (Entry<StatusType, Double> entry : resistances.getMap().entrySet()) {
				statusType = entry.getKey();

				/* Attempt to adjust the entity's resistance. Should return
				 * "true" to show that attribute has been applied to owning
				 * entity.
				 */
				active = this.adjustOwnerStatusResistance(statusType, true);

				if (!active) {
					/* FIXME: Should revert any previous adjustments and
					 * return "active" state.
					 */

					logger.warn("Failed application of status resistance \""
							+ statusType.getName() + "\"");
				}
			}
		} else {
			logger.warn("Status resistance list is empty");
		}

		return active;
	}

	/**
	 * Actions to take when deactivated. Super class sets activationState by
	 * calling this method via onUnequipped().
	 *
	 * @return
	 * 		Deactivated
	 */
	@Override
	protected boolean onDeactivate() {
		boolean active = this.isActivated();

		if (!active) {
			/* If the item is already inactive do not change the state or
			 * attributes. Inactive item state = false.
			 */
			return active;
		}

		StatusType statusType;
		if ((resistances != null) && !resistances.isEmpty()) {
			for (Entry<StatusType, Double> entry : resistances.getMap().entrySet()) {
				statusType = entry.getKey();

				/* Attempt to adjust the entity's resistance. Should get
				 * "false" value confirming that attribute has been removed
				 * from owning entity.
				 */
				active = !adjustOwnerStatusResistance(statusType, false);

				if (active) {
					/* FIXME: Should revert any previous adjustments and
					 * return "active" state.
					 */

					logger.warn("Failed removal of status resistance \""
							+ statusType.getName() + "\"");
				}
			}
		} else {
			logger.warn("Status resistance list is empty");
		}

		return active;
	}

	/**
	 * Add resistance values to description.
	 */
	@Override
	public String describe() {
		String description = super.describe();
		StringBuilder res = new StringBuilder();

		/* Add statuses resistance stats to description. */
		if (this.resistances == null) {
			return description;
		}
		Map<StatusType, Double> resistances = this.resistances.getMap();
		if ((resistances != null) && !resistances.isEmpty()) {
			for (Entry<StatusType, Double> entry : resistances.entrySet()) {
				String statusType = entry.getKey().toString().toLowerCase();

				/* Special treatment for status names ending in "ed" where
				 * only "d" should be removed.
				 */
				if (statusType.equals("confused")) {
					statusType = "confuse";
				}

				/* Remove "ed" suffix from status name. */
				final int nameLength = statusType.length();
				if (statusType.substring(nameLength - 2).equals("ed")) {
					statusType = statusType.substring(0, nameLength - 2);
				}
				statusType = statusType.substring(0, 1).toUpperCase() +
						statusType.substring(1);
				res.append(" ");
				res.append(statusType);
				res.append(" resist: ");
				res.append(Math.round(100 * entry.getValue()));
				res.append("%");
			}
		}

		if (res.length() > 0) {
			description = description + " Resistances (" + res.toString().trim() + ").";
		}

		return description;
	}

	/**
	 * Get the item's ability to resist a status attack.
	 *
	 * @param type
	 * 		The type of status to be resisted
	 * @return
	 * 		The resistance value
	 */
	public double getStatusResistanceValue(StatusType type) {
		return resistances.getStatusResistance(type);
	}

	/**
	 * Gets all status types and resistance values for this item.
	 *
	 * @return
	 * 		List containing types and resistance values
	 */
	public StatusResistancesList getStatusResistancesList() {
		return this.resistances;
	}


	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append(super.toString() + "\n");
		if (!resistances.isEmpty()) {
			st.append("Status resistances:\n\t" + resistances.toString().toLowerCase());
		}

		return st.toString();
	}
}
