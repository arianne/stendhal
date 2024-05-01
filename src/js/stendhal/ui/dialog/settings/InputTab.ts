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

import { AbstractSettingsTab } from "./AbstractSettingsTab";

import { SettingsDialog } from "../SettingsDialog";

import { singletons } from "../../../SingletonRepo";


export class InputTab extends AbstractSettingsTab {

	constructor(parent: SettingsDialog, element: HTMLElement) {
		super(element);


		/* *** pathfinding *** */

		parent.createCheckBox("chk_pathfinding", "pathfinding",
				"Click/Tap ground to walk", "Ground pathfinding disabled");

		parent.createCheckBox("chk_pathfindingmm", "pathfinding.minimap",
				"Click/Tap minimap to walk", "Minimap pathfinding disabled");


		/* *** joystick interface *** */

		// on-screen joystick
		const sel_joystick = parent.createSelectFromConfig("seljoystick", "joystick.style",
				undefined,
				function(e: Event) {
					singletons.getJoystickController().update();
				});
		const chk_joystick = parent.createCheckBox("chk_joystick", "joystick",
				undefined, undefined,
				function(e: Event) {
					sel_joystick.disabled = !chk_joystick.checked;
					singletons.getJoystickController().update();
				});
		chk_joystick.checked = singletons.getSessionManager().joystickEnabled();
		sel_joystick.disabled = !chk_joystick.checked;

		// joystck positioning
		for (const o of ["x", "y"]) {
			const orienter = parent.createNumberInput("txtjoystick" + o,
					parseInt(parent.storedStates["txtjoystick" + o], 10),
					"Joystick position on " + o.toUpperCase() + " axis");
			orienter.addEventListener("input", (e) => {
				// update configuration
				singletons.getConfigManager().set("joystick.center." + o, orienter.value || 0);
				// update on-screen joystick position
				singletons.getJoystickController().update();
			});
		}
	}
}
