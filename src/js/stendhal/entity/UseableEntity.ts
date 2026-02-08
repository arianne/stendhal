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
import { MenuItem } from "../action/MenuItem";
import { Paths } from "../data/Paths";
import { ImageSprite } from "sprite/image/ImageSprite";
import { images } from "sprite/image/ImageManager";


export class UseableEntity extends Entity {
	override zIndex = 3000
	action = "use";

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "class" || key === "name") {
			let filename = Paths.sprites + "/" + this["class"] + "/" + this["_name"] + ".png";
			let state = this["state"] * 32 || 0;
			this.imageSprite?.free();
			this.imageSprite = new ImageSprite(images.load(filename), 0, state, 32, 32);
		} else if (key === "state" && this.imageSprite) {
			this.imageSprite.offsetY = this["state"] * 32;
		}
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override buildActions(list: MenuItem[]) {
		super.buildActions(list);
		// FIXME: Java client adds "look" to Entity super class. Should this do the same? If so, there
		//        is an issue where certain entities, such as signs, end up with two options, "read" &
		//        "look" which do the same thing.
		const lookItem = {title: "Look", type: "look"} as MenuItem;
		if (list.indexOf(lookItem) < 0) {
			list.push(lookItem);
		}
	}
}
