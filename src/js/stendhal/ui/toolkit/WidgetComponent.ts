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

import { SettingsType } from "../../data/enum/SettingsType";


/**
 * Represents a component for configuring values.
 */
export abstract class WidgetComponent extends ComponentBase {

	/** Text description. */
	abstract labelElement: HTMLLabelElement;
	/** Setting type. */
	protected readonly _type: SettingsType;
	/** Called when the settings state or value changes. */
	public onchange?: Function;

	private initialized = false;


	constructor(_type: SettingsType) {
		super();
		this._type = _type;
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
			if (this.onchange) {
				this.onchange(evt);
			}
		});
	}

	/**
	 * Adds as child to DOM element.
	 *
	 * @param parent {ui.toolkit.ComponentBase.ComponentBase|HTMLElement}
	 *   Component or element to which to add.
	 */
	override addTo(parent: ComponentBase|HTMLElement) {
		const isComponent = parent instanceof ComponentBase;
		this.parentComponent = isComponent ? parent as ComponentBase : undefined;
		const parentElement = isComponent ? this.parentComponent!.componentElement
				: parent as HTMLElement;
		parentElement.appendChild(this.labelElement);
		if (!SettingsType.CHECK.equals(this._type)) {
			// check box component element is already child of label
			parentElement.appendChild(this.componentElement);
		}
	}
}
