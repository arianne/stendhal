/***************************************************************************
 *                 (C) Copyright 2005-2023 - Faiumoni e. V.                *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { FloatingWindow } from "../ui/toolkit/FloatingWindow";

import { TradeDialog } from "../ui/dialog/TradeDialog";
import { UIComponentEnum } from "../ui/UIComponentEnum";
import { ui } from "../ui/UI";

import { RPEvent } from "./RPEvent";

declare var stendhal: any

/**
 * handles player trade events
 */
export class TradeEvent extends RPEvent {

	public execute(entity: any): void {
		console.log("TradeEvent", this, entity);
		
		let dialog = ui.get(UIComponentEnum.TradeDialog);
		if (!dialog) {
			const dstate = stendhal.config.dialogstates["trade"];
			dialog = new TradeDialog();
			new FloatingWindow("Trade", dialog, dstate.x, dstate.y);
		}
	}

};
