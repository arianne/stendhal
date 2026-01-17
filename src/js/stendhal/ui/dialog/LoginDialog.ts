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

import { Client } from "../../Client";
import { ui } from "../UI";
import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { CreateAccountDialog } from "./CreateAccountDialog";

import { marauroa } from "marauroa"

/**
 * a dialog to enter username and password
 */
export class LoginDialog extends DialogContentComponent {

	constructor() {
		super("logindialog-template");

		// prevent KeyHandler's overrides of keypress events
		this.componentElement.addEventListener("keydown", (e: Event) => {
			e.stopPropagation();
		});
		this.componentElement.addEventListener("keyup", (e: Event) => {
			e.stopPropagation();
		});

		this.child("button")!.addEventListener("click", (event: Event) => {
			event.preventDefault();
			let username = (this.child("#username") as HTMLInputElement).value;
			let password = (this.child("#password") as HTMLInputElement).value;
			Client.get().username = username;
			marauroa.clientFramework.login(username, password);
			this.close();
		});

		this.child("a")!.addEventListener("click", (event: Event) => {
			event.preventDefault();
			this.close();
			ui.createSingletonFloatingWindow(
				"Create Account",
				new CreateAccountDialog(),
				100, 50);
		});
	}

}
