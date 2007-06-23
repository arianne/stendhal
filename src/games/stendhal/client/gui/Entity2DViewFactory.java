/*
 * @(#) games/stendhal/client/gui/Entity2DViewFactory.java
 *
 * $Id$
 */

package games.stendhal.client.gui;

//
//

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import games.stendhal.client.entity.*;

/**
 *
 */
public class Entity2DViewFactory { // implements EntityViewFactory {
	/**
	 * Logger.
	 */
	private static final Logger logger = Log4J.getLogger(Entity2DViewFactory.class);

	/**
	 * The shared instance.
	 */
	private static final Entity2DViewFactory	sharedInstance = new Entity2DViewFactory();

	/**
	 * The model-to-view class map.
	 */
	protected Map<Class, Class>	map;


	/**
	 * Create an entity view factory.
	 */
	public Entity2DViewFactory() {
		map = new HashMap<Class, Class>();

		configure();
	}


	//
	// Entity2DViewFactory
	//

	/**
	 * Create an entity view from an entity.
	 *
	 * @param	entity		An entity.
	 *
	 * @return	The corresponding view, or <code>null</code>.
	 */
	public Entity2DView create(Entity entity) {
		Class entityClass = entity.getClass();
		Class viewClass = getViewClass(entityClass);

		if(viewClass == null) {
			return null;
		}

		/*
		 * Is it an Entity2DView?
		 */
		if (!Entity2DView.class.isAssignableFrom(viewClass)) {
			logger.error("Class is not an Entity2DView: " + viewClass.getName());
			return null;
		}

		/*
		 * Search for a constructor with a compatible parameter type.
		 * The VM doesn't implicitely match super-classes.
		 */
		Constructor cnstr = null;

		while(entityClass != null) {
			try {
				cnstr = viewClass.getConstructor(new Class[] { entityClass });
				break;
			} catch (NoSuchMethodException ex) {
			}

			entityClass = entityClass.getSuperclass();
		}

		if(cnstr == null) {
			logger.error("Unable to find sutable contructor for: " + viewClass.getName());
			return null;
		}

		/*
		 * Create the view
		 */
		try {
			return (Entity2DView) cnstr.newInstance(new Object[] { entity });
		} catch (InstantiationException ex) {
			logger.error("Unable to create class: " + viewClass.getName(), ex);
			return null;
		} catch (IllegalAccessException ex) {
			logger.error("Unable to access class: " + viewClass.getName(), ex);
			return null;
		} catch (InvocationTargetException ex) {
			logger.error("Error creating class: " + viewClass.getName(), ex);
			return null;
		}
	}


	/**
	 * Configure the view map.
	 */
	protected void configure() {
		register(Blood.class, Blood2DView.class);
		register(BossCreature.class, BossCreature2DView.class);
		register(Chest.class, Chest2DView.class);
		register(Corpse.class, Corpse2DView.class);
		register(Creature.class, Creature2DView.class);
		register(Door.class, Door2DView.class);
		register(Fire.class, Fire2DView.class);
		register(FishSource.class, FishSource2DView.class);
		register(GoldSource.class, GoldSource2DView.class);
		register(GrainField.class, GrainField2DView.class);
		register(InvisibleEntity.class, InvisibleEntity2DView.class);
		register(Item.class, Item2DView.class);
		register(NPC.class, NPC2DView.class);
		register(Pet.class, Pet2DView.class);
		register(PlantGrower.class, PlantGrower2DView.class);
		register(Player.class, Player2DView.class);
		register(Ring.class, Ring2DView.class);
		register(Sheep.class, Sheep2DView.class);
		register(SheepFood.class, SheepFood2DView.class);
		register(Sign.class, Sign2DView.class);
		register(Spell.class, Spell2DView.class);
		register(StackableItem.class, StackableItem2DView.class);
		register(WellSource.class, WellSource2DView.class);
	}


	/**
	 * Get the shared [singleton] instance.
	 */
	public static Entity2DViewFactory get() {
		return sharedInstance;
	}


	/**
	 * Get the appropriete view class for a given entity class.
	 *
	 * @param	entityClass	The entity class.
	 *
	 * @return	A view class, or <code>null</code> if unknown.
	 */
	protected Class getViewClass(Class entityClass) {
		while(entityClass != null) {
			Class viewClass = map.get(entityClass);

			if(viewClass != null) {
				return viewClass;
			}

			entityClass = entityClass.getSuperclass();
		}

		return null;
	}


	/**
	 * Register an enity model-to-view mapping.
	 *
	 * @param	entityClass	The entity model class.
	 * @param	viewClass	The entity view class.
	 */
	public void register(Class entityClass, Class viewClass) {
		map.put(entityClass, viewClass);
	}
}
