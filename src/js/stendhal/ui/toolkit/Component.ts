/***************************************************************************
 *                (C) Copyright 2022-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ComponentBase } from "./ComponentBase";


export abstract class Component extends ComponentBase {

	/** The `HTMLElement` associated with this component. */
	override readonly componentElement!: HTMLElement;
	/** @deprecated */
	public cid?: string;
	/** Default display type when component is visible.
	 *  NOTE: this might not be necessary
	 */
	private visibleDisplay: string;


	/**
	 * Creates a new component.
	 *
	 * @param {string|HTMLElement} el
	 *   DOM element or ID of HTML element or template.
	 * @param {boolean} [themable=false]
	 *   Inherits theming visuals.
	 */
	constructor(el: string|HTMLElement, themable=false) {
		super();
		let element: HTMLElement|null;
		if (typeof(el) === "string") {
			const id = el as string;
			element = document.getElementById(id);
			if (!element) {
				throw new Error("Cannot create component because there is no HTML element with id " + id);
			}
		} else {
			element = el as HTMLElement;
		}

		if (element instanceof HTMLTemplateElement) {
			let fragment = element.content.cloneNode(true) as DocumentFragment
			element = fragment.children[0] as HTMLElement;
		}

		this.componentElement = element;
		if (themable) {
			this.componentElement.classList.add("background");
		}
		// store initial display property value
		this.visibleDisplay = this.getDisplayProperty();
		if (this.visibleDisplay === "none") {
			// element is initially hidden so we need to set visible display value manually
			this.visibleDisplay = "block";
		}
	}

	/**
	 * Called when parent `FloatingWindow` is closed.
	 */
	public onParentClose() {
		// do nothing
	};

	/**
	 * Called when parent `FloatingWindow` is moved.
	 */
	public onMoved() {
		// do nothing
	};

	/**
	 * Sets value for configuration to identify this component.
	 *
	 * @param cid
	 *     The string identifier.
	 * @deprecated
	 */
	public setConfigId(cid: string) {
		this.cid = cid;
	}

	/**
	 * Retrieves the string identifier for this component. If
	 * an identifier is not set, an empty string is returned.
	 *
	 * @return
	 *   String identifier.
	 * @deprecated
	 */
	public getConfigId(): string {
		return this.cid || "";
	}

	/**
	 * Retrieves display type of element.
	 *
	 * @return
	 *   Current display.
	 */
	private getDisplayProperty(): string {
		return getComputedStyle(this.componentElement).getPropertyValue("display");
	}

	/**
	 * Checks if the element is in a visible state.
	 *
	 * @return
	 *   `true` if the element is visible in the DOM.
	 */
	public isVisible(): boolean {
		return this.getDisplayProperty() !== "none";
	}

	/**
	 * Sets the element's visibility.
	 *
	 * @param visible
	 *   `true` if the element should be visible.
	 */
	public setVisible(visible=true) {
		this.componentElement.style.setProperty("display", visible ? this.visibleDisplay : "none");
	}

	/**
	 * Toggles the element's visibility.
	 */
	public toggleVisibility() {
		this.setVisible(!this.isVisible());
	}
}
