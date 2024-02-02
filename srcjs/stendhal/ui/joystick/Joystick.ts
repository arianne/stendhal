/***************************************************************************
 *                 Copyright © 2023-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { JoystickBase } from "./JoystickBase";
import { Direction } from "../../util/Direction";

declare var stendhal: any;


export class Joystick extends JoystickBase {

	private outer: HTMLImageElement;
	private inner: HTMLImageElement;

	private engaged = false;

	// number of pixels joystick can move before executing event
	private static readonly play_threshold = 24;


	public constructor() {
		super();

		this.outer = new Image();
		this.inner = new Image();

		for (const jimg of [this.outer, this.inner]) {
			jimg.classList.add("joystick-button");
			jimg.style.position = "absolute";
			jimg.draggable = false;

			// listen for activation events
			for (const etype of ["mousedown", "touchstart"]) {
				jimg.addEventListener(etype, (e) => {
					this.onMouseDown(e);
				});
			}
			// note "mouseup" is handled globally in the body element (see Client.ts)
			jimg.addEventListener("touchend", (e) => {
				this.onMouseUp(e);
			});

			for (const etype of ["mousemove", "touchmove"]) {
				// must be added to outer in case movement is too fast to be caught by inner
				jimg.addEventListener(etype, (e) => {
					if (e.type === "mousemove" && !this.engaged) {
						// prevent movement if mouse button not pressed
						return;
					}
					this.onMouseDown(e);
				});
			}
		}

		// update layout after outer image has loaded
		this.outer.onload = () => {
			// remove listener
			this.outer.onload = null;
			this.onInit();
		};
		this.outer.src = Joystick.getResource("joystick_outer");
	}

	private onInit() {
		this.radius = Math.floor(this.outer.width / 2);
		// set position of outer joystick
		this.outer.style.left = (Joystick.getCenterX() - this.radius) + "px";
		this.outer.style.top = (Joystick.getCenterY() - this.radius) + "px";
		this.reset();

		// add to DOM
		const container = document.getElementById("joystick-container")!;
		container.appendChild(this.outer);
		container.appendChild(this.inner);
	}

	private onMouseDown(e: Event) {
		if (!Joystick.checkActionEvent(e)) {
			return;
		}
		if (!this.engaged) {
			this.engaged = true;
			this.inner.src = Joystick.getResource("joystick_inner_active");
		}

		const pos = stendhal.ui.html.extractPosition(e);

		// update inner button position
		this.updateInner(pos.pageX, pos.pageY);

		const new_direction = this.getPressedDirection();
		if (new_direction != this.direction) {
			this.onDirectionChange(new_direction);
		}
	}

	private onMouseUp(e: Event) {
		if (!Joystick.checkActionEvent(e)) {
			return;
		}
		this.reset();
	}

	/**
	 * Retrieves the interpreted direction of joystick's state.
	 */
	private getPressedDirection(): Direction {
		let dir = Direction.STOP;
		// FIXME: need a smarter algorithm for detecting direction based on position of inner circle
		//        relative to center of outer
		const rect = this.inner.getBoundingClientRect();
		const oCenterX = Joystick.getCenterX(), oCenterY = Joystick.getCenterY();
		const iCenterX = rect.left + Math.floor(this.inner.width / 2);
		const iCenterY = rect.top + Math.floor(this.inner.height / 2);
		if (iCenterX < oCenterX - Joystick.play_threshold) {
			dir = Direction.LEFT;
		} else if (iCenterX > oCenterX + Joystick.play_threshold) {
			dir = Direction.RIGHT;
		} else if (iCenterY < oCenterY - Joystick.play_threshold) {
			dir = Direction.UP;
		} else if (iCenterY > oCenterY + Joystick.play_threshold) {
			dir = Direction.DOWN;
		}
		return dir;
	}

	/**
	 * Updates position of inner button.
	 */
	private updateInner(x: number, y: number) {
		// FIXME: need a smarter algorithm for detecting if center of inner circle is beyond radius
		//        of outer
		const bounds = this.outer.getBoundingClientRect();
		const xdiff = Math.abs(x - (bounds.left + this.radius));
		const ydiff = Math.abs(y - (bounds.top + this.radius));
		// don't allow inner button to move beyond radius of outer
		if (xdiff <= this.radius) {
			this.inner.style.left = (x - Math.floor(this.inner.width / 2)) + "px";
		}
		if (ydiff <= this.radius) {
			this.inner.style.top = (y - Math.floor(this.inner.height / 2)) + "px";
		}
	}

	public override reset(): void {
		this.engaged = false;
		this.inner.onload = () => {
			// remove listener
			this.inner.onload = null;
			// center inner joystick on outer
			const rect = this.outer.getBoundingClientRect();
			this.updateInner(rect.left + this.radius, rect.top + this.radius);
		};
		this.inner.src = Joystick.getResource("joystick_inner");

		if (this.direction != Direction.STOP) {
			// stop movement
			this.onDirectionChange(Direction.STOP);
		}
	}

	public override onRemoved(): void {
		// remove from DOM
		const container = document.getElementById("joystick-container")!;
		for (const jimg of [this.outer, this.inner]) {
			if (container.contains(jimg)) {
				container.removeChild(jimg);
			}
		}
	}
}
