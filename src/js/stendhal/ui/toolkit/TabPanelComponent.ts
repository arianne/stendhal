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
	 *   Positioning of tabs relative to contents (default: `util.Layout.Layout.BOTTOM`).
	 */
	constructor(layout: Layout=Layout.BOTTOM) {
		super("tabpanel-template");
		this.containerElement = this.child(".tabpanel-content")!;
		const tabs = this.child(".tabpanel-tabs")! as HTMLElement;
		if (Layout.BOTTOM.equals(layout)) {
			tabs.classList.add("tabpanel-tabs-bottom");
		} else if (Layout.TOP.equals(layout)) {
			tabs.classList.add("tabpanel-tabs-top");
			this.componentElement.removeChild(tabs);
			this.componentElement.insertBefore(tabs, this.containerElement);
		} else {
			// currently only supports positioning tabs at top & bottom
			console.warn("unsupported layout:", layout);
		}
	}

	override add(child: Component) {
		if (this.children.length != this.currentIndex) {
			child.componentElement.style.display = "none";
		}
		super.add(child);
	}

	addTab(label: string) {
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
		this.children[this.currentIndex].componentElement.style.display = "block";
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
