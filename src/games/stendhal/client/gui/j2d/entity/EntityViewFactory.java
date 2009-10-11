/*
 * @(#) games/stendhal/client/gui/j2d/entity/Entity2DViewFactory.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.StendhalClient;
import games.stendhal.client.Triple;
import games.stendhal.client.entity.IEntity;

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
	private static final Logger LOGGER = Logger.getLogger(EntityViewFactory.class);


	private static Map<Triple<String, String, String>, Class< ? extends EntityView>> viewMap = new HashMap<Triple<String, String, String>, Class< ? extends EntityView>>();
	/**
	 * The shared instance.
	 */
	private static final EntityViewFactory sharedInstance = new EntityViewFactory();

	/**
	 * Create an entity view factory.
	 */
	public EntityViewFactory() {

		configure();
	}

	//
	// Entity2DViewFactory
	//

	
	
	/**
	 * @param type
	 *            the type of the entity to be created, such as Item, creature
	 * @param eclass
	 *            the subtype of type such as book, drink, food , ,
	 *            small_animal, huge_animal
	 * @param subClass
	 * 
	 * @return the java class of the Entity belonging to type and eclass
	 */
	public static Class< ? extends EntityView> getClass(final String type,
			final String eclass, final String subClass) {
		Class< ? extends EntityView> result = viewMap.get(new Triple<String, String, String>(type, eclass, subClass));
		if (result == null) {
			result = viewMap.get(new Triple<String, String, String>(type,
					eclass, null));
		}
		if (result == null) {
			result = viewMap.get(new Triple<String, String, String>(type,
					null, null));
		}
		return result;
	}
	
	
	/**
	 * Create an entity view from an entity.
	 * 
	 * @param entity
	 *            An entity.
	 * 
	 * @return The corresponding view, or <code>null</code>.
	 */
	public static EntityView create(final IEntity entity) {
		
		try {
			final String type = entity.getType();

			if (type.equals("player") && StendhalClient.get().getAccountUsername().equalsIgnoreCase(
						entity.getName())) {
				User2DView user2DView = new User2DView();
				user2DView.initialize(entity);
				return user2DView;
			}
		
			String eclass = entity.getEntityClass();
			String subClass = entity.getEntitySubClass();

			
			final Class< ? extends EntityView> entityClass = getViewClass(type, eclass, subClass);
			if (entityClass == null) {
					return null;
			}

			final EntityView en = (EntityView) entityClass.newInstance();
			en.initialize(entity);

			return en;
		} catch (final Exception e) {
			LOGGER.error("Error creating entity for object: " + entity, e);
			return null;
		}
		


	}
	/**
	 * @param type
	 *            the type of the entity to be created, such as Item, creature
	 * @param eclass
	 *            the subtype of type such as book, drink, food , ,
	 *            small_animal, huge_animal
	 * @param subClass
	 * 
	 * @return the java class of the Entity belonging to type and eclass
	 */
	public static Class< ? extends EntityView> getViewClass(final String type,
			final String eclass, final String subClass) {
		Class< ? extends EntityView> result = viewMap
				.get(new Triple<String, String, String>(type, eclass, subClass));
		if (result == null) {
			result = viewMap.get(new Triple<String, String, String>(type,
					eclass, null));
		}
		if (result == null) {
			result = viewMap.get(new Triple<String, String, String>(type,
					null, null));
		}
		return result;
	}

	/**
	 * Configure the view map.
	 */
	protected void configure() {
		
		register("blood", null, null, Blood2DView.class);
		register("creature", "ent", null, BossCreature2DView.class);
		
		register("item", "box", null, Box2DView.class);
		
		register("growing_entity_spawner", "items/grower/wood_grower", null, CarrotGrower2DView.class);
		register("growing_entity_spawner", "items/grower/carrot_grower", null, CarrotGrower2DView.class);
		register("chest", null, null, Chest2DView.class);
		register("corpse", null, null, Corpse2DView.class);
		
		register("creature", null, null, Creature2DView.class);
		
		register("door", null, null, Door2DView.class);
	
		register("fire", null, null, AnimatedLoopEntity2DView.class);
		register("fish_source", null, null, FishSource2DView.class);

		
		register("game_board", null, null, GameBoard2DView.class);
		register("gate", null, null, Gate2DView.class);
		
		register("gold_source", null, null, GoldSource2DView.class);
		
		register("growing_entity_spawner", null, null, GrainField2DView.class);
		
		register("house_portal", null, null, HousePortal2DView.class);
		
		register("area", null, null,  InvisibleEntity2DView.class);
		
	    register("item", "special", "mithril clasp", Item2DView.class);
		register("item", null, null,  Item2DView.class);
		register("npc", null, null, NPC2DView.class);
		
		register("cat", null, null, Pet2DView.class);
		register("pet", null, null, Pet2DView.class);
		register("baby_dragon", null, null, Pet2DView.class);
		
		register("plant_grower", null, null, PlantGrower2DView.class);
		
		register("player", null, null, Player2DView.class);
		
		register("portal", null, null, Portal2DView.class);
		
		register("item", "ring", "emerald-ring", Ring2DView.class);
		
		register("sheep", null, null,  Sheep2DView.class);
		register("food", null, null, SheepFood2DView.class);
		
		register("sign", null, null,  Sign2DView.class);
		register("blackboard", null, null,  Sign2DView.class);
		register("tradecentersign", null, null, TradeCenterSign2DView.class);
		
		register("item", "jewellery", null,  StackableItem2DView.class);
		register("item", "flower", null,  StackableItem2DView.class);
		register("item", "resource", null,  StackableItem2DView.class);
		register("item", "herb", null, StackableItem2DView.class);
		register("item", "misc", null,  StackableItem2DView.class);
		register("item", "money", null,  StackableItem2DView.class);
		register("item", "missile", null,  StackableItem2DView.class);
		register("item", "ammunition", null,  StackableItem2DView.class);
		register("item", "container", null,  StackableItem2DView.class);
        register("item", "special", null,  StackableItem2DView.class);
		
		
		register("item", "club", "wizard_staff",  UseableItem2DView.class);
        register("item", "misc", "seed", UseableItem2DView.class);

		register("item", "scroll", null,  UseableItem2DView.class);

		register("item", "food", null, UseableItem2DView.class);
		register("item", "drink", null,  UseableItem2DView.class);
		register("item", "tool", "foodmill",  UseableItem2DView.class);
		
		register("item", "ring", null, UseableRing2DView.class);
		
		register("walkblocker", null, null, WalkBlocker2DView.class);
		register("well_source", null, null, WellSource2DView.class);
	}

	/**
	 * @param type
	 *            the type of the entity to be created, such as Item, creature
	 * @param eclass
	 *            the subtype of type such as book, drink, food , ,
	 *            small_animal, huge_animal
	 * @param subClass 
	 * @param entityClazz
	 *            the java class of the Entity
	 */
	private static void register(final String type, final String eclass,
			final String subClass, final Class< ? extends EntityView> entityClazz) {
		viewMap.put(
				new Triple<String, String, String>(type, eclass, subClass),
				entityClazz);
	}

	
	/**
	 * Get the shared [singleton] instance.
	 * 
	 * @return the singleton instance
	 */
	public static EntityViewFactory get() {
		return sharedInstance;
	}
}
