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

import { Component } from "../toolkit/Component";
import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";

declare var marauroa: any;
declare var stendhal: any;

/**
 * a dialog to display images
 */
export class TravelLogDialog extends Component {
	private currentProgressType = "";

	constructor(dataItems: string[]) {
		super("travellogdialog-template");
		ui.registerComponent(UIComponentEnum.TravelLogDialog, this);

		this.createHtml(dataItems);

		// trigger loading of content for first entry
		this.currentProgressType = dataItems[0];
		var action = {
			"type":           "progressstatus",
			"progress_type":  this.currentProgressType
		}
		marauroa.clientFramework.sendAction(action);
	};


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
		this.currentProgressType = (event.target as HTMLElement).id;
		var action = {
			"type":           "progressstatus",
			"progress_type":  this.currentProgressType
		}
		marauroa.clientFramework.sendAction(action);
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
			html += "<option value=\"" + stendhal.ui.html.esc(dataItems[i]) + "\">"
				+ stendhal.ui.html.esc(dataItems[i]) + "</option>";
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
		var html = "<h3>" + stendhal.ui.html.esc(selectedItem) + "</h3>";
		html += "<p id=\"travellogdescription\">" + stendhal.ui.html.esc(description) + "</p>";
		html += "<ul>";
		for (var i = 0; i < dataItems.length; i++) {
			html += "<li>" + stendhal.ui.html.esc(dataItems[i]);
		}
		html += "</ul>";
		this.componentElement.querySelector(".travellogdetails")!.innerHTML = html;
	}

	public override onParentClose() {
		ui.unregisterComponent(this);
	}

}
