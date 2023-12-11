/***************************************************************************
 *                    Copyright © 2003-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var stendhal: any;


export class TouchHandler {

	private touchEngaged = false;

	private readonly longTouchDuration = 300;
	private timestampTouchStart = 0;
	private timestampTouchEnd = 0;
	private held?: any;

	// location when touch event began
	private origin: {[index: string]: number}|undefined = undefined;
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
	 */
	public isTouchEvent(evt: Event) {
		return window["TouchEvent"] && evt instanceof TouchEvent;
	}

	/**
	 * Sets timestamp when touch applied.
	 */
	onTouchStart(x: number, y: number) {
		this.timestampTouchStart = +new Date();
		this.touchEngaged = true;
		this.origin = {x: x, y: y};
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
	 * Sets information for a held item representation.
	 *
	 * @param img
	 *     Sprite <code>Image</code> to be drawn.
	 */
	setHeldItem(img: HTMLImageElement) {
		this.held = {
			image: img,
			offsetX: document.getElementById("gamewindow")!.offsetWidth - 32,
			offsetY: 0
		};
	}

	/**
	 * Clears information for a held item representation.
	 */
	unsetHeldItem() {
		this.held = undefined;
	}

	unsetOrigin() {
		this.origin = undefined;
	}

	/**
	 * Checks if there is currently an item being held.
	 */
	holdingItem(): boolean {
		return this.held != undefined && this.held != null;
	}

	/**
	 * Draws representation of a held item.
	 *
	 * @param ctx
	 *     Canvas context where representation is drawn.
	 */
	drawHeld(ctx: CanvasRenderingContext2D) {
		ctx.globalAlpha = 0.5;
		ctx.drawImage(this.held.image,
				this.held.offsetX + stendhal.ui.gamewindow.offsetX,
				this.held.offsetY + stendhal.ui.gamewindow.offsetY);
		ctx.globalAlpha = 1.0;
	}
}
