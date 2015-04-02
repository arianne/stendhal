package games.stendhal.server.entity.item;

import games.stendhal.common.constants.Testing;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.status.StatusResistanceList;
import games.stendhal.server.entity.status.StatusType;

import java.util.Map;
import java.util.Map.Entry;

import marauroa.common.game.RPSlot;
import marauroa.common.game.SlotOwner;

import org.apache.log4j.Logger;

/**
 * An item that is resistant to status attacks when equipped.
 * 
 * @author AntumDeluge
 */
public class StatusResistantItem extends SlotActivatedItem {
	
	/** The logger instance */
	final Logger logger = Logger.getLogger(StatusResistantItem.class);
	
	/** List of status types that this item is resistant to. */
	private StatusResistanceList resistances;
	
	
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
	public StatusResistantItem(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		
		/* FIXME: Resistance should be adjusted for equipping entity if item
		 * is constructed in active slot.
		 *
		 * FIXME: If item is destroyed while in active slot resistance is not
		 * adjusted.
		 */
		
		/* Initialize resistances. */
		this.resistances = new StatusResistanceList(this);
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param item
	 * 		Item to copy
	 */
	public StatusResistantItem(final StatusResistantItem item) {
		super(item);
	}
	
	
/* XXX --- ITEM INITIALIZATION --- XXX */
	
	/**
	 * Create or reset resistances to status types for this item.
	 * 
	 * @param resistanceList
	 * 		Status types and resistant values
	 */
	@Override
	public void initializeStatusResistancesList(final Map<StatusType, Double> list) {
		/* FIXME: Here is where constructed item should checked if equipped
		 * in active slot.
		 */
		
		if (this.resistances == null) {
			this.resistances = new StatusResistanceList(this);
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
	 * @param resistanceList
	 * 		Status types and resistant values
	 */
	public void initializeStatusResistancesList(final StatusResistanceList list) {
		this.resistances = list;
		
		if (logger.isDebugEnabled() || Testing.DEBUG) {
			logger.info("StatusResistantItem: Initializing status resistances");
			if (this.resistances == null) {
				logger.error("Could not initialize status resistances list");
			}
		}
	}
	
	
/* XXX --- ITEM MANIPULATION --- XXX */
	
	/**
	 * Adjust an entities resistance to a specific status type.
	 * 
	 * @param owner
	 * 		Entity to receive resistance adjustment
	 * @param statusType
	 * 		Resisted status type
	 * @param newResistance
	 * 		The resistance value that the owner will have
	 * @return
	 * 		The value was successfully adjusted
	 */
	private boolean adjustStatusResistance(final String statusName,
			double newResistance) {
		SlotOwner slotOwner = this.getContainerBaseOwner();
		
		/* XXX: Is there any usefulness in casting to RPEntity? Would it be
		 *      better to simply only allow resistances for Player?
		 */
		if (slotOwner instanceof RPEntity) {
			RPEntity owner = (RPEntity)slotOwner;
			/*
			if (logger.isDebugEnabled() || Testing.DEBUG) {
				logger.info(owner.getName() + " " + statusName
						+ " resistance adjustment: "
						+ Double.toString(newResistance));
			}*/
			
			final String resistAttribute = "resist_"
				+ statusName.toLowerCase();
			final double currentResistance;
			
			/* Apply current resistance value if applicable. */
			if (owner.has(resistAttribute)) {
				currentResistance = owner.getDouble(resistAttribute);
				newResistance += currentResistance;
			}
			
			/* Safeguarding. Entity cannot be more than 100% resistant. Do not
			 * need to worry about less than zero because resistance will be
			 * removed in such case.
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
				return !owner.has(resistAttribute);
			} else {
				owner.put(resistAttribute, newResistance);
				return owner.getDouble(resistAttribute) == newResistance;
			}
		}
		
		/* Attributes cannot be applied to non-RPEntity */
		return false;
	}
	
	
/* XXX --- ITEM ACTIVATION --- XXX */
	
	/**
	 * Actions to take when activated. Super class sets activationState by
	 * calling this method via onEquipped().
	 * 
	 * @return
	 * 		Item activation state
	 */
	@Override
	protected boolean onActivate() {
		boolean isActive = false;
		
		StatusType statusType;
		double value;
		if ((resistances != null) && !resistances.isEmpty()) {
			for (Entry<StatusType, Double> entry : resistances.getMap().entrySet()) {
				statusType = entry.getKey();
				value = entry.getValue();
				
				/* Attempt to adjust the entity's resistance. */
				isActive = this.adjustStatusResistance(statusType.toString(),
						value);
				
				if (!isActive) {
					/* FIXME: Should revert any previous adjustments and
					 * break loop.
					 */
				}
			}
		} else {
			logger.warn("Status resistance list is empty");
		}
		/*
		if (logger.isDebugEnabled() || Testing.DEBUG) {
			SlotOwner owner = this.getContainerBaseOwner();
			String statusName;
			String statusResistancesString = "StatusResistantItem activated:";
			for (Entry<StatusType, Double> entry:
				this.resistances.getMap().entrySet()) {
				statusName = entry.getKey().toString().toLowerCase();
				statusResistancesString += " " + statusName + "("
						+ Double.toString(owner.getDouble("resist_" +
								statusName)) + ")";
			}
			logger.info(statusResistancesString);
		}*/
		
		return isActive;
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
		boolean isInactive = false;
		
		StatusType statusType;
		double value;
		if ((resistances != null) && !resistances.isEmpty()) {
			for (Entry<StatusType, Double> entry : resistances.getMap().entrySet()) {
				statusType = entry.getKey();
				/* The value is inverted for deactivation. */
				value = entry.getValue() * -1;
				
				/* Attempt to adjust the entity's resistance. */
				isInactive = adjustStatusResistance(statusType.toString(),
						value);
				
				if (!isInactive) {
					/* FIXME: Should revert any previous adjustments and
					 * break loop.
					 */
				}
			}
		} else {
			logger.warn("Status resistance list is empty");
		}
		/*
		if (logger.isDebugEnabled() || Testing.DEBUG) {
			SlotOwner owner = this.getContainerBaseOwner();
			String statusName;
			String statusResistancesString = "StatusResistantItem deactivated:";
			for (Entry<StatusType, Double> entry:
				this.resistances.getMap().entrySet()) {
				statusName = entry.getKey().toString().toLowerCase();
				if (owner.has(statusName)) {
					statusResistancesString += " " + statusName + "("
							+ Double.toString(owner.getDouble("resist_" +
									statusName)) + ")";
				}
			}
			logger.info(statusResistancesString);
		}*/
		
		/* FIXME: Should go through checks to make sture attributes have
		 *        been removed correctly.
		 */
		//return isInactive;
		return true;
	}
	
/* XXX --- ITEM INFORMATION --- XXX */
	
	/**
	 * Add resistance values to description.
	 */
	@Override
	public String describe() {
		String description = super.describe();
		StringBuilder res = new StringBuilder();
		
		/* Add statuses resistance stats to description. */
		Map<StatusType, Double> resistances = this.getStatusResistancesList().getMap();
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
		Double resistValue = resistances.getStatusResistance(type);
		if (resistValue == null) {
			return 0.0;
		}
		
		return resistValue.doubleValue();
	}
	
	/**
	 * Gets all status types and resistance values for this item.
	 * 
	 * @return
	 * 		List containing types and resistance values
	 */
	public StatusResistanceList getStatusResistancesList() {
		return this.resistances;
	}
}
