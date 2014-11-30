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

/**
 * Creature
 */
marauroa.rpobjectFactory.creature = marauroa.util.fromProto(marauroa.rpobjectFactory.rpentity, {
	minimapStyle: "rgb(255,255,0)",
	spritePath: "monsters",
	titleStyle: "#A00000",

	onclick: function(x, y) {
		marauroa.log.debug(this, x, y);
		var action = {
				"type": "attack", 
				"target": "#" + this.id
			};
		marauroa.clientFramework.sendAction(action);
	}
});
