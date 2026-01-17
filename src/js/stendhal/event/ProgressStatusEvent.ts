/***************************************************************************
 *                   (C) Copyright 2005-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { FloatingWindow } from "../ui/toolkit/FloatingWindow";
import { TravelLogDialog } from "../ui/dialog/TravelLogDialog";
import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";
import { RPEvent } from "marauroa"
import { stendhal } from "stendhal";


/**
 * show travel log and details
 */
export class ProgressStatusEvent extends RPEvent {
	public progress_type!: string;
	public data!: string;
	public item!: string;
	public description!: string;

	public execute(_entity: any): void {
		let progressType = this["progress_type"];
		let dataItems = this["data"].substring(1, this["data"].length - 1).split(/\t/);

		let travelLogDialog = ui.get(UIComponentEnum.TravelLogDialog) as TravelLogDialog;
		if (!this["progress_type"]) {
			if (!travelLogDialog) {
				const dstate = stendhal.config.getWindowState("travel-log");
				travelLogDialog = new TravelLogDialog(dataItems);
				new FloatingWindow("Travel Log", travelLogDialog, dstate.x, dstate.y).setId("travel-log");
			} else {
				travelLogDialog.setDataItems(dataItems);
			}
			travelLogDialog.updateTabs();
			return;
		}

		if (!travelLogDialog) {
			return;
		}
		travelLogDialog.updateTabs();

		if (!this["item"]) {
			if (progressType === "repeatable") {
				travelLogDialog.setRepeatable(dataItems);
			} else {
				travelLogDialog.progressTypeData(progressType, dataItems);
			}
		} else {
			travelLogDialog.itemData(progressType, this["item"], this["description"], dataItems);
		}
	}

};
