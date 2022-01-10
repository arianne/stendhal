"use strict";

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

var OutfitPartSelector = require("../../../build/ts/ui/dialog/outfit/OutfitPartSelector").OutfitPartSelector;
var OutfitColorSelector = require("../../../build/ts/ui/dialog/outfit/OutfitColorSelector").OutfitColorSelector;
var OutfitPaletteColorSelector = require("../../../build/ts/ui/dialog/outfit/OutfitPaletteColorSelector").OutfitPaletteColorSelector;



stendhal.ui.outfitCount = {
	"hat": 14,
	"hair": 48,
	"mask": 9,
	"eyes": 26,
	"mouth": 5,
	"head":  4,
	"dress": 63,
	"body": 3
}

stendhal.ui.OutfitDialog = function() {
	if (stendhal.ui.OutfitDialog.instance != null) {
		return;
	}
	const self = this;


	const content = "<div class='background'>" +
	"<div class='horizontalgroup'>" +

	"<div class='verticalgroup'>" + // part selectors
	"<div class='horizontalgroup'>" +
	"<button type='button' id='setoutfitprevhair'>&lt;</button>"+
	"<canvas id='setoutfithaircanvas' width='48' height='64'></canvas>" +
	"<button type='button' id='setoutfitnexthair'>&gt;</button>" +
	"</div>" +

	"<div class='horizontalgroup'>" +
	"<button type='button' id='setoutfitpreveyes'>&lt;</button>" +
	"<canvas id='setoutfiteyescanvas' width='48' height='64'></canvas>" +
	"<button type='button' id='setoutfitnexteyes'>&gt;</button><br>" +
	"</div>" +

	"<div class='horizontalgroup'>" +
	"<button type='button' id='setoutfitprevmouth'>&lt;</button>" +
	"<canvas id='setoutfitmouthcanvas' width='48' height='64'></canvas>" +
	"<button type='button' id='setoutfitnextmouth'>&gt;</button><br>" +
	"</div>" +

	"<div class='horizontalgroup'>" +
	"<button type='button' id='setoutfitprevhead'>&lt;</button>" +
	"<canvas id='setoutfitheadcanvas' width='48' height='64'></canvas>" +
	"<button type='button' id='setoutfitnexthead'>&gt;</button><br>" +
	"</div>" +

	"<div class='horizontalgroup'>" +
	"<button type='button' id='setoutfitprevbody'>&lt;</button>" +
	"<canvas id='setoutfitbodycanvas' width='48' height='64'></canvas>" +
	"<button type='button' id='setoutfitnextbody'>&gt;</button><br>" +
	"</div>" +

	"<div class='horizontalgroup'>" +
	"<button type='button' id='setoutfitprevdress'>&lt;</button>" +
	"<canvas id='setoutfitdresscanvas' width='48' height='64'></canvas>" +
	"<button type='button' id='setoutfitnextdress'>&gt;</button>" +
	"</div>" +
	"</div>" + // part selectors

	"<div class='verticalgroup'>" + // color selectors
	"<div class='verticalgroup'>" +
	"<div class='horizontalgroup'>" +
	"<input type='checkbox' id='setoutfithaircolortoggle'>" +
	"<label for='setoutfithaircolortoggle'>Hair color</label>" +
	"</div>" +
	"<canvas id='setoutfithaircolorcanvas' width='80' height='52'></canvas>" +
	"<canvas id='setoutfithaircolorgradient' width='80' height='10'></canvas>" +
	"</div>" +

	"<div class='verticalgroup'>" +
	"<div class='horizontalgroup'>" +
	"<input type='checkbox' id='setoutfiteyescolortoggle'>" +
	"<label for='setoutfiteyescolortoggle'>Eyes color</label>" +
	"</div>" +
	"<canvas id='setoutfiteyescolorcanvas' width='80' height='52'></canvas>" +
	"<canvas id='setoutfiteyescolorgradient' width='80' height='10'></canvas>" +
	"</div>" +

	"<div class='verticalgroup'>" +
	"<div class='horizontalgroup'>" +
	"<input type='checkbox' id='setoutfitskincolortoggle'>" +
	"<label for='setoutfitskincolortoggle'>Skin color</label>" +
	"</div>" +
	"<canvas id='setoutfitskincolorcanvas' width='80' height='52'></canvas>" +
	"<canvas id='setoutfitskincolorgradient' width='80' height='10'></canvas>" +
	"</div>" +

	"<div class='verticalgroup'>" +
	"<div class='horizontalgroup'>" +
	"<input type='checkbox' id='setoutfitdresscolortoggle'>" +
	"<label for='setoutfitdresscolortoggle'>Dress color</label>" +
	"</div>" +
	"<canvas id='setoutfitdresscolorcanvas' width='80' height='52'></canvas>" +
	"<canvas id='setoutfitdresscolorgradient' width='80' height='10'></canvas>" +
	"</div>" +
	"</div>" + // color selectors

	"<div class='verticalgroup'>" + // hat & mask part selectors

	"<div class='horizontalgroup'>" +
	"<button type='button' id='setoutfitprevhat'>&lt;</button>"+
	"<canvas id='setoutfithatcanvas' width='48' height='64'></canvas>" +
	"<button type='button' id='setoutfitnexthat'>&gt;</button>" +
	"</div>" +

	"<div class='horizontalgroup'>" +
	"<button type='button' id='setoutfitprevmask'>&lt;</button>"+
	"<canvas id='setoutfitmaskcanvas' width='48' height='64'></canvas>" +
	"<button type='button' id='setoutfitnextmask'>&gt;</button>" +
	"</div>" +

	"</div>" + // hat & mask part selectors

	"<div><canvas id='setoutfitcompositecanvas' width='48' height='64'></canvas></div>" +
	"</div>" + // horizontal group
	"<div align='right'><button type='button' id='setoutfitcancel'>Cancel</button>" +
	"<button type='button' id='setoutfitapply'>Change Outfit</button></div>" +

	"</div>"; //bg

	function makeSelector(part, index, partChanged) {
		// FIXME: selector should be showing a default if index is less than 0
		if (index < 0 || index === undefined) {
			index = 0;
		}

		const selector = new OutfitPartSelector(part, index, stendhal.ui.outfitCount[part] - 1, partChanged);

		document.getElementById("setoutfitprev" + part).addEventListener("click", function(e) {
			selector.previous();
		});
		document.getElementById("setoutfitnext" + part).addEventListener("click", function(e) {
			selector.next();
		});
		selector.draw();

		return selector;
	}

	function drawComposite() {
		function draw(ctx, selector) {
			const image = selector.image;
			image.then((img) => ctx.drawImage(img, -48, -128));
		}
		const canvas = document.getElementById('setoutfitcompositecanvas');
		const ctx = canvas.getContext("2d");
		ctx.fillStyle = "white";
		ctx.fillRect(0, 0, canvas.width, canvas.height);

		var drawHair = true;
		// hair is not drawn under certain hats/helmets
		if (stendhal.HATS_NO_HAIR !== null && stendhal.HATS_NO_HAIR !== undefined) {
			drawHair = !stendhal.HATS_NO_HAIR.includes(parseInt(hatSelector.index));
		}

		draw(ctx, bodySelector);
		draw(ctx, dressSelector);
		draw(ctx, headSelector);
		draw(ctx, mouthSelector);
		draw(ctx, eyesSelector);
		draw(ctx, maskSelector);
		if (drawHair) {
			draw(ctx, hairSelector);
		}
		draw(ctx, hatSelector);
	}

	function partChanged() {
		drawComposite();
	}

	self.popup = new stendhal.ui.Popup("Set outfit", content, 300, 200);
	self.popup.onClose = function(e) {
		stendhal.ui.OutfitDialog.instance = null;
	}
	stendhal.ui.OutfitDialog.instance = self;


	let outfit = marauroa.me["outfit_ext_orig"];
	if (outfit === undefined) {
		outfit = marauroa.me["outfit_ext"];
	}

	let entries = outfit.split(",");
	let currentOutfit = {};
	for (let i = 0; i < entries.length; i++) {
		 let entry = entries[i].split("=");
		 currentOutfit[entry[0]] = entry[1];
	}

	const hatSelector = makeSelector("hat", currentOutfit["hat"], partChanged);
	const hairSelector = makeSelector("hair", currentOutfit["hair"], partChanged);
	const maskSelector = makeSelector("mask", currentOutfit["mask"], partChanged);
	const eyesSelector = makeSelector("eyes", currentOutfit["eyes"], partChanged);
	const mouthSelector = makeSelector("mouth", currentOutfit["mouth"], partChanged);
	const headSelector = makeSelector("head", currentOutfit["head"], partChanged);
	const bodySelector = makeSelector("body", currentOutfit["body"], partChanged);
	const dressSelector = makeSelector("dress", currentOutfit["dress"], partChanged);

	function initialColorValue(part) {
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

	function createColorSelector(classObject, part, ...partSelectors) {
		const toggle = document.getElementById("setoutfit" + part + "colortoggle");
		const canvas = document.getElementById("setoutfit" + part + "colorcanvas");
		const gradientCanvas = document.getElementById("setoutfit" + part + "colorgradient");
		const selector = new classObject(canvas, gradientCanvas, (color) => {
			for (const partSelector of partSelectors) {
				partSelector.color = color;
			}
		});
		const initialColor = initialColorValue(part);
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

	const hairColorSelector = createColorSelector(OutfitColorSelector, "hair", hairSelector);
	const eyesColorSelector = createColorSelector(OutfitColorSelector, "eyes", eyesSelector);
	const dressColorSelector = createColorSelector(OutfitColorSelector, "dress", dressSelector);
	const skinColorSelector = createColorSelector(OutfitPaletteColorSelector, "skin", headSelector, bodySelector);

	drawComposite();

	document.getElementById("setoutfitcancel").addEventListener("click", function(e) {
		self.popup.close();
	});
	document.getElementById("setoutfitapply").addEventListener("click", function(e) {
		const outfitString =
				"body=" + bodySelector.index.toString() + "," +
				"dress=" + dressSelector.index.toString() + "," +
				"head=" + headSelector.index.toString() + "," +
				"mouth=" + mouthSelector.index.toString() + "," +
				"eyes=" + eyesSelector.index.toString() + "," +
				"mask=" + maskSelector.index.toString() + "," +
				"hair=" + hairSelector.index.toString() + "," +
				"hat=" + hatSelector.index.toString();

		const action = {
				"type": "outfit_ext",
				"zone": marauroa.currentZoneName,
				"value": outfitString
		};

		let color = hairColorSelector.color;
		if (color != null) {
			action["hair"] = color.toString();
		}
		color = eyesColorSelector.color;
		if (color != null) {
			action["eyes"] = color.toString();
		}
		color = dressColorSelector.color;
		if (color != null) {
			action["dress"] = color.toString();
		}
		color = skinColorSelector.color;
		if (color != null) {
			action["skin"] = color.toString();
		}
		marauroa.clientFramework.sendAction(action);
		self.popup.close();
	});
}
