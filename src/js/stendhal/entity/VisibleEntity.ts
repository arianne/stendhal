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

import { images } from "sprite/image/ImageManager";
import { Paths } from "../data/Paths";
import { Entity } from "./Entity";
import { ImageSprite } from "sprite/image/ImageSprite";

export class VisibleEntity extends Entity {

	override zIndex = 1;

	constructor() {
		super();
	}

	override init(): void {
		let filename = Paths.sprites + "/"
			+ (this["class"] || "") + "/"
			+ (this["subclass"] || "") + "/"
			+ (this["_name"] || "") + ".png";
		let state = this["state"] * 32 || 0;
		this.imageSprite = new ImageSprite(images.load(filename), 0, state, 32, 32);
	}

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "class" || key === "subclass" || key === "_name") {
			// if this was already initialized, create a new ImageSprite
			if (this.imageSprite) {
				this.imageSprite.free();
				this.init();
			}
		} else if (key === "state") {
			if (this.imageSprite) {
				this.imageSprite.offsetY = value * 32;
			}
		}
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/look.png) 1 3, auto";
	}

}
