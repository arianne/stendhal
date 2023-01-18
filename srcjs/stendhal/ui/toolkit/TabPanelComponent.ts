/***************************************************************************
 *                (C) Copyright 2015-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "./Component";
import { Panel } from "./Panel";

/**
 * a tabbed panel
 */
export class TabPanelComponent extends Panel {

	/**
	 * creates a new panel
	 *
	 * @param id id of HTML element or template
	 */
	constructor() {
		super("tabpanel-template");
		this.containerElement = this.componentElement.querySelector(".tabpanel-content")!;
	}

	addTab(label: string) {
		let button = document.createElement("button");
		button.innerText = label;
		this.componentElement.querySelector(".tabpanel-tabs")!.append(button);
	}
}
