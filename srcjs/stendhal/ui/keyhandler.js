/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
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
 * handling of key presses and releases
 */
stendhal.ui.keyhandler = {
	pressedKeys: {},



	onKeyDown: function(e) {
		var event = e;
		if (!event) {
			event = window.event;
		}
		var code = stendhal.ui.html.extractKeyCode(event);

		// if this is a repeated event, stop further processing
		if (stendhal.ui.keyhandler.pressedKeys[code]) {
			return;
		}
		stendhal.ui.keyhandler.pressedKeys[code] = true;

		// Face and Movement
		var type = "move";
		if (event.shiftKey) {
			type = "face";
		}
		if (code >= 37 && code <= 40) {
			var dir = code - 37;
			if (dir === 0) {
				dir = 4;
			}
			var action = {"type": type, "dir": ""+dir};
			marauroa.clientFramework.sendAction(action);
		}
	},

	onKeyUp: function(e) {
		var event = e
		if (!event) {
			event = window.event;
		}
		var code = stendhal.ui.html.extractKeyCode(event);
		delete stendhal.ui.keyhandler.pressedKeys[code];

		// Movement
		if (code >= 37 && code <= 40) {
			var dir = code - 37;
			if (dir === 0) {
				dir = 4;
			}
			var action = {"type": "stop"};
			marauroa.clientFramework.sendAction(action);
		}
	}
}
