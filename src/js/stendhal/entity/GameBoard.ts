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
import { ImageSprite } from "sprite/image/ImageSprite";
import { images } from "sprite/image/ImageManager";

export class GameBoard extends Entity {

	override minimapShow = false;
	override zIndex = 100;

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "class") {
			let filename = Paths.sprites + "/gameboard/" + this["class"] + ".png";
			this.imageSprite?.free();
			this.imageSprite = new ImageSprite(images.load(filename), 0, 0, 3*32, 3*32);
		}
	}

	override isVisibleToAction(_filter: boolean) {
		return false;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/walk.png) 1 3, auto";
	}

}
