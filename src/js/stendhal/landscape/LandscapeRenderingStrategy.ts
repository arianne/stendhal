/***************************************************************************
 *                (C) Copyright 2022-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Canvas } from "util/Types";
import { TileMap } from "../data/TileMap";
import { CombinedTilesetFactory } from "./CombinedTilesetFactory";


import { stendhal } from "../stendhal";

export abstract class LandscapeRenderingStrategy {

	public abstract onMapLoaded(map: TileMap): void;

	public abstract onTilesetLoaded(): void;

	public abstract render(
		canvas: Canvas, gamewindow: any,
		tileOffsetX: number, tileOffsetY: number, targetTileWidth: number, targetTileHeight: number): void;

}
