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

import { Chat } from "./Chat";


/**
 * Class for displaying some commonly used messages in chat log.
 */
export class StandardMessages {

	/**
	 * Static class.
	 */
	private constructor() {}

	/**
	 * Message when a change is made to the client that won't take effect until after player changes
	 * maps or the page is reloaded.
	 */
	public static changeNeedsRefresh() {
		Chat.log("client", "Changes will take effect after changing maps or reloading page.");
	}
}
