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

import { QuickMenuButton } from "./QuickMenuButton";

import { UIComponentEnum } from "../UIComponentEnum";

import { stendhal } from "../../stendhal";


/**
 * Button to toggle pathfinding via ground.
 */
export class PathFindingButton extends QuickMenuButton {

	constructor() {
		super("pathfinding", UIComponentEnum.QMPathFinding);
	}

	/**
	 * Updates button image.
	 */
	public override update() {
		this.setImageBasename(stendhal.config.getBoolean("pathfinding") ? "pathfinding"
				: "pathfinding-disabled");
	}

	/**
	 * Toggles pathfinding when clicked/tapped.
	 */
	protected override onClick(evt: Event) {
		stendhal.config.set("pathfinding", !stendhal.config.getBoolean("pathfinding"));
		this.update();
	}
}
