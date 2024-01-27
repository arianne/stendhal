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

import { QuickMenuButton } from "./QuickMenuButton";

declare var stendhal: any;


/**
 * Button to toggle on-screen joystick.
 */
export class JoystickButton extends QuickMenuButton {

	constructor() {
		super("joystick");
	}

	/**
	 * Updates button icon.
	 */
	public override update() {
		this.setImageBasename(stendhal.config.getBoolean("client.joystick") ? "joystick" : "joystick-disabled");
	}

	protected override onClick(evt: Event) {
		const newState = !stendhal.config.getBoolean("client.joystick");
		stendhal.config.set("client.joystick", newState);
		stendhal.ui.gamewindow.updateJoystick();
		this.update();
	}
}
