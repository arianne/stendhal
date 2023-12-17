/***************************************************************************
 *                (C) Copyright 2003-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";

import { Player } from "../../entity/Player";

declare let marauroa: any;
declare let stendhal: any;

/**
 * mini map
 */
export class MiniMapComponent extends Component {

	private width = 128;
	private height = 128;
	private minimumScale = 2;
	private xOffset = 1;
	private yOffset = 1;
	private mapWidth = 1;
	private mapHeight = 1;
	private scale = 1;
	private bgImage!: HTMLCanvasElement;
	private lastZone?: number[];

	constructor() {
		super("minimap");
		this.componentElement.addEventListener("click", (event) => {
			this.onClick(event);
		});
		this.componentElement.addEventListener("dblclick", (event) => {
			this.onClick(event);
		});
	}


	private zoneChange() {
		this.mapWidth = stendhal.data.map.zoneSizeX;
		this.mapHeight = stendhal.data.map.zoneSizeY;
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
		if (marauroa.currentZoneName === stendhal.data.map.currentZoneName
			|| stendhal.data.map.currentZoneName === "int_vault"
			|| stendhal.data.map.currentZoneName === "int_adventure_island"
			|| stendhal.data.map.currentZoneName === "tutorial_island") {

			this.scale = 10;

			this.zoneChange();
			this.updateBasePosition();
			let ctx = (this.componentElement as HTMLCanvasElement).getContext("2d")!;

			// IE does not support ctx.resetTransform(), so use the following workaround:
			ctx.setTransform(1, 0, 0, 1, 0, 0);

			// The area outside of the map
			ctx.fillStyle = "#606060";
			ctx.fillRect(0, 0, this.width, this.height);

			ctx.translate(Math.round(-this.xOffset), Math.round(-this.yOffset));
			this.drawBackground(ctx);
			this.drawEntities(ctx);
		}
	}

	drawBackground(ctx: CanvasRenderingContext2D) {
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

		if (stendhal.data.map.collisionData !== this.lastZone) {
			this.lastZone = stendhal.data.map.collisionData;
			this.bgImage = document.createElement("canvas");
			let ctx = this.bgImage.getContext("2d")!;
			let imgData = ctx.createImageData(width, height);

			for (let y = 0; y < height; y++) {
				for (let x = 0; x < width; x++) {
					// RGBA array. Find the actual position
					let pos = 4 * (y * width + x);
					if (stendhal.data.map.collision(x, y)) {
						// red collision
						imgData.data[pos] = 255;
					} else if (stendhal.data.map.isProtected(x, y)) {
						// light green for protection
						imgData.data[pos] = 202;
						imgData.data[pos + 1] = 230;
						imgData.data[pos + 2] = 202;
					} else {
						// light gray elsewhere
						imgData.data[pos] = 224;
						imgData.data[pos + 1] = 224;
						imgData.data[pos + 2] = 224;
					}
					imgData.data[pos + 3] = 255;
				}
			}
			this.bgImage.width  = width;
			this.bgImage.height = height;

			ctx.putImageData(imgData, 0, 0);
		}
	}

	drawEntities(ctx: CanvasRenderingContext2D) {
		ctx.fillStyle = "rgb(255,0,0)";
		ctx.strokeStyle = "rgb(0,0,0)";
		let isAdmin = marauroa.me["adminlevel"] && marauroa.me["adminlevel"] >= 600;

		for (let i in marauroa.currentZone) {
			let o = marauroa.currentZone[i];
			if (typeof(o["x"]) != "undefined" && typeof(o["y"]) != "undefined" && (o.minimapShow || isAdmin)) {
				o.onMiniMapDraw();

				// this.ctx.fillText(o.id, o.x * this.scale, o.y * this.scale);
				if (o.minimapStyle) {
					ctx.strokeStyle = o.minimapStyle;
				} else {
					ctx.strokeStyle = "rgb(128, 128, 128)";
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
		if (!stendhal.config.getBoolean("client.pathfinding")) {
			return;
		}
		let pos = stendhal.ui.html.extractPosition(event);
		let x = Math.floor((pos.canvasRelativeX + this.xOffset) / this.scale);
		let y = Math.floor((pos.canvasRelativeY + this.yOffset) / this.scale);
		if (!stendhal.data.map.collision(x, y)) {
			let action: any = {
					type: "moveto",
					x: x.toString(),
					y: y.toString()
			};

			if ("type" in event && event["type"] === "dblclick") {
				action["double_click"] = "";
			}

			marauroa.clientFramework.sendAction(action);
		}
	}

}
