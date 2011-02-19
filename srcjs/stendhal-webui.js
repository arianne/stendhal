stendhal = {};

/**
 * Stendhal User Interface
 */
stendhal.ui = {

	/**
	 * HTML code manipulation.
	 */
	html: {
		esc: function(msg){
			return msg.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace("\n", "<br>");
		}
	},

	/**
	 * chat and eventlog
	 */
	chatLog: {
		addLine: function(type, msg) {
			var e = document.createElement('p');
			e.className = "log" + stendhal.ui.html.esc(type);
			e.innerHTML = stendhal.ui.html.esc(msg);
			document.getElementById('chat').appendChild(e);
			document.getElementById('chat').scrollTop = 1000000;
		},

		clear: function() {
			document.getElementById("chat").innerHTML = "";
		}
	}
}