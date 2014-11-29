/***************************************************************************
 *                   (C) Copyright 2003-2014 - Stendhal                    *
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
stendhal.zone = stendhal.zone || {};

/**
 * a pseudo entity which represents the ground
 */
stendhal.zone.ground = {
	onclick: function(x, y) {
		var action = {
				"type": "moveto", 
				"x": "" + Math.floor(x / 32 + stendhal.ui.gamewindow.offsetX),
				"y": "" + Math.floor(y / 32 + stendhal.ui.gamewindow.offsetY)
				// TODO: "extend": direction
			};
		marauroa.clientFramework.sendAction(action);
	}
};