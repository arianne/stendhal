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

import { RPEvent } from "./RPEvent";

import { SubEvent } from "./generic/SubEvent";


/**
 * A generic event that will execute a registered sub-event.
 *
 * TODO: allow execution without an associated entity
 */
export class GenericEvent extends RPEvent {
	[index: string]: any;

	public subevent!: string;
	public flags!: string;


	override execute(entity: any) {
		const event = this[this["subevent"]];
		if (!(event.prototype instanceof SubEvent)) {
			console.warn("Unknown generic event:", this["subevent"]);
			return;
		}
		const flags = typeof(this["flags"]) === "string" ? this["flags"].split(",") : [];
		new event(flags).execute(entity);
	}
}
