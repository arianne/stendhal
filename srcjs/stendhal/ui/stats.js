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
var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

/**
 * buddylist
 */
stendhal.ui.stats = {
	keys: ['hp', 'base_hp', 'atk', 'atk_item', 'atk_xp', 'def', 'def_item', 'def_xp', 'xp', 'level'],

	update: function() {
		if (!stendhal.ui.stats.dirty) {
			return;
		}
		stendhal.ui.stats.dirty = false;
		var div = document.getElementById("stats");
		var object = marauroa.me;
		div.innerText =
			"HP: " + object["hp"] + " / " + object["base_hp"] + "\r\n"
			+ "ATK: " + object["atk"] + " x " + object["atk_item"] + "\r\n"
			+ "DEF: " + object["def"] + " x " + object["def_item"] + "\r\n"
			+ "XP: " + object["xp"] + "\r\n"
			+ "Level: " + object["level"];
	}
};
