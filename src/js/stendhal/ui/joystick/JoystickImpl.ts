/***************************************************************************
 *                    Copyright © 2023-2024 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"
import { stendhal } from "../../stendhal";

import { ui } from "../UI";
import { SoftwareJoystickController } from "../SoftwareJoystickController";
import { Direction } from "../../util/Direction";
import { Paths } from "../../data/Paths";


/**
 * On-screen movement control implementation.
 */
export abstract class JoystickImpl {

	/** Most recently executed direction change. */
	protected direction = Direction.STOP;
	/** Pixel radius joystick elements should abide by. */
	protected radius = 0;
	/** Property denoting joystick is in an engaged state. */
	protected engaged = false;

	/** ID used to veto buffered time before sending stop event to server. */
	private stopTimeoutId = 0;


	/**
	 * Determines centering position on the horizontal axis.
	 *
	 * @return {number}
	 *   Joystick center.
	 */
	protected static getCenterX(): number {
		return stendhal.config.getInt("joystick.center.x", 224);
	}

	/**
	 * Determines centering position on the vertical axis.
	 *
	 * @return {number}
	 *   Joystick center.
	 */
	protected static getCenterY(): number {
		return stendhal.config.getInt("joystick.center.y", 384);
	}

	/**
	 * Retrieves joystick image resource path.
	 *
	 * @param basename {string}
	 *   Base filename of image resource.
	 * @return {string}
	 *   Path to image.
	 */
	public static getResource(basename: string): string {
		return Paths.gui + "/joystick/" + basename + ".png";
	}

	/**
	 * Checks if event can be associated with a joystick action.
	 *
	 * @param e {Event}
	 *   Event to be checked.
	 * @return {boolean}
	 *   `true` if event can be applied to joystick.
	 */
	protected static checkActionEvent(e: Event): boolean {
		if (e instanceof MouseEvent && e.button == 0) {
			return true;
		}
		return e instanceof TouchEvent;
	}

	/**
	 * Retrieves an array of the elements associated with this joystick.
	 *
	 * @return {HTMLImageElement[]}
	 *   Joystick elements.
	 */
	protected abstract getElements(): HTMLImageElement[];

	/**
	 * Called when joystick elements' properties should be updated.
	 */
	public abstract update(): void;

	/**
	 * Called when joystick should be set to its default idle state.
	 */
	public reset() {
		// update engaged state
		this.engaged = false;
	}

	/**
	 * Called when joystick engagement starts.
	 *
	 * @param e {Event}
	 *   Event to be validated.
	 * @return {boolean}
	 *   `true` if engagement state set.
	 */
	protected onEngaged(e: Event): boolean {
		if (!JoystickImpl.checkActionEvent(e)) {
			return false;
		}
		// update engaged state
		this.engaged = true;
		return this.engaged;
	}

	/**
	 * Called when joystick engagement ends and character movement should stop.
	 *
	 * @param e {Event}
	 *   Event to be validated.
	 * @return {boolean}
	 *   `true` if engagement state unset.
	 */
	protected onDisengaged(e: Event): boolean {
		if (!JoystickImpl.checkActionEvent(e)) {
			return false;
		}
		this.reset();
		return !this.engaged;
	}

	/**
	 * Checks joystick's state of engagement.
	 *
	 * @return {boolean}
	 *   `true` if considered to be engaged.
	 */
	public isEngaged(): boolean {
		return this.engaged;
	}

	/**
	 * Checks status of all elements.
	 *
	 * @return {boolean}
	 *   `true` if all image elements are loaded.
	 */
	private isReady(): boolean {
		for (const el of this.getElements()) {
			if (!el.complete) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Initializes joystick positioning and makes visible if ready.
	 */
	protected onReady() {
			// update positioning before making visible
		this.update();
		this.reset();
		if (this.isInUse()) {
			const container = this.getContainer();
			for (let idx = 0; idx < container.children.length; idx++) {
				container.children[idx].classList.remove("hidden");
			}
		}
	}

	/**
	 * Adds elements associated with joystick to DOM.
	 */
	public addToDOM() {
		// ensure not re-adding to DOM
		this.removeFromDOM();
		const container = this.getContainer();
		for (const el of this.getElements()) {
			if (!container.contains(el)) {
				container.appendChild(el);
			}
		}
		if (this.isReady()) {
			this.onReady();
		}
	}

	/**
	 * Removes elements associated with joystick from DOM.
	 */
	public removeFromDOM() {
		const container = this.getContainer();
		for (const el of this.getElements()) {
			if (container.contains(el)) {
				container.removeChild(el);
				el.classList.add("hidden");
			}
		}
	}

	/**
	 * Retrieves the parent container where joystick elements are added as children.
	 *
	 * @return {HTMLDivElement}
	 *   Containing div element.
	 */
	private getContainer(): HTMLDivElement {
		return document.getElementById("joystick-container")! as HTMLDivElement;
	}

	/**
	 * Checks if client is currently using this joystick.
	 */
	private isInUse(): boolean {
		return SoftwareJoystickController.get().getCurrent() === this;
	}

	/**
	 * Called when user's character direction should be updated.
	 *
	 * @param dir {util.Direction}
	 *   New direction for character to move or stop.
	 */
	protected onDirectionChange(dir: Direction) {
		if (!ui.isDisplayReady() || !marauroa.me) {
			console.debug("not executing direction change before user ready");
			return;
		}
		this.direction = dir;
		if (this.stopTimeoutId) {
			// new direction pressed before timeout expired
			window.clearTimeout(this.stopTimeoutId);
			this.stopTimeoutId = 0;
		}
		// NOTE: in `ui.joystick.Joystick.Joystick` implementation, dragging inner button into
		//       play/dead zone & back to same direction of movement will cancel auto-walk
		marauroa.me.setDirection(dir, true);
	}

	/**
	 * Called when the user's character should stop moving.
	 *
	 * Executes a timer buffer before sending stop action to server to allow for subsequent facing
	 * direction changes. If the user's facing direction changes before the timer expires it is
	 * vetoed and the stop event is not sent.
	 */
	protected queueStop() {
		// NOTE: `marauroa.me` will be initialized by the time any stop event is queued, so no need
		//       to check
		this.direction = Direction.STOP;
		this.stopTimeoutId = window.setTimeout(() => {
			// new direction not pressed before timeout expired
			marauroa.me.stop();
			this.stopTimeoutId = 0;
		}, 300);
	}
}
