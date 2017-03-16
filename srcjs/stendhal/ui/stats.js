/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

"use strict";
window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

/**
 * buddylist
 */
stendhal.ui.stats = {
	update: function() {
		var div = document.getElementById("stats");
		var object = marauroa.me;
		div.innerText = 
			"HP: " + object.hp + " / " + object.base_hp + "\r\n"
			+ "ATK: " + object.atk + " x " + object.atk_item + "\r\n"
			+ "DEF: " + object.def + " x " + object.def_item + "\r\n"
		    + "XP: " + object.xp + "\r\n"
		    + "Level: " + object.xp;
	}
}