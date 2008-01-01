package games.stendhal.server.entity.mapstuff.office;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPObject;

/**
 * a list of storeable entities that can be accessed by a unique
 * identifier like a name.
 *
 * @author hendrik
 * @param <T> type of the storeable entities to be managed by this list
 */
abstract class StoreableEntityList<T extends Entity> {
	private StendhalRPZone zone;
	private Class<T> clazz;

	/**
	 * creates a new StoreableEntityList
	 *
	 * @param zone  zone to store the entities in
	 * @param clazz class object of the entities to manage
	 */
	// the class object is needed, because generic type variables (T)
	// cannot be used in instanceof.
	public StoreableEntityList(StendhalRPZone zone, Class<T> clazz) {
		super();
		this.zone = zone;
		this.clazz = clazz;
	}

	/**
     * Adds a storeable entity
     * 
     * @param entity storeable entity
     */
    public void add(T entity) {
    	zone.add(entity);
    	zone.storeToDatabase();
    }

	/**
     * returns the storeable entity for the specified identifier
     * 
     * @param identifier name of entity
     * @return storeable entity or <code>null</code> in case there is none
     */
    public T getByName(String identifier) {
    	List<T> entities = getList();
    	for (T entity : entities) {
    		if (getName(entity).equals(identifier)) {
    			return entity;
    		}
    	}
    	return null;
    }

	/**
     * removes all storeable entities for this identifier
     * 
     * @param identifier name of entity
     */
    public void removeByName(String identifier) {
    	List<T> entities = getList();
    	for (T entity : entities) {
    		if (getName(entity).equals(identifier)) {
    			zone.remove(entity);
    		}
    	}
    	zone.storeToDatabase();
    }

	/**
     * gets a list of storeable entities from the zone storage. Note: This is only a
     * temporary snapshot, do not save it outside the scope of a method.
     * 
     * @return List of storeabe entities.
     */
    private List<T> getList() {
    	List<T> res = new LinkedList<T>();
    	for (RPObject object : zone) {
    		if (clazz.isInstance(object)) {
    			T entity = clazz.cast(object);
    			res.add(entity);
    		}
    	}
    	return res;
    }
   
    public abstract String getName(T entity);

}