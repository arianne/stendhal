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

declare var stendhal: any;

import { JoystickImpl } from "./JoystickImpl";
import { AngleRange } from "../../util/AngleRange";
import { Direction } from "../../util/Direction";
import { MathHelper } from "../../util/MathHelper";
import { Pair } from "../../util/Pair";
import { Point } from "../../util/Point";


/**
 * On-screen movement control implementation representing a joystick-like interface.
 *
 * Consists of two elements: An outer static button and an inner interactive button. The outer
 * determines overall joystick positioning and boundaries. The inner is used to control
 * character's walking direction based on positioning of its center relative to center of outer
 * button.
 */
export class Joystick extends JoystickImpl {

	/** Element representing joystick boundaries. */
	private outer: HTMLImageElement;
	/** Element used to determine moving direction of character. */
	private inner: HTMLImageElement;

	/** Difference in pixels of inner button center relative to outer button center before a move
	 *  event will be executed. */
	private readonly playThreshold = 24;

	/** Property denoting joystick is in an engaged state. */
	private engaged = false;

	/**
	 * Angles representing directions of movement.
	 *
	 * NOTE: currently supports only 4 sectors but should be updated to represent sectors as laid out
	 *       in https://github.com/arianne/stendhal/issues/608#issuecomment-1872485986
	 */
	private readonly sectors: Pair<Direction, AngleRange>[] = [
		// NOTE: 0 degrees represents right on joystick plane
		new Pair(Direction.UP, new AngleRange(225, 314.9)),
		new Pair(Direction.RIGHT, new AngleRange(315, 44.9)),
		new Pair(Direction.DOWN, new AngleRange(45, 134.9)),
		new Pair(Direction.LEFT, new AngleRange(135, 224.9))
	];

	/** Singleton instance. */
	private static instance: Joystick;


	/**
	 * Retrieves singleton instance.
	 */
	public static get(): Joystick {
		if (!Joystick.instance) {
			Joystick.instance = new Joystick();
		}
		return Joystick.instance;
	}

	/**
	 * Hidden singleton constructor.
	 *
	 * Creates the HTML elements but does not add them to DOM.
	 */
	private constructor() {
		super();

		this.outer = new Image();
		this.inner = new Image();

		for (const jimg of [this.outer, this.inner]) {
			jimg.classList.add("joystick-button", "hidden");
			jimg.style.position = "absolute";
			jimg.draggable = false;

			// listen for click/touch start events to initialize engagement
			// NOTE: should this use "pointerdown" like DirectionPad/DPadButton?
			for (const etype of ["mousedown", "touchstart"]) {
				jimg.addEventListener(etype, (e) => {
					this.onEngaged(e);
				});
			}
			// listen for touch end events to end engagement
			// NOTE: "mouseup" is handled globally in the body element (see Client.Client.registerBrowserEventHandlers)
			jimg.addEventListener("touchend", (e) => {
				this.onDisengaged(e);
			});
			// listen for mouse/touch movement to detect dragging
			// NOTE: should this use "pointermove"?
			for (const etype of ["mousemove", "touchmove"]) {
				// must be added to outer in case movement is too fast to be caught by inner
				jimg.addEventListener(etype, (e) => {
					this.onDragWhileEngaged(e);
				});
			}
		}

		// configure radius and inner button after outer button image has loaded
		this.outer.onload = () => {
			// remove listener
			this.outer.onload = null;
			// radius is based on dimensions of outer button
			this.radius = Math.floor(this.outer.width / 2);
			this.onOuterReady();
		};
		this.outer.src = Joystick.getResource("joystick_outer");
	}

	/**
	 * Loads inner button image and finalizes initialization.
	 */
	private onOuterReady() {
		this.inner.onload = () => {
			// remove listener
			this.inner.onload = null;
			// center inner joystick on outer
			const rect = this.outer.getBoundingClientRect();
			this.updateInner(rect.left + this.radius, rect.top + this.radius);
			this.onReady();
		};
		this.inner.src = Joystick.getResource("joystick_inner");
	}

	/**
	 * Retrieves an array of the elements associated with this joystick.
	 *
	 * @return {HTMLImageElement[]}
	 *   Image elements representing outer and inner buttons.
	 */
	protected override getElements(): HTMLImageElement[] {
		return [this.outer, this.inner];
	}

	/**
	 * Updates joystick positioning based on configured center X & Y coordinates.
	 */
	public override update() {
		this.outer.style.left = (Joystick.getCenterX() - this.radius) + "px";
		this.outer.style.top = (Joystick.getCenterY() - this.radius) + "px";
	}

	/**
	 * Resets all buttons to a state of disengaged.
	 */
	public override reset() {
		// update engaged state
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
			// immediately stop movement
			this.onDirectionChange(Direction.STOP);
		}
	}

	/**
	 * Updates position of inner button.
	 *
	 * @param x {number}
	 *   Positioning center on X axis.
	 * @param y {number}
	 *   Positioning center on Y axis.
	 */
	private updateInner(x: number, y: number) {
		const pos = this.keepInside(x, y);
		this.inner.style.left = (pos.x - Math.floor(this.inner.width / 2)) + "px";
		this.inner.style.top = (pos.y - Math.floor(this.inner.height / 2)) + "px";
	}

	/**
	 * Restricts movement of inner button to joystick radial boundaries.
	 *
	 * Reference: https://stackoverflow.com/a/8528999/4677917
	 *
	 * @param x {number}
	 *   Absolute coordinate on X axis.
	 * @param y {number}
	 *   Absolute coordinate on Y axis.
	 * @return {util.Point.Point}
	 *   Adjusted positioning.
	 */
	private keepInside(x: number, y: number): Point {
		const bounds = this.outer.getBoundingClientRect();
		const cx = bounds.left + this.radius;
		const cy = bounds.top + this.radius;
		const relX = x - cx;
		const relY = y - cy;
		if (this.outside(relX, relY)) {
			const rad = MathHelper.pointToRad(relX, relY);
			x = Math.cos(rad) * this.radius + cx;
			y = Math.sin(rad) * this.radius + cy;
		}
		return new Point(x, y);
	}

	/**
	 * Called when joystick engagement starts in order to determine direction of movement and update
	 * inner button.
	 *
	 * @param e {Event}
	 *   Event to be validated.
	 */
	private onEngaged(e: Event) {
		if (!Joystick.checkActionEvent(e)) {
			return;
		}

		// update engaged state
		this.engaged = true;
		this.inner.src = Joystick.getResource("joystick_inner_active");
		this.onDragWhileEngaged(e);
	}

	/**
	 * Called when drag gesture is detected to update movement and buttons.
	 *
	 * @param e {Event}
	 *   Event denoting an update to joystick state.
	 */
	public onDragWhileEngaged(e: Event) {
		if (!this.isEngaged()) {
			// prevent movement if joystick is not engaged
			return;
		}

		// update inner button position
		const pos = stendhal.ui.html.extractPosition(e);
		this.updateInner(pos.pageX, pos.pageY);

		// set new direction of movement
		const new_direction = this.getPressedDirection();
		if (new_direction != this.direction) {
			this.onDirectionChange(new_direction);
		}
	}

	/**
	 * Checks joystick's state of engagement.
	 *
	 * This is overridden because joystick interface can be considered engaged even when player is
	 * not moving.
	 *
	 * @return {boolean}
	 *   `true` if considered to be engaged.
	 */
	protected override isEngaged(): boolean {
		return this.engaged;
	}

	/**
	 * Retrieves the interpreted direction of joystick.
	 *
	 * @return {util.Direction}
	 *   Facing direction the joystick's state represents.
	 */
	private getPressedDirection(): Direction {
		const irect = this.inner.getBoundingClientRect();
		const ix = irect.left + Math.floor(this.inner.width / 2) - Joystick.getCenterX();
		const iy = irect.top + Math.floor(this.inner.height / 2) - Joystick.getCenterY();
		if (this.inDeadZone(ix, iy)) {
			return Direction.STOP;
		}
		const angle = MathHelper.pointToDeg(ix, iy);
		for (const pair of this.sectors) {
			if (pair.second.contains(angle)) {
				return pair.first;
			}
		}
		return Direction.STOP;
	}

	/**
	 * Checks if a position is inside dead zone area.
	 *
	 * @param relX {number}
	 *   Coordinate on X axis relative to joystick center.
	 * @param relY {number}
	 *   Coordinate on Y axis relative to joystick center.
	 * @return {boolean}
	 *   `true` if position is not in an area of joystick denoting character movement.
	 */
	private inDeadZone(relX: number, relY: number): boolean {
		return Math.pow(Math.abs(relX), 2) + Math.pow(Math.abs(relY), 2) <= Math.pow(this.playThreshold, 2);
	}

	/**
	 * Checks if a position extends beyond radial limit.
	 *
	 * @param relX {number}
	 *   Coordinate on X axis relative to joystick center.
	 * @param relY {number}
	 *   Coordinate on Y axis relative to joystick center.
	 * @return {boolean}
	 *   `true` if position is beyond radius.
	 */
	private outside(relX: number, relY: number): boolean {
		return Math.pow(Math.abs(relX), 2) + Math.pow(Math.abs(relY), 2) >= Math.pow(this.radius, 2);
	}
}
