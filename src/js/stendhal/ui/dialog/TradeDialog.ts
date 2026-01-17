/***************************************************************************
 *                (C) Copyright 2007-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Player } from "../../entity/Player";
import { ItemInventoryComponent } from "../component/ItemInventoryComponent";
import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";

import { marauroa } from "marauroa"

/**
 * a dialog to display images
 */
export class TradeDialog extends DialogContentComponent {
	private static empty = {};
	private otherItemsComponent;
	private myItemsComponent;

	constructor() {
		super("tradedialog-template");
		ui.registerComponent(UIComponentEnum.TradeDialog, this);
		this.refresh();
		this.otherItemsComponent = new ItemInventoryComponent(
			TradeDialog.empty, "trade", 2, 2, false, undefined);
		this.myItemsComponent = new ItemInventoryComponent(
			TradeDialog.empty, "trade", 2, 2, false, undefined);

		this.createHtml();
	}

	private showState(element: HTMLElement, state: string) {
		element.className = "trade-" + state;
		switch (state) {
			case "NO_ACTIVE_TRADE":
				element.innerText = "Inactive";
				break;
			case "MAKING_OFFERS":
				element.innerText = "Changing";
				break;
			case "LOCKED":
				element.innerText = "Offered";
				break;
			case "DEAL_WAITING_FOR_OTHER_DEAL":
				element.innerText = "ACCEPTED";
				break;
		}
	}

	updateTradeState(me: Player, partner: Player, myState: string, partnerState: string) {
		if (partner) {
			this.child("#trade-partner-name")!.innerText = partner["_name"];
		}
		this.showState(this.child("#trade-partner-status")!, partnerState);
		this.showState(this.child("#trade-my-status")!, myState);

		(this.child("#trade-offer") as HTMLButtonElement).disabled = !(myState === "MAKING_OFFERS");
		(this.child("#trade-accept") as HTMLButtonElement).disabled
			= !(myState === "LOCKED" && (partnerState === "LOCKED" || partnerState === "DEAL_WAITING_FOR_OTHER_DEAL"));

		this.otherItemsComponent.setObject(partner ? partner : TradeDialog.empty);
		this.myItemsComponent.setObject(me);
		if (myState === "TRADE_COMPLETED") {
			this.componentElement.dispatchEvent(new Event("close"));
		}
	}


	public override getConfigId(): string {
		return "trade";
	}

	private createHtml() {

		this.child("#trade-partner-items")!.append(this.otherItemsComponent.componentElement);
		this.child("#trade-my-items")!.append(this.myItemsComponent.componentElement);
		this.child("#trade-accept")!.addEventListener("click", () => {
			this.onAcceptClicked();
		});
		this.child("#trade-offer")!.addEventListener("click", () => {
			this.onOfferClicked();
		});
		this.child("#trade-cancel")!.addEventListener("click", () => {
			this.onCancelClicked();
		})
	}


	public onAcceptClicked() {
		let action = {
			"type": "trade",
			"action": "deal",
			"zone": marauroa.currentZoneName
		};
		marauroa.clientFramework.sendAction(action);
	}

	public onOfferClicked() {
		let action = {
			"type": "trade",
			"action": "lock",
			"zone": marauroa.currentZoneName
		};
		marauroa.clientFramework.sendAction(action);
	}

	public onCancelClicked() {
		this.componentElement.dispatchEvent(new Event("close"));
	}

	public override onParentClose() {
		this.otherItemsComponent.onParentClose();
		this.myItemsComponent.onParentClose();
		ui.unregisterComponent(this);

		let action = {
			"type": "trade",
			"action": "cancel",
			"zone": marauroa.currentZoneName
		};
		marauroa.clientFramework.sendAction(action);
	}

}
