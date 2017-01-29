"use strict";

Polymer({
	is: "stendhal-stats", 

	properties: {
		text: {
			type: String,
			value: "Stats"
		}
	},

	update: function(object) {
		this.text = "HP: " + object.hp + " / " + object.base_hp + "\r\n"
			+ "ATK: " + object.atk + " x " + object.atk_item + "\r\n"
			+ "DEF: " + object.def + " x " + object.def_item + "\r\n"
		    + "XP: " + object.xp + "\r\n"
		    + "Level: " + object.xp;
	}
});