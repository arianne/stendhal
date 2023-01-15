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

import { ItemInventoryComponent } from "../component/ItemInventoryComponent";
import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";


/**
 * a dialog to display images
 */
export class TradeDialog extends DialogContentComponent {

	constructor() {
		super("tradedialog-template");
		ui.registerComponent(UIComponentEnum.TradeDialog, this);
		this.refresh();

		this.createHtml();
	}


	public override getConfigId(): string {
		return "trade";
	}

	private createHtml() {
		const otherItemsComponent = new ItemInventoryComponent(
			this, "trade", 2, 2, false, undefined);
		const myItemsComponent = new ItemInventoryComponent(
			this, "trade", 2, 2, false, undefined);
		
		this.componentElement.querySelector("#trade-other-items")!.append(otherItemsComponent.componentElement);
		this.componentElement.querySelector("#trade-my-items")!.append(myItemsComponent.componentElement);
	}

	public override onParentClose() {
		ui.unregisterComponent(this);
	}
}
