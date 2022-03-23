/***************************************************************************
 *                    Copyright 2003-2022 © - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

let SettingsDialog = require("../../../build/ts/ui/dialog/SettingsDialog").SettingsDialog;

window.marauroa = window.marauroa || {};
window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};


stendhal.ui.settings = {

	onOpenSettingsMenu: function(e) {
		const wstate = stendhal.config.dialogstates["settings"];
		const offset = stendhal.ui.getPageOffset();

		const content = new SettingsDialog();
		const dialog = ui.createSingletonFloatingWindow(
			"Settings", content,
			wstate.x - offset.x, wstate.y - offset.y);

		content.setFrame(dialog);
	}
}
