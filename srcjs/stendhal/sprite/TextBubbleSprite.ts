/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var stendhal: any;


export abstract class TextBubbleSprite {

	protected text: string;
	protected timeStamp: number;
	protected x = -1;
	protected y = -1;
	protected width = -1;
	protected height = -1;
	protected baseDuration = 2000;

	protected onRemovedAction?: Function;


	constructor(text: string) {
		this.text = text;
		this.timeStamp = Date.now();
	}

	/**
	 * Handles drawing the sprite on the screen.
	 *
	 * @param ctx
	 *     Drawing canvas.
	 * @return
	 *     <code>true</code> if the sprites duration time has expired
	 *     & should be removed from the screen.
	 */
	abstract draw(ctx: CanvasRenderingContext2D): boolean;

	/**
	 * Checks if a point or area clips the boundries of this sprite.
	 *
	 * @param x1
	 *     Left-most position of area to check.
	 * @param x2
	 *     Right-most position of area to check.
	 * @param y1
	 *     Top-most position of area to check.
	 * @param y2
	 *     Bottom-most position of area to check.
	 * @return
	 *     <code>true</code> if any point is within the area of the
	 *     sprite.
	 */
	clips(x1: number, x2: number, y1: number, y2: number): boolean {
		const r = this.x + this.width;
		const b = this.y + this.height;

		return (this.x <= x1 && x1 <= r && this.x <= x2 && x2 <= r)
			&& (this.y <= y1 && y1 <= b && this.y <= y2 && y2 <= b);
	}

	/**
	 * Checks if a point clips the boundaries of this sprite.
	 *
	 * @param x
	 *     Horizonal coordinate.
	 * @param y
	 *     Vertical coordinate.
	 * @return
	 *     <code>true</code> if the point is within the area of the
	 *     sprite.
	 */
	clipsPoint(x: number, y: number) {
		return this.clips(x, x, y, y);
	}

	onClick(evt: MouseEvent) {
		const screenRect = document.getElementById("gamewindow")!
				.getBoundingClientRect();
		const pointX = evt.clientX - screenRect.x
				+ stendhal.ui.gamewindow.offsetX;
		const pointY = evt.clientY - screenRect.y
				+ stendhal.ui.gamewindow.offsetY + 15;

		if (this.clipsPoint(pointX, pointY)) {
			evt.stopPropagation();
			stendhal.ui.gamewindow.removeTextBubble(this, pointX, pointY);
		}
	}

	/**
	 * Action to execute when the sprite is removed.
	 */
	onRemoved() {
		if (typeof(this.onRemovedAction) !== "undefined") {
			this.onRemovedAction();
		}
	}

	/**
	 * Sets the base duration that the sprite should be displayed on the
	 * screen.
	 *
	 * @param dur
	 *     New duration time.
	 */
	setBaseDuration(dur: number) {
		this.baseDuration = dur;
	}

	/**
	 * Checks if the sprite duration time has expired & should be
	 * removed from the screen.
	 *
	 * @return
	 *     <code>true</code> if the duration time has been exceeded.
	 */
	expired(): boolean {
		return Date.now() > this.timeStamp + this.baseDuration + 20
				* this.text.length;
	}
}
