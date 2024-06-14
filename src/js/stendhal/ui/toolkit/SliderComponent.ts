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

import { WidgetType } from "../../data/enum/WidgetType";

import { ConfigManager } from "../../util/ConfigManager";


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
	 * @param {string} id
	 *   DOM element ID.
	 * @param {string} label
	 *   Text label to display.
	 * @param {number} min
	 *   Lowest possible value this component can represent.
	 * @param {number} max
	 *   Highest possible value this component can represent.
	 * @param {boolean} [experimental=false]
	 *   Marks this element to be hidden unless settings debugging is enabled.
	 */
	constructor(id: string, label: string, min: number, max: number, experimental=false) {
		super(WidgetType.SLIDER);

		this.labelElement = document.createElement("label");
		this.labelElement.htmlFor = id;
		this.labelElement.innerText = label;
		const componentElement = document.createElement("input") as HTMLInputElement;
		componentElement.type = this.type;
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
	 * @param {number} value
	 *   Current value.
	 */
	override setValue(value: number) {
		(this.componentElement as HTMLInputElement).value = ""+value;
	}

	/**
	 * Retrieves component's current value representation.
	 *
	 * @returns {number}
	 *   Current value.
	 */
	override getValue(): number {
		return parseInt((this.componentElement as HTMLInputElement).value, 10);
	}

	override setConfigId(cid: string) {
		this.setValue(ConfigManager.get().getInt(cid));
		this.setConfigListener(cid);
	}
}
