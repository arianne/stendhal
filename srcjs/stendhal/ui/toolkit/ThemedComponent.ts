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

declare var stendhal: any;


/**
 * Component representation that applies the current theme.
 */
export class ThemedComponent extends Component {

	constructor(id: string) {
		super(id);
		this.componentElement.classList.add("background");
		this.applyTheme();
	}

	/**
	 * Applies the current theme to the main element.
	 */
	protected applyTheme() {
		stendhal.config.applyTheme(this.componentElement);
	}

	/**
	 * Applies the current theme to the main element & all
	 * its children recursively.
	 */
	protected applyThemeRecursive() {
		stendhal.config.applyTheme(this.componentElement, true, true);
	}
}
