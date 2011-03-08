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



	//*************************************************************************
	//                                 Chat Bar                                
	//*************************************************************************


	chatBar: {
		history: [],
		historyIndex: 0,

		clear: function() {
			document.getElementById('chatbar').value = '';
		},

		fromHistory: function(i) {
			this.historyIndex = this.historyIndex + i;
			if (this.historyIndex < 0) {
				this.historyIndex = 0;
			}
			if (this.historyIndex >= this.history.length) {
				this.historyIndex = this.history.length;
				this.clear();
			} else {
				document.getElementById('chatbar').value = this.history[this.historyIndex];
			}
		},

		keydown: function(e) {
			var event = e
			if (!event) {
				event = window.event;
			}
			if (event.shiftKey) {
				var code;
				if (event.which) {
					code = event.which;
				} else {
					code = e.keyCode;
				}
				if (code == 38) {
					stendhal.ui.chatBar.fromHistory(-1);
				} else if (code == 40){
					stendhal.ui.chatBar.fromHistory(1);
				}
			}
		},
		remember: function(text) {
			if (this.history.length > 100) {
				this.history.shift();
			}
			this.history[this.history.length] = text;
			this.historyIndex = this.history.length;
		},

		send: function() {
			var val = document.getElementById('chatbar').value;
			var array = val.split(" ");
			if (array[0] == "/choosecharacter") {
				marauroa.clientFramework.chooseCharacter(array[1]);
			} else if (val == '/close') {
				marauroa.clientFramework.close();
			} else {
				if (stendhal.slashActionRepository.execute(val)) {
					this.remember(val);
				}
			}
			this.clear();
		}
	},



	//*************************************************************************
	//                                 Chat Log                                
	//*************************************************************************

	chatLog: {
		addLine: function(type, msg) {
			var e = document.createElement('p');
			e.className = "log" + stendhal.ui.html.esc(type);
			var date = new Date();
			var time = "" + date.getHours() + ":";
			if (date.getHours < 10) {
				time = "0" + time;
			}
			if (date.getMinutes() < 10) {
				time = time + "0";
			};
			time = time + date.getMinutes();
			
			e.innerHTML = "[" + time + "] " + stendhal.ui.html.esc(msg);
			document.getElementById('chat').appendChild(e);
			document.getElementById('chat').scrollTop = 1000000;
		},

		clear: function() {
			document.getElementById("chat").innerHTML = "";
		}
	},



	//*************************************************************************
	//                                Mini Map                                 
	//*************************************************************************

	minimap: {
		drawEntities: function() {
			var zoom = 10;
			var canvas = document.getElementById("minimap");
			this.ctx = canvas.getContext("2d");
			this.ctx.fillStyle = "rgb(224,224,224)";
			this.ctx.fillRect(0, 0, canvas.width, canvas.height);
			this.ctx.fillStyle = "rgb(255,0,0)";
			this.ctx.strokeStyle = "rgb(0,0,0)";

			for (var i in marauroa.currentZone) {
				var o = marauroa.currentZone[i];
				if (typeof(o.x) != "undefined" && typeof(o.y) != "undefined") {
					this.ctx.fillText(o.id, o.x * zoom, o.y * zoom);
					if (typeof(o.minimapStyle) != "undefined") {
						this.ctx.strokeStyle = o.minimapStyle;
					} else {
						this.ctx.strokeStyle = "rgb(128, 128, 128)";
					}
					this.ctx.strokeRect(o.x * zoom, o.y * zoom, o.width * zoom, o.height * zoom);
				}
			}
		}
	},




	//*************************************************************************
	//                               Buddy List                                
	//*************************************************************************

	buddyList: {
		update: function() {
			var div = document.getElementById("buddyList");
			var html = "";
			for (var i in marauroa.me.buddies) {
				var styleClass;
				if (marauroa.me.buddies[i] == "true") {
					styleClass = "online";
				} else {
					styleClass = "offline";
				}
				html = html + "<li class='" + styleClass + "'>" + i + "</li>";
			}
			div.innerHTML = "<ul>" + html + "</ul>";
		}
	},



	//*************************************************************************
	//                                   Bag                                   
	//*************************************************************************

	bag: {
		update: function() {
			var div = document.getElementById("bag");
			var html = "";
			for (var i in marauroa.me.bag) {
				html = html + "<li>" + marauroa.me.bag[i].title + "</li>";
			}
			div.innerHTML = "<ul>" + html + "</ul>";
		}
	}
}