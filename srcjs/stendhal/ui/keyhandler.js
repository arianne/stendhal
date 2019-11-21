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
	pressedKeys: [],

	extractMoveOrFaceActionFromEvent: function(event) {
		if (event.shiftKey) {
			return "face";
		}
		return "move";
	},

	extractDirectionFromKeyCode: function(code) {
		var dir = code - 37;
		if (dir === 0) {
			dir = 4;
		}
		return dir;
	},


	onKeyDown: function(e) {
		var event = e;
		if (!event) {
			event = window.event;
		}
		var code = stendhal.ui.html.extractKeyCode(event);

		if (code >= 37 && code <= 40) {
			// if this is a repeated event, stop further processing
			if (stendhal.ui.keyhandler.pressedKeys.indexOf(code) > -1) {
				return;
			}
			stendhal.ui.keyhandler.pressedKeys.push(code);

			var type = stendhal.ui.keyhandler.extractMoveOrFaceActionFromEvent(event);
			var dir = stendhal.ui.keyhandler.extractDirectionFromKeyCode(code);
			var action = {"type": type, "dir": ""+dir};
			marauroa.clientFramework.sendAction(action);
		} else {
			// move focus to chat-input on keydown
			// but don't do that for Ctrl+C, etc.
			if (!event.altKey && !event.metaKey && !event.ctrlKey && event.key !== "Control") {
				if (document.activeElement.localName !== "input") {
					document.getElementById("chatinput").focus();
				}
			}
		}
	},

	onKeyUp: function(e) {
		var event = e
		if (!event) {
			event = window.event;
		}
		var code = stendhal.ui.html.extractKeyCode(event);

		if (code >= 37 && code <= 40) {
			var code = stendhal.ui.html.extractKeyCode(event);
			var i = stendhal.ui.keyhandler.pressedKeys.indexOf(code);
			if (i > -1) {
				stendhal.ui.keyhandler.pressedKeys.splice(i, 1);
			}

			var action = {"type": "stop"};
			marauroa.clientFramework.sendAction(action);

			if (stendhal.ui.keyhandler.pressedKeys.length > 0) {
				code = stendhal.ui.keyhandler.pressedKeys[0];
				var type = stendhal.ui.keyhandler.extractMoveOrFaceActionFromEvent(event);
				var dir = stendhal.ui.keyhandler.extractDirectionFromKeyCode(code);
				var action = {"type": type, "dir": ""+dir};
				marauroa.clientFramework.sendAction(action);
			}
		}
	}
}
