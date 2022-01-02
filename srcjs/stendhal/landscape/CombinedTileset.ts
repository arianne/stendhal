/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e. V.                   *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var stendhal: any;

export class CombinedTileset {
	private canvas!: HTMLCanvasElement;
	public readonly ctx: CanvasRenderingContext2D;

	constructor(numberOfTiles: number) {
		this.canvas = document.createElement("canvas");
		this.canvas.width = stendhal.data.map.tileWidth * numberOfTiles;
		this.canvas.height = stendhal.data.map.tileHeight;
		this.ctx = this.canvas.getContext("2d")!;
	}


}