stendhal.ui.chatLog.addLine = function(type, msg) {
	if (!document.getElementById("list").data) {
		document.getElementById("list").data = [];
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
	document.getElementById("list").data.push({
		time: time,
		type: type,
		message: msg
	});;
	document.getElementById("list").scrollTarget=document.getElementById("hPanel");
	document.getElementById("list").updateSize();
	
};