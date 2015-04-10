"use strict";

Polymer("stendhal-inventory", {
	
	size: 1,
	slot: [],

	ready: function() {
		for (var i=0; i < this.size; i++) {
			this.slot.push({sprite: "none", entry: i});
		}
	},
	
	onClick: function(e) {
		var idx = 0;
		for (var i in marauroa.me[this.slotName]) {
			if (!isNaN(i)) {
				var o = marauroa.me[this.slotName][i];
				this.slot[idx].sprite = "url(/data/sprites/items/" + o['class'] + "/" + o.subclass + ".png " + ")";
				if (o.quantity && o.quantity !== 1) {
					this.slot[idx].quantity = o.quantity;
				} else {
					this.slot[idx].quantity = "";
				}
				idx++;
			}
		}
		for (; idx < this.size; idx++) {
			this.slot[idx].sprite = "none";
			this.slot[idx].quantity = "";
		}
		
	}

});