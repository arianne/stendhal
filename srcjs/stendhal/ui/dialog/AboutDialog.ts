/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { DialogContentComponent } from "../toolkit/DialogContentComponent";


export class AboutDialog extends DialogContentComponent {

	constructor() {
		super("aboutdialog-template");
		this.child("#btn_config_close")!.addEventListener("click", () => {
			this.close();
		});
	}

	public override getConfigId(): string {
		return "about";
	}
}
