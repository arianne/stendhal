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

	busty_dress: {
		"001": true,
		"004": true,
		"006": true,
		"007": true,
		"010": true,
		"011": true,
		"013": true,
		"016": true,
		"029": true,
		"037": true,
		"040": true,
		"053": true,
		"054": true,
		"056": true,
		"061": true,
		"064": true,
		"967": true,
		"968": true,
		"977": true,
		"980": true,
		"989": true,
		"990": true,
		"999": true
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
