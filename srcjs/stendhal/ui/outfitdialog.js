"use strict";

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

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
			this._ctx = canvas.getContext("2d");
			this._width = canvas.width;
			this._height = canvas.height;
			this._color = null;
			this._image = null;
		}
		
		draw() {
			const image = this._getPartSprite(this._part, this._index, this._color);
			this._image = image;
			this._ctx.clearRect(0, 0, this._width, this._height);	
			
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
				return stendhal.data.sprites.getFiltered(fname, "trueColor", color);
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
				this.ctx.fillStyle = "black";
				this.ctx.beginPath();
				this.ctx.moveTo(this.x, 0);
				this.ctx.lineTo(this.x, this.baseImage.height);
				this.ctx.moveTo(0, this.y);
				this.ctx.lineTo(this.baseImage.width, this.y);
				this.ctx.stroke();
			} else {
				this.ctx.fillStyle = "gray";
				this.ctx.fillRect(0, 0, this.baseImage.width, this.baseImage.height);
			}
			this._drawGradientPart();
		}
		
		_drawGradientPart() {
			if (this.enabled) {
				const width = this.baseImage.width;
				const height = this.baseImage.height;
				const gradient = this.gradCtx.createLinearGradient(0, 0, this.baseImage.width, 0);
				
				const hslLeft = [this.x / width, 1 - this.y / height, 0.08];
				const hslMiddle = [this.x / width, 1 - this.y / height, 0.5];
				const hslRight = [this.x / width, 1 - this.y / height, 0.92];
				const rgbLeft = stendhal.data.sprites.filter.hsl2rgb(hslLeft);
				const rgbMiddle = stendhal.data.sprites.filter.hsl2rgb(hslMiddle);
				const rgbRight = stendhal.data.sprites.filter.hsl2rgb(hslRight);
				
				gradient.addColorStop(0, this._rgbToCssString(rgbLeft));
				gradient.addColorStop(0.5, this._rgbToCssString(rgbMiddle));
				gradient.addColorStop(1, this._rgbToCssString(rgbRight));
				
				const ctx = this.gradCtx;
				ctx.fillStyle = gradient;
				ctx.fillRect(0, 0, width, 10);
				
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

	const content = "<div>" +
		"<button type='button' id='setoutfitprevhair'>&lt;</button>"+
		"<canvas id='setoutfithaircanvas' width='48' height='64'></canvas>" +
		"<button type='button' id='setoutfitnexthair'>&gt;</button>" +
		
		"<input type='checkbox' id='setoutfithaircolortoggle'>" +
		"<label for='setoutfithaircolortoggle'>Hair color</label>" +
		"<canvas id='setoutfithaircolorcanvas' width='80' height='52'></canvas>" +
		"<canvas id='setoutfithaircolorgradient' width='80' height='10'></canvas>" +
		"<br>" +

		"<button type='button' id='setoutfitprevhead'>&lt;</button>" +
		"<canvas id='setoutfitheadcanvas' width='48' height='64'></canvas>" +
		"<button type='button' id='setoutfitnexthead'>&gt;</button><br>" +

		"<button type='button' id='setoutfitprevbody'>&lt;</button>" +
		"<canvas id='setoutfitbodycanvas' width='48' height='64'></canvas>" +
		"<button type='button' id='setoutfitnextbody'>&gt;</button><br>" +

		"<button type='button' id='setoutfitprevdress'>&lt;</button>" +
		"<canvas id='setoutfitdresscanvas' width='48' height='64'></canvas>" +
		"<button type='button' id='setoutfitnextdress'>&gt;</button>" +
		
		"<input type='checkbox' id='setoutfitdresscolortoggle'>" +
		"<label for='setoutfitdresscolortoggle'>Dress color</label>" +
		"<canvas id='setoutfitdresscolorcanvas' width='80' height='52'></canvas>" +
		"<canvas id='setoutfitdresscolorgradient' width='80' height='10'></canvas>" +

		"</div>" +
		"<div><canvas id='setoutfitcompositecanvas' width='48' height='64'></canvas></div><br>" +
		"<div><button type='button' id='setoutfitcancel'>Cancel</button>" +
		"<button type='button' id='setoutfitapply'>Change Outfit</button></div>";

	function indexString(index) {
		if (index < 10) {
			return "0" + index;
		}
		return "" + index;
	}
	
	function makeSelector(part, partChanged) {
		const outfit = marauroa.me["outfit"];
		let maxindex;
		let divider;
		switch (part) {
			case "hair": divider = 1000000;
				maxindex = 46;
				break;
			case "head" : divider = 10000;
				maxindex = 22;
				break;
			case "dress" : divider = 100;
				maxindex = 63;
				break;
			case "body": divider = 1;
				maxindex = 15;
				break;
		}
		const index = Math.floor(outfit/divider) % 100;
		const selector = new PartSelector(part, index, maxindex, partChanged);

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
		ctx.clearRect(0, 0, canvas.width, canvas.height);
		draw(ctx, bodySelector);
		draw(ctx, headSelector);
		draw(ctx, dressSelector);
		draw(ctx, hairSelector);
	}

	function partChanged() {
		drawComposite();
	}
	
	self.popup = new stendhal.ui.Popup("Set outfit", content, 300, 200);
	self.popup.onClose = function(e) {
		stendhal.ui.OutfitDialog.instance = null;
	}
	stendhal.ui.OutfitDialog.instance = self;

	const hairSelector = makeSelector("hair", partChanged);
	const headSelector = makeSelector("head", partChanged);
	const bodySelector = makeSelector("body", partChanged);
	const dressSelector = makeSelector("dress", partChanged);
	
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
	
	function createColorSelector(part, partSelector) {
		const toggle = document.getElementById("setoutfit" + part + "colortoggle");
		const canvas = document.getElementById("setoutfit" + part + "colorcanvas");
		const gradientCanvas = document.getElementById("setoutfit" + part + "colorgradient");
		const selector = new ColorSelector(canvas, gradientCanvas, (color) => { partSelector.color = color; });
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
	
	const hairColorSelector = createColorSelector("hair", hairSelector);
	const dressColorSelector = createColorSelector("dress", dressSelector);
	
	drawComposite();

	document.getElementById("setoutfitcancel").addEventListener("click", function(e) {
		self.popup.close();
	});
	document.getElementById("setoutfitapply").addEventListener("click", function(e) {
		const outfitCode = hairSelector.index + headSelector.index +
			dressSelector.index + bodySelector.index;
		const action = {
				"type": "outfit",
				"zone": marauroa.currentZoneName,
				"value": outfitCode
		};
		let color = hairColorSelector.color;
		if (color != null) {
			action["hair"] = color.toString();
		}
		color = dressColorSelector.color;
		if (color != null) {
			action["dress"] = color.toString();
		}
		marauroa.clientFramework.sendAction(action);
		self.popup.close();
	});
}
