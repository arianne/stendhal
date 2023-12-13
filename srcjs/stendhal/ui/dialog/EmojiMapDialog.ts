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

import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { singletons } from "../../SingletonRepo";

declare var marauroa: any;
declare var stendhal: any;


export class EmojiMapDialog extends DialogContentComponent {

	constructor() {
		super("emojimap-template");

		const emojiStore = singletons.getEmojiStore();
		let idx = 0;
		let row: HTMLDivElement = document.createElement("div");
		this.componentElement.appendChild(row);
		for (const emoji of emojiStore.getEmojiList()) {
			if (idx > 0 && idx % 13 == 0) {
				// new row
				row = document.createElement("div");
				this.componentElement.appendChild(row);
			}
			const button = document.createElement("button");
			button.className = "emojibutton";
			button.appendChild(stendhal.data.sprites.get(stendhal.paths.sprites + "/emoji/" + emoji + ".png").cloneNode());
			button.addEventListener("click", (evt) => {
				this.onButtonPressed(emoji);
			});
			//~ this.componentElement.appendChild(button);
			row.appendChild(button);
			idx++;
		}
	}

	private onButtonPressed(emoji: string) {
		const action = {
			"type": "chat",
			"text": ":" + emoji + ":"
		} as any;
		marauroa.clientFramework.sendAction(action);
		this.close();
	}
}
