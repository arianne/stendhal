/***************************************************************************
 *                       Copyright © 2024 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { QuickMenuButton } from "./QuickMenuButton";

import { SlashActionRepo } from "../../SlashActionRepo";


/**
 * Button to bring up travel log dialog window.
 */
export class TravelLogButton extends QuickMenuButton {

	constructor() {
		super("log");
	}

	/**
	 * Opens travel log window when clicked/tapped.
	 */
	protected override onClick(evt: Event) {
		SlashActionRepo.get().execute("/progressstatus");
	}
}
