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


export class DirectionPad extends JoystickBase {

	private up: HTMLImageElement;
	private down: HTMLImageElement;
	private left: HTMLImageElement;
	private right: HTMLImageElement;


	public constructor() {
		super();

		// create a temporary image to set radius
		const tmp = new Image();
		tmp.onload = () => {
			// radius must be larger than button size as we cannot detect transparent pixels
			this.radius = tmp.width * 1.25;
			this.onInit();
		};
		tmp.src = this.getResource("dpad_button");

		this.up = new Image();
		this.down = new Image();
		this.left = new Image();
		this.right = new Image();
	}

	private onInit() {
		const center_x = this.getCenterX();
		const center_y = this.getCenterY();

		const container = document.getElementById("joystick-container")!;

		// positioning
		this.up.onload = () => {
			this.up.style.left = (center_x - (this.up.width / 2)) + "px";
			this.up.style.top = (center_y - this.radius) + "px";
			// add to DOM
			container.appendChild(this.up);
			// remove listener
			this.up.onload = null;
		};
		this.down.onload = () => {
			this.down.style.left = (center_x - (this.down.width / 2)) + "px";
			this.down.style.top = (center_y + this.radius - this.down.height) + "px";
			// add to DOM
			container.appendChild(this.down);
			// remove listener
			this.down.onload = null;
		};
		this.left.onload = () => {
			this.left.style.left = (center_x - this.radius) + "px";
			this.left.style.top = (center_y - (this.left.height / 2)) + "px";
			// add to DOM
			container.appendChild(this.left);
			// remove listener
			this.left.onload = null;
		};
		this.right.onload = () => {
			this.right.style.left = (center_x + this.radius - this.right.width) + "px";
			this.right.style.top = (center_y - (this.right.height / 2)) + "px";
			// add to DOM
			container.appendChild(this.right);
			// remove listener
			this.right.onload = null;
		};

		for (const dimg of [this.up, this.down, this.left, this.right]) {
			dimg.style.position = "absolute";
			dimg.draggable = false;
			dimg.addEventListener("mousedown", (e: Event) => {
				this.onMouseDown(e);
			});
			dimg.addEventListener("touchstart", (e: Event) => {
				this.onMouseDown(e);
			})
			// note "mouseup" is handled globally in the body element (see Client.ts)
			dimg.addEventListener("touchend", (e: Event) => {
				this.onMouseUp(e);
			})
		}

		this.reset();
	}

	private onMouseDown(e: Event) {
		if (!this.checkActionEvent(e)) {
			return;
		}

		let new_direction = 0;
		switch(e.target) {
			case this.up:
				this.up.src = this.getResource("dpad_button_active");
				new_direction = 1;
				break;
			case this.right:
				this.right.src = this.getResource("dpad_button_active");
				this.right.style["transform"] = "rotate(90deg)";
				new_direction = 2;
				break;
			case this.down:
				this.down.src = this.getResource("dpad_button_active");
				this.down.style["transform"] = "rotate(180deg)";
				new_direction = 3;
				break;
			case this.left:
				this.left.src = this.getResource("dpad_button_active");
				this.left.style["transform"] = "rotate(-90deg)";
				new_direction = 4;
				break;
		}
		if (new_direction != this.direction) {
			this.onDirectionChange(new_direction);
		}
	}

	private onMouseUp(e: Event) {
		if (!this.checkActionEvent(e)) {
			return;
		}
		this.reset();
	}

	public override reset() {
		// reset images
		this.up.src = this.getResource("dpad_button");
		this.down.src = this.getResource("dpad_button");
		this.down.style["transform"] = "rotate(180deg)";
		this.left.src = this.getResource("dpad_button");
		this.left.style["transform"] = "rotate(-90deg)";
		this.right.src = this.getResource("dpad_button");
		this.right.style["transform"] = "rotate(90deg)";

		if (this.direction != 0) {
			// stop movement
			this.onDirectionChange(0);
		}
	}

	public override onRemoved() {
		// remove from DOM
		const container = document.getElementById("joystick-container")!;
		for (const dimg of [this.up, this.down, this.left, this.right]) {
			if (container.contains(dimg)) {
				container.removeChild(dimg);
			}
		}
	}
}
