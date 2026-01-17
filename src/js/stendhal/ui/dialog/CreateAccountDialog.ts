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

import { ui } from "../UI";
import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { LoginDialog } from "./LoginDialog";

import { marauroa } from "marauroa"

/**
 * a dialog to enter username and password
 */
export class CreateAccountDialog extends DialogContentComponent {

	constructor() {
		super("createaccountdialog-template");
		this.child("input[type=submit]")!.addEventListener("click", (event: Event) => {
			event.preventDefault();
			let username = (this.child("#username") as HTMLInputElement).value;
			let password = (this.child("#password") as HTMLInputElement).value;
			let passwordRepeat = (this.child("#passwordrepeat") as HTMLInputElement).value;
			let email = (this.child("#email") as HTMLInputElement).value;
			if (!username || !password || !passwordRepeat) {
				alert("Please fill in all required fields.");
				return;
			}
			if (password != passwordRepeat) {
				alert("Password and password repetition do not match.");
				return;
			}
			marauroa.clientFramework.createAccount(username, password, email);
		});

		this.child("a")!.addEventListener("click", (event: Event) => {
			event.preventDefault();
			this.close();
			ui.createSingletonFloatingWindow(
				"Login",
				new LoginDialog(),
				100, 50);
		});
	}


}
