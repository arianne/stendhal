/*
 * @(#) games/stendhal/client/gui/j2d/entity/Entity2DViewFactory.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.Blood;
import games.stendhal.client.entity.BossCreature;
import games.stendhal.client.entity.Box;
import games.stendhal.client.entity.Chest;
import games.stendhal.client.entity.CarrotGrower;
import games.stendhal.client.entity.Corpse;
import games.stendhal.client.entity.Creature;
import games.stendhal.client.entity.Door;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Fire;
import games.stendhal.client.entity.FishSource;
import games.stendhal.client.entity.GoldSource;
import games.stendhal.client.entity.GrainField;
import games.stendhal.client.entity.InvisibleEntity;
import games.stendhal.client.entity.Item;
import games.stendhal.client.entity.NPC;
import games.stendhal.client.entity.Pet;
import games.stendhal.client.entity.PlantGrower;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.Portal;
import games.stendhal.client.entity.Ring;
import games.stendhal.client.entity.Sheep;
import games.stendhal.client.entity.SheepFood;
import games.stendhal.client.entity.Sign;
import games.stendhal.client.entity.Spell;
import games.stendhal.client.entity.StackableItem;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.WellSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 *
 */
public class Entity2DViewFactory { // implements EntityViewFactory {
	/**
	 * Log4J.
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
		register(Box.class, Box2DView.class);
		register(CarrotGrower.class, CarrotGrower2DView.class);
		register(Chest.class, Chest2DView.class);
		register(Corpse.class, Corpse2DView.class);
		register(Creature.class, Creature2DView.class);
		register(Door.class, Door2DView.class);
		register(Fire.class, AnimatedLoopEntity2DView.class);
		register(FishSource.class, FishSource2DView.class);
		register(GoldSource.class, GoldSource2DView.class);
		register(GrainField.class, GrainField2DView.class);
		register(InvisibleEntity.class, InvisibleEntity2DView.class);
		register(Item.class, Item2DView.class);
		register(NPC.class, NPC2DView.class);
		register(Pet.class, Pet2DView.class);
		register(PlantGrower.class, PlantGrower2DView.class);
		register(Player.class, Player2DView.class);
		register(Portal.class, Portal2DView.class);
		register(Ring.class, Ring2DView.class);
		register(Sheep.class, Sheep2DView.class);
		register(SheepFood.class, SheepFood2DView.class);
		register(Sign.class, Sign2DView.class);
		register(Spell.class, Spell2DView.class);
		register(StackableItem.class, StackableItem2DView.class);
		register(User.class, User2DView.class);
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
