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

import { Corpse } from "./Corpse";
import { RPSlot } from "marauroa";

export class CorpseSlot extends RPSlot {

	public _name?: string;
	public _parent?: Corpse;

	override add(object: any) {
		super.add(object);
		if (this._objects.length > 0) {
			this._parent?.autoOpenIfDesired();
		}
	}

	override del(key: any) {
		super.del(key);
		if (this._objects.length == 0) {
			this._parent?.closeCorpseInventory();
		}
	}

}
