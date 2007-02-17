/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.client.entity;

import games.stendhal.common.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * EntityMap registers the relationship between Type,eclass and java class of
 * entity Objects eclass represents a subtype of type EntityMap encapsulates the
 * implementation
 * 
 */
public final class EntityMap {

	private static Map<Pair<String, String>, Class> entityMap;

	static {
		entityMap = new HashMap<Pair<String, String>, Class>();
		register();
	}

	/**
	 * fills EntityMap with initial values
	 */
	private static void register() {
		register("player", null, Player.class);

		register("creature", "small_animal", SmallCreature.class);
		register("creature", "giant_animal", BigCreature.class);
		register("creature", "huge_animal", HugeCreature.class);
		register("creature", "mythical_animal", MythicalCreature.class);
		register("creature", null, NormalCreature.class);

		register("sheep", null, Sheep.class);

		register("npc", null, NPC.class);

		register("plant_grower", null, PlantGrower.class);
		register("growing_entity_spawner", null, GrainField.class);
		register("grain_field", null, GrainField.class); // compatibility with server <= 0.56
		register("food", null, SheepFood.class);
		register("chest", null, Chest.class);

		register("corpse", null, Corpse.class);

		register("blood", null, Blood.class);
		register("sign", null, Sign.class);
		register("blackboard", null, Sign.class);

		register("item", null, Item.class);
		register("item", "book", MiscItem.class);
		register("item", "drink", StackableItem.class);
		register("item", "food", StackableItem.class);
		register("item", "herb", MiscItem.class);
		register("item", "key", MiscItem.class);
		register("item", "money", StackableItem.class);
		register("item", "projectiles", StackableItem.class);
		register("item", "resource", StackableItem.class);
		register("item", "scroll", StackableItem.class);

		register("portal", null, Portal.class);
		register("door", null, Door.class);
	}

	/**
	 * @param type
	 *            the type of the entity to be created, such as Item, creature
	 * @param eclass
	 *            the subtype of type such as book, drink, food , ,
	 *            small_animal, huge_animal
	 * @param entityClass
	 *            the java class of the Entity
	 */
	private static void register(String type, String eclass, Class entityClass) {
		entityMap.put(new Pair<String, String>(type, eclass), entityClass);
	}

	/**
	 * @param type
	 *            the type of the entity to be created, such as Item, creature
	 * @param eclass
	 *            the subtype of type such as book, drink, food , ,
	 *            small_animal, huge_animal
	 * 
	 * @return the java class of the Entity belonging to type and eclass
	 */
	public static Class getClass(String type, String eclass) {
		return entityMap.get(new Pair<String, String>(type, eclass));
	}

}
