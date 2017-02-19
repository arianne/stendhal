/***************************************************************************
 *                   (C) Copyright 2015-2017 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

"use strict";
window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

stendhal.ui.Popup = function(title, content, x, y) {
	this.close = function() {
		document.getElementById("popupcontainer").removeChild(this.popupdiv);
	}

	function createTitleHtml() {
		return "<div class='popuptitle' style='cursor: default'><div class='popuptitleclose' style='float:right'>X</div>" + stendhal.ui.html.esc(title) + "</div>";
	}

	function close(e) {
		var popupcontainer = document.getElementById("popupcontainer");
		popupcontainer.removeChild(that.popupdiv);
		if (that.onClose) {
			that.onClose.call(that);
		}
		e.preventDefault();
	}

	function onMouseDown(e) {
		console.log("down", that, that.popupdiv, title);
		e.preventDefault();
	}

	var that = this;
	var popupcontainer = document.getElementById("popupcontainer");
	this.popupdiv = document.createElement('div');
	this.popupdiv.style = "position: absolute; left: " + x + "px; top: " + y + "px";
	var temp = content;
	if (title) {
		temp = createTitleHtml() + content;
	}
	this.popupdiv.innerHTML = temp;

	this.popupdiv.querySelector(".popuptitle").addEventListener("mousedown", onMouseDown);
	this.popupdiv.querySelector(".popuptitleclose").addEventListener("click", close);
	popupcontainer.appendChild(this.popupdiv);
}