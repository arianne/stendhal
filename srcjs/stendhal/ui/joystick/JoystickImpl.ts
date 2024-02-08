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

declare var marauroa: any;
declare var stendhal: any;

import { ui } from "../UI";
import { Direction } from "../../util/Direction";


/**
 * On-screen movement control implementation.
 */
export abstract class JoystickImpl {

	/** Most recently executed direction change. */
	protected direction = Direction.STOP;
	/** Pixel radius joystick elements should abide by. */
	protected radius = 0;

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
		return stendhal.paths.gui + "/joystick/" + basename + ".png";
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
	public abstract reset(): void;

	/**
	 * Called when joystick engagement ends and character movement should stop.
	 *
	 * This is made public so that `ui.joystick.DPadButton.DPadButton` can access it.
	 *
	 * @param e {Event}
	 *   Event to be validated.
	 */
	public onDisengaged(e: Event) {
		if (!JoystickImpl.checkActionEvent(e)) {
			return;
		}
		this.reset();
	}

	/**
	 * Checks joystick's state of engagement.
	 *
	 * @return {boolean}
	 *   `true` if considered to be engaged.
	 */
	protected isEngaged(): boolean {
		return this.direction != Direction.STOP;
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
		return stendhal.ui.gamewindow.joystick != null && stendhal.ui.gamewindow.joystick === this;
	}

	/**
	 * Called when user's character direction should be updated.
	 *
	 * @param dir {util.Direction}
	 *   New direction character move or stop.
	 */
	protected onDirectionChange(dir: Direction) {
		if (!ui.isDisplayReady()) {
			console.debug("not executing direction change before display is ready");
			return;
		}
		this.direction = dir;
		if (this.direction == Direction.STOP) {
			marauroa.clientFramework.sendAction({type: "stop"});
		} else {
			if (this.stopTimeoutId) {
				clearTimeout(this.stopTimeoutId);
				this.stopTimeoutId = 0;
			}
			marauroa.clientFramework.sendAction({type: "move", dir: ""+this.direction.val});
		}
	}

	/**
	 * Called when the user's character should stop moving.
	 *
	 * Executes a timer buffer before sending stop action to server to allow for subsequent facing
	 * direction changes. If the user's facing direction changes before the timer expires it is
	 * vetoed and the stop event is not sent.
	 */
	protected queueStop() {
		this.direction = Direction.STOP;
		this.stopTimeoutId = setTimeout(() => {
			// new direction not pressed before timeout expired
			marauroa.clientFramework.sendAction({type: "stop"});
			this.stopTimeoutId = 0;
		}, 300);
	}
}
