/***************************************************************************
 *                (C) Copyright 2003-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"

import { Component } from "../toolkit/Component";

import { Player } from "../../entity/Player";

import { Color } from "../../data/color/Color";
import { RenderingContext2D } from "util/Types";
import { stendhal } from "stendhal";
import { TileMap } from "data/TileMap";


/**
 * mini map
 */
export class MiniMapComponent extends Component {

	private map: TileMap;
	private width = 128;
	private height = 128;
	private minimumScale = 2;
	private xOffset = 1;
	private yOffset = 1;
	private mapWidth = 1;
	private mapHeight = 1;
	private scale = 1;
	private bgImage?: ImageBitmap;
	private lastZone?: number[];

	// ground/collision colors
	private static readonly COLOR_COLLISION = Color.parseRGB(Color.COLLISION); // red
	private static readonly COLOR_GROUND = Color.parseRGB(Color.BACKGROUND); // light gray
	private static readonly COLOR_PROTECTION = Color.parseRGB(Color.PROTECTION); // green


	constructor() {
		super("minimap");
		this.map = TileMap.get();
		this.componentElement.addEventListener("click", (event) => {
			this.onClick(event);
		});
		this.componentElement.addEventListener("dblclick", (event) => {
			this.onClick(event);
		});
	}


	private zoneChange() {
		this.mapWidth = this.map.zoneSizeX;
		this.mapHeight = this.map.zoneSizeY;
		this.scale = Math.max(this.minimumScale, Math.min(this.height / this.mapHeight, this.width / this.mapWidth));
		this.createBackgroundImage();
	};

	private updateBasePosition() {
		if (!marauroa.me) {
			return;
		}

		this.xOffset = 0;
		this.yOffset = 0;

		let imageWidth = this.mapWidth * this.scale;
		let imageHeight = this.mapHeight * this.scale;

		let xpos = Math.round((marauroa.me["x"] * this.scale) + 0.5) - this.width / 2;
		let ypos = Math.round((marauroa.me["y"] * this.scale) + 0.5) - this.width / 2;

		if (imageWidth > this.width) {
			// need to pan width
			if ((xpos + this.width) > imageWidth) {
				// x is at the screen border
				this.xOffset = imageWidth - this.width;
			} else if (xpos > 0) {
				this.xOffset = xpos;
			}
		}

		if (imageHeight > this.height) {
			// need to pan height
			if ((ypos + this.height) > imageHeight) {
				// y is at the screen border
				this.yOffset = imageHeight - this.height;
			} else if (ypos > 0) {
				this.yOffset = ypos;
			}
		}
	}

	public draw() {
		if (marauroa.currentZoneName === this.map.currentZoneName
			|| this.map.currentZoneName === "int_vault"
			|| this.map.currentZoneName === "int_adventure_island"
			|| this.map.currentZoneName === "tutorial_island") {

			this.scale = 10;

			this.zoneChange();
			this.updateBasePosition();
			let ctx = (this.componentElement as HTMLCanvasElement).getContext("2d")!;

			// IE does not support ctx.resetTransform(), so use the following workaround:
			ctx.setTransform(1, 0, 0, 1, 0, 0);

			// The area outside of the map
			ctx.fillStyle = Color.DARK_GRAY;
			ctx.fillRect(0, 0, this.width, this.height);

			ctx.translate(Math.round(-this.xOffset), Math.round(-this.yOffset));
			this.drawBackground(ctx);
			this.drawEntities(ctx);
		}
	}

	drawBackground(ctx: RenderingContext2D) {
		ctx.save();
		ctx.imageSmoothingEnabled = false;

		ctx.scale(this.scale, this.scale);
		if (this.bgImage) {
			ctx.drawImage(this.bgImage, 0, 0);
		}
		ctx.restore();
	}

	createBackgroundImage() {
		let width = this.mapWidth;
		let height = this.mapHeight;
		if (width <= 0 || height <= 0) {
			return;
		}

		if (this.map.collisionData !== this.lastZone) {
			this.lastZone = this.map.collisionData;
			let canvas = new OffscreenCanvas(width, height);
			let ctx = canvas.getContext("2d")!;
			let imgData = ctx.createImageData(width, height);

			for (let y = 0; y < height; y++) {
				for (let x = 0; x < width; x++) {
					let color = MiniMapComponent.COLOR_GROUND;
					// RGBA array. Find the actual position
					let pos = 4 * (y * width + x);
					if (this.map.collision(x, y)) {
						// red collision
						color = MiniMapComponent.COLOR_COLLISION;
					} else if (this.map.isProtected(x, y)) {
						// light green for protection
						color = MiniMapComponent.COLOR_PROTECTION;
					}
					imgData.data[pos] = color.R;
					imgData.data[pos + 1] = color.G;
					imgData.data[pos + 2] = color.B;
					imgData.data[pos + 3] = 255; // opacity
				}
			}
			ctx.putImageData(imgData, 0, 0);
			if (this.bgImage) {
				this.bgImage.close();
			}
			this.bgImage = canvas.transferToImageBitmap();
		}
	}

	drawEntities(ctx: RenderingContext2D) {
		ctx.fillStyle = Color.RED;
		ctx.strokeStyle = Color.BLACK;
		let isAdmin = marauroa.me["adminlevel"] && marauroa.me["adminlevel"] >= 600;

		for (let i in marauroa.currentZone) {
			let o = marauroa.currentZone[i];
			if (typeof(o["x"]) != "undefined" && typeof(o["y"]) != "undefined" && (o.minimapShow || isAdmin)) {
				o.onMiniMapDraw();

				// this.ctx.fillText(o.id, o.x * this.scale, o.y * this.scale);
				if (o.minimapStyle) {
					ctx.strokeStyle = o.minimapStyle;
				} else {
					ctx.strokeStyle = Color.GRAY;
				}

				if (o instanceof Player) {
					let adj_scale = this.scale;
					if (adj_scale < 6) {
						// + is hard to see in wider views
						adj_scale = 6;
					}

					let ho = (o["width"] * adj_scale) / 2;
					let vo = (o["height"] * adj_scale) / 2;
					const hc = o["x"] * this.scale + ho;
					const vc = o["y"] * this.scale + vo;

					ctx.beginPath();
					ctx.moveTo(hc - ho, vc);
					ctx.lineTo(hc + ho, vc);
					ctx.moveTo(hc, vc - vo);
					ctx.lineTo(hc, vc + vo);
					ctx.stroke();
					ctx.closePath();
				} else {
					ctx.strokeRect(o["x"] * this.scale, o["y"] * this.scale, o["width"] * this.scale, o["height"] * this.scale);
				}
			}
		}
	}

	onClick(event: MouseEvent) {
		if (!stendhal.config.getBoolean("pathfinding.minimap")) {
			return;
		}
		let pos = stendhal.ui.html.extractPosition(event);
		let x = Math.floor((pos.canvasRelativeX + this.xOffset) / this.scale);
		let y = Math.floor((pos.canvasRelativeY + this.yOffset) / this.scale);
		if (!this.map.collision(x, y)) {
			let action: any = {
					type: "moveto",
					x: x.toString(),
					y: y.toString()
			};

			if ("type" in event && event["type"] === "dblclick") {
				action["double_click"] = "";
			}

			marauroa.me.moveTo(action);
		}
	}

}
