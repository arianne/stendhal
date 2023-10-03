/***************************************************************************
 *                (C) Copyright 2015-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { DialogContentComponent } from "../toolkit/DialogContentComponent";

declare var marauroa: any;

/**
 * a dialog to enter username and password
 */
export class LoginDialog extends DialogContentComponent {

	constructor() {
		super("logindialog-template");
		this.child("button")!.addEventListener("click", () => {
			let username = (this.child("#username") as HTMLInputElement).value;
			let password = (this.child("#password") as HTMLInputElement).value;
			marauroa.clientFramework.login(username, password);
			this.close();
		});
	}

}
