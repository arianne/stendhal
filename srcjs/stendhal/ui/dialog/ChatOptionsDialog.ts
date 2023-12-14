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
declare var stendhal: any;


export class ChatOptionsDialog extends DialogContentComponent {

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
		const custom_options = [];
		for (let opt of stendhal.config.get("chat.custom_keywords").split(",")) {
			opt = opt.trim();
			if (opt) {
				custom_options.push(opt);
			}
		}

		// attending NPC (note that options are parsed from most recent NPC if there are multiple attending)
		const npc_options = [];
		const not_attending = Chat.options.length == 1 && Chat.options[0].toLowerCase() === "hello";
		for (let opt of Chat.options) {
			opt = opt.toLowerCase();
			const original = ChatOptionsDialog.aliases[opt];
			if (original) {
				opt = original;
			}
			// don't add duplicates if already in custom options
			if (custom_options.indexOf(opt) == -1) {
				npc_options.push(opt);
			}
		}
		if (npc_options.length > 0) {
			this.addGroup(not_attending || !Chat.attending ? undefined : Chat.attending, npc_options);
		}

		if (custom_options.length > 0) {
			this.addGroup("Custom", custom_options);
		}
	}

	private addGroup(title: string|undefined, options: string[]) {
		if (title) {
			const titleElement = document.createElement("div");
			titleElement.innerText = title;
			this.componentElement.appendChild(titleElement);
		}

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
