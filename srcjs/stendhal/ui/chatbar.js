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
stendhal.ui = stendhal.ui || {};

/**
 * Chat Bar                                
 */
stendhal.ui.chatBar = {
	history: [],
	historyIndex: 0,

	clear: function() {
		document.getElementById('chatbar').value = '';
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
			document.getElementById('chatbar').value = this.history[this.historyIndex];
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
		if (event.shiftKey) {
			if (code == 38) {
				stendhal.ui.chatBar.fromHistory(-1);
			} else if (code == 40){
				stendhal.ui.chatBar.fromHistory(1);
			}
		} else {
			// Movement
			if (code >= 37 && code <= 40) {
				var dir = code - 37;
				if (dir == 0) {
					dir = 4;
				}
				var action = {"type": "move", "dir": ""+dir};
				marauroa.clientFramework.sendAction(action);
			}
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
			stendhal.ui.chatBar.send();
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
		var val = document.getElementById('chatbar').value;
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
}

