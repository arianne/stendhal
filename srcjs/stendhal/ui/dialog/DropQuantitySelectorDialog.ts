/***************************************************************************
 *                (C) Copyright 2015-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";

declare var marauroa: any;

export class DropQuantitySelectorDialog extends Component {

	constructor(private action: any) {
		super("dropquantityselectordialog-template");

		this.child(".quantityselectorbutton")?.addEventListener("click", (event) => {
			this.onDrop(event);
		});

		let valueInput = this.child(".quantityselectorvalue") as HTMLInputElement
		queueMicrotask( () => {
			valueInput.select();
			valueInput.focus();
		});
	}

	private onDrop(event: Event) {
		let quantityStr = (this.child(".quantityselectorvalue") as HTMLInputElement).value;
		let quantity = parseInt(quantityStr);
		if (quantity > 0) {
			this.action["quantity"] = quantityStr;
			marauroa.clientFramework.sendAction(this.action);
		}
		this.componentElement.dispatchEvent(new Event("close"));
		event.preventDefault();
	}

}
