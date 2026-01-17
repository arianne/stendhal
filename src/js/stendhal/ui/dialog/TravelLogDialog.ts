/***************************************************************************
 *                (C) Copyright 2007-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Paths } from "../../data/Paths";
import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";

import { marauroa } from "marauroa"
import { stendhal } from "../../stendhal";

/**
 * a dialog to display images
 */
export class TravelLogDialog extends DialogContentComponent {
	private currentProgressType = "";
	private repeatable: {[key: string]: boolean;} = {};

	constructor(dataItems?: string[]) {
		super("travellogdialog-template");
		ui.registerComponent(UIComponentEnum.TravelLogDialog, this);
		this.refresh();

		this.child(".travellogitems")!.innerHTML = "<option value=\"\">(loading)</option>";
		if (dataItems) {
			this.setDataItems(dataItems);
		}
	}

	public setDataItems(dataItems: string[]) {
		this.createHtml(dataItems);

		// trigger loading of content for first entry
		this.currentProgressType = dataItems[0];
		var action = {
			"type":           "progressstatus",
			"progress_type":  this.currentProgressType
		}
		marauroa.clientFramework.sendAction(action);
	}

	public override refresh() {
		this.componentElement.style.setProperty("font-family", stendhal.config.get("font.travel-log"));
	}

	public override getConfigId(): string {
		return "travel-log";
	}

	private createHtml(dataItems: string[]) {
		let buttons = "";
		for (var i = 0; i < dataItems.length; i++) {
			buttons = buttons + "<button id=\"" + stendhal.ui.html.esc(dataItems[i])
				+ "\" class=\"progressTypeButton\">"
				+ stendhal.ui.html.esc(dataItems[i]) + "</button>";
		}
		this.child(".tavellogtabpanel")!.innerHTML = buttons;

		this.componentElement.querySelectorAll(".progressTypeButton").forEach((button) => {
			button.addEventListener("click", (event) => {
				this.onProgressTypeButtonClick(event);
			});
		});

		this.child(".travellogitems")!.addEventListener("change", (event) => {
			this.onTravelLogItemsChange(event);
		});
	}

	public updateTabs() {
		document.querySelectorAll(".progressTypeButton").forEach((tab) => {
			const element = document.getElementById(tab.id)! as HTMLElement;
			if (element.id == this.currentProgressType) {
				// highlight selected tab
				element.classList.add("active");
			} else {
				element.classList.remove("active");
			}
		});
	}

	private onProgressTypeButtonClick(event: Event) {
		// clear details when changing category
		this.refreshDetails();

		this.currentProgressType = (event.target as HTMLElement).id;
		this.updateTabs();
		var action = {
			"type":           "progressstatus",
			"progress_type":  this.currentProgressType
		};
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
		const value = (event.target as HTMLInputElement).value;
		if (!value) {
			// ignore options without value
			return;
		}
		var action = {
			"type":           "progressstatus",
			"progress_type":  this.currentProgressType,
			"item":           value
		};
		marauroa.clientFramework.sendAction(action);
	}


	public progressTypeData(progressType: string, dataItems: string[]) {
		if (progressType !== this.currentProgressType) {
			return;
		}
		if (dataItems.length == 1 && dataItems[0] === "") {
			// prevent infinitely re-sending request when list is technically empty
			dataItems = [];
		}
		// sort items alphabetically
		dataItems.sort();
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
		const itemList = this.child(".travellogitems")! as HTMLSelectElement;
		itemList.innerHTML = html;

		if (dataItems.length > 0) {
			// trigger loading of content for first entry
			itemList.selectedIndex = 0;
			itemList.dispatchEvent(new Event("change"));
		} else if (stendhal.session.touchOnly()) {
			// show a "none" option for touch enabled devices
			itemList.innerHTML = "<option value=\"\">(none)</option>";
			itemList.selectedIndex = 0;
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
				+ "<img src=\"" + Paths.gui + "/rp.png\" /> <em>I can do this quest again.</em></p>";
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
		const details = this.child(".travellogdetails")!;
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
