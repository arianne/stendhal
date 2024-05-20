/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { SlashAction } from "./SlashAction";

/**
 * opens the specified website in the browser
 */
export class OpenWebsiteAction extends SlashAction {
	readonly minParams = 0;
	readonly maxParams = 0;

	/**
	 * creates a OpenWebsiteAction
	 *
	 * @param url website to open
	 */
	constructor(private url: string) {
		super();
	}

	execute(_type: string, _params: string[], _remainder: string): boolean {
		window.location.href = this.url;
		return true;
	}

};
