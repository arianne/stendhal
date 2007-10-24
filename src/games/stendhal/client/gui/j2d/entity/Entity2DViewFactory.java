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
import games.stendhal.client.entity.CarrotGrower;
import games.stendhal.client.entity.Chest;
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
	Logger logger = Log4J.getLogger(Entity2DViewFactory.class);
	/**
	 * The shared instance.
	 */
	protected static Entity2DViewFactory sharedInstance;

	/**
	 * The model-to-view class map.
	 */
	protected Map<Class<? extends Entity>, Class<? extends Entity2DView>> map;

	/**
	 * Create an entity view factory.
	 */
	protected Entity2DViewFactory() {
		map = new HashMap<Class<? extends Entity>, Class<? extends Entity2DView>>();


	}

	//
	// Entity2DViewFactory
	//

	/**
	 * Create an entity view from an entity.
	 *
	 * @param entity
	 *            An entity.
	 *
	 * @return The corresponding view, or <code>null</code>.
	 */
	public Entity2DView create(Entity entity) {
		Class<? extends Entity2DView> viewClass = map.get(entity.getClass());

		Constructor<? extends Entity2DView> view;
		try {
			view = viewClass.getConstructor(entity.getClass());
			return view.newInstance(entity);
		} catch (SecurityException e) {

			logger.error(e);
		} catch (NoSuchMethodException e) {

			logger.error(e);
		}

		catch (IllegalArgumentException e) {
			logger.error(e);
		} catch (InstantiationException e) {

			logger.error(e);
		} catch (IllegalAccessException e) {

			logger.error(e);
		} catch (InvocationTargetException e) {

			logger.error(e);
		}
		return null;

	}

	/**
	 * Configure the view map.
	 */
	protected void configure() {
		map.put(Blood.class, Blood2DView.class);
		map.put(BossCreature.class, BossCreature2DView.class);
		map.put(Box.class, Box2DView.class);
		map.put(CarrotGrower.class, CarrotGrower2DView.class);
		map.put(Chest.class, Chest2DView.class);
		map.put(Corpse.class, Corpse2DView.class);
		map.put(Creature.class, Creature2DView.class);
		map.put(Door.class, Door2DView.class);
		map.put(Fire.class, AnimatedLoopEntity2DView.class);
		map.put(FishSource.class, FishSource2DView.class);
		map.put(GoldSource.class, GoldSource2DView.class);
		map.put(GrainField.class, GrainField2DView.class);
		map.put(InvisibleEntity.class, InvisibleEntity2DView.class);
		map.put(Item.class, Item2DView.class);
		map.put(NPC.class, NPC2DView.class);
		map.put(Pet.class, Pet2DView.class);
		map.put(PlantGrower.class, PlantGrower2DView.class);
		map.put(Player.class, Player2DView.class);
		map.put(Portal.class, Portal2DView.class);
		map.put(Ring.class, Ring2DView.class);
		map.put(Sheep.class, Sheep2DView.class);
		map.put(SheepFood.class, SheepFood2DView.class);
		map.put(Sign.class, Sign2DView.class);
		map.put(Spell.class, Spell2DView.class);
		map.put(StackableItem.class, StackableItem2DView.class);
		map.put(User.class, User2DView.class);
		map.put(WellSource.class, WellSource2DView.class);
	}

	/**
	 * Get the shared [singleton] instance.
	 */
	public static Entity2DViewFactory get() {
		if (sharedInstance==null){
			sharedInstance = new Entity2DViewFactory();
			sharedInstance.configure();
		}
		return sharedInstance;
	}
}
