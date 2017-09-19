"use strict";

Polymer({
	is: "stendhal-admin-app",
	
	properties: {
		chat: {
			type: Array,
			value: []
		}
	},

	addChatLine: function(type, msg) {
		var date = new Date();
		var time = "" + date.getHours() + ":";
		if (date.getHours < 10) {
			time = "0" + time;
		}
		if (date.getMinutes() < 10) {
			time = time + "0";
		}
		time = time + date.getMinutes();
		this.push('chat', {
			time: time,
			type: "log" + type,
			message: msg
		});
		this.$.list.scrollTarget = this.$.hPanel;
		this.$.list.fire('resize');
	},

	clickBan: function(e) {
		this.fillChatbar("/ban " + this.$.chatbar.value);
	},
	clickDeepInspect: function(e) {
		this.fillChatbar("/script DeepInspect.class character " + this.$.chatbar.value);
	},
	clickInspect: function(e) {
		this.fillChatbar("/inspect " + this.$.chatbar.value);
	},
	clickSupport: function(e) {
		this.fillChatbar("/support " + this.$.chatbar.value);
	},
	clickSupportAnswer: function(e) {
		this.fillChatbar("/supporta " + this.$.chatbar.value);
	},
	clickWho: function(e) {
		this.fillChatbar("/who");
		this.$.chatbar.send();
	},
	clickWhere: function(e) {
		this.fillChatbar("/where " + this.$.chatbar.value);
	},
	
	fillChatbar: function(value) {
		this.$.chatbar.value = value;
		this.$.chatbar.focus();
	}
});