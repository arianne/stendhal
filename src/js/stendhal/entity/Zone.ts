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

import { marauroa } from "marauroa"
import { stendhal } from "../stendhal";

import { Entity } from "./Entity";
import { Player } from "./Player";


/**
 * stendhal zone
 */
export class Zone {

	/** Entities found in this zone. */
	private entities: Entity[] = [];

	/** Singleton instance. */
	private static instance: Zone;


	/**
	 * Retrieves singleton instance.
	 */
	public static get(): Zone {
		if (!Zone.instance) {
			Zone.instance = new Zone();
		}
		return Zone.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	getEntitiesAt(x: number, y: number, filter: boolean): Entity[] {
		const xGrid = x / 32;
		const yGrid = y / 32;
		const entities = [] as Entity[];
		for (const i in this.entities) {
			const obj = this.entities[i];
			if (obj.isVisibleToAction(filter)
					&& (obj["_x"] <= xGrid) && (obj["_y"] <= yGrid)
					&& (obj["_x"] + (obj["width"] || 1) >= xGrid)
					&& (obj["_y"] + (obj["height"] || 1) >= yGrid)) {

				entities.push(obj);
			}
		}

		return entities;
	}

	entityAt(x: number, y: number, filter: boolean): Entity {
		let res = stendhal.zone.ground;
		for (const obj of this.getEntitiesAt(x, y, filter)) {
			res = obj;
		}

		// If we found an entity, return it
		if (res != stendhal.zone.ground) {
			return res;
		}

		// Otherwise we check the draw area
		for (var i in this.entities) {
			let obj = this.entities[i];
			if (!obj.isVisibleToAction(filter) || !obj["drawHeight"]) {
				continue;
			}
			let localX = obj["_x"] * 32;
			let localY = obj["_y"] * 32;
			let drawHeight = obj["drawHeight"];
			let drawWidth = obj["drawWidth"];
			var drawX = ((obj["width"] * 32) - drawWidth) / 2;
			var drawY = (obj["height"] * 32) - drawHeight;

			if ((localX + drawX <= x) && (localX + drawX + drawWidth >= x)
				&& (localY + drawY <= y) && (localY + drawY + drawHeight >= y)) {

				res = obj;
			}
		}

		return res;
	}

	sortEntities() {
		this.entities = [];
		for (var i in marauroa.currentZone) {
			if (marauroa.currentZone.hasOwnProperty(i) && typeof(marauroa.currentZone[i]) != "function") {
				this.entities.push(marauroa.currentZone[i]);
			}
		}

		this.entities.sort(function(entity1, entity2) {
			var rv = entity1.zIndex - entity2.zIndex;
			if (rv == 0) {
				rv = (entity1.y + entity1.height) - (entity2.y + entity2.height);
				if (rv == 0) {
					rv = entity1.id - entity2.id;
				}
			}

			return rv;
		});
	}

	/**
	 * Finds player instance in zone.
	 *
	 * @param name {string}
	 *   Player name to search for.
	 * @return {Player}
	 *   Player instance or `undefined`.
	 */
	findPlayer(name: string): Player|undefined {
		for (const ent of this.entities) {
			if (ent instanceof Player && ent["name"] === name) {
				return ent as Player;
			}
		}
	}
}
