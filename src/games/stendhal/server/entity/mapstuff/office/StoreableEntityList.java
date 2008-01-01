package games.stendhal.server.entity.mapstuff.office;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPObject;

public abstract class StoreableEntityList<T extends Entity> {
	private StendhalRPZone zone;
	private Class<T> clazz;

	public StoreableEntityList(StendhalRPZone zone, Class<T> clazz) {
		super();
		this.zone = zone;
		this.clazz = clazz;
	}

	/**
     * Adds an ArrestWarrant
     * 
     * @param warrant
     *            ArrestWarrant
     */
    public void add(T warrant) {
    	zone.add(warrant);
    	zone.storeToDatabase();
    }

	/**
     * returns the ArrestWarrant for the specified player name
     * 
     * @param criminal
     *            name of player to be arrested
     * @return ArrestWarrant or <code>null</code> in case there is none
     */
    public T getByName(String criminal) {
    	List<T> arrestWarrants = getList();
    	for (T arrestWarrant : arrestWarrants) {
    		if (getName(arrestWarrant).equals(criminal)) {
    			return arrestWarrant;
    		}
    	}
    	return null;
    }

	/**
     * removes all ArrestWarrants for this player
     * 
     * @param criminal
     *            name of player
     */
    public void removeByName(String criminal) {
    	List<T> arrestWarrants = getList();
    	for (T arrestWarrant : arrestWarrants) {
    		if (getName(arrestWarrant).equals(criminal)) {
    			zone.remove(arrestWarrant);
    		}
    	}
    	zone.storeToDatabase();
    }

	/**
     * gets a list of ArrestWarrant from the zone storage. Note: This is only a
     * temporary snapshot, do not save it outside the scope of a method.
     * 
     * @return List of ArrestWarrants.
     */
    private List<T> getList() {
    	List<T> res = new LinkedList<T>();
    	for (RPObject object : zone) {
    		if (clazz.isInstance(object)) {
    			T arrestWarrant = clazz.cast(object);
    			res.add(arrestWarrant);
    		}
    	}
    	return res;
    }
   
    public abstract String getName(T entity);

}