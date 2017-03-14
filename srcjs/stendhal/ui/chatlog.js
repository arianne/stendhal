/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
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
 * Chat Log
 */
stendhal.ui.chatLog = {
	addLine: function(type, message) {
		var chatElement = document.getElementById("chat");
		if (!chatElement) {
			return;
		}
		var date = new Date();
		var time = "" + date.getHours() + ":";
		if (date.getHours < 10) {
			time = "0" + time;
		}
		if (date.getMinutes() < 10) {
			time = time + "0";
		};
		time = time + date.getMinutes();

		var div = document.createElement("div");
		div.className = "log" + type;
		div.innerHTML = "[" + time + "] " + message;
		
		var isAtBottom = (chatElement.scrollHeight - chatElement.clientHeight) == chatElement.scrollTop;
		chatElement.appendChild(div);

		if (isAtBottom) {
			chatElement.scrollTop = chatElement.scrollHeight;
		}
	},

	clear: function() {
		document.getElementById("chat").innerHTML = "";
	}
}
