/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { JoystickBase } from "./JoystickBase";


export class Joystick extends JoystickBase {

	private outer: HTMLImageElement;
	private inner: HTMLImageElement;

	private radius = 0;


	public constructor() {
		super();

		// TODO: make smaller joystick image
		this.outer = new Image();
		this.inner = new Image();

		for (const jimg of [this.outer, this.inner]) {
			jimg.style.position = "absolute";
			jimg.draggable = false;

			// add to DOM
			document.body.appendChild(jimg);

			// listen for mouse events
			jimg.addEventListener("mousedown", (e) => {
				this.onMouseDown(e);
			});
			jimg.addEventListener("mouseup", (e) => {
				this.onMouseUp(e);
			});

			// update layout after all images have loaded
			jimg.onload = () => {
				if (this.outer.complete && this.inner.complete) {
					this.onInit();
					// remove listener
					this.outer.onload = null;
					this.inner.onload = null;
				}
			}
		}

		this.outer.src = this.getResource("joystick_outer");
		this.inner.src = this.getResource("joystick_inner");
	}

	private onInit() {
		this.radius = Math.floor(this.outer.width / 2);
		// set position of outer joystick
		this.outer.style.left = (this.getCenterX() - this.radius) + "px";
		this.outer.style.top = (this.getCenterY() - this.radius) + "px";
		this.center();
	}

	private center() {
		const rect = this.outer.getBoundingClientRect();
		this.inner.style.left = (rect.left + this.radius - Math.floor(this.inner.width / 2)) + "px";
		this.inner.style.top = (rect.top + this.radius - Math.floor(this.inner.height / 2)) + "px";
	}

	private onMouseDown(e: Event) {
		this.inner.src = this.getResource("joystick_inner_active");
	}

	private onMouseUp(e: Event) {
		this.inner.src = this.getResource("joystick_inner");
		this.center();
	}

	public override onRemoved(): void {
		// remove from DOM
		for (const jimg of [this.outer, this.inner]) {
			if (document.body.contains(jimg)) {
				document.body.removeChild(jimg);
			}
		}
	}
}
