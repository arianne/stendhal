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
register("area",         InvisibleEntity2DView, InvisibleEntity);
register("baby_dragon",  Pet2DView,             Pet);
register("blackboard",   Sign2DView,            Sign);
register("block",        LookableEntity2DView,  Block);
register("cat",          Pet2DView,             Pet);
register("chest",        Chest2DView,           Chest);
register("corpse",       Corpse2DView,          Corpse);
register("creature",     Creature2DView,        Creature);
register("door",         Door2DView,            Door);
register("entity",                              Entity);
register("fire",         UseableEntity2DView,   Fire);
register("game_board",   GameBoard2DView,       GameBoard);
register("house_portal", HousePortal2DView,     HousePortal);

register("item", "box", null, Box2DView,        Box);
			boxes.xml:    <type class="box" subclass="present" tileid="-1"/>
			boxes.xml:    <type class="box" subclass="basket" tileid="-1"/>
			boxes.xml:    <type class="box" subclass="stocking" tileid="-1"/>

register("item", "ring", null, UseableRing,     Ring2DView);
register("item", "ring", "emerald-ring",              Ring
register("item", "ring", "wedding", *,  UseableRing2DView);
			rings.xml:    <type class="ring" subclass="medicinal_ring" tileid="-1"/>
			rings.xml:    <type class="ring" subclass="antivenom_ring" tileid="-1"/>
			rings.xml:    <type class="ring" subclass="antitoxin_ring" tileid="-1"/>
			rings.xml:    <type class="ring" subclass="wedding" tileid="-1"/>
			rings.xml:    <type class="ring" subclass="engagement_ring" tileid="-1"/>
			rings.xml:    <type class="ring" subclass="emerald-ring" tileid="-1"/>

register("looped_sound_source",              LoopedSoundSource);

register("pet",              Pet2DView,      Pet);
register("plant_grower",     PlantGrower2DView,     PlantGrower);
register("player",           Player2DView,   Player);
register("portal",           Portal2DView,   Portal);
register("purple_dragon",    Pet2DView,      Pet);
register("rented_sign",      Sign2DView,     Sign);
register("sheep",            Sheep2DView,    Sheep);
register("shop_sign",        ShopSign2DView, Sign);
register("sign",             Sign2DView,     Sign);
register("spell",            Spell2DView,    Spell);
register("tradecentersign",  TradeCenterSign2DView,  Sign);
register("useable_entity",   UseableEntity2DView,    StatefulEntity);
register("wall",             Wall2DView,             Wall);
register("weather_entity",     *,                    InvisibleEntity);

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
