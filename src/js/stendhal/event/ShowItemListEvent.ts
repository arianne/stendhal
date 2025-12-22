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


export class ShowItemListEvent extends RPEvent {

	title!: string;
	caption!: string;
	content!: any;

	execute(_entity: any) {
		let title = "Items";
		let caption = "";
		let items = [];
		console.log("Items", this);

		if (this.hasOwnProperty("title")) {
			title = this["title"];
		}
		if (this.hasOwnProperty("caption")) {
			caption = this["caption"];
		}
		if (this.hasOwnProperty("content")) {
			for (var obj in this["content"]) {
				if (this["content"].hasOwnProperty(obj)) {
					var slotObj = this["content"][obj];
					var data = this["content"][obj]["a"];
					const i = {
						clazz: data["class"],
						subclass: data["subclass"],
						img: data["class"] + "/" + data["subclass"] + ".png",
						price: data["price"],
						desc: data["description_info"]
					}

					// seller shops prefix prices with "-"
					if (i.price.startsWith("-")) {
						i.price = i.price.substr(1);
					}
					items.push(i);
				}
			}
		}

		const content = new class extends DialogContentComponent { }("empty-div-template");
		content.componentElement.classList.add("shopsign");
		const captionElement = document.createElement("div");
		captionElement.className = "horizontalgroup shopcaption";
		captionElement.textContent = caption + "\nItem\t-\tPrice\t-\tDescription";
		content.componentElement.appendChild(captionElement);
		const itemList = document.createElement("div");
		itemList.className = "shoplist";
		content.componentElement.appendChild(itemList);

		// TODO: organize in columns & show item sprites
		for (const i of items) {
			const row = document.createElement("div");
			row.className = "horizontalgroup shoprow";
			const img = document.createElement("div");
			img.className = "shopcol";
			img.appendChild(stendhal.data.sprites.get(stendhal.paths.sprites + "/items/" + i.img));
			row.appendChild(img);
			const price = document.createElement("div");
			price.className = "shopcol";
			price.textContent = i.price;
			row.appendChild(price);
			const desc = document.createElement("div");
			desc.className = "shopcol shopcolr";
			desc.textContent = i.desc;
			row.appendChild(desc);
			itemList.appendChild(row);
		}

		stendhal.ui.globalInternalWindow.set(
			ui.createSingletonFloatingWindow(title, content, 20, 20));
	}

}
