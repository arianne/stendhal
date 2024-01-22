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


export class DirectionPad extends JoystickBase {

	private static instance?: DirectionPad;

	private up: DPadButton;
	private down: DPadButton;
	private left: DPadButton;
	private right: DPadButton;

	private readonly buttons: DPadButton[];


	public static get(): DirectionPad|undefined {
		return DirectionPad.instance;
	}

	public constructor() {
		super();
		DirectionPad.instance = this;

		// create a temporary image to set radius
		const tmp = new Image();
		tmp.onload = () => {
			// radius must be larger than button size as we cannot detect transparent pixels
			this.radius = tmp.width * 1.25;
			this.onInit();
		};
		tmp.src = DirectionPad.getResource("dpad_button");

		this.up = new DPadButton(Direction.UP);
		this.down = new DPadButton(Direction.DOWN);
		this.left = new DPadButton(Direction.LEFT);
		this.right = new DPadButton(Direction.RIGHT);

		this.buttons = [this.up, this.down, this.left, this.right];
	}

	private onInit() {
		const centerX = DirectionPad.getCenterX();
		const centerY = DirectionPad.getCenterY();
		for (const button of this.buttons) {
			button.add(this.radius, centerX, centerY);
		}

		this.reset();
	}

	public onMouseDown(e: Event) {
		if (!DirectionPad.checkActionEvent(e)) {
			return;
		}

		let button;
		switch (e.target) {
			case this.up.image:
				button = this.up;
				break;
			case this.right.image:
				button = this.right;
				break;
			case this.down.image:
				button = this.down;
				break;
			case this.left.image:
				button = this.left;
				break;
		}
		let newDirection = Direction.STOP;
		if (button) {
			newDirection = button.direction;
			button.engage();
		}
		if (newDirection != this.direction) {
			this.onDirectionChange(newDirection);
		}
	}

	public onMouseUp(e: Event) {
		if (!DirectionPad.checkActionEvent(e)) {
			return;
		}
		this.reset();
	}

	public onDragWhileEngaged(button: DPadButton) {
		if (!this.isEngaged()) {
			return;
		}
		if (button.direction != this.direction) {
			this.disengageAll();
			button.engage();
			this.onDirectionChange(button.direction);
		}
	}

	public isEngaged(): boolean {
		return this.direction != Direction.STOP;
	}

	private disengageAll() {
		for (const button of this.buttons) {
			button.disengage();
		}
	}

	public override reset() {
		this.disengageAll();
		if (this.direction != Direction.STOP) {
			// stop movement
			this.queueStop();
		}
	}

	public override onRemoved() {
		// FIXME: buttons not always removed from DOM
		for (const button of this.buttons) {
			button.remove();
		}
		DirectionPad.instance = undefined;
	}
}


class DPadButton {

	public readonly direction: Direction;
	public readonly image: HTMLImageElement;
	private rotation?: number;


	constructor(dir: Direction) {
		this.direction = dir;
		this.image = new Image();
		this.image.classList.add("joystick-button");

		switch (this.direction) {
			case Direction.DOWN:
				this.rotation = 180;
				break;
			case Direction.LEFT:
				this.rotation = -90;
				break;
			case Direction.RIGHT:
				this.rotation = 90;
				break;
		}
	}

	public add(radius: number, centerX: number, centerY: number) {
		const container = document.getElementById("joystick-container")!;

		this.image.onload = () => {
			// remove listener
			this.image.onload = null;
			// add to DOM
			container.appendChild(this.image);
			this.image.style.position = "absolute";
			this.image.draggable = false;
			// this is needed to make changing direction on touch-move work
			this.image.style.touchAction = "none";

			// positioning
			switch (this.direction) {
				case Direction.UP:
					this.image.style.left = (centerX - (this.image.width / 2)) + "px";
					this.image.style.top = (centerY - radius) + "px";
					break;
				case Direction.DOWN:
					this.image.style.left = (centerX - (this.image.width / 2)) + "px";
					this.image.style.top = (centerY + radius - this.image.height) + "px";
					break;
				case Direction.LEFT:
					this.image.style.left = (centerX - radius) + "px";
					this.image.style.top = (centerY - (this.image.height / 2)) + "px";
					break;
				case Direction.RIGHT:
					this.image.style.left = (centerX + radius - this.image.width) + "px";
					this.image.style.top = (centerY - (this.image.height / 2)) + "px";
					break;
			}

			this.image.addEventListener("pointerdown", (e: PointerEvent) => {
				if (e.target) {
					// release pointer capture for touch events
					(e.target as Element).releasePointerCapture(e.pointerId);
				}
				DirectionPad.get()!.onMouseDown(e);
			});
			// note "mouseup" is handled globally in the body element (see Client.ts)
			this.image.addEventListener("touchend", (e: Event) => {
				DirectionPad.get()!.onMouseUp(e);
			});
			this.image.addEventListener("pointerenter", (e: Event) => {
				DirectionPad.get()!.onDragWhileEngaged(this);
			});
		};
	}

	public remove() {
		const container = document.getElementById("joystick-container")!;
		if (container.contains(this.image)) {
			// remove from DOM
			container.removeChild(this.image);
		}
	}

	public engage() {
		// update button image
		this.image.src = DirectionPad.getResource("dpad_button_active");
		if (this.rotation) {
			this.image.style["transform"] = "rotate(" + this.rotation + "deg)";
		}
	}

	public disengage() {
		this.image.src = DirectionPad.getResource("dpad_button");
		if (this.rotation) {
			this.image.style["transform"] = "rotate(" + this.rotation + "deg)";
		}
	}
}
