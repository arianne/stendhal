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


/**
 * Base component class.
 */
export abstract class ComponentBase {

	/** The `HTMLElement` associated with this component. */
	abstract componentElement: HTMLElement;
	/** The parent `Component` of this one. Usually a floating window. */
	parentComponent?: ComponentBase;


	/**
	 * Instructions when component should be refreshed.
	 */
	public refresh() {
		// does nothing in the implementation
	};

	/**
	 * Adds as child to DOM element.
	 *
	 * @param parent {ui.toolkit.ComponentBase.ComponentBase|HTMLElement}
	 *   Component or element to which to add.
	 */
	addTo(parent: ComponentBase|HTMLElement) {
		const isComponent = parent instanceof ComponentBase;
		this.parentComponent = isComponent ? parent as ComponentBase : undefined;
		const parentElement = isComponent && this.parentComponent
				? this.parentComponent.componentElement : parent as HTMLElement;
		parentElement.appendChild(this.componentElement);
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
	 *
	 * @return {boolean}
	 *   `true` if the document's active element is this component's element.
	 */
	hasFocus(): boolean {
		return document.activeElement == this.componentElement;
	}

	/**
	 * Enables or disables the HTML element.
	 *
	 * @param {boolean} enabled
	 *   Enabled state to be set.
	 */
	setEnabled(enabled: boolean) {
		(this.componentElement as any).disabled = !enabled;
	}
}
