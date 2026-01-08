/***************************************************************************
 *                    Copyright © 2003-2024 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../stendhal";

import { Point } from "../util/Point";


/**
 * Manages touch events.
 */
export class TouchHandler {

	/** Property denoting a touch is engaged. */
	private touchEngaged = false;

	/** Threshold determining if time between touch start & touch end represents a "long" touch. */
	private readonly longTouchDuration = 300;
	/** Time at which touch was engaged. */
	private timestampTouchStart = 0;
	/** Time at which touch was released. */
	private timestampTouchEnd = 0;
	/** Property denoting an object is being "held". */
	private held = false;

	/** Position on page when touch event began. */
	private origin?: Point;
	/** Number of pixels touch can move before being vetoed as a long touch or double tap. */
	private readonly moveThreshold = 16;

	/** Singleton instance. */
	private static instance: TouchHandler;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): TouchHandler {
		if (!TouchHandler.instance) {
			TouchHandler.instance = new TouchHandler();
		}
		return TouchHandler.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Checks for touch event.
	 *
	 * @param evt {Event}
	 *   Event to be checked.
	 * @return {boolean}
	 *   `true` if "evt" represents a `TouchEvent`.
	 */
	public isTouchEvent(evt: Event): boolean {
		return window["TouchEvent"] && evt instanceof TouchEvent;
	}

	/**
	 * Sets timestamp when touch applied.
	 *
	 * @param x {number}
	 *   Touch position relative to page on X axis.
	 * @param y {number}
	 *   Touch position relative to page on Y axis.
	 */
	onTouchStart(x: number, y: number) {
		this.timestampTouchStart = +new Date();
		this.touchEngaged = true;
		// TODO: handle object origin in `ui.HeldObject.HeldObject`
		this.origin = new Point(x, y);
	}

	/**
	 * Sets timestamp when touch released.
	 */
	onTouchEnd() {
		this.timestampTouchEnd = +new Date();
		this.touchEngaged = false;
	}

	/**
	 * Can be used to detect if a mouse event was triggered by touch.
	 *
	 * @return {boolean}
	 *   Value of `ui.TouchHandler.TouchHandler.touchEngaged` property.
	 */
	isTouchEngaged(): boolean {
		return this.touchEngaged;
	}

	/**
	 * Checks if a touch event represents a long touch after release.
	 *
	 * @param evt {Event}
	 *   Event object to be checked.
	 * @return {boolean}
	 *   `true` if touch was released after duration threshold.
	 */
	isLongTouch(evt?: Event): boolean {
		if (evt && !this.isTouchEvent(evt)) {
			return false;
		}
		const durationMatch = (this.timestampTouchEnd - this.timestampTouchStart > this.longTouchDuration);
		let positionMatch = true;
		if (evt && this.origin) {
			const pos = stendhal.ui.html.extractPosition(evt);
			// if position has moved too much it's not a long touch
			positionMatch = (Math.abs(pos.pageX - this.origin.x) <= this.moveThreshold)
					&& (Math.abs(pos.pageY - this.origin.y) <= this.moveThreshold);
		}
		return durationMatch && positionMatch;
	}

	/**
	 * Unsets `ui.TouchHandler.TouchHandler.origin` property.
	 */
	unsetOrigin() {
		this.origin = undefined;
	}

	/**
	 * Sets `ui.TouchHandler.TouchHandler.held` property.
	 */
	setHolding(held: boolean) {
		this.held = held;
	}

	/**
	 * Checks if an held object was initiated using touch.
	 */
	holding(): boolean {
		return this.held;
	}
}
