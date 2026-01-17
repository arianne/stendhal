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

export class InvisibleEntity extends Entity {

	override isVisibleToAction(_filter: boolean) {
		return false;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/walk.png) 1 3, auto";
	}

}
