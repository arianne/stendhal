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

import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { Panel } from "../toolkit/Panel";
import { TabPanelComponent } from "../toolkit/TabPanelComponent";

import { Layout } from "../../util/Layout";


export class AboutDialog extends DialogContentComponent {

	constructor() {
		super("aboutdialog-template");
		this.child("#btn_config_close")!.addEventListener("click", () => {
			this.close();
		});

		const tabs = new TabPanelComponent(Layout.TOP);
		tabs.addTab("License");
		const licenseTab = new Panel(this.child("#license")!);
		tabs.add(licenseTab);
		tabs.addTab("Contributors");
		const contribsTab = new Panel(this.child("#contributors")!);
		tabs.add(contribsTab);
		this.child("#aboutdialog-content")!.appendChild(tabs.componentElement);

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
