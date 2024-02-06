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
import { singletons } from "../../SingletonRepo";


/**
 * Button to bring up settings dialog window.
 */
export class SettingsButton extends QuickMenuButton {

	constructor() {
		super("settings");
	}

	/**
	 * Opens settings window when clicked/tapped.
	 */
	protected override onClick(evt: Event) {
		singletons.getSlashActionRepo().execute("/settings");
	}
}
