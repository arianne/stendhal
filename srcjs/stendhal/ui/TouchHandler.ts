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

	private readonly longTouchDuration = 300;
	private timestampTouchStart = 0;
	private timestampTouchEnd = 0;
	private held?: any;

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
	 * Sets timestamp when touch applied.
	 */
	onTouchStart() {
		this.timestampTouchStart = +new Date();
	}

	/**
	 * Sets timestamp when touch released.
	 */
	onTouchEnd() {
		this.timestampTouchEnd = +new Date();
	}

	/**
	 * Checks if a touch event represents a long touch after release.
	 */
	isLongTouch(): boolean {
		return (this.timestampTouchEnd - this.timestampTouchStart
				> this.longTouchDuration);
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
