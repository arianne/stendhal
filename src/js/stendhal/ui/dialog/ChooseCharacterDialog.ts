/***************************************************************************
 *                (C) Copyright 2015-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { DialogContentComponent } from "../toolkit/DialogContentComponent";

import { Client } from "../../Client";
import { SlashActionRepo } from "../../SlashActionRepo";


/**
 * a dialog to select your character from
 */
export class ChooseCharacterDialog extends DialogContentComponent {

	constructor(characters: any) {
		super("choose-character-template");

		const characterList = this.child("#characters")!;
		for (var i in characters) {
			if (characters.hasOwnProperty(i)) {
				let name = characters[i]["a"]["name"];
				let button = document.createElement("button");
				button.classList.add("menubutton");
				button.innerText = name;
				button.addEventListener("click", () => {
					this.componentElement.dispatchEvent(new Event("close"));
					Client.get().chooseCharacter(name);
				});
				characterList.append(button);
			}
		}

		this.child("#logout")!.addEventListener("click", (e: Event) => {
			this.componentElement.dispatchEvent(new Event("close"));
			this.onLogout();
		});
	}

	onLogout() {
		queueMicrotask(() => {
			// TODO: detect if not logged in via website & simply re-display login dialog
			SlashActionRepo.get().execute("/logout");
		});
	}
}
