package games.stendhal.server.entity.item;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.status.StatusResistanceList;
import games.stendhal.server.entity.status.StatusType;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * An item that is resistant to status attacks when equipped.
 * 
 * @author AntumDeluge
 */
public class StatusResistantItem extends Item {
	/** Logger instance */
	final Logger logger;
	
	/** List of status types that this item is resistant to. */
	private StatusResistanceList resistances;
	
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
		
		// Initialize logger
		this.logger = Logger.getLogger(StatusResistantItem.class);
		
		// Initialize resistances
		this.resistances = new StatusResistanceList(this);
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
	 * 		unknown, see note in super.onEquipped()
	 */
	@Override
	public boolean onEquipped(RPEntity owner, String slot) {
		return super.onEquipped(owner, slot);
		
	}

	/**
	 * Removes or reduces resistance value from entity.
	 * 
	 * @param owner
	 * 		Entity that is unequipping the item
	 * @return
	 * 		Unknown, see note in super.onUnequipped()
	 */
	public boolean onUnequipped(RPEntity owner) {
		return super.onUnequipped();
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
		this.resistances= resistanceList;
	}

}
