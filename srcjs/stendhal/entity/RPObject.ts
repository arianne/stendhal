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

declare var marauroa: any;


/**
 * base class for RPObjects
 */
export class RPObject {
	[key: string]: any;

	onEvent(e: any) {
		var event = marauroa.rpeventFactory.create(e["c"]);
		for (var i in e["a"]) {
			if (e["a"].hasOwnProperty(i)) {
				event[i] = e["a"][i];
			}
			event["_rpclass"] = e["c"];
		}

		// Event slots
		for (var slot in e["s"]) {
			if (e["s"].hasOwnProperty(slot)) {
				event[slot] = e["s"][slot];
			}
		}
		event.execute(this);
	}

	set(key: string, value: object) {
		this[key] = value;
	}

	setMapEntry(map: string, key: string, value: object) {
		this[map][key] = value;
	}

	unset(key: string) {
		delete this[key];
	}

	unsetMapEntry(map: string, key: string) {
		delete this[map][key];
	}

	destroy(_parent: RPObject) {
		// do nothing
	}

	createSlot(name: string) {
		var slot = marauroa.rpslotFactory.create(name);
		slot._parent = this;
		return slot;
	}

	init() {
		// do nothing
	}
}
