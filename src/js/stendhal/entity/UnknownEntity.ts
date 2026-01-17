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

import { marauroa } from "marauroa"

export class UnknownEntity extends Entity {

	override zIndex = 1;

	constructor() {
		super();
		window.setTimeout(() => {
			if (this["_rpclass"]) {
				console.log("Unknown entity", this["_rpclass"], "at", marauroa.currentZoneName, this["x"], this["y"], "is", this);
			}
		}, 1);
	}

	override isVisibleToAction(_filter: boolean) {
		return (marauroa.me["adminlevel"] && marauroa.me["adminlevel"] >= 600);
	}

}
