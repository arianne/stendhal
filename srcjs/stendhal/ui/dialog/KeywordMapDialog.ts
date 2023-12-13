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

declare var marauroa: any;


export class KeywordMapDialog extends DialogContentComponent {

	// commonly used keywors
	private static readonly keywords = [
		"hi", "bye", "yes", "no", "help", "job", "offer", "quest", "done"
	];

	constructor() {
		super("keywordmap-template");

		let row: HTMLDivElement = document.createElement("div");
		this.componentElement.appendChild(row);
		for (let idx = 0; idx < KeywordMapDialog.keywords.length; idx++) {
			if (idx > 0 && idx % 13 == 0) {
				// new row
				row = document.createElement("div");
				this.componentElement.appendChild(row);
			}
			const keyword = KeywordMapDialog.keywords[idx];
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
