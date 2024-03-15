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

declare var stendhal: any;

import { Point } from "../util/Point";


export class TouchHandler {

	private touchEngaged = false;

	private readonly longTouchDuration = 300;
	private timestampTouchStart = 0;
	private timestampTouchEnd = 0;
	private held = false;

	// location when touch event began
	private origin?: Point;
	private readonly moveThreshold = 16;

	/** Property for debugging. */
	private debugging = false;

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
	 */
	public isTouchEvent(evt: Event): boolean {
		return window["TouchEvent"] && evt instanceof TouchEvent;
	}

	/**
	 * Sets timestamp when touch applied.
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
	 */
	isTouchEngaged(): boolean {
		return this.touchEngaged;
	}

	/**
	 * Checks if a touch event represents a long touch after release.
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

	/**
	 * Sets debugging property for touch events.
	 */
	setDebuggingEnabled(enable: boolean) {
		this.debugging = enable;
	}

	/**
	 * Checks if debugging is enabled for touch events.
	 */
	isDebuggingEnabled(): boolean {
		return this.debugging;
	}
}
