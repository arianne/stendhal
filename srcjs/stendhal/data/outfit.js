/***************************************************************************
 *                     Copyright © 2003-2022 - Arianne                     *
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
stendhal.data = stendhal.data || {};


stendhal.data.outfit = {
	// player pickable layers
	count: {
		"hat": 19,
		"hair": 57,
		"mask": 9,
		"eyes": 28,
		"mouth": 5,
		"head": 4,
		"dress": 65,
		"body": 3
	},

	// hair should not be drawn with hat indexes in this list
	hats_no_hair: [3, 4, 13, 16, 992, 993, 994, 996, 997],

	/**
	 * Determines if hair should be drawn under a determinted hat index.
	 *
	 * @param hat
	 *     Hat index to be checked.
	 * @return
	 *     <code>true</code> if hair should be drawn, <code>false</code> otherwise.
	 */
	drawHair: function(hat) {
		return !stendhal.data.outfit.hats_no_hair.includes(hat);
	}
}
