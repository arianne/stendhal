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
		var code = stendhal.ui.html.extractKeyCode(event);

		// chat history
		if (event.shiftKey) {
			if (code === 38) {
				stendhal.ui.chatinput.fromHistory(-1);
			} else if (code === 40){
				stendhal.ui.chatinput.fromHistory(1);
			}
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
