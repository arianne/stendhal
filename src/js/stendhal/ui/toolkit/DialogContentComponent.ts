/***************************************************************************
 *                    Copyright © 2003-2024 - Arianne                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "./Component";
import { FloatingWindow } from "./FloatingWindow";

/**
 * Component representing the contents of a floating dialog.
 */
export abstract class DialogContentComponent extends Component {

	protected frame?: FloatingWindow;


	constructor(id: string) {
		super(id, true);
		this.componentElement.classList.add("dialogcontents");
	}

	/**
	 * Sets the closable dialog frame.
	 */
	public setFrame(frame: FloatingWindow) {
		this.frame = frame;
	}

	public getFrame(): FloatingWindow|undefined {
		return this.frame;
	}

	/**
	 * Closes the containing FloatingWindow.
	 */
	public close() {
		if (this.frame) {
			this.frame.close();
		}
	}

	/**
	 * Adds a dialog button.
	 *
	 * @param label {string}
	 *   Text displayed on button.
	 * @param action {Function}
	 *   Action to execute when button is pressed (default: `undefined`).
	 * @param id {string}
	 *   HTML element ID (default: `undefined`).
	 * @return {HTMLButtonElement}
	 *   The created button.
	 */
	addButton(label: string, action?: EventListener, id?: string): HTMLButtonElement {
		let buttonContainer = this.child(".dialogbuttons");
		if (typeof(buttonContainer) === "undefined" || buttonContainer == null) {
			buttonContainer = document.createElement("div");
			buttonContainer.classList.add("horizontalgroup dialogbuttons");
			this.componentElement.appendChild(buttonContainer);
		}
		let button: HTMLButtonElement|undefined = undefined;
		if (id) {
			button = this.child("#" + id) as HTMLButtonElement|undefined;
		}
		if (typeof(button) === "undefined" || button == null) {
			button = document.createElement("button");
		}
		button.id = id ? id : button.id;
		button.classList.add("dialogbutton");
		button.innerText = label;
		if (action) {
			button.onclick = action!;
		}
		buttonContainer.appendChild(button);
		return button;
	}

	/**
	 * Adds a dialog button that will close the window.
	 *
	 * @param id {string}
	 *   HTML element ID (default: `undefined`).
	 * @return {HTMLButtonElement}
	 *   The created button.
	 */
	addCloseButton(id?: string): HTMLButtonElement {
		return this.addButton("Close", () => {
			this.close();
		}, id ? id : "button-close");
	}
}
