"use strict";

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

stendhal.ui.OutfitDialog = function() {
	if (stendhal.ui.OutfitDialog.instance != null) {
		return;
	}
	var self = this;

	var content = "<div>" +
		"<button type='button' id='setoutfitprevhair'>&lt;</button>"+
		"<canvas id='setoutfithaircanvas' width='48' height='64'></canvas>" +
		"<button type='button' id='setoutfitnexthair'>&gt;</button><br>" +

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

	function getPartSprite(part, index) {
		var fname = "/data/sprites/outfit/" + part + "/" + part + "_0" + indexString(index) + ".png";
		return stendhal.data.sprites.get(fname);
	}

	function makeSelector(part, partChanged) {
		var selector = (function() {
			var _image = null;

			function _draw() {
				var canvas = document.getElementById('setoutfit' + part + 'canvas');
				var ctx = canvas.getContext("2d");
				_image = getPartSprite(part, index);
				ctx.clearRect(0, 0, canvas.width, canvas.height);
				if (_image.width != 0) {
					ctx.drawImage(_image, -48, -128);
				}
				_image.onload = function() {
					ctx.drawImage(_image, -48, -128);
					partChanged();
				}
			}

			function _previous() {
				var numOutfits = maxindex + 1;
				index = index + maxindex;
				index %= numOutfits;
				selector.draw();
				partChanged();
			}

			function _next() {
				var numOutfits = maxindex + 1;
				index++;
				index %= numOutfits;
				selector.draw();
				partChanged();
			}

			function _getImage() {
				return _image;
			}

			return {
				draw: _draw,
				previous: _previous,
				next: _next,
				getImage: _getImage,
				getIndex: function() {
					return indexString(index);
				}
			};
		})();

		var outfit = marauroa.me["outfit"];
		var maxindex;
		var divider;
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
		var index = Math.floor(outfit/divider) % 100;

		var prevButton = document.getElementById("setoutfitprev" + part);
		prevButton.addEventListener("click", function(e) {
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
			var image = selector.getImage();
			if (image.width != 0) {
				ctx.drawImage(image, -48, -128);
			}
		}
		var canvas = document.getElementById('setoutfitcompositecanvas');
		var ctx = canvas.getContext("2d");
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

	var hairSelector = makeSelector("hair", partChanged);
	var headSelector = makeSelector("head", partChanged);
	var bodySelector = makeSelector("body", partChanged);
	var dressSelector = makeSelector("dress", partChanged);

	drawComposite();

	document.getElementById("setoutfitcancel").addEventListener("click", function(e) {
		self.popup.close();
	});
	document.getElementById("setoutfitapply").addEventListener("click", function(e) {
		var outfitCode = hairSelector.getIndex() + headSelector.getIndex() +
			dressSelector.getIndex() + bodySelector.getIndex();
		console.log(outfitCode);
		var action = {
				"type": "outfit",
				"zone": marauroa.currentZoneName,
				"value": outfitCode
		}
		marauroa.clientFramework.sendAction(action);
		self.popup.close();
	});
}
