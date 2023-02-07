/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Entity } from "./Entity";

declare var stendhal: any;

export class UseableEntity extends Entity {
	override zIndex = 3000
	action = "use";

	constructor() {
		super();
		this.sprite = {
			height: 32,
			width: 32
		};
	}

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "class" || key === "name") {
			this.sprite.filename = stendhal.paths.sprites + "/"
				+ this["class"] + "/" + this["_name"] + ".png";
		}
		if (key === "state") {
			this.sprite.offsetY = this["state"] * 32;
		}
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

}
