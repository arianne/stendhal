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

import { Component } from "./Component";
import { Panel } from "./Panel";

/**
 * a tabbed panel
 */
export class TabPanelComponent extends Panel {
	private currentIndex = 0;
	private buttons: HTMLButtonElement[] = [];

	/**
	 * creates a new panel
	 *
	 * @param id id of HTML element or template
	 */
	constructor() {
		super("tabpanel-template");
		this.containerElement = this.child(".tabpanel-content")!;
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
	}

	onTabClick(event: Event) {
		this.setCurrentTab(Number.parseInt((event.target as HTMLElement).dataset.index!, 10));
	}
}
