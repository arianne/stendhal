/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    * 
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var marauroa = window.marauroa = window.marauroa || {};

/*
		register("area", null, null, InvisibleEntity2DView.class);
		register("area", null, null, InvisibleEntity.class);

		register("baby_dragon", null, null, Pet2DView.class);
		register("baby_dragon", null, null, Pet.class);
		register("blackboard", null, null, Sign2DView.class);
		register("blackboard", null, null, Sign.class);
		register("block", null, null, Block.class);
		register("block", null, null, LookableEntity2DView.class);

		register("cat", null, null, Pet2DView.class);
		register("cat", null, null, Pet.class);
		register("chest", null, null, Chest2DView.class);
		register("chest", null, null, Chest.class);
		register("corpse", null, null, Corpse2DView.class);
		register("corpse", null, null, Corpse.class);
		register("creature", "ent", null, BossCreature2DView.class);
		register("creature", "ent", null, BossCreature.class);
		register("creature", null, null, Creature2DView.class);
		register("creature", null, null, Creature.class);

		register("door", null, null, Door2DView.class);
		register("door", null, null, Door.class);

		register("entity", null, null, Entity.class);

		register("fire", null, null, Fire.class);
		register("fire", null, null, UseableEntity2DView.class);

		register("game_board", null, null, GameBoard2DView.class);
		register("game_board", null, null, GameBoard.class);

		register("house_portal", null, null, HousePortal2DView.class);
		register("house_portal", null, null, HousePortal.class);

		register("item", "box", null, Box2DView.class);
		register("item", "box", null, Box.class);
			boxes.xml:    <type class="box" subclass="present" tileid="-1"/>
			boxes.xml:    <type class="box" subclass="basket" tileid="-1"/>
			boxes.xml:    <type class="box" subclass="stocking" tileid="-1"/>
		register("item", "ring", null, UseableRing.class);
		register("item", "ring", null, Ring2DView.class);
		register("item", "ring", "emerald-ring", Ring.class);
		register("item", "ring", "wedding", UseableRing2DView.class);
			rings.xml:    <type class="ring" subclass="medicinal_ring" tileid="-1"/>
			rings.xml:    <type class="ring" subclass="antivenom_ring" tileid="-1"/>
			rings.xml:    <type class="ring" subclass="antitoxin_ring" tileid="-1"/>
			rings.xml:    <type class="ring" subclass="wedding" tileid="-1"/>
			rings.xml:    <type class="ring" subclass="engagement_ring" tileid="-1"/>
			rings.xml:    <type class="ring" subclass="emerald-ring" tileid="-1"/>

		register("looped_sound_source", null, null, LoopedSoundSource.class);

		register("npc", null, null, NPC2DView.class);
		register("npc", null, null, NPC.class);

		register("pet", null, null, Pet2DView.class);
		register("pet", null, null, Pet.class);
		register("plant_grower", null, null, PlantGrower2DView.class);
		register("plant_grower", null, null, PlantGrower.class);
		register("player", null, null, Player2DView.class);
		register("player", null, null, Player.class);
		register("portal", null, null, Portal2DView.class);
		register("portal", null, null, Portal.class);
		register("purple_dragon", null, null, Pet2DView.class);
		register("purple_dragon", null, null, Pet.class);

		register("rented_sign", null, null, Sign2DView.class);
		register("rented_sign", null, null, Sign.class);

		register("sheep", null, null, Sheep2DView.class);
		register("sheep", null, null, Sheep.class);
		register("shop_sign", null, null, ShopSign2DView.class);
		register("shop_sign", null, null, Sign.class);
		register("sign", null, null, Sign2DView.class);
		register("sign", null, null, Sign.class);
		register("spell", null, null, Spell2DView.class);
		register("spell", null, null, Spell.class);

		register("tradecentersign", null, null, Sign.class);
		register("tradecentersign", null, null, TradeCenterSign2DView.class);

		register("useable_entity", null, null, StatefulEntity.class);
		register("useable_entity", null, null, UseableEntity2DView.class);

		register("wall", null, null, Wall2DView.class);
		register("wall", null, null, Wall.class);
		register("weather_entity", null, null, InvisibleEntity.class);

 */

/**
 * Unknown entity
 */
marauroa.rpobjectFactory["unknown"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {
	zIndex: 1,

	init: function() {
		marauroa.rpobjectFactory["unknown"].proto.init.apply(this, arguments);
		var that = this;
		setTimeout(function() {
			console.log("Unknown entity", that["_rpclass"], that["x"], that["y"]);
		}, 1);
	},
	
	isVisibleToAction: function(filter) {
		return (marauroa.me["adminlevel"] && marauroa.me["adminlevel"] >= 600);
	}
});

marauroa.rpobjectFactory["_default"] = marauroa.rpobjectFactory["unknown"];
