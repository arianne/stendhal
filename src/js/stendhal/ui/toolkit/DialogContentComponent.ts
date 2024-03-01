/***************************************************************************
 *                    Copyright © 2003-2023 - Arianne                      *
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

declare let stendhal: any;


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
}
