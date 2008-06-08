package games.stendhal.server.entity.mapstuff.office;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.Entity;

import java.awt.Rectangle;
import java.awt.Shape;
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
public abstract class StoreableEntityList<T extends Entity> implements TurnListener {
	private StendhalRPZone zone;
	private Class<T> clazz;
	private Shape shape;
	private int notifyDelta;

	/**
	 * Creates a new StoreableEntityList.
	 *
	 * @param zone  zone to store the entities in
	 * @param clazz class object of the entities to manage
	 */
	// the class object is needed, because generic type variables (T)
	// cannot be used in instanceof.
	StoreableEntityList(StendhalRPZone zone, Class<T> clazz) {
		this.zone = zone;
		this.clazz = clazz;
	}

	public StoreableEntityList(StendhalRPZone zone, Shape shape, Class<T> clazz) {
	    this(zone, clazz);
	    this.shape = shape;
    }

	/**
     * Adds a storeable entity.
     * 
     * @param entity storeable entity
     * @return true in case the entity was added successfully; 
     * 				false in case no free spot for it was found
     */
	public boolean add(T entity) {
		boolean success = calculatePosition(entity);
		if (!success) {
			return false;
		}
		zone.add(entity);
		zone.storeToDatabase();
		return true;
	}

	/**
	 * calculates a free spot to place this entity into.
	 *
	 * @param entity entity
	 * @return true, in case a spot was found or this entity should 
	 * 				not be place in the zone; false otherwise
	 */
	private boolean calculatePosition(T entity) {
		if (shape == null) {
			return true;
		}

		Rectangle rect = shape.getBounds();
		for (int x = rect.x; x < rect.x + rect.width; x++) {
			for (int y = rect.y; y < rect.y + rect.height; y++) {
				if (shape.contains(x, y)) {
					if (!zone.collides(entity, x, y)) {
						entity.setPosition(x, y);
						return true;
					}
				}
			}
		}

		return false;
    }

	/**
     * Returns the storeable entity for the specified identifier.
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
     * Removes all storeable entities for this identifier.
     * 
     * @param identifier name of entity
     */
    public boolean removeByName(String identifier) {
    	List<T> entities = getList();
    	boolean changed = false;
    	for (T entity : entities) {
    		if (getName(entity).equals(identifier)) {
    			zone.remove(entity);
    			zone.storeToDatabase();
    			changed = true;
    		}
    	}
    	return changed;
    }

	/**
     * gets a list of storeable entities from the zone storage. Note: This is only a
     * temporary snapshot, do not save it outside the scope of a method.
     * 
     * @return List of storeabe entities.
     */
    protected List<T> getList() {
    	List<T> res = new LinkedList<T>();
    	for (RPObject object : zone) {
    		if (clazz.isInstance(object)) {
    			T entity = clazz.cast(object);
    			res.add(entity);
    		}
    	}
    	return res;
    }

	protected void setupTurnNotifier(int notifyDelta) {
		this.notifyDelta = notifyDelta;
		SingletonRepository.getTurnNotifier().notifyInSeconds(notifyDelta, this);
	}

    public void onTurnReached(int currentTurn) {
		boolean modified = false;
    	List<T> entities = getList();
    	for (T entity : entities) {
    		if (shouldExpire(entity)) {
    			zone.remove(entity);
    			modified = true;
    		}
    	}
    	
    	if (modified) {
    		zone.storeToDatabase();
    	}

		SingletonRepository.getTurnNotifier().notifyInSeconds(notifyDelta, this);
    }

	protected abstract String getName(T entity);

	protected boolean shouldExpire(T entity) {
		return false;
	}

}