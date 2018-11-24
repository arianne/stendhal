"use strict";

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

stendhal.ui.OutfitDialog = function() {
	if (stendhal.ui.OutfitDialog.instance != null) {
		return;
	}
	const self = this;
	
	class ColorSelector {
		constructor(canvas, onColorChanged) {
			this.ctx = canvas.getContext("2d");
			this.baseImage = this._createBaseImage(canvas.width, canvas.height);
			this.onColorChanged = onColorChanged;
			this._enabled = false;
			this.x = 0;
			this.y = 0;
			canvas.addEventListener("mousedown", e => this._onMouseDown(e));
			canvas.addEventListener("mousemove", e => this._onMouseMove(e));
		}
		
		set enabled(value) {
			this._enabled = value ? true : false;
			this._draw();
			this.onColorChanged(this.color);
		}
		
		get enabled() {
			return this._enabled;
		}
		
		get color() {
			if (this.enabled) {
				const hsl = [this.x / this.baseImage.width, 1 - this.y / this.baseImage.height, 0.5];
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
		
		_draw() {
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
		}
		
		_onMouseDown(event) {
			if (!this.enabled) {
				return;
			}
			this.x = event.offsetX;
			this.y = event.offsetY;
			this._draw();
			this.onColorChanged(this.color);
		}
		
		_onMouseMove(event) {
			if (event.buttons) {
				this._onMouseDown(event);
			}
		}
	}

	const content = "<div>" +
		"<button type='button' id='setoutfitprevhair'>&lt;</button>"+
		"<canvas id='setoutfithaircanvas' width='48' height='64'></canvas>" +
		"<button type='button' id='setoutfitnexthair'>&gt;</button>" +
		
		"<input type='checkbox' id='setoutfithaircolortoggle'>" +
		"<canvas id='setoutfithaircolorcanvas' width='80' height='52'></canvas>" +
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

	function getPartSprite(part, index, color = null) {
		const fname = "/data/sprites/outfit/" + part + "/" + part + "_0" + indexString(index) + ".png";
		if (color != null) {
			return stendhal.data.sprites.getFiltered(fname, "trueColor", color);
		}
		return stendhal.data.sprites.get(fname);
	}
	
	function makeSelector(part, partChanged) {
		let index = 0;
		const selector = (function() {
			let _image = null;
			let _color = null;

			function _draw() {
				const canvas = document.getElementById('setoutfit' + part + 'canvas');
				const ctx = canvas.getContext("2d");
				_image = getPartSprite(part, index, _color);
				ctx.clearRect(0, 0, canvas.width, canvas.height);
				if (_image.width !== 0) {
					ctx.drawImage(_image, -48, -128);
				}
				_image.onload = function() {
					ctx.drawImage(_image, -48, -128);
					partChanged();
				}
			}

			function _previous() {
				const numOutfits = maxindex + 1;
				index += maxindex;
				index %= numOutfits;
				selector.draw();
				partChanged();
			}

			function _next() {
				const numOutfits = maxindex + 1;
				index++;
				index %= numOutfits;
				selector.draw();
				partChanged();
			}

			function _getImage() {
				return _image;
			}
			
			function _setColor(color) {
				_color = color;
				selector.draw();
			}

			return {
				draw: _draw,
				previous: _previous,
				next: _next,
				getImage: _getImage,
				setColor: _setColor,
				getIndex: function() {
					return indexString(index);
				}
			};
		})();

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
		index = Math.floor(outfit/divider) % 100;

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
			const image = selector.getImage();
			if (image.width !== 0) {
				ctx.drawImage(image, -48, -128);
			}
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
	
	function createColorSelector(toggle, canvas, partSelector) {
		const selector = new ColorSelector(canvas, (color) => { partSelector.setColor(color); });
		selector.enabled = toggle.checked;
		toggle.addEventListener("change", function(e) {
			selector.enabled = toggle.checked;
		});
		return selector;
	}
	
	const hairColorSelector = createColorSelector(document.getElementById("setoutfithaircolortoggle"),
			document.getElementById("setoutfithaircolorcanvas"), hairSelector);
	
	function initialColorValue(part) {
		const colors = marauroa.me["outfit_colors"];
		if (colors != null) {
			let colorName = part;
			console.log(colors);
			if (part === "body" || part === "head") {
				colorName = "skin";
			}
			return colors[colorName];
		}
		return null;
	}
	
	hairColorSelector.color = initialColorValue("hair");
	
	drawComposite();

	document.getElementById("setoutfitcancel").addEventListener("click", function(e) {
		self.popup.close();
	});
	document.getElementById("setoutfitapply").addEventListener("click", function(e) {
		const outfitCode = hairSelector.getIndex() + headSelector.getIndex() +
			dressSelector.getIndex() + bodySelector.getIndex();
		const action = {
				"type": "outfit",
				"zone": marauroa.currentZoneName,
				"value": outfitCode
		};
		let color = hairColorSelector.color;
		if (color != null) {
			action["hair"] = color.toString();
		}
		marauroa.clientFramework.sendAction(action);
		self.popup.close();
	});
}
