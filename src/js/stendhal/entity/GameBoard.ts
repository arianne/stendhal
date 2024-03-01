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

export class GameBoard extends Entity {

	override minimapShow = false;
	override zIndex = 100;

	constructor() {
		super();
		this.sprite = {
			height: 32 * 3,
			width: 32 * 3
		};
	}

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "class") {
			this.sprite.filename = stendhal.paths.sprites + "/gameboard/"
				+ this["class"] + ".png";
		}
	}

	override isVisibleToAction(_filter: boolean) {
		return false;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + stendhal.paths.sprites + "/cursor/walk.png) 1 3, auto";
	}

}
