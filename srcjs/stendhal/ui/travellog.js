/***************************************************************************
 *                   (C) Copyright 2007-2019 - Stendhal                    *
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
 * handling of key presses and releases
 */
stendhal.ui.travellog = {
	currentProgressType: "",

	open: function(dataItems) {
		if (!document.getElementById("tavellogpopup")) {
			stendhal.ui.travellog.createWindow(dataItems);
		}

		// trigger loading of content for first entry
		stendhal.ui.travellog.currentProgressType = dataItems[0];
		var action = {
			"type":           "progressstatus",
			"progress_type":  stendhal.ui.travellog.currentProgressType
		}
		marauroa.clientFramework.sendAction(action);
	},


	progressTypeData: function(progressType, dataItems) {
		if (progressType !== stendhal.ui.travellog.currentProgressType) {
			return;
		}
		var html = "";
		for (var i = 0; i < dataItems.length; i++) {
			html += "<option value=\"" + stendhal.ui.html.esc(dataItems[i]) + "\">"
				+ stendhal.ui.html.esc(dataItems[i]) + "</option>";
		}
		document.getElementById("travellogitems").innerHTML = html;

		// trigger loading of content for first entry
		if (dataItems) {
			document.getElementById("travellogitems").value = dataItems[0];
			var action = {
				"type":           "progressstatus",
				"progress_type":  progressType,
				"item":           dataItems[0]
			}
			marauroa.clientFramework.sendAction(action);
		}
	},


	itemData: function(progressType, selectedItem, description, dataItems) {
		var html = "<h3>" + stendhal.ui.html.esc(selectedItem) + "</h3>";
		html += "<p id=\"travellogdescription\">" + stendhal.ui.html.esc(description) + "</p>";
		html += "<ul>";
		for (var i = 0; i < dataItems.length; i++) {
			html += "<li>" + stendhal.ui.html.esc(dataItems[i]);
		}
		html += "</ul>";
		document.getElementById("travellogdetails").innerHTML = html;
	},


	createWindow: function(dataItems) {
		var html = "<div class=\"tavellogpopup\">";
		for (var i = 0; i < dataItems.length; i++) {
			html += "<button id=\"" + stendhal.ui.html.esc(dataItems[i]) + "\" class=\"progressTypeButton\">"
				+ stendhal.ui.html.esc(dataItems[i]) + "</button>";
		}
		html += "<div>";
		html += "<select id=\"travellogitems\" size=\"20\"></select>";
		html += "<div id=\"travellogdetails\"></div>";
		html += "</div>"
		html += "</div>";

		var popup = new stendhal.ui.Popup("Travel Log", html, 160, 50);

		progressTypeButtons = document.querySelectorAll(".progressTypeButton").forEach(function(button) {
			button.addEventListener("click", function(e) {
				stendhal.ui.travellog.currentProgressType = e.target.id;
				var action = {
					"type":           "progressstatus",
					"progress_type":  stendhal.ui.travellog.currentProgressType
				}
				marauroa.clientFramework.sendAction(action);
			});
		});
		document.getElementById("travellogitems").addEventListener("change", function(e) {
			var action = {
				"type":           "progressstatus",
				"progress_type":  stendhal.ui.travellog.currentProgressType,
				"item":           e.target.value
			}
			marauroa.clientFramework.sendAction(action);
		});

		return popup;
	}
}
