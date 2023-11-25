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

declare var marauroa: any;


export class DirectionPad extends JoystickBase {

	private up: HTMLImageElement;
	private down: HTMLImageElement;
	private left: HTMLImageElement;
	private right: HTMLImageElement;

	private readonly radius = 70;


	public constructor() {
		super();

		this.up = new Image();
		this.down = new Image();
		this.left = new Image();
		this.right = new Image();

		const center_x = this.getCenterX();
		const center_y = this.getCenterY();

		// positioning
		this.up.onload = () => {
			this.up.style.left = (center_x - (this.up.width / 2)) + "px";
			this.up.style.top = (center_y - this.radius) + "px";
			// add to DOM
			document.body.appendChild(this.up);
			// remove listener
			this.up.onload = null;
		};
		this.down.onload = () => {
			this.down.style.left = (center_x - (this.down.width / 2)) + "px";
			this.down.style.top = (center_y + this.radius - this.down.height) + "px";
			// add to DOM
			document.body.appendChild(this.down);
			// remove listener
			this.down.onload = null;
		};
		this.left.onload = () => {
			this.left.style.left = (center_x - this.radius) + "px";
			this.left.style.top = (center_y - (this.left.height / 2)) + "px";
			// add to DOM
			document.body.appendChild(this.left);
			// remove listener
			this.left.onload = null;
		};
		this.right.onload = () => {
			this.right.style.left = (center_x + this.radius - this.right.width) + "px";
			this.right.style.top = (center_y - (this.right.height / 2)) + "px";
			// add to DOM
			document.body.appendChild(this.right);
			// remove listener
			this.right.onload = null;
		};

		for (const dimg of [this.up, this.down, this.left, this.right]) {
			dimg.style.position = "absolute";
			dimg.draggable = false;
			dimg.addEventListener("mousedown", (e: Event) => {
				this.onMouseDown(e);
			});
			dimg.addEventListener("mouseup", (e: Event) => {
				this.onMouseUp(e);
			});
		}

		this.reset();
	}

	private onMouseDown(e: Event) {
		if (e instanceof MouseEvent && e.button == 0) {
			let action: {[index: string]: string} = {type: "move"};
			switch(e.target) {
				case this.up:
					this.up.src = this.getResource("dpad_arrow_up_active");
					action.dir = "1";
					break;
				case this.right:
					this.right.src = this.getResource("dpad_arrow_right_active");
					action.dir = "2";
					break;
				case this.down:
					this.down.src = this.getResource("dpad_arrow_down_active");
					action.dir = "3";
					break;
				case this.left:
					this.left.src = this.getResource("dpad_arrow_left_active");
					action.dir = "4";
					break;
			}
			if (typeof(action.dir) !== "undefined") {
				marauroa.clientFramework.sendAction(action);
			}
		}
	}

	private onMouseUp(e: Event) {
		if (e instanceof MouseEvent && e.button == 0) {
			this.reset();
			marauroa.clientFramework.sendAction({type: "stop"});
		}
	}

	public override reset() {
		// reset images
		this.up.src = this.getResource("dpad_arrow_up");
		this.down.src = this.getResource("dpad_arrow_down");
		this.left.src = this.getResource("dpad_arrow_left");
		this.right.src = this.getResource("dpad_arrow_right");
	}

	public override onRemoved() {
		// remove from DOM
		for (const dimg of [this.up, this.down, this.left, this.right]) {
			if (document.body.contains(dimg)) {
				document.body.removeChild(dimg);
			}
		}
	}
}
