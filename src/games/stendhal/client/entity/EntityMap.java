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

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Pair;

/**
 * EntityMap registers the relationship between Type, eclass and java class of
 * entity Objects eclass represents a subtype of type EntityMap encapsulates the
 * implementation
 * 
 */
public final class EntityMap {
	private static Map<Pair<String, String>, Class<? extends Entity>> entityMap = new HashMap<Pair<String, String>, Class<? extends Entity>>();

	static {
		register();
	}

	/**
	 * fills EntityMap with initial values
	 */
	private static void register() {
		register("player", null, Player.class);

		register("creature", "boss", BossCreature.class);
		register("creature", null, Creature.class);

		register("sheep", null, Sheep.class);

		/*
		 * Not sure whether to register individual pets from child classes, or
		 * the whole parent class Pet. suggestions welcome.
		 */
		register("baby_dragon", null, Pet.class);
		register("cat", null, Pet.class);
		register("pet", null, Pet.class);

		register("npc", null, NPC.class);

		register("plant_grower", null, PlantGrower.class);
		register("growing_entity_spawner", "items/grower/carrot_grower",
				CarrotGrower.class);
		register("growing_entity_spawner", null, GrainField.class);

		register("gold_source", null, GoldSource.class);
		register("fish_source", null, FishSource.class);
		register("well_source", null, WellSource.class);

		register("area", null, InvisibleEntity.class);

		register("food", null, SheepFood.class);
		register("chest", null, Chest.class);

		register("corpse", null, Corpse.class);

		register("blood", null, Blood.class);
		register("sign", null, Sign.class);
		register("blackboard", null, Sign.class);

		register("item", null, Item.class);
		register("item", "box", Box.class);
		register("item", "ring", Ring.class);
		register("item", "drink", StackableItem.class);
		register("item", "food", StackableItem.class);
		register("item", "herb", StackableItem.class);
		register("item", "misc", StackableItem.class);
		register("item", "money", StackableItem.class);
		register("item", "missile", StackableItem.class);
		register("item", "ammunition", StackableItem.class);
		register("item", "container", StackableItem.class);

		register("item", "resource", StackableItem.class);

		register("item", "scroll", StackableItem.class);
		register("item", "jewellery", StackableItem.class);

		register("portal", null, Portal.class);
		register("door", null, Door.class);

		register("fire", null, Fire.class);
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
	private static void register(final String type, final String eclass,
			final Class<? extends Entity> entityClazz) {
		entityMap.put(new Pair<String, String>(type, eclass), entityClazz);
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
	public static Class<? extends Entity> getClass(final String type,
			final String eclass) {
		return entityMap.get(new Pair<String, String>(type, eclass));
	}
}
