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

import { NPC } from "./NPC";

declare var marauroa: any;
declare var stendhal: any;


export class TrainingDummy extends NPC {

	/**
	 * Default action when clicked.
	 */
	override onclick(_x: number, _y: number) {
		var action = {
				"type": "attack",
				"target": "#" + this["id"]
			};
		marauroa.clientFramework.sendAction(action);
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + stendhal.paths.sprites + "/cursor/attack.png) 1 3, auto";
	}
}
