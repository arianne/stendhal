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

import { stendhal } from "../../stendhal";

import { JoystickImpl } from "./JoystickImpl";
import { AngleRange } from "../../util/AngleRange";
import { Direction } from "../../util/Direction";
import { MathUtil } from "../../util/MathUtil";
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

	/** Secondary direction of movement. */
	private secondaryDir = 0;

	/** Difference in pixels of inner button center relative to outer button center before a move
	 *  event will be executed. */
	private readonly playThreshold = 24;

	/**
	 * Angles representing directions of movement.
	 *
	 * NOTE: currently supports only 4 sectors but should be updated to represent sectors as laid out
	 *       in https://github.com/arianne/stendhal/issues/608#issuecomment-1872485986
	 */
	private readonly sectors: {[key: string]: Pair<number, AngleRange>[]} = {
		primary: [
			// NOTE: 0 degrees represents right on joystick plane
			new Pair(Direction.UP.val, new AngleRange(225, 314.9)),
			new Pair(Direction.RIGHT.val, new AngleRange(315, 44.9)),
			new Pair(Direction.DOWN.val, new AngleRange(45, 134.9)),
			new Pair(Direction.LEFT.val, new AngleRange(135, 224.9))
		],
		secondary: []
	};

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

		// initialize secondary movement ranges
		// all sectors should be equal size
		const ssize = this.sectors.primary[0].second.getSize() / 3;
		for (const p of this.sectors.primary) {
			const rangeCC = new AngleRange(p.second.min, p.second.min + ssize);
			const rangeC = new AngleRange(p.second.max - ssize, p.second.max);
			this.sectors.secondary.push(new Pair(p.first + .1, rangeCC));
			this.sectors.secondary.push(new Pair(p.first + .2, rangeC));
		}

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
		const centerX = Joystick.getCenterX();
		const centerY = Joystick.getCenterY();
		this.outer.style.left = (centerX - this.radius) + "px";
		this.outer.style.top = (centerY - this.radius) + "px";
		// we could call `updateInner` but there is no need for calling checks to keep inside radius here
		this.inner.style.left = (centerX - Math.floor(this.inner.width / 2)) + "px";
		this.inner.style.top = (centerY - Math.floor(this.inner.height / 2)) + "px";
	}

	/**
	 * Resets inner buttons to disengaged state & stops movement.
	 */
	public override reset() {
		// update engaged state
		super.reset();
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
			const rad = MathUtil.pointToRad(relX, relY);
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
	protected override onEngaged(e: Event): boolean {
		if (!super.onEngaged(e)) {
			return false;
		}
		this.inner.src = Joystick.getResource("joystick_inner_active");
		this.onDragWhileEngaged(e);
		return this.engaged;
	}

	/**
	 * Called when secondary direction should be updated.
	 *
	 * @param dir {number}
	 *   New secondary direction.
	 */
	private onSecondaryDirChange(dir: number) {
		this.secondaryDir = dir;
		// TODO: send event to set secondary direction server side.
	}

	/**
	 * Called when drag gesture is detected to update movement and buttons.
	 *
	 * @param e {Event}
	 *   Event denoting an update to joystick state.
	 */
	public onDragWhileEngaged(e: Event) {
		if (!this.engaged) {
			// prevent movement if joystick is not engaged
			return;
		}

		// update inner button position
		const pos = stendhal.ui.html.extractPosition(e);
		this.updateInner(pos.pageX, pos.pageY);

		// set new direction of movement
		let sec = this.getPressedDirection();
		const pri = Math.floor(sec);
		if (pri != this.direction.val) {
			if (pri == Direction.STOP.val) {
				this.queueStop();
			} else {
				this.onDirectionChange(Direction.VALUES[pri]);
			}
		}
		sec = sec - pri == 0 ? 0 : sec;
		if (sec != this.secondaryDir) {
			this.onSecondaryDirChange(sec);
		}
	}

	/**
	 * Retrieves the interpreted direction of joystick.
	 *
	 * @return {number}
	 *   Direction of movement the joystick's state represents.
	 */
	private getPressedDirection(): number {
		const irect = this.inner.getBoundingClientRect();
		const ix = irect.left + Math.floor(this.inner.width / 2) - Joystick.getCenterX();
		const iy = irect.top + Math.floor(this.inner.height / 2) - Joystick.getCenterY();
		if (this.inDeadZone(ix, iy)) {
			return Direction.STOP.val;
		}
		const angle = MathUtil.pointToDeg(ix, iy);
		for (const pair of this.sectors.secondary) {
			if (pair.second.contains(angle)) {
				return pair.first;
			}
		}
		for (const pair of this.sectors.primary) {
			if (pair.second.contains(angle)) {
				return pair.first;
			}
		}
		return Direction.STOP.val;
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
