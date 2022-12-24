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

import { InvisibleEntity } from "./InvisibleEntity";

declare var marauroa: any;


export class LoopedSoundSource extends InvisibleEntity {

	// DEBUG:
	constructor() {
		super();
		setTimeout(() => {
			console.log("Looped sound source", "at", marauroa.currentZoneName, this["x"], this["y"], "is", this);
		}, 1);
	}
}
