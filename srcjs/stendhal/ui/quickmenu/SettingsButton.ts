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


export class SettingsButton extends QuickMenuButton {

	constructor() {
		super("settings");
	}

	protected override onClick(evt: Event) {
		singletons.getSlashActionRepo().execute("/settings");
	}
}
