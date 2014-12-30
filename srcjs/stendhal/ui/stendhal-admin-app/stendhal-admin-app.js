"use strict";

Polymer("stendhal-admin-app", {

	addChatLine: function(type, msg) {
		if (!this.$.list.data) {
			this.$.list.data = [];
		}
		var date = new Date();
		var time = "" + date.getHours() + ":";
		if (date.getHours < 10) {
			time = "0" + time;
		}
		if (date.getMinutes() < 10) {
			time = time + "0";
		};
		time = time + date.getMinutes();
		this.$.list.data.push({
			time: time,
			type: type,
			message: msg
		});
		console.log(this.$.list, this.$.hPanel);
		this.$.list.scrollTarget = this.$.hPanel;
		this.$.list.updateSize();
	}

});