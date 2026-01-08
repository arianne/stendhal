/***************************************************************************
 *                       Copyright © 2024 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../../stendhal";

import { QuickMenuButton } from "./QuickMenuButton";
import { SoftwareJoystickController } from "../SoftwareJoystickController";
import { UIComponentEnum } from "../UIComponentEnum";


/**
 * Button to toggle on-screen joystick.
 */
export class JoystickButton extends QuickMenuButton {

	constructor() {
		super("joystick", UIComponentEnum.QMJoystick);
	}

	/**
	 * Updates button image.
	 */
	public override update() {
		this.setImageBasename(stendhal.config.getBoolean("joystick") ? "joystick" : "joystick-disabled");
	}

	/**
	 * Toggles joystick visibility when clicked/tapped.
	 */
	protected override onClick(evt: Event) {
		const newState = !stendhal.config.getBoolean("joystick");
		stendhal.config.set("joystick", newState);
		SoftwareJoystickController.get().update();
	}
}
