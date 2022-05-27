/***************************************************************************
 *                (C) Copyright 2007-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";

declare var marauroa: any;
declare var stendhal: any;

/**
 * a dialog to display images
 */
export class TravelLogDialog extends DialogContentComponent {
	private currentProgressType = "";
	private repeatable: {[key: string]: boolean;} = {};

	constructor(dataItems: string[]) {
		super("travellogdialog-template");
		ui.registerComponent(UIComponentEnum.TravelLogDialog, this);
		this.refresh();

		this.createHtml(dataItems);

		// trigger loading of content for first entry
		this.currentProgressType = dataItems[0];
		var action = {
			"type":           "progressstatus",
			"progress_type":  this.currentProgressType
		}
		marauroa.clientFramework.sendAction(action);
	};

	public override refresh() {
		this.componentElement.style.setProperty("font-family", stendhal.config.get("ui.font.tlog"));
	}

	public override getConfigId(): string {
		return "travellog";
	}

	private createHtml(dataItems: string[]) {
		let buttons = "";
		for (var i = 0; i < dataItems.length; i++) {
			buttons = buttons + "<button id=\"" + stendhal.ui.html.esc(dataItems[i])
				 + "\" class=\"progressTypeButton\">"
				+ stendhal.ui.html.esc(dataItems[i]) + "</button>";
		}
		this.componentElement.querySelector(".tavellogtabpanel")!.innerHTML = buttons;

		this.componentElement.querySelectorAll(".progressTypeButton").forEach((button) => {
			button.addEventListener("click", (event) => {
				this.onProgressTypeButtonClick(event);
			});
		});

		this.componentElement.querySelector(".travellogitems")!.addEventListener("change", (event) => {
			this.onTravelLogItemsChange(event);
		});
	}


	private onProgressTypeButtonClick(event: Event) {
		// clear details when changing category
		this.refreshDetails();

		this.currentProgressType = (event.target as HTMLElement).id;
		var action = {
			"type":           "progressstatus",
			"progress_type":  this.currentProgressType
		}
		marauroa.clientFramework.sendAction(action);

		// request repeatable quests
		if (this.currentProgressType === "Completed Quests") {
			marauroa.clientFramework.sendAction({
				"type": "progressstatus",
				"progress_type": "repeatable"
			});
		}
	}

	private onTravelLogItemsChange(event: Event) {
		var action = {
			"type":           "progressstatus",
			"progress_type":  this.currentProgressType,
			"item":           (event.target as HTMLInputElement).value
		}
		marauroa.clientFramework.sendAction(action);
	}


	public progressTypeData(progressType: string, dataItems: string[]) {
		if (progressType !== this.currentProgressType) {
			return;
		}
		var html = "";
		for (var i = 0; i < dataItems.length; i++) {
			const currentItem = dataItems[i];
			html += "<option value=\"" + stendhal.ui.html.esc(currentItem) + "\">"
				+ stendhal.ui.html.esc(currentItem);
			if (this.repeatable[currentItem]) {
				html += " (R)";
			}
			html += "</option>";
		}
		this.componentElement.querySelector(".travellogitems")!.innerHTML = html;

		// trigger loading of content for first entry
		if (dataItems) {
			// this.componentElement.querySelector(".travellogitems")!.value = dataItems[0];
			var action = {
				"type":           "progressstatus",
				"progress_type":  progressType,
				"item":           dataItems[0]
			}
			marauroa.clientFramework.sendAction(action);
		}
	}


	public itemData(progressType: string, selectedItem: string, description: string, dataItems: string[]) {
		if (progressType !== this.currentProgressType) {
			return;
		}

		const detailsSpan = document.createElement("span");

		detailsSpan.innerHTML = "<h3>" + stendhal.ui.html.esc(selectedItem) + "</h3>";
		if (this.repeatable[selectedItem]) {
			detailsSpan.innerHTML += "<p id=\"travellogrepeatable\">"
				+ "<img src=\"data/gui/rp.png\" /> <em>I can do this quest again.</em></p>";
		}

		detailsSpan.innerHTML += "<p id=\"travellogdescription\">"
				+ stendhal.ui.html.esc(description) + "</p>";

		const ul = document.createElement("ul");
		ul.className = "uniform";

		for (var i = 0; i < dataItems.length; i++) {
			let content = []
			let html = stendhal.ui.html.esc(dataItems[i], ["em", "tally"]);
			if (html.includes("<tally>") && html.includes("</tally>")) {
				 content = stendhal.ui.html.formatTallyMarks(html);
			} else {
				content.push(html);
			}

			const li = document.createElement("li");
			li.className = "uniform";
			li.innerHTML = content[0];
			if (content[1]) {
				li.appendChild(content[1]);

				if (content[2]) {
					li.innerHTML += content[2];
				}
			}

			ul.appendChild(li);
		}

		detailsSpan.appendChild(ul);
		this.refreshDetails("", detailsSpan);
	}

	private refreshDetails(html: string="", newDetails?: HTMLElement) {
		const details = this.componentElement.querySelector(".travellogdetails")!;
		details.innerHTML = html;

		if (newDetails) {
			details.appendChild(newDetails);
		}
	}

	public override onParentClose() {
		ui.unregisterComponent(this);
	}

	public setRepeatable(dataItems: string[]) {
		const repeatable = {} as {[key: string]: boolean};
		for (const item of dataItems) {
			repeatable[item] = true;
		}

		this.repeatable = repeatable;
	}
}
