package games.stendhal.server.entity.item;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.status.StatusResistanceList;
import games.stendhal.server.entity.status.StatusType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * An item that is resistant to status attacks when equipped.
 * 
 * @author AntumDeluge
 */
public class StatusResistantItem extends Item {
	
	/** The logger instance */
	final Logger logger;
	
	/** List of status types that this item is resistant to. */
	private StatusResistanceList resistances;
	
	/** List of slots where this item is active when equpped. */
	private List<String> resistancesActiveSlotList; // Should java.util.Collection be used instead?
	
	/** Entity that equips the item. */
	private RPEntity owner;
	
	/** Current slot where item is equipped. */
	private String currentSlot;
	
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
		
		// FIXME: Resistance should be adjusted for equipping entity if item
		//        is constructed in active slot.
		
		// FIXME: If item is destroyed while in active slot resistance is not
		//        adjusted.
		
		// Initialize logger
		this.logger = Logger.getLogger(StatusResistantItem.class);
		
		// Initialize resistances
		this.resistances = new StatusResistanceList(this);
		
		this.resistancesActiveSlotList = new ArrayList<String>();
		
		if (logger.isInfoEnabled()) {
			logger.info("Created new StatusResistantItem");
		}
	}
	
	/**
	 * Tells whether the item is active in the current slot
	 * 
	 * @param slot
	 * 		The slot to where the item is being (un)equipped
	 * @return
	 * 		Active in slot
	 */
	private boolean activeSlot(String slot) {
		
		if ((resistancesActiveSlotList != null)
				&& !resistancesActiveSlotList.isEmpty()) {
			return resistancesActiveSlotList.contains(slot);
		}
		
		return false;
	}
	
	/**
	 * Add a slot to the list in which item can be active.
	 * 
	 * @param slotName
	 * 		Name of slot where equipped item is active
	 */
	public void addStatusResistancesActiveSlot(String slotName) {
		this.resistancesActiveSlotList.add(slotName);
	}
	
	/**
	 * Adjust an entities resistance to a specific status type.
	 * 
	 * @param entity
	 * 		Entity to receive resistance adjustment
	 * @param statusType
	 * 		Resisted status type
	 * @param adjustValue
	 * 		Amount to change resistance
	 */
	private void adjustStatusResistance(RPEntity entity, StatusType statusType,
			Double adjustValue) {
		String statusAttribute = "resist_" + statusType.toString().toLowerCase();
		Double newResistance;
		
		// Add attribute if entity does not already have any resistance
		if (!entity.has(statusAttribute)) {
			newResistance = adjustValue;
		} else {
			Double currentResistance = entity.getDouble(statusAttribute);
			newResistance = currentResistance + adjustValue;
		}
		
		// Safeguarding. Don't need to worry about less than zero because
		// resistance will be removed if value is zeor or less.
		if (newResistance > 1.0) {
			newResistance = 1.0;
		}
		
		// Remove reference if entity is no longer resistant
		if (newResistance <= 0.0) {
			entity.remove(statusAttribute);
		} else {
			entity.put(statusAttribute, newResistance);
		}
	}
	
	/**
	 * Add resistance values to the description.
	 */
	@Override
	public String describe() {
		String description = super.describe();
		StringBuilder res = new StringBuilder();
		
		// Add statuses resistance stats to description
		Map<StatusType, Double> resistances = this.getStatusResistanceList().getMap();
		if ((resistances != null) && !resistances.isEmpty()) {
			for (Entry<StatusType, Double> entry : resistances.entrySet()) {
				String statusType = entry.getKey().toString().toLowerCase();
				if (statusType.equals("confused")) {
					statusType = "confuse";
				}
				Integer nameLength = statusType.length();
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
	public double getStatusResistance(StatusType statusType) {
		return resistances.getStatusResistance(statusType);
	}
	
	/**
	 * Gets all status types and resistance values for this item.
	 * 
	 * @return
	 * 		Status types and resistance values
	 */
	public StatusResistanceList getStatusResistanceList() {
		return this.resistances;
	}
	
	/**
	 * Gives the entity a status resistance
	 * 
	 * @param owner
	 * 		Entity that is equipping the item 
	 * @param slot
	 * 		The slot where the item is equipped
	 * @return
	 * 		See Item.onEquipped()
	 */
	@Override
	public boolean onEquipped(RPEntity owner, String slot) {
		Boolean ret = super.onEquipped(owner, slot);
		
		// Get the owner for onUnequipped()
		this.owner = owner;
		this.currentSlot = slot;
		
		if (this.logger.isInfoEnabled()) {
			this.logger.info("Equipped StatusResistantItem (ID "
					+ Integer.toString(this.getID().getObjectID()) + ") to "
					+ this.currentSlot);
		}
		
		Boolean toActiveSlot = this.activeSlot(slot);
		
		// Add the resistance if values can be activated in target slot
		if (toActiveSlot) {
			StatusType statusType;
			Double value;
			if ((resistances != null) && !resistances.isEmpty()) {
				for (Entry<StatusType, Double> entry : resistances.getMap().entrySet()) {
					statusType = entry.getKey();
					value = entry.getValue();
					adjustStatusResistance(owner, statusType, value);
					
					if (logger.isInfoEnabled()) {
						logger.info(statusType.toString() + " adjustment: "
								+ value.toString() + " (" + owner.getName()
								+ ")");
					}
				}
			}
		} else {
			ret = false;
		}
		
		return ret;
	}

	/**
	 * Removes or reduces resistance value from entity.
	 * 
	 * @param owner
	 * 		Entity that is unequipping the item
	 * @return
	 * 		See Item.onUnequipped()
	 */
	@Override
	public boolean onUnequipped() {
		Boolean ret = super.onUnequipped();
		
		if ((owner != null) && (currentSlot != null)) {
			
			if ((owner.has("resist_status"))) {
					this.owner.remove("resist_status");
			}
			
			if (this.logger.isInfoEnabled()) {
				this.logger.info(this.owner.getName()
						+ ": Unequipped StatusResistantItem (ID "
						+ Integer.toString(this.getID().getObjectID())
						+ ") from " + this.currentSlot);
			}
			
			Boolean fromActiveSlot = this.activeSlot(this.currentSlot);
			
			// Only adjust resistance values if item was previously active in
			// an appropriate slot
			if (fromActiveSlot) {
				StatusType statusType;
				Double value;
				if ((resistances != null) && !resistances.isEmpty()) {
					for (Entry<StatusType, Double> entry : resistances.getMap().entrySet()) {
						statusType = entry.getKey();
						value = entry.getValue() * -1;
						adjustStatusResistance(owner, statusType, value);
						
						if (logger.isInfoEnabled()) {
							logger.info(statusType.toString() + " adjustment: "
									+ value.toString() + " (" + owner.getName()
									+ ")");
						}
					}
				} else {
					ret = false;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Create or reset resistances to status types for this item.
	 * 
	 * @param resistanceList
	 * 		Status types and resistant values
	 */
	@Override
	public void setStatusResistanceList(Map<StatusType, Double> resistanceList) {
		if (this.resistances == null) {
			this.resistances = new StatusResistanceList(this);
		}
		
		this.resistances.setStatusResistances(resistanceList);
	}

	/**
	 * Create or reset resistances to status types for this item.
	 * 
	 * @param resistanceList
	 * 		Status types and resistant values
	 */
	public void setStatusResistanceList(StatusResistanceList resistanceList) {
		this.resistances = resistanceList;
	}
	
}
