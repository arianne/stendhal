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
import games.stendhal.client.entity.Fire;
import games.stendhal.client.entity.FishSource;
import games.stendhal.client.entity.GoldSource;
import games.stendhal.client.entity.GrainField;
import games.stendhal.client.entity.IEntity;
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
import games.stendhal.client.entity.UseableItem;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.WellSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 */
public class EntityViewFactory { 
	/**
	 * Log4J.
	 */
	private static final Logger logger = Logger.getLogger(EntityViewFactory.class);

	/**
	 * The shared instance.
	 */
	private static final EntityViewFactory sharedInstance = new EntityViewFactory();

	/**
	 * The model-to-view class map.
	 */
	protected Map<Class< ? extends IEntity>, Class< ? extends EntityView>> map;

	/**
	 * Create an entity view factory.
	 */
	public EntityViewFactory() {
		map = new HashMap<Class< ? extends IEntity>, Class< ? extends EntityView>>();

		configure();
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
	public EntityView create(final IEntity entity) {
		final Class< ? extends IEntity> entityClass =  entity.getClass();
		final Class< ? extends EntityView> viewClass = map.get(entityClass);

		if (viewClass == null) {
			return null;
		}

		try {
			final Constructor< ? extends EntityView> cns = viewClass
					.getConstructor(entity.getClass());
			final EntityView view = cns.newInstance(entity);

			return view;
		} catch (final SecurityException e) {

			logger.error(e);
		} catch (final NoSuchMethodException e) {

			logger.error(e);
		} catch (final IllegalArgumentException e) {

			logger.error(e);
		} catch (final InstantiationException e) {

			logger.error(e);
		} catch (final IllegalAccessException e) {

			logger.error(e);
		} catch (final InvocationTargetException e) {

			logger.error(e);
		}
		return null;

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
		register(UseableItem.class, UseableItem2DView.class);
		register(User.class, User2DView.class);
		register(WellSource.class, WellSource2DView.class);
	}

	/**
	 * Get the shared [singleton] instance.
	 * 
	 * @return the singleton instance
	 */
	public static EntityViewFactory get() {
		return sharedInstance;
	}

	/**
	 * Register an entity model-to-view mapping.
	 * 
	 * @param entityClass
	 *            The entity model class.
	 * @param viewClass
	 *            The entity view class.
	 */
	private void register(final Class< ? extends IEntity> entityClass,
			final Class< ? extends EntityView> viewClass) {
		map.put(entityClass, viewClass);
	}
}
