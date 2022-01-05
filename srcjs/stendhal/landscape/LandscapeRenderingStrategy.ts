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

import { CombinedTilesetFactory } from "./CombinedTilesetFactory";
import { LandscapeRenderer } from "./LandscapeRenderer";


declare var stendhal: any;
declare var ImagePreloader: any;

export abstract class LandscapeRenderingStrategy {

	public abstract onMapLoaded(map: any): void;

	public abstract onTilesetLoaded(): void;

	public abstract render(
		canvas: HTMLCanvasElement, gamewindow: any,
		tileOffsetX: number, tileOffsetY: number, targetTileWidth: number, targetTileHeight: number): void;

}

export class CombinedTilesetRenderingStrategy extends LandscapeRenderingStrategy {

	public onMapLoaded(map: any): void {
		let combinedTilesetFactory = new CombinedTilesetFactory(map);
		stendhal.data.map.combinedTileset = combinedTilesetFactory.combine();
	}

	public onTilesetLoaded(): void {
		let body = document.getElementById("body")!;
		body.style.cursor = "auto";
	}

	public render(
		canvas: HTMLCanvasElement, gamewindow: any,
		tileOffsetX: number, tileOffsetY: number, targetTileWidth: number, targetTileHeight: number): void {
	
		let landscapeRenderder = new LandscapeRenderer();
		landscapeRenderder.drawLayer(
			canvas,
			stendhal.data.map.combinedTileset, 
			0,
			tileOffsetX, tileOffsetY, targetTileWidth, targetTileHeight);

		gamewindow.drawEntities();

		landscapeRenderder.drawLayer(
			canvas,
			stendhal.data.map.combinedTileset, 
			1,
			tileOffsetX, tileOffsetY, targetTileWidth, targetTileHeight);
	}
	
}


export class IndividualTilesetRenderingStrategy extends LandscapeRenderingStrategy {

	public onMapLoaded(_map: any): void {
		// do nothing
		console.log("Using IndividualTilesetRenderingStrategy.")
	}

	public onTilesetLoaded(): void {
		new ImagePreloader(stendhal.data.map.tilesetFilenames, function() {
			let body = document.getElementById("body")!;
			body.style.cursor = "auto";
		});
	}

	public render(
		canvas: HTMLCanvasElement, gamewindow: any,
		tileOffsetX: number, tileOffsetY: number, _targetTileWidth: number, _targetTileHeight: number): void {
		for (var drawingLayer=0; drawingLayer < stendhal.data.map.layers.length; drawingLayer++) {
			var name = stendhal.data.map.layerNames[drawingLayer];
			if (name !== "protection" && name !== "collision" && name !== "objects"
				&& name !== "blend_ground" && name !== "blend_roof") {
				gamewindow.paintLayer(canvas, drawingLayer, tileOffsetX, tileOffsetY);
			}
			if (name === "2_object") {
				gamewindow.drawEntities();
			}
		}

	}
}