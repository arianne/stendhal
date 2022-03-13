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

var stendhal = window.stendhal = window.stendhal || {};

stendhal.config = {
	sound: {
		play: false
	},

	gamescreen: {
		blood: true
	},

	init: function(args) {
		this.gamescreen.blood = args.get("noblood") == null;
		this.gamescreen.shadows = args.get("noshadows") == null;

		this.character = args.get("char");
		this.theme = args.get("theme") || "wood";
		this.itemDoubleClick = args.get("item_doubleclick") != null;

		// store window information for this session
		this.windowstates = {
			settings: {x: 20, y: 20}
		};
	}
};
