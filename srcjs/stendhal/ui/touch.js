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

	toMouseEvent: function(evt, type, drag=false) {
		const touch = evt.changedTouches[0] || evt.targetTouches[0] || evt.touches[0];
		const def = {
				"screenX": touch.screenX, "screenY": touch.screenY,
				"clientX": touch.clientX, "clientY": touch.clientY,
				"ctrlKey": evt.ctrlKey, "shiftKey": evt.shiftKey,
				"altKey": evt.altKey, "metaKey": evt.metaKey
		};

		if (drag) {
			return new DragEvent(type, def);
		}
		return new MouseEvent(type, def);
	},

	toDragEvent: function(evt, type) {
		return this.toMouseEvent(evt, type, true);
	},

	dispatchConverted: function(srcEvt, newEvt) {
		const target = stendhal.ui.html.extractPosition(srcEvt).target;
		if (target) {
			target.dispatchEvent(newEvt);
		}
	},

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
