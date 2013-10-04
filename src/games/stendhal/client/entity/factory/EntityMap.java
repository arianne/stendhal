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

package games.stendhal.client.entity.factory;

import games.stendhal.client.Triple;
import games.stendhal.client.entity.Block;
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
import games.stendhal.client.entity.GameBoard;
import games.stendhal.client.entity.Gate;
import games.stendhal.client.entity.GrainField;
import games.stendhal.client.entity.HousePortal;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.InvisibleEntity;
import games.stendhal.client.entity.Item;
import games.stendhal.client.entity.LoopedSoundSource;
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
import games.stendhal.client.entity.StatefulEntity;
import games.stendhal.client.entity.UseableItem;
import games.stendhal.client.entity.UseableRing;
import games.stendhal.client.entity.WalkBlocker;
import games.stendhal.client.entity.Wall;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Registers the relationship between Type, eclass and java class of entity
 * Objects.
 * <p>
 * eclass represents a subtype of type
 * <p>
 * EntityMap encapsulates the implementation
 * 
 */
public final class EntityMap {
	private static Map<Triple<String, String, String>, Class< ? extends IEntity>> entityMap = new HashMap<Triple<String, String, String>, Class< ? extends IEntity>>();

	static {
		register();
	}

	/**
	 * Fills EntityMap with initial values.
	 */
	private static void register() {
		register("entity", null, null, Entity.class);

		register("player", null, null, Player.class);

		register("creature", "ent", null, BossCreature.class);
		register("creature", null, null, Creature.class);

		register("sheep", null, null, Sheep.class);

		register("baby_dragon", null, null, Pet.class);
		register("cat", null, null, Pet.class);
		register("pet", null, null, Pet.class);

		register("npc", null, null, NPC.class);

		register("plant_grower", null, null, PlantGrower.class);
		register("walkblocker", null, null, WalkBlocker.class);

		register("growing_entity_spawner", "items/grower/carrot_grower", null,
				CarrotGrower.class);
		register("growing_entity_spawner", "items/grower/wood_grower", null,
				CarrotGrower.class);
		register("growing_entity_spawner", null, null, GrainField.class);

		register("useable_entity", null, null, StatefulEntity.class);
		// deprecated start
		register("gold_source", null, null, StatefulEntity.class);
		register("fish_source", null, null, StatefulEntity.class);
		register("well_source", null, null, StatefulEntity.class);
		// deprecated end

		register("area", null, null, InvisibleEntity.class);
		register("block", null, null, Block.class);

		register("food", null, null, SheepFood.class);
		register("chest", null, null, Chest.class);

		register("corpse", null, null, Corpse.class);

		register("blood", null, null, Blood.class);

		register("blackboard", null, null, Sign.class);
		register("sign", null, null, Sign.class);
		register("rented_sign", null, null, Sign.class);
		register("shop_sign", null, null, Sign.class);
		register("tradecentersign", null, null, Sign.class);

		register("item", null, null, Item.class);
		register("item", "box", null, Box.class);
		register("item", "ring", "emerald-ring", Ring.class);
		register("item", "ring", null, UseableRing.class);

		register("item", "drink", null, UseableItem.class);
		register("item", "flower", null, StackableItem.class);
		register("item", "food", null, UseableItem.class);
		register("item", "tool", "foodmill", UseableItem.class);
		register("item", "tool", "sugarmill", UseableItem.class);
		register("item", "tool", "scrolleraser", UseableItem.class);
		register("item", "herb", null, StackableItem.class);
		register("item", "misc", null, StackableItem.class);
		register("item", "money", null, StackableItem.class);
		register("item", "missile", null, StackableItem.class);
		register("item", "ammunition", null, StackableItem.class);
		register("item", "container", null, StackableItem.class);
		register("item", "special", null, StackableItem.class);
		register("item", "special", "mithril clasp", Item.class);
		register("item", "club", "wizard_staff", UseableItem.class);
		register("item", "misc", "seed", UseableItem.class);
		register("item", "misc", "bulb", UseableItem.class);

		register("item", "resource", null, StackableItem.class);

		register("item", "scroll", null, UseableItem.class);
		register("item", "jewellery", null, StackableItem.class);

		register("item", "token", null, Item.class);
		
		register("portal", null, null, Portal.class);
		register("house_portal", null, null, HousePortal.class);
		register("door", null, null, Door.class);
		register("fire", null, null, Fire.class);
		
		register("spell", null, null, Spell.class);

		register("gate", null, null, Gate.class);
		register("game_board", null, null, GameBoard.class);
		register("wall", null, null, Wall.class);

		register("looped_sound_source", null, null, LoopedSoundSource.class);
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
			final String subClass, final Class< ? extends IEntity> entityClazz) {
		entityMap.put(
				new Triple<String, String, String>(type, eclass, subClass),
				entityClazz);
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
	public static Class< ? extends IEntity> getClass(final String type,
			final String eclass, final String subClass) {
		Class< ? extends IEntity> result = entityMap
				.get(new Triple<String, String, String>(type, eclass, subClass));
		if (result == null) {
			result = entityMap.get(new Triple<String, String, String>(type,
					eclass, null));
		}
		if (result == null) {
			result = entityMap.get(new Triple<String, String, String>(type,
					null, null));
		}
		if (result == null) {
			
			Logger.getLogger(EntityMap.class).error("no class for " + type + "," + eclass + "," + subClass);
		}
		// System.out.println(type + "," + eclass + "," + subClass);
		return result;
	}
}
