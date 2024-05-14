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

import { SettingsType } from "../../data/enum/SettingsType";


/**
 * Unthemed component representing a slider widget and its label.
 */
export class SliderComponent extends WidgetComponent {

	/** The `HTMLElement` associated with this component. */
	override readonly componentElement: HTMLElement;
	/** Text description. */
	override readonly labelElement: HTMLLabelElement;


	/**
	 * Creates a new slider settings component.
	 *
	 * @param id {string}
	 *   DOM element ID.
	 * @param label {string}
	 *   Text label to display.
	 * @param min {number}
	 *   Lowest possible value this component can represent.
	 * @param max {number}
	 *   Highest possible value this component can represent.
	 * @param experimental {boolean}
	 *   Marks this element to be hidden unless settings debugging is enabled.
	 */
	constructor(id: string, label: string, min: number, max: number, experimental=false) {
		super(SettingsType.SLIDER);

		this.labelElement = document.createElement("label");
		this.labelElement.htmlFor = id;
		this.labelElement.innerText = label;
		const componentElement = document.createElement("input") as HTMLInputElement;
		componentElement.type = this._type.value;
		componentElement.id = id;
		componentElement.min = ""+min;
		componentElement.max = ""+max;
		this.componentElement = componentElement;
		// listen for changes to component element
		this.initChangeListener();

		if (experimental) {
			this.componentElement.classList.add("experimental");
		}
	}

	/**
	 * Sets component's current value representation.
	 *
	 * @param value {number}
	 *   Current value.
	 */
	public setValue(value: number) {
		(this.componentElement as HTMLInputElement).value = ""+value;
	}

	/**
	 * Retrieves component's current value representation.
	 *
	 * @return {number}
	 *   Current value.
	 */
	public getValue(): number {
		return parseInt((this.componentElement as HTMLInputElement).value, 10);
	}
}
