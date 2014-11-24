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
 * Chat Log
 */
stendhal.ui.chatLog = {
	addLine: function(type, msg) {
		var e = document.createElement('p');
		e.className = "log" + stendhal.ui.html.esc(type);
		var date = new Date();
		var time = "" + date.getHours() + ":";
		if (date.getHours < 10) {
			time = "0" + time;
		}
		if (date.getMinutes() < 10) {
			time = time + "0";
		};
		time = time + date.getMinutes();
		
		e.innerHTML = "[" + time + "] " + stendhal.ui.html.esc(msg);
		document.getElementById('chat').appendChild(e);
		document.getElementById('chat').scrollTop = 1000000;
	},

	clear: function() {
		document.getElementById("chat").innerHTML = "";
	}
}