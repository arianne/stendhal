/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";


var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};


stendhal.ui.equip = {
	inventory: [],

	update: function() {
		for (var i in stendhal.ui.equip.inventory) {
			stendhal.ui.equip.inventory[i].update();
		}
	}

};
