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
stendhal.ui = stendhal.ui || {};

/**
 * HTML code manipulation.
 */
stendhal.ui.html = {
	esc: function(msg, filter=[]){
		msg = msg.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/\n/g, "<br>");
		// restore filtered tags
		for (const tag of filter) {
			msg = msg.replace("&lt;" + tag + "&gt;", "<" + tag + ">")
					.replace("&lt;/" + tag + "&gt;", "</" + tag + ">");
		}

		return msg;
	},

	extractKeyCode: function(event) {
		if (event.which) {
			return event.which;
		} else {
			return e.keyCode;
		}
	},

	niceName: function(s) {
		if (!s) {
			return "";
		}
		let temp = s.replace(/_/g, " ").trim();
		return temp.charAt(0).toUpperCase() + temp.slice(1);
	},

	extractPosition: function(event) {
		if (event.changedTouches) {
			var pos = {
				pageX: Math.round(event.changedTouches[0].pageX),
				pageY: Math.round(event.changedTouches[0].pageY),
				target: event.changedTouches[0].target
			}
			pos.offsetX = pos.pageX - event.changedTouches[0].target.offsetLeft;
			pos.offsetY = pos.pageY - event.changedTouches[0].target.offsetTop;
			return pos;
		}
		return event;
	},

	formatTallyMarks: function(line) {
		let tmp = line.split("<tally>");
		const pre = tmp[0];
		tmp = tmp[1].split("</tally>");
		const post = tmp[1];
		const count = parseInt(tmp[0].trim(), 10);

		let tallyString = "";
		if (count > 0) {
			let t = 0
			for (let idx = 0; idx < count; idx++) {
				t++
				if (t == 5) {
					tallyString += "5";
					t = 0;
				}
			}

			if (t > 0) {
				tallyString += t;
			}
		} else {
			tallyString = "0";
		}

		const tally = document.createElement("span");
		tally.className = "tally";
		tally.textContent = tallyString;

		return [pre, tally, post];
	}
};
