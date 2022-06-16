/***************************************************************************
 *                    Copyright 2003-2022 © - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};


stendhal.ui.touch = {
	longTouchDuration: 300,
	timestampTouchStart: 0,
	timestampTouchEnd: 0,

	onTouchStart: function() {
		stendhal.ui.touch.timestampTouchStart = +new Date();
	},

	onTouchEnd: function() {
		stendhal.ui.touch.timestampTouchEnd = +new Date();
	},

	isLongTouch: function() {
		return (stendhal.ui.touch.timestampTouchEnd - stendhal.ui.touch.timestampTouchStart
				> stendhal.ui.touch.longTouchDuration);
	},

	setHeldItem: function(img) {
		stendhal.ui.touch.held = {
			image: img
		}
	},

	unsetHeldItem: function() {
		stendhal.ui.touch.held = undefined;
	}
}
