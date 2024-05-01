/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Panel } from "../toolkit/Panel";
import { TabDialogContentComponent } from "../toolkit/TabDialogContentComponent";

import { Layout } from "../../util/Layout";


export class AboutDialog extends TabDialogContentComponent {

	constructor() {
		super("aboutdialog-template", Layout.TOP, "#aboutdialog-content");

		this.addTab("License", new Panel(this.child("#license")!));
		this.addTab("Contributors", new Panel(this.child("#contributors")!));
		this.addCloseButton();

		this.load();
	}

	async load() {
		let response = await fetch("LICENSE.html");
		let content = await response.text();
		this.child("#license")!.innerHTML = content;

		let response2 = await fetch("contributors.html");
		let content2 = await response2.text();
		this.child("#contributors")!.innerHTML = content2;
	}

	public override getConfigId(): string {
		return "about";
	}
}
