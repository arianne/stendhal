"use strict";

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};


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

	class PartSelector {
		constructor(part, initialIndex, maxindex, onPartChanged) {
			this._part = part;
			this._onPartChanged = onPartChanged;
			this._index = initialIndex;
			this._maxindex = maxindex;

			const canvas = document.getElementById('setoutfit' + part + 'canvas');
			canvas.style.margin = "5px";
			this._ctx = canvas.getContext("2d");
			this._width = canvas.width;
			this._height = canvas.height;
			this._color = null;
			this._image = null;
		}

		draw() {
			const image = this._getPartSprite(this._part, this._index, this._color);
			this._image = image;
			this._ctx.fillStyle = "white";
			this._ctx.fillRect(0, 0, this._width, this._height);

			image.then((img) => {
				this._ctx.drawImage(img, -48, -128);
				this._onPartChanged();
			});
		}

		get image() {
			return this._image;
		}

		get index() {
			return indexString(this._index);
		}

		set index(newIndex) {
			this._index = newIndex;
			this.draw();
		}

		set color(newColor) {
			this._color = newColor;
			this.draw();
		}

		previous() {
			const numOutfits = this._maxindex + 1;
			this._index += this._maxindex;
			this._index %= numOutfits;
			this.draw();
			this._onPartChanged();
		}

		next() {
			const numOutfits = this._maxindex + 1;
			this._index++;
			this._index %= numOutfits;
			this.draw();
			this._onPartChanged();
		}

		_getPartSprite(part, index, color = null) {
			const fname = "/data/sprites/outfit/" + part + "/" + part + "_0" + indexString(index) + ".png";
			if (color != null) {
				return stendhal.data.sprites.getFilteredWithPromise(fname, "trueColor", color);
			}
			return stendhal.data.sprites.getWithPromise(fname);
		}
	}

	class ColorSelector {
		constructor(canvas, gradientCanvas, onColorChanged) {
			this.ctx = canvas.getContext("2d");
			this.gradCtx = gradientCanvas.getContext("2d");
			this.baseImage = this._createBaseImage(canvas.width, canvas.height);
			this.onColorChanged = onColorChanged;
			this._enabled = false;
			this.x = this.baseImage.width / 2;
			this.y = this.baseImage.height / 2;
			this.hX = this.x;
			canvas.addEventListener("mousedown", (e) => this._onMouseDown(e));
			canvas.addEventListener("mousemove", (e) => this._onMouseMove(e));

			gradientCanvas.addEventListener("mousedown", (e) => this._onMouseDownGrad(e));
			gradientCanvas.addEventListener("mousemove", (e) => this._onMouseMoveGrad(e));
			gradientCanvas.style.margin = "5px 0px 0px 0px";
		}

		set enabled(value) {
			this._enabled = value ? true : false;
			this.draw();
			this.onColorChanged(this.color);
		}

		get enabled() {
			return this._enabled;
		}

		get color() {
			if (this.enabled) {
				const hsl = [this.x / this.baseImage.width, 1 - this.y / this.baseImage.height,
					this.hX / this.baseImage.width];
				const rgbArray = stendhal.data.sprites.filter.hsl2rgb(hsl);
				return stendhal.data.sprites.filter.mergergb(rgbArray);
			}
			return null;
		}

		set color(rgb) {
			if (rgb != null) {
				const hsl = stendhal.data.sprites.filter.rgb2hsl(stendhal.data.sprites.filter.splitrgb(rgb));
				this.x = hsl[0] * this.baseImage.width;
				this.y = (1 - hsl[1]) * this.baseImage.height;
				this.hX = hsl[2] * this.baseImage.width;
				this.enabled = true;
			} else {
				this.enabled = false;
			}
		}

		_createBaseImage(width, height) {
			const img = document.createElement("canvas");
			img.width  = width;
			img.height = height;
			const ctx = img.getContext("2d");
			for (let x = 0; x < width; x++) {
				for (let y = 0; y < height; y++) {
					const hsl = [x / width, 1 - y / height, 0.5];
					const rgb = stendhal.data.sprites.filter.hsl2rgb(hsl);
					ctx.fillStyle = this._rgbToCssString(rgb);
					ctx.fillRect(x, y, 1, 1);
				}
			}
			return img;
		}

		_rgbToCssString(rgb) {
			return "rgb(".concat(rgb[0], ",", rgb[1], ",", rgb[2], ")");
		}

		draw() {
			if (this.enabled) {
				this.ctx.drawImage(this.baseImage, 0, 0);
				this._drawSelection();
			} else {
				this.ctx.fillStyle = "gray";
				this.ctx.fillRect(0, 0, this.baseImage.width, this.baseImage.height);
			}
			this._drawGradientPart();
		}

		_drawSelection() {
			this.ctx.strokeStyle = "black";
			this.ctx.beginPath();
			this.ctx.moveTo(this.x, 0);
			this.ctx.lineTo(this.x, this.baseImage.height);
			this.ctx.moveTo(0, this.y);
			this.ctx.lineTo(this.baseImage.width, this.y);
			this.ctx.stroke();
		}

		_drawGradientPart() {
			if (this.enabled) {
				const gradient = this.gradCtx.createLinearGradient(0, 0, this.baseImage.width, 0);
				const stops = this._calculateGradientStops();
				gradient.addColorStop(0, this._rgbToCssString(stops[0]));
				gradient.addColorStop(0.5, this._rgbToCssString(stops[1]));
				gradient.addColorStop(1, this._rgbToCssString(stops[2]));

				const ctx = this.gradCtx;
				ctx.fillStyle = gradient;
				ctx.fillRect(0, 0, this.baseImage.width, 10);

				ctx.fillStyle = "black";
				ctx.beginPath();
				ctx.moveTo(this.hX, 0);
				ctx.lineTo(this.hX, 10);
				ctx.stroke();
			} else {
				this.gradCtx.fillStyle = "gray";
				this.gradCtx.fillRect(0, 0, this.baseImage.width, 10);
			}
		}

		_calculateGradientStops() {
			const width = this.baseImage.width;
			const height = this.baseImage.height;
			const hslLeft = [this.x / width, 1 - this.y / height, 0.08];
			const hslMiddle = [this.x / width, 1 - this.y / height, 0.5];
			const hslRight = [this.x / width, 1 - this.y / height, 0.92];
			const rgbLeft = stendhal.data.sprites.filter.hsl2rgb(hslLeft);
			const rgbMiddle = stendhal.data.sprites.filter.hsl2rgb(hslMiddle);
			const rgbRight = stendhal.data.sprites.filter.hsl2rgb(hslRight);

			return [rgbLeft, rgbMiddle, rgbRight];
		}

		_onMouseDown(event) {
			if (!this.enabled) {
				return;
			}
			this.x = event.offsetX;
			this.y = event.offsetY;
			this.draw();
			this.onColorChanged(this.color);
		}

		_onMouseMove(event) {
			if (event.buttons) {
				this._onMouseDown(event);
			}
		}

		_onMouseDownGrad(event) {
			if (!this.enabled) {
				return;
			}
			this.hX = event.offsetX;
			this.draw();
			this.onColorChanged(this.color);
		}

		_onMouseMoveGrad(event) {
			if (event.buttons) {
				this._onMouseDownGrad(event);
			}
		}
	}

	class PaletteColorSelector extends ColorSelector {
		constructor(canvas, gradientCanvas, onColorChanged) {
			super(canvas, gradientCanvas, onColorChanged);
			this._blockWidth = this.baseImage.width / 4;
			this._blockHeight = this.baseImage.height / 4;
			this._x = 0;
			this._y = 0;
			this.hX = this.baseImage.width / 2;
			// These are stupid, but they make closure compiler shut up about
			// undefined properties. (And there's no standard way to define
			// fields in JS, duh).
			this._colorMap = this._colorMap;
			this._hsMap = this._hsMap;
		}

		set x(newX) {
			this._x = Math.floor(newX / this._blockWidth);
		}

		get x() {
			return this._x;
		}

		set y(newY) {
			this._y = Math.floor(newY / this._blockHeight);
		}

		get y() {
			return this._y;
		}

		get color() {
			if (this.enabled) {
				const hs = this._hsMap[this.x][this.y];
				const hsl = [hs[0], hs[1], this.hX / this.baseImage.width];
				const rgbArray = stendhal.data.sprites.filter.hsl2rgb(hsl);
				return stendhal.data.sprites.filter.mergergb(rgbArray);
			}
			return null;
		}

		set color(rgb) {
			if (rgb != null) {
				this.enabled = true;
				const hsl = stendhal.data.sprites.filter.rgb2hsl(stendhal.data.sprites.filter.splitrgb(rgb));
				this.hX = hsl[2] * this.baseImage.width;
				let bestDelta = Number.MAX_VALUE;
				for (let i = 0; i < 4; i++) {
					for (let j = 0; j < 4; j++) {
						const hs = this._hsMap[i][j];
						const hueDelta = hs[0] - hsl[0];
						const satDelta = hs[1] - hsl[1];
						const delta = (hueDelta * hueDelta) + (satDelta * satDelta);
						if (delta < bestDelta) {
							bestDelta = delta;
							this._x = i;
							this._y = j;
						}
					}
				}
			} else {
				this.enabled = false;
			}
		}

		_createColorMap() {
			const hues = [0.05, 0.07, 0.09, 0.11];
			const saturations = [0.70, 0.55, 0.40, 0.25];
			const hsMap = [[],[],[],[]];
			const colors = [[],[],[],[]];
			for (let i = 0; i < 4; i++) {
				for (let j = 0; j < 4; j++) {
					const hue = hues[j];
					const sat = saturations[i];
					hsMap[i].push([hue, sat]);
					const color = stendhal.data.sprites.filter.hsl2rgb([hue, sat, 0.5]);
					colors[i].push(color);
				}
			}
			this._hsMap = hsMap;
			this._colorMap = colors;
		}

		_createBaseImage(width, height) {
			this._createColorMap();
			const img = document.createElement("canvas");
			img.width  = width;
			img.height = height;
			const ctx = img.getContext("2d");

			const blockWidth = width / 4;
			const blockHeight = height / 4;

			for (let x = 0; x < 4; x++) {
				for (let y = 0; y < 4; y++) {
					const rgb = this._colorMap[x][y];
					ctx.fillStyle = this._rgbToCssString(rgb);
					ctx.fillRect(x * blockWidth, y * blockHeight, blockWidth, blockHeight);
				}
			}
			return img;
		}

		_calculateGradientStops() {
			let hs = this._hsMap[this.x][this.y];
			const hslLeft = [hs[0], hs[1], 0.08];
			const hslMiddle = [hs[0], hs[1], 0.5];
			const hslRight = [hs[0], hs[1], 0.92];
			const rgbLeft = stendhal.data.sprites.filter.hsl2rgb(hslLeft);
			const rgbMiddle = stendhal.data.sprites.filter.hsl2rgb(hslMiddle);
			const rgbRight = stendhal.data.sprites.filter.hsl2rgb(hslRight);

			return [rgbLeft, rgbMiddle, rgbRight];
		}

		_drawSelection() {
			this.ctx.strokeStyle = "white";
			this.ctx.strokeRect(this.x * this._blockWidth, this.y * this._blockHeight, this._blockWidth, this._blockHeight);
		}


	}

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

	function indexString(index) {
		if (index < 10) {
			return "0" + index;
		}
		return "" + index;
	}

	function makeSelector(part, index, partChanged) {
		const selector = new PartSelector(part, index, stendhal.ui.outfitCount[part] - 1, partChanged);

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


	const outfit = marauroa.me["outfit_ext"];

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
			return colors[colorName];
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

	const hairColorSelector = createColorSelector(ColorSelector, "hair", hairSelector);
	const eyesColorSelector = createColorSelector(ColorSelector, "eyes", eyesSelector);
	const dressColorSelector = createColorSelector(ColorSelector, "dress", dressSelector);
	const skinColorSelector = createColorSelector(PaletteColorSelector, "skin", headSelector, bodySelector);

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
