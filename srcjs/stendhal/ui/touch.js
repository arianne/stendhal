/***************************************************************************
 *                    Copyright © 2003-2022 - Stendhal                     *
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

	/**
	 * Sets timestamp when touch applied.
	 */
	onTouchStart: function() {
		stendhal.ui.touch.timestampTouchStart = +new Date();
	},

	/**
	 * Sets timestamp when touch released.
	 */
	onTouchEnd: function() {
		stendhal.ui.touch.timestampTouchEnd = +new Date();
	},

	/**
	 * Checks if a touch event represents a long touch after release.
	 */
	isLongTouch: function() {
		return (stendhal.ui.touch.timestampTouchEnd - stendhal.ui.touch.timestampTouchStart
				> stendhal.ui.touch.longTouchDuration);
	},

	/**
	 * Sets information for a held item representation.
	 *
	 * @param img
	 *     Sprite <code>Image</code> to be drawn.
	 */
	setHeldItem: function(img) {
		stendhal.ui.touch.held = {
			image: img,
			offsetX: document.getElementById("gamewindow").offsetWidth - 32,
			offsetY: 0
		}
	},

	/**
	 * Clears information for a held item representation.
	 */
	unsetHeldItem: function() {
		stendhal.ui.touch.held = undefined;
	},

	/**
	 * Draws representation of a held item.
	 *
	 * @param ctx
	 *     Canvas context where representation is drawn.
	 */
	drawHeld: function(ctx) {
		ctx.globalAlpha = 0.5;
		ctx.drawImage(stendhal.ui.touch.held.image,
				stendhal.ui.touch.held.offsetX + stendhal.ui.gamewindow.offsetX,
				stendhal.ui.touch.held.offsetY + stendhal.ui.gamewindow.offsetY);
		ctx.globalAlpha = 1.0;
	}
}
