/***************************************************************************
 *                 Copyright © 2024 - 2025  Faiumoni e. V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { RPEvent } from "./RPEvent";

import { ui } from "../ui/UI";
import { DialogContentComponent } from "../ui/toolkit/DialogContentComponent";

declare var stendhal: any;


export class ShowOutfitListEvent extends RPEvent {

	title!: string;
	caption!: string;
	outfits!: string;

	execute(_entity: any) {
		let title = "Outfits";
		let caption = "";
		let outfits = [];

		if (this.hasOwnProperty("title")) {
			title = this["title"];
		}
		if (this.hasOwnProperty("caption")) {
			caption = this["caption"];
		}
		if (this.hasOwnProperty("outfits")) {
			for (let o of this["outfits"].split(":")) {
				let parts = o.split(";");
				if (parts.length > 2) {
					outfits.push([parts[0], parts[1], parts[2]]);
				}
			}
		}
		if (this.hasOwnProperty("show_base")) {
			//Chat.log("normal", this["show_base"]);
		}

		const content = new class extends DialogContentComponent { }("empty-div-template");
		content.componentElement.classList.add("shopsign");
		const captionElement = document.createElement("div");
		captionElement.className = "horizontalgroup shopcaption";
		captionElement.textContent = caption;
		content.componentElement.appendChild(captionElement);
		const itemList = document.createElement("div");
		itemList.className = "shoplist";
		content.componentElement.appendChild(itemList);

		// TODO: organize in columns & show outfit sprites
		for (const o of outfits) {
			const row = document.createElement("div");
			row.className = "horizontalgroup shoprow";
			row.textContent = o[0] + ": " + o[2];
			itemList.appendChild(row);
		}

		stendhal.ui.globalInternalWindow.set(
			ui.createSingletonFloatingWindow(title, content, 20, 20));
	}
}
