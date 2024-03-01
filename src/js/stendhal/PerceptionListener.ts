/***************************************************************************
 *                 Copyright © 2023-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var stendhal: any;

import { RPObject } from "./entity/RPObject";


/**
 * Class to override `marauroa.perceptionListener` & handle creating
 * User object.
 */
export class PerceptionListener {
	[key: string]: any;
	private readonly _super: any;


	constructor(perceptionListener: any) {
		this._super = perceptionListener;
		for (const prop of Object.getOwnPropertyNames(perceptionListener)) {
			if (typeof(this[prop]) === "undefined") {
				// members & methods that are not overridden by this class
				this[prop] = perceptionListener[prop];
			}
		}
	}

	/**
	 * Action when an object is added to the world.
	 *
	 * @param obj {entity.RPObject}
	 *   Object to be added.
	 * @return {boolean}
	 *   `true` if object was added.
	 * @see marauroa.perceptionListener.onAdded
	 */
	onAdded(obj: RPObject): boolean {
		if (obj["c"] === "player") {
			if (obj.hasOwnProperty("a")) {
				if (obj["a"]["name"] === stendhal.session.getCharName()) {
					// create User object for player controlled by this client
					obj["c"] = "user";
				}
			}
		}
		return this._super.onAdded(obj);
	}
}
