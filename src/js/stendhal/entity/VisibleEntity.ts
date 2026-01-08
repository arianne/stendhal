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

import { Paths } from "../data/Paths";
import { Entity } from "./Entity";

import { stendhal } from "../stendhal";

export class VisibleEntity extends Entity {

	override zIndex = 1;

	constructor() {
		super();
		this.sprite = {
			height: 32,
			width: 32
		};
	}

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "class" || key === "subclass" || key === "_name") {
			this.sprite.filename = Paths.sprites + "/"
				+ (this["class"] || "") + "/"
				+ (this["subclass"] || "") + "/"
				+ (this["_name"] || "") + ".png";
		} else if (key === "state") {
			this.sprite.offsetY = value * 32;
		}
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/look.png) 1 3, auto";
	}

}
