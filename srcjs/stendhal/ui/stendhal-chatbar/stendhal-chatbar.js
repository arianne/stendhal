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
Polymer("stendhal-chatbar", {
	history: [],
	historyIndex: 0,
	pressedKeys: {},
	
	ready: function() {
		this.onkeydown = this.keydown;
		this.onkeyup = this.keyup;
		this.onkeypress = this.keypress;
	},

	clear: function() {
		this.value = '';
	},

	fromHistory: function(i) {
		this.historyIndex = this.historyIndex + i;
		if (this.historyIndex < 0) {
			this.historyIndex = 0;
		}
		if (this.historyIndex >= this.history.length) {
			this.historyIndex = this.history.length;
			this.clear();
		} else {
			this.value = this.history[this.historyIndex];
		}
	},

	keydown: function(e) {
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
		
		// chat history
		if (event.shiftKey) {
			if (code == 38) {
				this.fromHistory(-1);
			} else if (code == 40){
				this.fromHistory(1);
			}
		}

		// if this is a repeated event, stop further processing
		if (this.pressedKeys[code]) {
			return;
		}
		this.pressedKeys[code] = true;

		// Face and Movement
		var type = "move";
		if (event.shiftKey) {
			type = "face";
		}
		if (code >= 37 && code <= 40) {
			var dir = code - 37;
			if (dir == 0) {
				dir = 4;
			}
			var action = {"type": type, "dir": ""+dir};
			marauroa.clientFramework.sendAction(action);
		}
	},

	keyup: function(e) {
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
		delete this.pressedKeys[code];

		// Movement
		if (code >= 37 && code <= 40) {
			var dir = code - 37;
			if (dir == 0) {
				dir = 4;
			}
			var action = {"type": "stop"};
			marauroa.clientFramework.sendAction(action);
		}
	},
	
	keypress: function(e) {
		if (e.keyCode == 13) {
			this.send();
			return false;
		}
		return true;
	},

	remember: function(text) {
		if (this.history.length > 100) {
			this.history.shift();
		}
		this.history[this.history.length] = text;
		this.historyIndex = this.history.length;
	},

	send: function() {
		var val = this.value;
		var array = val.split(" ");
		if (array[0] == "/choosecharacter") {
			marauroa.clientFramework.chooseCharacter(array[1]);
		} else if (val == '/close') {
			marauroa.clientFramework.close();
		} else {
			if (stendhal.slashActionRepository.execute(val)) {
				this.remember(val);
			}
		}
		this.clear();
	}

});