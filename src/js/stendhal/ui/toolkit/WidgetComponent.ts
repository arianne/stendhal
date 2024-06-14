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

import { ComponentBase } from "./ComponentBase";

import { WidgetType } from "../../data/enum/WidgetType";

import { ConfigManager } from "../../util/ConfigManager";


/**
 * Represents a component for configuring values.
 */
export abstract class WidgetComponent extends ComponentBase {

	/** Text description. */
	abstract labelElement: HTMLLabelElement;
	/** Setting type. */
	protected readonly type: WidgetType;
	/** Called when the settings state or value changes. */
	private changeListeners: Function[];

	private initialized = false;


	/**
	 * Creates a widget component.
	 *
	 * @param {WidgetType} type
	 *   Widget type.
	 */
	constructor(type: WidgetType) {
		super();
		this.type = type;
		this.changeListeners = [];
	}

	/**
	 * Inheriting classes constructors should call this method after the component element is created.
	 */
	protected initChangeListener() {
		if (this.initialized) {
			console.warn("Tried to re-initialize widged component");
			return;
		}
		if (!this.componentElement) {
			throw new Error("WidgetComponent.componentElement not defined");
		}
		this.initialized = true;

		// listen for changes to component element
		this.componentElement.addEventListener("change", (evt: Event) => {
			for (const listener of this.changeListeners) {
				listener(evt);
			}
			this.refresh();
		});
	}

	/**
	 * Adds listener for change event.
	 *
	 * @param {Function} listener
	 *   Function to call when change event occurs.
	 * @return {Function}
	 */
	addListener(listener: Function): Function {
		this.changeListeners.push(listener);
		return listener;
	}

	/**
	 * Adds listener for change event.
	 *
	 * @param {number} idx
	 *   Insertion index in list of listeners.
	 * @param {Function} listener
	 *   Function to call when change event occurs.
	 * @return {Function}
	 */
	insertListener(idx: number, listener: Function): Function {
		this.changeListeners.splice(idx, 0, listener);
		return listener;
	}

	/**
	 * Removes change event listener.
	 *
	 * @param {number|Function} listener
	 *   Listener function or index.
	 */
	removeListener(listener: number|Function) {
		let idx: number;
		if (typeof(listener) === "number") {
			idx = listener as number;
		} else {
			idx = this.changeListeners.indexOf(listener as Function);
		}
		if (idx > -1 && idx < this.changeListeners.length) {
			this.changeListeners.splice(idx, 1);
		}
	}

	/**
	 * Adds as child to DOM element.
	 *
	 * @param {ComponentBase|HTMLElement} parent
	 *   Component or element to which to add.
	 */
	override addTo(parent: ComponentBase|HTMLElement) {
		const isComponent = parent instanceof ComponentBase;
		this.parentComponent = isComponent ? parent as ComponentBase : undefined;
		const parentElement = isComponent ? this.parentComponent!.componentElement
				: parent as HTMLElement;
		parentElement.appendChild(this.labelElement);
		if (WidgetType.CHECK !== this.type) {
			// check box component element is already child of label
			parentElement.appendChild(this.componentElement);
		}
	}

	/**
	 * Sets value of component element.
	 *
	 * @param {string|number|boolean} value
	 *   New value to be used.
	 */
	abstract setValue(value: string|number|boolean): void;

	/**
	 * Retrieves current value of component.
	 *
	 * @returns {string|number|boolean}
	 *   Component value.
	 */
	abstract getValue(): string|number|boolean;

	/**
	 * Updates value from configuration ID & adds listener to update configuration when value changes.
	 *
	 * @param {string} cid
	 *   Configuration ID.
	 */
	setConfigId(cid: string) {
		this.setValue(ConfigManager.get().get(cid) as string);
		this.setConfigListener(cid);
	}

	/**
	 * Adds listener to update configuration when value changes.
	 *
	 * @param {string} cid
	 *   Configuration ID.
	 */
	protected setConfigListener(cid: string) {
		// needs to be first listener called
		this.insertListener(0, (evt: Event) => {
			ConfigManager.get().set(cid, this.getValue());
		});
	}
}
