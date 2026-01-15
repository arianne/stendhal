/***************************************************************************
 *                 Copyright © 2023-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../stendhal";

import { SlashAction } from "./SlashAction";

import { ui } from "../ui/UI";

import { SettingsDialog } from "../ui/dialog/SettingsDialog";


export class SettingsAction extends SlashAction {
	readonly minParams = 0;
	readonly maxParams = 0;

	override readonly desc = "Opens settings dialog.";


	execute(_type: string, _params: string[], _remainder: string): boolean {
		const wstate = stendhal.config.getWindowState("menu");
		const offset = ui.getPageOffset();

		const content = new SettingsDialog();
		const dialog = ui.createSingletonFloatingWindow(
			"Settings", content,
			wstate.x - offset.x, wstate.y - offset.y);
		dialog.setId("menu");
		content.setFrame(dialog);
		return true;
	}
}
