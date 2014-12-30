"use strict";

stendhal.ui.chatLog.addLine = function(type, msg) {
	document.getElementById("app").addChatLine(type, msg);
};

