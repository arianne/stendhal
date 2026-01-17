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

import { stendhal } from "../stendhal";


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
					outfits.push({
						name: parts[0],
						outfit: parts[1],
						price: parts[2]
					});
				}
			}
		}
		if (this.hasOwnProperty("show_base")) {
			//Chat.log("normal", this["show_base"]);
		}

		const content = new class extends DialogContentComponent { }("empty-div-template");
		content.componentElement.classList.add("shopsign");

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

		const headers = ["Name", "Outfit", "Price"];
		for (const text of headers) {
			const th = document.createElement("th");
			th.textContent = text;
			headerRow.appendChild(th);
		}

		thead.appendChild(headerRow);
		table.appendChild(thead);

		// Body
		const tbody = document.createElement("tbody");

		for (const outfit of outfits) {
			const row = document.createElement("tr");

			// Name
			const nameCell = document.createElement("td");
			nameCell.textContent = outfit.name;
			row.appendChild(nameCell);

			// Image
			const imageCell = document.createElement("td");
			let url = ShowOutfitListEvent.outfitToUrl(outfit.outfit);
			const img1 = document.createElement("img");
			const img2 = document.createElement("img");
			img1.src = url;
			img2.src = url + "&offset=0";
			imageCell.append(img1);
			imageCell.append(img2);
			row.appendChild(imageCell);

			// Price
			const priceCell = document.createElement("td");
			priceCell.className = "number";
			priceCell.textContent = outfit.price;
			row.appendChild(priceCell);

			tbody.appendChild(row);
		}

		table.appendChild(tbody);

		stendhal.ui.globalInternalWindow.set(
			ui.createSingletonFloatingWindow(title, content, 20, 20));
	}

	private static outfitToUrl(outfit?: string): string {
		if (!outfit) {
			return "";
		}

		// Required order
		const order = ["body", "dress", "head", "mouth", "eyes", "mask", "hair", "hat", "detail"];

		// Parse input into a map
		let values: Record<string, number> = {};

		for (let part of outfit.split(",")) {
			let [key, value] = part.split("=");
			values[key] = Number(value);
		}

		// Build path
		let parts = order.map(key => {
			let value = values[key] ?? 0;
			return `${key}-${value}-0`;
		});

		let url =
			"https://stendhalgame.org/createoutfit.php?rewritten=true&dialog=shop&outfit=" +
			parts.join("_");

		return url;
	}
}
