/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { QuickMenuButton } from "./QuickMenuButton";
import { FacingHandler } from "../../util/FacingHandler";


/**
 * Button to rotate character clockwise.
 */
export class RotateRButton extends QuickMenuButton {

	constructor() {
		super("rotate-r");
	}

	/**
	 * Rotates character clockwise when clicked/tapped.
	 */
	protected override onClick(evt: Event) {
		FacingHandler.get().turnClockwise();
	}
}
