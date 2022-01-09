/***************************************************************************
 *                   (C) Copyright 2015-2017 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

/**
 * @constructor
 */
stendhal.ui.Popup = function(title, content, x, y) {

	const closeSound = "click-1";

	this.close = function() {
		if (that.onClose) {
			that.onClose.call(that);
		}
		var popupcontainer = document.getElementById("popupcontainer");
		if (popupcontainer.contains(that.popupdiv)) {
			popupcontainer.removeChild(that.popupdiv);
		}
	};

	function createTitleHtml() {
		return "<div class='popuptitle background'><div class='popuptitleclose'>X</div>" + stendhal.ui.html.esc(title) + "</div>";
	}

	function onClose(e) {
		that.close();
		e.preventDefault();

		stendhal.ui.sound.playGlobalizedEffect(closeSound);
	}

	/**
	 * start draging of popup window
	 */
	function onMouseDown(e) {
		window.addEventListener("mousemove", onMouseMovedDuringDrag, true);
		window.addEventListener("mouseup", onMouseUpDuringDrag, true);
		e.preventDefault();
		var box = that.popupdiv.getBoundingClientRect();
		that.offsetX = e.clientX - box.left - window.pageXOffset;
		that.offsetY = e.clientY - box.top - window.pageYOffset;
	}

	/**
	 * updates position of popup window during drag
	 */
	function onMouseMovedDuringDrag(e) {
		that.popupdiv.style.left = e.clientX - that.offsetX + 'px';
		that.popupdiv.style.top = e.clientY - that.offsetY + 'px';
	}

	/**
	 * deregister global event listeners used for dragging popup window
	 */
	function onMouseUpDuringDrag(e) {
		window.removeEventListener("mousemove", onMouseMovedDuringDrag, true);
		window.removeEventListener("mouseup", onMouseUpDuringDrag, true);
	}


	var that = this;
	var popupcontainer = document.getElementById("popupcontainer");
	this.popupdiv = document.createElement('div');
	this.popupdiv.style.position = "absolute";
	this.popupdiv.style.left = x + "px";
	this.popupdiv.style.top = y + "px";
	this.popupdiv.className = "popupdiv";
	var temp = content;
	if (title) {
		temp = createTitleHtml() + content;
	}
	this.popupdiv.innerHTML = temp;
	this.popupdiv.querySelector(".popuptitle").addEventListener("mousedown", onMouseDown);
	this.popupdiv.querySelector(".popuptitleclose").addEventListener("click", onClose);
	popupcontainer.appendChild(this.popupdiv);
}
