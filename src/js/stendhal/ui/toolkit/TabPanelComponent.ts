/***************************************************************************
 *                (C) Copyright 2015-2024 - Faiumoni e. V.                 *
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

import { Layout } from "../../util/Layout";


/**
 * a tabbed panel
 */
export class TabPanelComponent extends Panel {

	/** Index value of currently visible tab. */
	private currentIndex = 0;
	/** Button representation for each tab. */
	private buttons: HTMLButtonElement[] = [];

	/** Optional instructions to execute when active tab changes. */
	public onTabChanged?: Function;


	/**
	 * Creates a new tabbed panel.
	 *
	 * @param layout {util.Layout.Layout}
	 *   Positioning of tabs relative to contents.
	 */
	constructor(layout: Layout) {
		super("tabpanel-template");
		this.containerElement = this.child(".tabpanel-content")!;
		const tabs = this.child(".tabpanel-tabs")! as HTMLElement;
		if (Layout.TOP.equals(layout)) {
			tabs.classList.add("tabpanel-tabs-top");
		} else if (Layout.BOTTOM.equals(layout)) {
			tabs.classList.add("tabpanel-tabs-bottom");
			// move to end of elements list
			this.componentElement.appendChild(tabs);
		} else if (Layout.LEFT.equals(layout)) {
			this.componentElement.className = "tabpanel-h";
			tabs.classList.add("tabpanel-tabs-h");
			tabs.classList.add("tabpanel-tabs-left");
		} else if (Layout.RIGHT.equals(layout)) {
			this.componentElement.className = "tabpanel-h";
			tabs.classList.add("tabpanel-tabs-h");
			tabs.classList.add("tabpanel-tabs-right");
			// move to end of elements list
			this.componentElement.appendChild(tabs);
		} else {
			console.warn("unsupported layout:", layout);
		}
	}

	override add(child: Component) {
		if (this.children.length != this.currentIndex) {
			child.componentElement.style.display = "none";
		}
		super.add(child);
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
		let button = document.createElement("button");
		button.innerText = label;
		button.dataset.index = "" + this.child(".tabpanel-tabs")!.children.length;
		button.addEventListener("click", (e) => {
			this.onTabClick(e);
		})
		if (this.buttons.length === 0) {
			button.classList.add("active");
		}
		this.buttons.push(button);
		this.child(".tabpanel-tabs")!.append(button);
		if (content) {
			this.add(content);
		}
	}

	/**
	 * Sets the visible child panel.
	 *
	 * @param idx
	 *     Index of child panel to show.
	 */
	setCurrentTab(idx: number) {
		if (idx == this.currentIndex) {
			return;
		}

		// hide
		this.children[this.currentIndex].componentElement.style.display = "none";
		this.buttons[this.currentIndex].classList.remove("active");

		// select
		this.currentIndex = idx;

		// show
		this.children[this.currentIndex].componentElement.style.display = "";
		this.buttons[this.currentIndex].classList.add("active");

		if (this.onTabChanged) {
			this.onTabChanged();
		}
	}

	/**
	 * Retrieves index value of current tab.
	 *
	 * @return {number}
	 *   Indexed number value.
	 */
	getCurrentIndex(): number {
		return this.currentIndex;
	}

	onTabClick(event: Event) {
		this.setCurrentTab(Number.parseInt((event.target as HTMLElement).dataset.index!, 10));
	}
}
