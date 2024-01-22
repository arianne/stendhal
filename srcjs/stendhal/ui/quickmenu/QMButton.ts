/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
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
import { LogButton } from "./LogButton";
import { MenuButton } from "./MenuButton";
import { SettingsButton } from "./SettingsButton";
import { SoundButton } from "./SoundButton";

import { Paths } from "../../data/Paths";


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
		btn_main.style["cursor"] = "url(" + Paths.sprites + "/cursor/highlight.png) 1 3, auto";
		btn_main.draggable = false;

		// sub-buttons
		QMButton.buttonList.push(new MenuButton());
		QMButton.buttonList.push(new SettingsButton());
		QMButton.buttonList.push(new LogButton());
		QMButton.buttonList.push(new SoundButton());
		QMButton.buttonList.push(new JoystickButton());

		btn_main.onload = () => {
			// remove listener
			btn_main.onload = null;
			this.refresh();
		}
		btn_main.src = Paths.gui + "/quickmenu/main.png";

		btn_main.addEventListener("click", function(e) {
			QMButton.toggle();
		});

		// hide buttons by default
		QMButton.update();
	}

	/**
	 * Updates button positioning.
	 */
	public static refresh() {
		// place buttons in upper-right corner of viewport
		const btn_main = document.getElementById("qm-main")! as HTMLImageElement;
		const rect = document.getElementById("gamewindow")!.getBoundingClientRect();
		let x = rect.right - btn_main.width;
		const y = 0;
		btn_main.style["left"] = x + "px";
		btn_main.style["top"] = y + "px";
		for (const btn of QMButton.buttonList) {
			// all buttons should be same size
			x -= btn_main.width;
			btn.setPos(x, y);
		}
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
