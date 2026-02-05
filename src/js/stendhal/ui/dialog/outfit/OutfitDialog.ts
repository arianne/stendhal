/***************************************************************************
 *                (C) Copyright 2007-2026 - Faiumoni e. V.                 *
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

import { OutfitPreviewComponent } from "../../component/OutfitPreviewComponent";

import { marauroa } from "marauroa"
import { stendhal } from "../../../stendhal";


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

	/** Composite preview. */
	private outfitPreview: OutfitPreviewComponent;


	constructor() {
		super("outfitdialog-template");
		ui.registerComponent(UIComponentEnum.OutfitDialog, this);

		this.outfitPreview = new OutfitPreviewComponent();
		this.outfitPreview.componentElement.id = "setoutfitcompositecanvas";
		this.outfitPreview.setBGColor("white");
		this.child("#outfit-preview-composite")!.appendChild(this.outfitPreview.componentElement);

		this.child("#rotate-c")!.addEventListener("click", (e: Event) => {
			this.outfitPreview.nextDirection();
		});
		this.child("#rotate-cc")!.addEventListener("click", (e: Event) => {
			this.outfitPreview.prevDirection();
		});

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

	/**
	 * Creates an outfit string from individual parts selectors.
	 *
	 * @returns {string}
	 *   Outfit formatted string ("body=0,head=2,eyes=1,...).
	 */
	private buildOutfitString(): string {
		return "body=" + this.bodySelector.index.toString() + ","
				+ "dress=" + this.dressSelector.index.toString() + ","
				+ "head=" + this.headSelector.index.toString() + ","
				+ "mouth=" + this.mouthSelector.index.toString() + ","
				+ "eyes=" + this.eyesSelector.index.toString() + ","
				+ "mask=" + this.maskSelector.index.toString() + ","
				+ "hair=" + this.hairSelector.index.toString() + ","
				+ "hat=" + this.hatSelector.index.toString();
	}

	/**
	 * Creates an outfit coloring string from individual parts selectors.
	 *
	 * @returns {string}
	 *   Outfit coloring formatted string ("skin=14179110,hair=16446211,...").
	 */
	private buildColoringString(): string {
		const colors: string[] = [];
		let color = this.hairColorSelector.color;
		if (color != null) {
			colors.push("hair=" + color.toString());
		}
		color = this.eyesColorSelector.color;
		if (color != null) {
			colors.push("eyes=" + color.toString());
		}
		color = this.dressColorSelector.color;
		if (color != null) {
			colors.push("dress=" + color.toString());
		}
		color = this.skinColorSelector.color;
		if (color != null) {
			colors.push("skin=" + color.toString());
		}
		return colors.join(",");
	}

	private onApply(event: Event) {
		const action: any = {
				"type": "outfit_ext",
				"zone": marauroa.currentZoneName,
				"value": this.buildOutfitString()
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
		let minIndex = 0;
		if (part === "dress" && stendhal.config.getBoolean("effect.no-nude")) {
			minIndex = 1;
		}

		// FIXME: selector should be showing a default if index is less than 0
		if (index < minIndex || index === undefined) {
			index = minIndex;
		}

		const selector = new OutfitPartSelector(part, index, minIndex, stendhal.data.outfit.count[part] - 1, () => {
			this.drawComposite();
		});

		document.getElementById("setoutfitprev" + part)!.addEventListener("click", function(_e) {
			selector.previous();
		});
		document.getElementById("setoutfitnext" + part)!.addEventListener("click", function(_e) {
			selector.next();
		});
		selector.draw();

		return selector;
	}

	drawComposite() {
		this.outfitPreview.setOutfit(this.buildOutfitString(), this.buildColoringString());
	}


	initialColorValue(part: string) {
		const colors = marauroa.me["outfit_colors"];
		if (colors != null) {
			const colorName = stendhal.data.outfit.isSkinLayer(part) ? "skin" : part;

			let layer_color = colors[colorName + "_orig"];
			if (layer_color === undefined) {
				layer_color = colors[colorName];
			}

			return layer_color;
		}
		return null;
	}

	createColorSelector(classObject: any, part: string, ...partSelectors: OutfitPartSelector[]) {
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
		toggle.addEventListener("change", function(_e) {
			selector.enabled = toggle.checked;
		});
		selector.draw();
		return selector;
	}

	public override onParentClose() {
		this.outfitPreview.close();
		this.hatSelector.close();
		this.hairSelector.close();
		this.maskSelector.close();
		this.eyesSelector.close();
		this.mouthSelector.close();
		this.headSelector.close();
		this.bodySelector.close();
		this.dressSelector.close();
		ui.unregisterComponent(this);
	}
}
