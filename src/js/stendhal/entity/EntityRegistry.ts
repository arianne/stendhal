/***************************************************************************
 *                   (C) Copyright 2003-2026 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"

import { Blood } from "./Blood";
import { Chest} from "./Chest";
import { Corpse } from "./Corpse";
import { Creature } from "./Creature";
import { DomesticAnimal } from "./DomesticAnimal";
import { Door } from "./Door";
import { Food } from "./Food";
import { GameBoard } from "./GameBoard";
import { Gate } from "./Gate";
import { GrowingEntitySpawner } from "./GrowingEntitySpawner";
import { InvisibleEntity } from "./InvisibleEntity";
import { Item } from "./Item";
import { LoopedSoundSource } from "./LoopedSoundSource";
import { NPC } from "./NPC";
import { Player } from "./Player";
import { Portal } from "./Portal";
import { Sign } from "./Sign";
import { TrainingDummy } from "./TrainingDummy";
import { UnknownEntity } from "./UnknownEntity";
import { UseableEntity } from "./UseableEntity";
import { User } from "./User";
import { VisibleEntity } from "./VisibleEntity";
import { WalkBlocker } from "./WalkBlocker";

export class EntityRegistry {

	public init() {
		marauroa.rpobjectFactory.register("area", InvisibleEntity);
		marauroa.rpobjectFactory.register("baby_dragon", DomesticAnimal);
		marauroa.rpobjectFactory.register("blackboard", Sign);
		marauroa.rpobjectFactory.register("blocktarget", InvisibleEntity);
		marauroa.rpobjectFactory.register("block", VisibleEntity);
		marauroa.rpobjectFactory.register("blood", Blood);
		marauroa.rpobjectFactory.register("cat", DomesticAnimal);
		marauroa.rpobjectFactory.register("chest", Chest);
		marauroa.rpobjectFactory.register("corpse", Corpse);
		marauroa.rpobjectFactory.register("creature", Creature);
		marauroa.rpobjectFactory.register("_default", UnknownEntity);
		marauroa.rpobjectFactory.register("domesticanimal", DomesticAnimal);
		marauroa.rpobjectFactory.register("door", Door);
		marauroa.rpobjectFactory.register("flyover", InvisibleEntity);
		marauroa.rpobjectFactory.register("food", Food);
		marauroa.rpobjectFactory.register("game_board", GameBoard);
		marauroa.rpobjectFactory.register("gate", Gate);
		marauroa.rpobjectFactory.register("growing_entity_spawner", GrowingEntitySpawner);
		marauroa.rpobjectFactory.register("house_portal", Portal);
		marauroa.rpobjectFactory.register("invisible_entity", InvisibleEntity);
		marauroa.rpobjectFactory.register("item", Item);
		marauroa.rpobjectFactory.register("looped_sound_source", LoopedSoundSource);
		marauroa.rpobjectFactory.register("npc", NPC);
		marauroa.rpobjectFactory.register("plant_grower", VisibleEntity);
		marauroa.rpobjectFactory.register("player", Player);
		marauroa.rpobjectFactory.register("portal", Portal);
		marauroa.rpobjectFactory.register("rented_sign", Sign);
		marauroa.rpobjectFactory.register("sheep", DomesticAnimal);
		marauroa.rpobjectFactory.register("sign", Sign);
		marauroa.rpobjectFactory.register("tiled_entity", InvisibleEntity);
		marauroa.rpobjectFactory.register("training_dummy", TrainingDummy);
		marauroa.rpobjectFactory.register("unknown", UnknownEntity);
		marauroa.rpobjectFactory.register("useable_entity", UseableEntity);
		marauroa.rpobjectFactory.register("user", User);
		marauroa.rpobjectFactory.register("visible_entity", VisibleEntity);
		marauroa.rpobjectFactory.register("walkblocker", WalkBlocker);
		marauroa.rpobjectFactory.register("wall", InvisibleEntity);
		marauroa.rpobjectFactory.register("weather_entity", InvisibleEntity);
	}
}
