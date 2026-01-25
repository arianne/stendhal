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

import { Paths } from "../data/Paths";

import { marauroa } from "marauroa"
import { stendhal } from "../stendhal";
import { TileMap } from "data/TileMap";


/**
 * a pseudo entity which represents the ground
 */
export class Ground {

	isVisibleToAction(filter: boolean): boolean {
		return false;
	}

	/**
	 * Calculates whether the click was close enough to a zone border to trigger
	 * a zone change.
	 *
	 * @param x x of click point in world coordinates
	 * @param y y of click point in world coordinates
	 * @return Direction of the zone to change to, <code>null</code> if no zone change should happen
	 */
	private calculateZoneChangeDirection(x: number, y: number): string|null {
		let map = TileMap.get();
		if (x < 15) {
			return "4"; // LEFT
		}
		if (x > map.zoneSizeX * 32 - 15) {
			return "2"; // RIGHT
		}
		if (y < 15) {
			return "1"; // UP
		}
		if (y > map.zoneSizeY * 32 - 15) {
			return "3"; // DOWN
		}
		return null;
	}

	getCursor(x: number, y: number): string {
		let map = TileMap.get();
		if ((x < 15) || (y < 15) || (x > map.zoneSizeX * 32 - 15) || (y > map.zoneSizeY * 32 - 15)) {
			return "url(" + Paths.sprites + "/cursor/walkborder.png) 1 3, auto"
		}
		var worldX = Math.floor(x / 32);
		var worldY = Math.floor(y / 32);
		if (map.collision(worldX, worldY)) {
			return "url(" + Paths.sprites + "/cursor/stop.png) 1 3, auto";
		}
		return "url(" + Paths.sprites + "/cursor/walk.png) 1 3, auto";
	}

	onclick(x: number, y: number, dblclick: boolean) {
		if (!stendhal.config.getBoolean("pathfinding")) {
			return;
		}
		var gameX = x + stendhal.ui.gamewindow.offsetX;
		var gameY = y + stendhal.ui.gamewindow.offsetY;
		var action = {
			"type": "moveto",
			"x": "" + Math.floor(gameX / 32),
			"y": "" + Math.floor(gameY / 32)
		} as any;

		if (typeof dblclick == "boolean" && dblclick) {
			action["double_click"] = "";
		}

		var extend = this.calculateZoneChangeDirection(gameX, gameY);
		if (extend) {
			action.extend = extend;
		}
		marauroa.me.moveTo(action);
	}
}
