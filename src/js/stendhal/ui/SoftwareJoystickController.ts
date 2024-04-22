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

import { ui } from "./UI";
import { UIComponentEnum } from "./UIComponentEnum";

import { DirectionPad } from "./joystick/DirectionPad";
import { Joystick } from "./joystick/Joystick";
import { JoystickImpl } from "./joystick/JoystickImpl";

import { QuickMenuButton } from "./quickmenu/QuickMenuButton";

import { ConfigManager } from "../util/ConfigManager";
import { SessionManager } from "../util/SessionManager";


/**
 * Manages the on-screen joystick.
 */
export class SoftwareJoystickController {

	/** Active on-screen joystck. */
	private joystick?: JoystickImpl;

	/** Denotes state of event handlers initialization. */
	private static registered = false;

	/** Singleton instance. */
	private static instance: SoftwareJoystickController;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): SoftwareJoystickController {
		if (!SoftwareJoystickController.instance) {
			SoftwareJoystickController.instance = new SoftwareJoystickController();
		}
		return SoftwareJoystickController.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Registers handlers for joystick events that occur outside the elements' area.
	 *
	 * TODO: move registration to when joystick is engaged & de-register when disengaged
	 */
	registerGlobalEventHandlers() {
		if (SoftwareJoystickController.registered) {
			console.warn("Tried to re-register software joystick controller event handlers");
			return;
		}
		SoftwareJoystickController.registered = true;

		// handle mouse cursor movement
		document.body.addEventListener("mousemove", (evt: MouseEvent) => {
			// handle updating joystick when mouse moves outside radius
			if (this.joystick && this.joystick.isEngaged()) {
				if (this.joystick instanceof Joystick) {
					(this.joystick as Joystick).onDragWhileEngaged(evt);
				}
				// prevent default action
				// FIXME: doesn't work
				evt.stopPropagation();
				return;
			}
		});

		// handle disengaging joystick when mouse button released outside joystick area
		document.body.addEventListener("mouseup", (evt: MouseEvent) => {
			if (evt.button == 0 && this.joystick && this.joystick.isEngaged()) {
				this.joystick.reset();
				// prevent executing "mouseup" on items & entities
				// FIXME: doesn't work
				evt.stopPropagation();
			}
		});
	}

	/**
	 * Updates the on-screen joystick.
	 */
	update() {
		// update quick menu button image
		const menuButton = ui.get(UIComponentEnum.QMJoystick) as QuickMenuButton;
		if (menuButton) {
			menuButton.update();
		}

		if (!SessionManager.get().joystickEnabled()) {
			this.remove();
			return;
		}
		const newJoystick = ConfigManager.get().get("joystick.style") === "dpad" ? DirectionPad.get()
				: Joystick.get();
		if (this.joystick && newJoystick === this.joystick) {
			this.joystick.update();
		} else {
			this.remove();
			this.joystick = newJoystick;
			newJoystick.addToDOM();
		}
	}

	/**
	 * Removes joystick elements from DOM and unsets `joystick` property.
	 */
	private remove() {
		if (this.joystick) {
			this.joystick.removeFromDOM();
		}
		this.joystick = undefined;
	}

	/**
	 * Checks for an active joystick that is engaged.
	 *
	 * @return {boolean}
	 *   `true` if a joystick is set and is in engaged state.
	 */
	isEngaged(): boolean {
		return this.joystick != undefined && this.joystick.isEngaged();
	}

	/**
	 * Retrieves the current joystick.
	 *
	 * @return {ui.joystick.JoystickImpl.JoystickImpl}
	 *   Joystick instance or `undefined`.
	 */
	getCurrent(): JoystickImpl|undefined {
		return this.joystick;
	}
}
