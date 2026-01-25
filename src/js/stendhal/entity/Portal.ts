/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Entity } from "./Entity";
import { MenuItem } from "../action/MenuItem";

import { Color } from "../data/color/Color";
import { Paths } from "../data/Paths";

import { marauroa } from "marauroa"
import { TileMap } from "data/TileMap";

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
		// don't try to walk to if on collision tile
		let map = TileMap.get();
		if (map.collision(this["x"], this["y"])) {
			return {
				"type": "use",
				"target": "#" + this["id"],
				"zone": marauroa.currentZoneName
			} as any;
		}
		return {
			"type": "moveto",
			"x": "" + this["x"],
			"y": "" + this["y"],
			"zone": marauroa.currentZoneName
		} as any;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/portal.png) 1 3, auto";
	}

}
