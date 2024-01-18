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

import { ButtonBase } from "./ButtonBase";
import { JoystickButton } from "./JoystickButton";
import { MenuButton } from "./MenuButton";
import { SoundButton } from "./SoundButton";

import { Paths } from "../../data/Paths";

declare var stendhal: any;


/**
 * Main button to show/hide quick menu buttons.
 */
export class QMButton {

	private static initialized = false;
	private static expanded = false;
	private static readonly buttonList: ButtonBase[] = [];


	private constructor() {
		// do nothing
	}

	public static init() {
		if (QMButton.initialized) {
			console.warn("WARNING: attempted to re-initialize quick menu buttons")
			return;
		}
		QMButton.initialized = true;

		const btn_main = document.getElementById("qm-main")! as HTMLImageElement;
		// ensure visible at startup
		btn_main.style["display"] = "block";
		btn_main.src = Paths.gui + "/quickmenu/main.png";

		// sub-buttons
		QMButton.buttonList.push(new MenuButton());
		QMButton.buttonList.push(new SoundButton());
		QMButton.buttonList.push(new JoystickButton());

		// positioning
		// FIXME: position is not consistent accross browsers/platforms
		let pos_next = stendhal.ui.gamewindow.width - 24;
		btn_main.style["left"] = pos_next + "px";
		for (const btn of QMButton.buttonList) {
			pos_next -= 48;
			btn.setPosX(pos_next);
		}

		btn_main.addEventListener("click", function(e) {
			QMButton.toggle();
		});

		// hide buttons by default
		QMButton.update();
	}

	private static toggle() {
		QMButton.expanded = !QMButton.expanded;
		QMButton.update();
	}

	private static update() {
		(document.getElementById("qm-main")! as HTMLImageElement).style["transform"] = "rotate("
				+ (QMButton.expanded ? 90 : 0) + "deg)";
		for (const btn of QMButton.buttonList) {
			btn.setVisible(QMButton.expanded);
		}
	}
}
