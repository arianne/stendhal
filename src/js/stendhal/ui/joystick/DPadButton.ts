/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { DirectionPad } from "./DirectionPad";
import { Direction } from "../../util/Direction";


/**
 * Button class representing a single direction of movement.
 */
export class DPadButton {

	/** Direction this button represents. */
	public readonly direction: Direction;
	/** Image element that is added to DOM. */
	public readonly element: HTMLImageElement;
	/** Property denoting button's state of engagement. */
	private engaged = false;


	/**
	 * Creates a new button.
	 *
	 * @param dir {util.Direction.Direction}
	 *   Direction to be represented.
	 */
	constructor(dir: Direction) {
		this.direction = dir;
		this.element = new Image();
		this.element.classList.add("joystick-button", "hidden");
		this.element.draggable = false;
		// TODO: move these to CSS
		this.element.style.position = "absolute";
		// this is needed to make changing direction on touch-move work
		this.element.style.touchAction = "none";

		// set amount of rotation to be used on displayed image
		let rotation = 0;
		switch (dir) {
			case Direction.DOWN:
				rotation = 180;
				break;
			case Direction.LEFT:
				rotation = -90;
				break;
			case Direction.RIGHT:
				rotation = 90;
				break;
		}
		if (rotation) {
			this.element.style["transform"] = "rotate(" + rotation + "deg)";
		}

		// event listeners

		// listen for click/touch start events to initialize engagement
		this.element.addEventListener("pointerdown", (e: PointerEvent) => {
			if (e.target) {
				// release pointer capture for touch events
				(e.target as Element).releasePointerCapture(e.pointerId);
			}
			DirectionPad.get().onButtonEngaged(e, this);
		});
		// listen for touch end events to end engagement
		// NOTE: "mouseup" is handled globally in the body element (see Client.Client.registerBrowserEventHandlers)
		this.element.addEventListener("touchend", (e: Event) => {
			DirectionPad.get().onButtonDisengaged(e);
		});
		// listen for mouse/touch movement to detect dragging
		this.element.addEventListener("pointerenter", (e: Event) => {
			DirectionPad.get().onDragWhileEngaged(this);
		});

		// initialize in disengaged state
		this.setEngaged(false);
	}

	/**
	 * Updates button positioning.
	 *
	 * @param radius {number}
	 *   Overall radius of direction pad.
	 * @param centerX {number}
	 *   Overall center position of direction pad on X axis.
	 * @param centerY {number}
	 *   Overall center position of direction pad on Y axis.
	 */
	private updateInternal(radius: number, centerX: number, centerY: number) {
		const halfWidth = Math.floor(this.element.width / 2);
		const halfHeight = Math.floor(this.element.height / 2);
		switch (this.direction) {
			case Direction.UP:
				this.element.style.left = (centerX - halfWidth) + "px";
				this.element.style.top = (centerY - radius) + "px";
				break;
			case Direction.DOWN:
				this.element.style.left = (centerX - halfWidth) + "px";
				this.element.style.top = (centerY + radius - this.element.height) + "px";
				break;
			case Direction.LEFT:
				this.element.style.left = (centerX - radius) + "px";
				this.element.style.top = (centerY - halfHeight) + "px";
				break;
			case Direction.RIGHT:
				this.element.style.left = (centerX + radius - this.element.width) + "px";
				this.element.style.top = (centerY - halfHeight) + "px";
				break;
		}
	}

	/**
	 * Updates button positioning.
	 *
	 * @param radius {number}
	 *   Overall radius of direction pad.
	 * @param centerX {number}
	 *   Overall center position of direction pad on X axis.
	 * @param centerY {number}
	 *   Overall center position of direction pad on Y axis.
	 */
	public update(radius: number, centerX: number, centerY: number) {
		if (this.element.complete) {
			this.updateInternal(radius, centerX, centerY);
			return;
		}
		this.element.onload = () => {
			// remove listener
			this.element.onload = null;
			this.updateInternal(radius, centerX, centerY);
		};
	}

	/**
	 * Sets engagement property and updates displayed image.
	 *
	 * @param engaged {boolean}
	 *   New state of engagement.
	 */
	private setEngaged(engaged: boolean) {
		this.engaged = engaged;
		this.element.src = DirectionPad.getResource(engaged ? "dpad_button_active" : "dpad_button");
	}

	/**
	 * Called when button engagement starts and updates image.
	 */
	public onEngaged() {
		if (this.engaged) {
			return;
		}
		this.setEngaged(true);
	}

	/**
	 * Called when button engagement ends and updates image.
	 */
	public onDisengaged() {
		if (!this.engaged) {
			return;
		}
		this.setEngaged(false);
	}
}
