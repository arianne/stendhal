/***************************************************************************
 *                   (C) Copyright 2005-2026 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "stendhal";
import { RPEvent } from "marauroa"

/**
 * displays an achievement banner
 */
export class ReachedAchievementEvent extends RPEvent {

	public category!: string;
	public title!: string;
	public description!: string;

	public execute(_entity: any): void {
		stendhal.ui.gamewindow.addAchievementNotif(this["category"], this["title"], this["description"]);
	}
};
