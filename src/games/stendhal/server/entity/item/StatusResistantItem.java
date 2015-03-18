package games.stendhal.server.entity.item;

import games.stendhal.server.entity.status.Status;

import java.util.Map;

/**
 * An item that is resistant to status attacks when equipped
 * 
 * @author AntumDeluge
 */
public class StatusResistantItem extends Item {
	
	public StatusResistantItem(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		// TODO Auto-generated constructor stub
		
		System.out.println("\n!!! CREATING STATUS RESISTANT ITEM: " + name + " !!!\n");
	}
	
	/**
	 * Finds the resistant value of a specified status
	 * 
	 * @param status being resisted
	 * @return resistant value
	 */
	public double getStatusResistance(Status st) {
		return super.getStatusResistance(st);
	}

}
