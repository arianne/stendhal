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
import { ui } from "../UI";

import { stendhal } from "../../stendhal";


/**
 * Button to open the main application menu dialog window.
 */
export class MenuButton extends QuickMenuButton {

	constructor() {
		super("menu");
	}

	/**
	 * Opens application menu window when clicked/tapped.
	 */
	protected override onClick(evt: Event) {
		ui.showApplicationMenu();
	}
}
