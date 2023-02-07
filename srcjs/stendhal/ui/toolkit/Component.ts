/***************************************************************************
 *                (C) Copyright 2022-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare let stendhal: any;


export abstract class Component {

	readonly componentElement!: HTMLElement;
	public parentComponent?: Component;
	public cid?: string;
	private defaultDisplay: string;


	constructor(id: string) {
		let element = document.getElementById(id);
		if (!element) {
			throw new Error("Cannot create component because there is no HTML element with id " + id);
		}

		if (element instanceof HTMLTemplateElement) {
			let fragment = element.content.cloneNode(true) as DocumentFragment
			element = fragment.children[0] as HTMLElement;
		}

		this.componentElement = element;
		this.defaultDisplay = element.style.display;
		if (this.defaultDisplay === "none") {
			this.defaultDisplay = "";
		}
	}

	public onParentClose() {
		// do nothing
	};

	public onMoved() {
		// do nothing
	};

	public refresh() {
		// inheriting classes can override
	};

	/**
	 * Sets value for configuration to identify this component.
	 *
	 * @param cid
	 *     The string identifier.
	 */
	public setConfigId(cid: string) {
		this.cid = cid;
	}

	/**
	 * Retrieves the string identifier for this component. If
	 * an identifier is not set, an empty string is returned.
	 *
	 * @return
	 *     String identifier.
	 */
	public getConfigId(): string {
		return this.cid || "";
	}

	public isVisible(): boolean {
		return this.componentElement.style.display !== "none";
	}

	public setVisible(visible=true) {
		if (visible) {
			this.componentElement.style.display = this.defaultDisplay;
		} else {
			this.componentElement.style.display = "none";
		}
	}

	public toggleVisibility() {
		this.setVisible(!this.isVisible());
	}
}
