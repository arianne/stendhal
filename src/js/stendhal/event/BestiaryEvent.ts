/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
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


export class BestiaryEvent extends RPEvent {

	enemies!: string;


	execute(entity: any) {
		if (!this.hasOwnProperty("enemies")) {
			console.error("Event does not have \"enemies\" attribute");
			return;
		}

		// --- title & headers --- //

		const header = ["Bestiary:", "\"???\" = unknown"];
		const hasRare = this["enemies"].includes("(rare)");
		const hasAbnormal = this["enemies"].includes("(abnormal)");

		// show explanation of "rare" & "abnormal" creatures in header
		if (hasRare || hasAbnormal) {
			let subheader = "";
			if (!hasRare) {
				subheader += "\"abnormal\"";
			} else {
				subheader += "\"rare\"";
				if (hasAbnormal) {
					subheader += " and \"abnormal\"";
				}
			}
			header[1] = subheader + " creatures not required for achievements";
		}

		// --- contents --- //

		// TODO: clean up columns

		const content = new class extends DialogContentComponent {} ("empty-div-template");
		content.setConfigId("bestiary");
		content.componentElement.classList.add("bestiary");

		const layout = document.createElement("div");
		layout.className = "horizontalgroup stretchgroup";
		const col1 = document.createElement("div");
		const col2 = document.createElement("div");
		const col3 = document.createElement("div");
		col1.className = "verticalgroup stretchgroup";
		col2.className = "verticalgroup stretchgroup";
		col3.className = "verticalgroup stretchgroup";

		const t1 = document.createElement("div");
		const t2 = document.createElement("div");
		const t3 = document.createElement("div");
		t1.classList.add("shopcol");
		t2.classList.add("shopcol");
		t3.classList.add("shopcol");
		t1.textContent = "Name";
		t2.textContent = "Solo";
		t3.textContent = "Shared";

		col1.appendChild(t1);
		col2.appendChild(t2);
		col3.appendChild(t3);

		for (const e of this["enemies"].split(";")) {
			const info = e.split(",");
			// empty text will not render outline
			let solo = "-";
			let shared = "-";
			if (info[1] == "true") {
				solo = "✔";
			}
			if (info[2] == "true") {
				shared = "✔";
			}

			const l1 = document.createElement("div");
			const l2 = document.createElement("div");
			const l3 = document.createElement("div");
			l1.classList.add("shopcol");
			l2.classList.add("shopcol");
			l3.classList.add("shopcol");

			l1.textContent = info[0];
			l2.textContent = solo;
			l3.textContent = shared;

			col1.appendChild(l1);
			col2.appendChild(l2);
			col3.appendChild(l3);
		}

		layout.appendChild(col1);
		layout.appendChild(col2);
		layout.appendChild(col3);
		content.componentElement.appendChild(layout);

		stendhal.ui.globalInternalWindow.set(ui.createSingletonFloatingWindow(header.join(" "),
				content, 20, 20));
	}
}
