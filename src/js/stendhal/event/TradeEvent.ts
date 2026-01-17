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

import { RPEvent } from "marauroa"

import { stendhal } from "../stendhal";
import { marauroa } from "marauroa"
import { Player } from "../entity/Player";

/**
 * handles player trade events
 */
export class TradeEvent extends RPEvent {
	public partner_id!: string;
	public user_trade_state!: string;
	public partner_trade_state!: string;

	public execute(entity: any): void {
		let dialog = ui.get(UIComponentEnum.TradeDialog) as TradeDialog;

		if (this["user_trade_state"] !== "NO_ACTIVE_TRADE" && !dialog) {
			const dstate = stendhal.config.getWindowState("trade");
			dialog = new TradeDialog();
			new FloatingWindow("Trade", dialog, dstate.x, dstate.y).setId("trade");
		}

		if (dialog) {
			let partner = marauroa.currentZone[this["partner_id"]];
			dialog.updateTradeState(marauroa.me as Player, partner, this["user_trade_state"], this["partner_trade_state"]);
		}
	}

};
