/***************************************************************************
 *                   (C) Copyright 2003-2026 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

/**
 * base class for RPSlots
 */
export class RPSlot {
	[key: string]: any;
	protected _objects: Record<string, any>[] = [];

	add(value: any) {
		if (value && value["id"]) {
			this._objects.push(value);
		}
	}

	get(key: string|number) {
		let idx = this.getIndex(key);
		if (idx > -1) {
			return this._objects[idx];
		}
		return undefined;
	}

	getByIndex(idx: number) {
		return this._objects[idx];
	}

	count() {
		return this._objects.length;
	}

	getIndex(key: string|number): number {
		let i;
		let c = this._objects.length;
		for (i = 0; i < c; i++) {
			if (this._objects[i]["id"] === key) {
				return i;
			}
		}
		return -1;
	}
	
	del(key: string|number) {
		let idx = this.getIndex(key);
		if (idx > -1) {
			this._objects.splice(idx, 1);
		}
	}
	
	first() {
		if (this._objects.length > 0) {
			return this._objects[0];
		}
		return undefined;
	}

}
