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

import { RPEvent } from "marauroa"

import { ui } from "../ui/UI";
import { DialogContentComponent } from "../ui/toolkit/DialogContentComponent";
import { Chat } from "../util/Chat";
import { Paths } from "../data/Paths";
import { singletons } from "../SingletonRepo";

import { stendhal } from "../stendhal";


export class ShowItemListEvent extends RPEvent {

	title!: string;
	caption!: string;
	content!: any;

	execute(_entity: any) {
		let title = "Items";
		let caption = "";
		let items = [];

		if (this.hasOwnProperty("title")) {
			title = this["title"];
		}
		if (this.hasOwnProperty("caption")) {
			caption = this["caption"];
		}
		if (this.hasOwnProperty("content")) {
			for (let obj in this["content"]) {
				if (this["content"].hasOwnProperty(obj)) {
					let data = this["content"][obj]["a"];
					let item = {
						clazz: data["class"],
						subclass: data["subclass"],
						img: data["class"] + "/" + data["subclass"] + ".png",
						price: data["price"],
						desc: data["description_info"]
					}

					// seller shops prefix prices with "-"
					if (item.price.startsWith("-")) {
						item.price = item.price.substr(1);
					}
					items.push(item);
				}
			}
		}

		const content = new class extends DialogContentComponent { }("empty-div-template");
		content.componentElement.classList.add("shopsign");
		// Create table
		const table = document.createElement("table");
		table.className = "shoptable";
		content.componentElement.appendChild(table);

		// Caption
		const tableCaption = document.createElement("caption");
		tableCaption.className = "shopcaption";
		tableCaption.textContent = caption;
		table.appendChild(tableCaption);

		// Header
		const thead = document.createElement("thead");
		const headerRow = document.createElement("tr");

		const headers = ["Item", "Price", "Description"];
		for (const text of headers) {
			const th = document.createElement("th");
			th.textContent = text;
			headerRow.appendChild(th);
		}

		thead.appendChild(headerRow);
		table.appendChild(thead);

		// Body
		const tbody = document.createElement("tbody");

		for (const i of items) {
			const row = document.createElement("tr");

			// Item (image)
			const itemCell = document.createElement("td");
			itemCell.appendChild(
				singletons.getSpriteStore().get(
					Paths.sprites + "/items/" + i.img
				)
			);
			row.appendChild(itemCell);

			// Price
			const priceCell = document.createElement("td");
			priceCell.className = "number";
			priceCell.textContent = i.price;
			row.appendChild(priceCell);

			// Description
			const descCell = document.createElement("td");
			descCell.innerHTML = Chat.formatLogEntry(i.desc);
			row.appendChild(descCell);

			tbody.appendChild(row);
		}

		table.appendChild(tbody);

		stendhal.ui.globalInternalWindow.set(
			ui.createSingletonFloatingWindow(title, content, 20, 20));
	}

}
