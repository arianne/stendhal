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

import { WidgetComponent } from "./WidgetComponent";

import { OptionsEnum } from "../../data/enum/OptionsEnum";
import { WidgetType } from "../../data/enum/WidgetType";


/**
 * Unthemed component representing a configuration setting.
 *
 * TODO:
 * - rename, such as "InputComponent" so not exclusively used for "settings" or separate into
 *   individual classes
 */
export class SettingsComponent extends WidgetComponent {

	/** The `HTMLElement` associated with this component. */
	override readonly componentElement!: HTMLElement;
	/** Text description. */
	override readonly labelElement: HTMLLabelElement;


	/**
	 * Creates a new settings component.
	 *
	 * @param {string} id
	 *   DOM element ID.
	 * @param {string} label
	 *   Text label to display.
	 * @param {WidgetType} [_type=WidgetType.CHECK]
	 *   The type of this component. E.g. check box, text input, etc.
	 * @param {OptionsEnum} [options={}]
	 *   Options for multi-select enumeration.
	 * @param {boolean} [experimental=false]
	 *   Marks this element to be hidden unless settings debugging is enabled.
	 */
	constructor(id: string, label: string, _type=WidgetType.CHECK, options: OptionsEnum={},
			experimental=false) {
		super(_type);
		// create label first
		this.labelElement = document.createElement("label") as HTMLLabelElement;

		if (WidgetType.SELECT.equals(_type)) {
			this.componentElement = this.initSelect(id, label, options);
		} else {
			this.componentElement = this.initInput(id, label);
		}
		// listen for changes to component element
		this.initChangeListener();

		if (experimental) {
			this.componentElement.classList.add("experimental");
		}
	}

	/**
	 * Initializes main component element as input type.
	 *
	 * @param {string} id
	 *   DOM element ID.
	 * @param {string} label
	 *   Text label to display.
	 * @returns {HTMLElement}
	 *   Main component element.
	 */
	private initInput(id: string, label: string): HTMLElement {
		let componentElement: HTMLElement;
		switch(this._type) {
			case WidgetType.CHECK:
				this.labelElement.classList.add("checksetting");
				const inputHTML = "<input type=\"" + this._type.value + "\" id=\"" + id + "\">";
				this.labelElement.innerHTML = inputHTML + label;
				componentElement = this.labelElement.querySelector("#" + id)!;
				break;
			default:
				this.labelElement.htmlFor = id;
				this.labelElement.innerText = label;
				componentElement = document.createElement("input");
				const inputElement = (componentElement as HTMLInputElement);
				inputElement.type = this._type.value;
				inputElement.id = id;
		}
		return componentElement;
	}

	/**
	 * Initializes main component element as select type.
	 *
	 * @param {string} id
	 *   DOM element ID.
	 * @param {string} label
	 *   Text label to display.
	 * @param {OptionsEnum} [options={}]
	 *   Options for multi-select enumeration.
	 * @returns {HTMLElement}
	 *   Main component element.
	 */
	private initSelect(id: string, label: string, options: OptionsEnum={}): HTMLElement {
		this.labelElement.htmlFor = id;
		this.labelElement.innerText = label;
		const componentElement = document.createElement("select");
		componentElement.id = id;

		// populate options
		for (const ol in options) {
			this.addOption(ol, options[ol]);
		}
		this.setSelected(0);
		return componentElement;
	}

	/**
	 * Sets selected index.
	 *
	 * @param {number} idx
	 *   Index to set as selected.
	 */
	setSelected(idx: number) {
		if (!WidgetType.SELECT.equals(this._type)) {
			throw new Error("Settings component of type \"" + this._type.value
					+ "\" does not support index selection");
		}
		const selectElement = this.componentElement as HTMLSelectElement;
		if (selectElement.options.length <= idx) {
			console.warn("Invalid index of HTMLSelectElement");
			return;
		}
		if (idx > -1) {
			selectElement.selectedIndex = idx;
		}
	}

	/**
	 * Sets value of component element.
	 *
	 * @param {string|number|boolean} value
	 *   New value to be used.
	 */
	setValue(value: string|number|boolean) {
		switch(this._type) {
			case WidgetType.SELECT:
				const options = Array.from((this.componentElement as HTMLSelectElement).options)
						.map(o => o.value);
				const idx = typeof(value) === "number" ? value as number : options.indexOf(value as string);
				this.setSelected(idx);
				break;
			case WidgetType.CHECK:
				(this.componentElement as HTMLInputElement).checked = value as boolean;
				break;
			default:
				// assume default to be text or numeric
				(this.componentElement as HTMLInputElement).value = value as string;
		}
	}

	/**
	 * Adds a selectable option.
	 *
	 * @param {string} label
	 *   Displayed text.
	 * @param {string} [value=""]
	 *   Value the option represents.
	 */
	addOption(label: string, value="") {
		if (!WidgetType.SELECT.equals(this._type)) {
			throw new Error("Settings component of type \"" + this._type.value
					+ "\" does not support adding selection options");
		}
		if (!value) {
			// use label as value default
			value = label;
		}
		const selectElement = this.componentElement as HTMLSelectElement;
		const opt = document.createElement("option");
		opt.value = value;
		opt.textContent = label;
		selectElement.appendChild(opt);
	}
}