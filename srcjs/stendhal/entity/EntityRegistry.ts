/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var marauroa: any;

import { Blood } from "./Blood";
import { Chest} from "./Chest";
import { Corpse } from "./Corpse";
import { Creature } from "./Creature";
import { Door } from "./Door";
import { Food } from "./Food";
import { GameBoard } from "./GameBoard";
import { Gate } from "./Gate";
import { GrowingEntitySpawner } from "./GrowingEntitySpawner";
import { InvisibleEntity } from "./InvisibleEntity";
import { Item } from "./Item";
import { Portal } from "./Portal";
import { Sign } from "./Sign";
import { UnknownEntity } from "./UnknownEntity";
import { UseableEntity } from "./UseableEntity";
import { VisibleEntity } from "./VisibleEntity";
import { WalkBlocker } from "./WalkBlocker";

marauroa.rpobjectFactory["area"] = InvisibleEntity;
marauroa.rpobjectFactory["blackboard"] = Sign;
marauroa.rpobjectFactory["blocktarget"] = InvisibleEntity;
marauroa.rpobjectFactory["block"] = VisibleEntity;
marauroa.rpobjectFactory["blood"] = Blood;
marauroa.rpobjectFactory["chest"] = Chest;
marauroa.rpobjectFactory["corpse"] = Corpse;
marauroa.rpobjectFactory["creature"] = Creature;
marauroa.rpobjectFactory["_default"] = UnknownEntity;
marauroa.rpobjectFactory["door"] = Door;
marauroa.rpobjectFactory["flyover"] = InvisibleEntity;
marauroa.rpobjectFactory["food"] = Food;
marauroa.rpobjectFactory["game_board"] = GameBoard;
marauroa.rpobjectFactory["gate"] = Gate
marauroa.rpobjectFactory["growing_entity_spawner"] = GrowingEntitySpawner;
marauroa.rpobjectFactory["house_portal"] = Portal;
marauroa.rpobjectFactory["invisible_entity"] = InvisibleEntity;
marauroa.rpobjectFactory["item"] = Item;
marauroa.rpobjectFactory["looped_sound_source"] = InvisibleEntity;
marauroa.rpobjectFactory["plant_grower"] = VisibleEntity;
marauroa.rpobjectFactory["portal"] = Portal;
marauroa.rpobjectFactory["rented_sign"] = Sign;
marauroa.rpobjectFactory["sign"] = Sign; 
marauroa.rpobjectFactory["tiled_entity"] = InvisibleEntity;
marauroa.rpobjectFactory["training_dummy"] = marauroa.rpobjectFactory["npc"];
marauroa.rpobjectFactory["unknown"] = UnknownEntity;
marauroa.rpobjectFactory["useable_entity"] = UseableEntity;
marauroa.rpobjectFactory["visible_entity"] = VisibleEntity;
marauroa.rpobjectFactory["walkblocker"] = WalkBlocker;
marauroa.rpobjectFactory["wall"] = InvisibleEntity;


