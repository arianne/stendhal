/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "./Component";
import { DialogContentComponent } from "./DialogContentComponent";
import { TabPanelComponent } from "./TabPanelComponent";

import { Layout } from "../../util/Layout";


export class TabDialogContentComponent extends DialogContentComponent {

	/** Tabs interface. */
	private readonly tabs: TabPanelComponent;


	/**
	 * Creates a new tabbed content component.
	 *
	 * @param id {string}
	 *   HTML component element ID.
	 * @param layout {util.Layout.Layout}
	 *   Positioning of tabs relative to contents (default: `util.Layout.Layout.BOTTOM`).
	 * @param tabsParent {string}
	 *   Selector that should contain the tabs instead of main component (default: `undefined`).
	 */
	constructor(id: string, layout: Layout=Layout.BOTTOM, tabsParent?: string) {
		super(id);
		this.tabs = new TabPanelComponent(layout);
		if (tabsParent) {
			this.child(tabsParent)!.appendChild(this.tabs.componentElement);
		} else {
			this.componentElement.appendChild(this.tabs.componentElement);
		}
	}

	/**
	 * Adds a new tab.
	 *
	 * @param label {string}
	 *   Tab button label.
	 * @param content {ui.toolkit.Component.Component}
	 *   Contents to display when this tab is active (default: `undefined`).
	 */
	addTab(label: string, content?: Component) {
		this.tabs.addTab(label, content);
	}
}
