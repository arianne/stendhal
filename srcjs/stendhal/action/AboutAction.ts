/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var stendhal: any;

import { SlashAction } from "./SlashAction";

import { ui } from "../ui/UI";

import { AboutDialog } from "../ui/dialog/AboutDialog";


export class AboutAction extends SlashAction {
	readonly minParams = 0;
	readonly maxParams = 0;

	execute(_type: string, _params: string[], _remainder: string): boolean {
		const wstate = stendhal.config.getWindowState("about");
		const offset = stendhal.ui.getPageOffset();

		const content = new AboutDialog();
		const dialog = ui.createSingletonFloatingWindow(
			"About", content,
			wstate.x - offset.x, wstate.y - offset.y);
		dialog.setId("about");
		content.setFrame(dialog);
		return true;
	}
}
