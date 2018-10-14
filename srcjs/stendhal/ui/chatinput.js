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

stendhal.ui.chatinput = {
	history: [],
	historyIndex: 0,
	pressedKeys: {},

	clear: function() {
		document.getElementById("chatinput").value = "";
	},

	setText: function(text) {
		var chatinput = document.getElementById("chatinput");
		chatinput.value = text;
		chatinput.focus();
	},

	fromHistory: function(i) {
		stendhal.ui.chatinput.historyIndex = stendhal.ui.chatinput.historyIndex + i;
		if (stendhal.ui.chatinput.historyIndex < 0) {
			stendhal.ui.chatinput.historyIndex = 0;
		}
		if (stendhal.ui.chatinput.historyIndex >= stendhal.ui.chatinput.history.length) {
			stendhal.ui.chatinput.historyIndex = stendhal.ui.chatinput.history.length;
			stendhal.ui.chatinput.clear();
		} else {
			document.getElementById("chatinput").value = stendhal.ui.chatinput.history[stendhal.ui.chatinput.historyIndex];
		}
	},

	onKeyDown: function(e) {
		var event = e;
		if (!event) {
			event = window.event;
		}
		var code;
		if (event.which) {
			code = event.which;
		} else {
			code = e.keyCode;
		}

		// chat history
		if (event.shiftKey) {
			if (code === 38) {
				stendhal.ui.chatinput.fromHistory(-1);
			} else if (code === 40){
				stendhal.ui.chatinput.fromHistory(1);
			}
		}

		// if this is a repeated event, stop further processing
		if (stendhal.ui.chatinput.pressedKeys[code]) {
			return;
		}
		stendhal.ui.chatinput.pressedKeys[code] = true;

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
		var code;
		if (event.which) {
			code = event.which;
		} else {
			code = e.keyCode;
		}
		delete stendhal.ui.chatinput.pressedKeys[code];

		// Movement
		if (code >= 37 && code <= 40) {
			var dir = code - 37;
			if (dir === 0) {
				dir = 4;
			}
			var action = {"type": "stop"};
			marauroa.clientFramework.sendAction(action);
		}
	},

	onKeyPress: function(e) {
		if (e.keyCode === 13) {
			stendhal.ui.chatinput.send();
			return false;
		}
		return true;
	},

	remember: function(text) {
		if (stendhal.ui.chatinput.history.length > 100) {
			stendhal.ui.chatinput.history.shift();
		}
		stendhal.ui.chatinput.history[stendhal.ui.chatinput.history.length] = text;
		stendhal.ui.chatinput.historyIndex = stendhal.ui.chatinput.history.length;
	},

	send: function() {
		var val = document.getElementById("chatinput").value;
		var array = val.split(" ");
		if (array[0] === "/choosecharacter") {
			marauroa.clientFramework.chooseCharacter(array[1]);
		} else if (val === '/close') {
			marauroa.clientFramework.close();
		} else {
			if (stendhal.slashActionRepository.execute(val)) {
				stendhal.ui.chatinput.remember(val);
			}
		}
		stendhal.ui.chatinput.clear();
	}
};
