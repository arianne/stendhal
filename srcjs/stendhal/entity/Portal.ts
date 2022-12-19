/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { MenuItem } from "../action/MenuItem";
import { Entity } from "./Entity";

import { Color } from "../util/Color";

declare var marauroa: any;
declare var stendhal: any;

export class Portal extends Entity {

	override minimapShow = true;
	override minimapStyle = Color.BLACK;
	override zIndex = 5000;

	override buildActions(list: MenuItem[]) {
		super.buildActions(list);

		if (this["_rpclass"] == "house_portal") {
			list.push({
				title: "Use",
				type: "use"
			});
			list.push({
				title: "Knock",
				type: "knock"
			});

		} else {

			// remove default action "look" unless it is a house portal
			list.splice(list.indexOf({title: "Look", type: "look"}), 1);

			list.push({
				title: "Use",
				type: "use"
			});
		}
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}


	/**
	 * Create the default action for this entity. If the entity specifies a
	 * default action description, interpret it as an action command.
	 */
	override getDefaultAction() {
		return {
			"type": "moveto",
			"x": "" + this["x"],
			"y": "" + this["y"],
			"zone": marauroa.currentZoneName
		} as any;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + stendhal.paths.sprites + "/cursor/portal.png) 1 3, auto";
	}

}
