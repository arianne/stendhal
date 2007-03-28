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

	private static Map<Pair<String, String>, Class> entityMap= new HashMap<Pair<String, String>, Class>();

	static {
		
		try {
			register();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}

	/**
	 * fills EntityMap with initial values
	 */
	private static void register() throws ClassNotFoundException {
		register("player", null, "Player");

		register("creature", "small_animal", "SmallCreature");
		register("creature", "giant_animal", "BigCreature");
		register("creature", "huge_animal", "HugeCreature");
		register("creature", "mythical_animal", "MythicalCreature");
		register("creature", null, "NormalCreature");

		register("sheep", null, "Sheep");

		register("npc", null, "NPC");

		register("plant_grower", null, "PlantGrower");
		register("growing_entity_spawner", "items/grower/carrot_grower", "CarrotGrower");
		register("growing_entity_spawner", null, "GrainField");
		register("grain_field", null, "GrainField"); // compatibility with server <= 0.56
		register("gold_source", null, "GoldSource");

		register("walk_blocker", null, "InvisibleEntity");
		register("damaging_area", null, "InvisibleEntity");

		register("food", null, "SheepFood");
		register("chest", null, "Chest");

		register("corpse", null, "Corpse");

		register("blood", null, "Blood");
		register("sign", null, "Sign");
		register("blackboard", null, "Sign");

		register("item", null, "Item");
		register("item", "box", "Box");
		register("item", "drink", "StackableItem");
		register("item", "food", "StackableItem");
		register("item", "herb", "StackableItem");
		register("item", "misc", "StackableItem");
		register("item", "money", "StackableItem");
		register("item", "projectiles", "StackableItem");
		register("item", "resource", "StackableItem");
		register("item", "scroll", "StackableItem");

		register("portal", null, "Portal");
		register("door", null, "Door");
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
	private static void register(String type, String eclass, String entityClass) throws ClassNotFoundException {
		entityMap.put(new Pair<String, String>(type, eclass), Class.forName("games.stendhal.client.entity." + entityClass));
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
