/***************************************************************************
 *                (C) Copyright 2007-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { DialogContentComponent } from "../../toolkit/DialogContentComponent";
import { ui } from "../../UI";
import { UIComponentEnum } from "../../UIComponentEnum";

import { OutfitPartSelector } from "./OutfitPartSelector";
import { OutfitColorSelector } from "./OutfitColorSelector";
import { OutfitPaletteColorSelector } from "./OutfitPaletteColorSelector";

declare var marauroa: any;
declare var stendhal: any;


/**
 * a dialog to choose an outfit from
 */
export class OutfitDialog extends DialogContentComponent {
	private hatSelector!: OutfitPartSelector;
	private hairSelector!: OutfitPartSelector;
	private maskSelector!: OutfitPartSelector;
	private eyesSelector!: OutfitPartSelector;
	private mouthSelector!: OutfitPartSelector;
	private headSelector!: OutfitPartSelector;
	private bodySelector!: OutfitPartSelector;
	private dressSelector!: OutfitPartSelector;
	private hairColorSelector!: OutfitColorSelector;
	private eyesColorSelector!: OutfitColorSelector;
	private dressColorSelector!: OutfitColorSelector;
	private skinColorSelector!: OutfitPaletteColorSelector;


	constructor() {
		super("outfitdialog-template");
		ui.registerComponent(UIComponentEnum.OutfitDialog, this);

		queueMicrotask( () => {
			this.createDialog();
		});
	}

	public override getConfigId(): string {
		return "outfit";
	}

	private createDialog() {
		let outfit = marauroa.me["outfit_ext_orig"];
		if (outfit === undefined) {
			outfit = marauroa.me["outfit_ext"];
		}

		let entries = outfit.split(",");
		let currentOutfit: any = {};
		for (let i = 0; i < entries.length; i++) {
			let entry = entries[i].split("=");
			currentOutfit[entry[0]] = entry[1];
		}

		this.hatSelector = this.makeSelector("hat", currentOutfit["hat"]);
		this.hairSelector = this.makeSelector("hair", currentOutfit["hair"]);
		this.maskSelector = this.makeSelector("mask", currentOutfit["mask"]);
		this.eyesSelector = this.makeSelector("eyes", currentOutfit["eyes"]);
		this.mouthSelector = this.makeSelector("mouth", currentOutfit["mouth"]);
		this.headSelector = this.makeSelector("head", currentOutfit["head"]);
		this.bodySelector = this.makeSelector("body", currentOutfit["body"]);
		this.dressSelector = this.makeSelector("dress", currentOutfit["dress"]);

		this.hairColorSelector = this.createColorSelector(OutfitColorSelector, "hair", this.hairSelector);
		this.eyesColorSelector = this.createColorSelector(OutfitColorSelector, "eyes", this.eyesSelector);
		this.dressColorSelector = this.createColorSelector(OutfitColorSelector, "dress", this.dressSelector);
		this.skinColorSelector = this.createColorSelector(OutfitPaletteColorSelector, "skin", this.headSelector, this.bodySelector);

		this.drawComposite();

		this.child("#setoutfitcancel")!.addEventListener("click", (event) => {
			this.onCancel(event);
		});
		this.child("#setoutfitapply")!.addEventListener("click", (event) => {
			this.onApply(event);
		});
	}

	private onCancel(event: Event) {
		this.componentElement.dispatchEvent(new Event("close"));
		event.preventDefault();
	}

	private onApply(event: Event) {
		const outfitString =
				"body=" + this.bodySelector.index.toString() + "," +
				"dress=" + this.dressSelector.index.toString() + "," +
				"head=" + this.headSelector.index.toString() + "," +
				"mouth=" + this.mouthSelector.index.toString() + "," +
				"eyes=" + this.eyesSelector.index.toString() + "," +
				"mask=" + this.maskSelector.index.toString() + "," +
				"hair=" + this.hairSelector.index.toString() + "," +
				"hat=" + this.hatSelector.index.toString();

		const action: any = {
				"type": "outfit_ext",
				"zone": marauroa.currentZoneName,
				"value": outfitString
		};

		let color = this.hairColorSelector.color;
		if (color != null) {
			action["hair"] = color.toString();
		}
		color = this.eyesColorSelector.color;
		if (color != null) {
			action["eyes"] = color.toString();
		}
		color = this.dressColorSelector.color;
		if (color != null) {
			action["dress"] = color.toString();
		}
		color = this.skinColorSelector.color;
		if (color != null) {
			action["skin"] = color.toString();
		}
		marauroa.clientFramework.sendAction(action);
		event.preventDefault();
		this.componentElement.dispatchEvent(new Event("close"));
	}

	makeSelector(part: string, index: number): OutfitPartSelector {
		// FIXME: selector should be showing a default if index is less than 0
		if (index < 0 || index === undefined) {
			index = 0;
		}

		const selector = new OutfitPartSelector(part, index, stendhal.data.outfit.count[part] - 1, () => {
			this.drawComposite();
		});

		document.getElementById("setoutfitprev" + part)!.addEventListener("click", function(e) {
			selector.previous();
		});
		document.getElementById("setoutfitnext" + part)!.addEventListener("click", function(e) {
			selector.next();
		});
		selector.draw();

		return selector;
	}

	drawComposite() {
		function draw(ctx: CanvasRenderingContext2D, selector: OutfitPartSelector) {
			const image = selector.image;
			image?.then((img: CanvasImageSource) => ctx.drawImage(img, -48, -128));
		}
		const canvas = document.getElementById('setoutfitcompositecanvas') as HTMLCanvasElement;
		const ctx = canvas.getContext("2d")!;
		ctx.fillStyle = "white";
		ctx.fillRect(0, 0, canvas.width, canvas.height);

		draw(ctx, this.bodySelector);
		draw(ctx, this.dressSelector);
		draw(ctx, this.headSelector);
		draw(ctx, this.mouthSelector);
		draw(ctx, this.eyesSelector);
		draw(ctx, this.maskSelector);
		// hair is not drawn under certain hats/helmets
		if (stendhal.data.outfit.drawHair(parseInt(this.hatSelector.index, 10))) {
			draw(ctx, this.hairSelector);
		}
		draw(ctx, this.hatSelector);
	}


	initialColorValue(part: string) {
		const colors = marauroa.me["outfit_colors"];
		if (colors != null) {
			let colorName = part;
			if (part === "body" || part === "head") {
				colorName = "skin";
			}

			let layer_color = colors[colorName + "_orig"];
			if (layer_color === undefined) {
				layer_color = colors[colorName];
			}

			return layer_color;
		}
		return null;
	}

	createColorSelector(classObject: any, part: string, ...partSelectors: any) {
		const toggle = document.getElementById("setoutfit" + part + "colortoggle") as HTMLInputElement;
		const canvas = document.getElementById("setoutfit" + part + "colorcanvas")!;
		const gradientCanvas = document.getElementById("setoutfit" + part + "colorgradient");
		const selector = new classObject(canvas, gradientCanvas, (color: any) => {
			for (const partSelector of partSelectors) {
				partSelector.color = color;
			}
		});
		const initialColor = this.initialColorValue(part);
		if (initialColor != null) {
			toggle.checked = true;
			selector.color = initialColor;
		}
		toggle.addEventListener("change", function(e) {
			selector.enabled = toggle.checked;
		});
		selector.draw();
		return selector;
	}

	public override onParentClose() {
		ui.unregisterComponent(this);
	}

}
