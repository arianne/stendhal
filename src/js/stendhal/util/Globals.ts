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

import { ConfigManager } from "./ConfigManager";
import { SessionManager } from "./SessionManager";


export namespace Globals {

	const config = ConfigManager.get();
	const session = SessionManager.get();

	/**
	 * Retrieves menu style from session/config.
	 *
	 * @returns {string}
	 *   Either "traditional" or "floating".
	 */
	export function getMenuStyle(): string {
		return !config.isSet("menu.style") && session.touchOnly() ? "floating"
				: config.get("menu.style") || "traditional";
	};
}
