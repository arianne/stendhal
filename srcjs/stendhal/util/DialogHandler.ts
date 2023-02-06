/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { FloatingWindow } from "../ui/toolkit/FloatingWindow";


/**
 * Representation of an internal dialog window.
 */
export class DialogHandler {

	private content?: FloatingWindow;


	/**
	 * Sets the dialog window content.
	 *
	 * @param c
	 *     The FloatingWindow instance to be set.
	 */
	set(c: FloatingWindow) {
		//~ if (!c) {
			//~ console.error("cannot set dialogHandler content to \""
					//~ + typeof(c) + "\", must be FloatingWindow instance");
			//~ return;
		//~ }

		// make sure this is closed before opening again
		if (this.isOpen()) {
			this.close();
		}

		this.content = c;
		this.content.componentElement.addEventListener("mousedown", function(e: MouseEvent) {
			// prevent global handler from closing when clicked on
			e.preventDefault();
			e.stopPropagation();
		});
	}

	/**
	 * Sets the content to <code>null</code>.
	 */
	unset() {
		this.content = undefined;
	}

	/**
	 * Retrieves the window.
	 *
	 * @return
	 *     FloatingWindow instance or <code>null</code> if not set.
	 */
	get(): FloatingWindow|undefined {
		return this.content;
	}

	/**
	 * Checks if the action context menu is open.
	 *
	 * @return
	 *     The FloatingWindow open state.
	 */
	isOpen(): boolean {
		return typeof(this.content) !== "undefined" && this.content.isOpen();
	}

	/**
	 * Closes the dialog window.
	 *
	 * @param unset
	 *     If <code>true</code>, resets content to <code>null</code>.
	 */
	close(unset=false) {
		if (!this.content) {
			return;
		}
		this.content.close();
		if (unset) {
			this.unset();
		}
	}
}
