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


export abstract class Component {

	/** The `HTMLElement` associated with this component. */
	readonly componentElement!: HTMLElement;
	/** The parent `Component` of this one. Usually a floating window. */
	public parentComponent?: Component;
	/** @deprecated */
	public cid?: string;
	/** Default display type when component is visible. */
	private visibleDisplay: string;


	/**
	 * Creates a new component.
	 *
	 * @param el
	 *   DOM element or ID of HTML element or template.
	 * @param themable
	 *   Inherits theming visuals (default: `false`).
	 */
	constructor(el: string|HTMLElement, themable=false) {
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

	public refresh() {
		// inheriting classes can override
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

	/**
	 * Retrieves a child element.
	 *
	 * @param selector
	 *   Child element's identification string.
	 * @return
	 *   `HTMLElement` or `undefined` if child not found.
	 */
	protected child(selector: string): HTMLElement|undefined {
		return this.componentElement.querySelector(selector) as HTMLElement|undefined;
	}

	/**
	 * Checks if the associated element currently has focus.
	 */
	public hasFocus(): boolean {
		return document.activeElement == this.componentElement;
	}
}
