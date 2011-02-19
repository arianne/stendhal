stendhal = {};
stendhal.ui = {
	html: {
		esc: function(msg){
			return msg.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
		}
	},
	addChatLogLine: function(type, msg) {
		var e = document.createElement('p');
		e.className = "log" + stendhal.ui.html.esc(type);
		e.innerHTML = stendhal.ui.html.esc(msg);
		document.getElementById('chat').appendChild(e);
		document.getElementById('chat').scrollTop = 1000000;
	}
}