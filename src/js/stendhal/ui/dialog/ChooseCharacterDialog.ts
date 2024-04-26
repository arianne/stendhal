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

import { Client } from "../../Client";
import { DialogContentComponent } from "../toolkit/DialogContentComponent";

/**
 * a dialog to select your character from
 */
export class ChooseCharacterDialog extends DialogContentComponent {

	constructor(characters: any) {
		super("choose-character-template");

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
				this.componentElement.append(button);
			}
		}
	}

}
