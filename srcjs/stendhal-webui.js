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
		width: 128,
		height: 128,
		titleHeight: 15,
		minimumScale: 2,

		zoneChange: function() {
			this.mapWidth = /*marauroa.currentZone.width;*/ 128;
			this.mapHeight = /*marauroa.currentZone.height;*/ 64;
			this.scale = Math.max(this.minimumScale, Math.min(this.height / this.mapHeight, this.width / this.mapWidth));
			/*final int width = Math.min(WIDTH, mapWidth * scale);
			final int height = Math.min(HEIGHT, mapHeight * scale);*/
		},

		updateBasePosition: function() {
			this.xOffset = 0;
			this.yOffset = 0;

			var imageWidth = this.mapWidth * this.scale
			var imageHeight = this.mapHeight * this.scale

			var xpos = Math.round((marauroa.me.x * this.scale) + 0.5) - this.width / 2;
			var ypos = Math.round((marauroa.me.y * this.scale) + 0.5) - this.width / 2;

			if (imageWidth > this.width) {
				// need to pan width
				if ((xpos + this.width) > imageWidth) {
					// x is at the screen border
					this.xOffset = imageWidth - this.width;
				} else if (xpos > 0) {
					this.xOffset = xpos;
				}
			}

			if (imageHeight > this.height) {
				// need to pan height
				if ((ypos + this.height) > imageHeight) {
					// y is at the screen border
					this.yOffset = imageHeight - this.height;
				} else if (ypos > 0) {
					this.yOffset = ypos;
				}
			}
		},

		drawEntities: function() {
			this.scale = 10;
			
			this.zoneChange();
			this.updateBasePosition();
			
			var canvas = document.getElementById("minimap");
			this.ctx = canvas.getContext("2d");
			this.ctx.fillStyle = "rgb(224,224,224)";
			this.ctx.fillRect(0, 0, canvas.width, canvas.height);
			this.ctx.fillStyle = "rgb(255,0,0)";
			this.ctx.strokeStyle = "rgb(0,0,0)";

			this.ctx.translate(Math.round(-this.xOffset), Math.round(-this.yOffset));

			for (var i in marauroa.currentZone) {
				var o = marauroa.currentZone[i];
				if (typeof(o.x) != "undefined" && typeof(o.y) != "undefined" && (o.minimapShow || (marauroa.me.adminlevel && marauroa.me.adminlevel >= 600))) {
					// not supported by IE <= 8
					if (typeof(this.ctx.fillText) != "undefined") {
//						this.ctx.fillText(o.id, o.x * this.scale, o.y * this.scale);
					}
					if (typeof(o.minimapStyle) != "undefined") {
						this.ctx.strokeStyle = o.minimapStyle;
					} else {
						this.ctx.strokeStyle = "rgb(128, 128, 128)";
					}
					this.ctx.strokeRect(o.x * this.scale, o.y * this.scale, o.width * this.scale, o.height * this.scale);
				}
			}

			this.ctx.translate(Math.round(this.xOffset), Math.round(this.yOffset));
		},
	},




	//*************************************************************************
	//                               Buddy List                                
	//*************************************************************************

	buddyList: {
		update: function() {
			var div = document.getElementById("buddyList");
			var html = "";
			for (var i in marauroa.me.buddies) {
				if (marauroa.me.buddies.hasOwnProperty(i)) {
					var styleClass;
					if (marauroa.me.buddies[i] == "true") {
						styleClass = "online";
					} else {
						styleClass = "offline";
					}
					html = html + "<li class='" + styleClass + "'>" + i + "</li>";
				}
			}
			div.innerHTML = "<ul>" + html + "</ul>";
		}
	},



	//*************************************************************************
	//                                   Bag                                   
	//*************************************************************************

	equip: {
		slots: ["head", "lhand", "rhand", "finger", "armor", "cloak", "legs", "feet"],

		update: function() {
			for (var i in this.slots) {
				var s = marauroa.me[this.slots[i]];
				if (typeof(s) != "undefined") {
					var o = s.first();
					if (typeof(o) != "undefined") {
						document.getElementById(this.slots[i]).style.backgroundImage = "url(" + stendhal.server + "/data/sprites/items/" + o['class'] + "/" + o.subclass + ".png" + ")";
					} else {
						document.getElementById(this.slots[i]).style.backgroundImage = "none";
					}
				} else {
					document.getElementById(this.slots[i]).style.backgroundImage = "none";
				}
			}
		}
	},

	bag: {
		update: function() {
			stendhal.ui.itemContainerWindow.render("bag", 12);
		}
	},

	keyring: {
		update: function() {
			stendhal.ui.itemContainerWindow.render("keyring", 8);
		}
	},

	itemContainerWindow: {
		render: function(name, size) {
			var cnt = 0;
			for (var i in marauroa.me[name]) {
				if (!isNaN(i)) {
					var o = marauroa.me[name][i];
					document.getElementById(name + cnt).style.backgroundImage = "url(" + stendhal.server + "/data/sprites/items/" + o['class'] + "/" + o.subclass + ".png " + ")";
					cnt++;
				}
			}
			for (var i = cnt; i < size; i++) {
				document.getElementById(name + i).style.backgroundImage = "none";
			}
		}
	}
}