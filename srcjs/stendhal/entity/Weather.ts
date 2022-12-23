/***************************************************************************
 *                    Copyright © 2003-2022 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Entity } from "./Entity";

declare var marauroa: any;


export class Weather extends Entity {

	// DEBUG:
	constructor() {
		super();
		setTimeout(() => {
			console.log("Weather entity", "at", marauroa.currentZoneName, this["x"], this["y"], "is", this);
		}, 1);
	}

	override isVisibleToAction(_filter: boolean) {
		return (marauroa.me["adminlevel"] && marauroa.me["adminlevel"] >= 600);
	}
}
