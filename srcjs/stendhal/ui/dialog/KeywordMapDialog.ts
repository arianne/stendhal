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
import { Chat } from "../../util/Chat";

declare var marauroa: any;


export class KeywordMapDialog extends DialogContentComponent {

	// commonly used words
	private static readonly keywords = [
		"hello", "goodbye", "yes", "no", "help", "job", "offer", "quest", "done"
	];

	/* Some keywords don't need repeated in NPC options. Others, such as "task" or "favor" which
	 * serve as alternatives to "quest", may be highlighted in dialogue so keep those.
	 */
	private static readonly aliases: {[key: string]: string} = {
		"bye": "goodbye",
		"hi": "hello",
	}

	constructor() {
		super("keywordmap-template");

		// common chat options
		this.addGroup("Common", KeywordMapDialog.keywords);

		// attending NPC (note that options are parsed from most recent NPC if there are multiple attending)
		const options = [];
		for (let opt of Chat.options) {
			opt = opt.toLowerCase();
			const original = KeywordMapDialog.aliases[opt];
			if (original) {
				opt = original;
			}
			if (KeywordMapDialog.keywords.indexOf(opt) == -1) {
				options.push(opt);
			}
		}
		if (options.length > 0) {
			this.addGroup(Chat.attending ? Chat.attending : "NPC", options);
		}
	}

	private addGroup(title: string, options: string[]) {
		const titleElement = document.createElement("div");
		titleElement.innerText = title;
		this.componentElement.appendChild(titleElement);

		let row: HTMLDivElement = document.createElement("div");
		this.componentElement.appendChild(row);
		for (let idx = 0; idx < options.length; idx++) {
			if (idx > 0 && idx % 13 == 0) {
				// new row
				row = document.createElement("div");
				this.componentElement.appendChild(row);
			}
			const keyword = options[idx];
			const button = document.createElement("button");
			button.className = "shortcut-button";
			button.innerText = keyword;
			button.addEventListener("click", (evt) => {
				this.onButtonPressed(keyword);
			});
			row.appendChild(button);
		}
	}

	private onButtonPressed(keyword: string) {
		const action = {
			"type": "chat",
			"text": keyword
		} as any;
		marauroa.clientFramework.sendAction(action);
		this.close();
	}
}