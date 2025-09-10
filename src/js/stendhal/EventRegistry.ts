/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var marauroa: any;
declare var stendhal: any;

import { RPEntity } from "./entity/RPEntity";
import { RPObject } from "./entity/RPObject";

import { BestiaryEvent } from "./event/BestiaryEvent";
import { ChatOptionsEvent } from "./event/ChatOptionsEvent";
import { ExamineEvent } from "./event/ExamineEvent";
import { GenericEvent } from "./event/GenericEvent";
import { GroupChangeEvent } from "./event/GroupChangeEvent";
import { GroupInviteEvent } from "./event/GroupInviteEvent";
import { PlayerLoggedOnEvent } from "./event/PlayerLoggedOnEvent";
import { PlayerLoggedOutEvent } from "./event/PlayerLoggedOutEvent";
import { ProgressStatusEvent } from "./event/ProgressStatusEvent";
import { RPEvent } from "./event/RPEvent";
import { SoundEvent } from "./event/SoundEvent";
import { TradeEvent } from "./event/TradeEvent";
import { ViewChangeEvent } from "./event/ViewChangeEvent";

import { ui } from "./ui/UI";

import { DialogContentComponent } from "./ui/toolkit/DialogContentComponent";

import { Chat } from "./util/Chat";


export class EventRegistry {

	private initialized = false;

	/** Singleton instance. */
	private static instance: EventRegistry;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): EventRegistry {
		if (!EventRegistry.instance) {
			EventRegistry.instance = new EventRegistry();
		}
		return EventRegistry.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Registers standard events.
	 */
	init() {
		if (this.initialized) {
			console.warn("tried to re-initialize EventRegistry");
			return;
		}
		this.initialized = true;

		this.register("generic_event", new GenericEvent());

		this.register("chat_options", new ChatOptionsEvent());
		this.register("examine", new ExamineEvent());
		this.register("group_change_event", new GroupChangeEvent());
		this.register("group_invite_event", new GroupInviteEvent());
		this.register("progress_status_event", new ProgressStatusEvent());
		this.register("trade_state_change_event", new TradeEvent());

		this.register("attack", {
			execute: function(entity: RPEntity) {
				var target = entity.getAttackTarget();
				if (!target) {
					return;
				}
				if (this.hasOwnProperty("hit")) {
					var damage = parseInt(this["damage"], 10);
					if (damage !== 0) {
						target.onDamaged(entity, damage);
					} else {
						target.onBlocked(entity);
					}
				} else {
					target.onMissed(entity);
				}
				entity.onAttackPerformed(parseInt(this["type"], 10), this.hasOwnProperty("ranged"), this["weapon"]);
			}
		}); // attack

		this.register("bestiary", new BestiaryEvent());

		this.register("global_visual_effect", {
			execute: function(rpobject: RPObject) {
				// TODO: new GlobalVisualEffectEvent();
			}
		}); // global_visual_effect

		this.register("image_event", {
			execute: function(rpobject: RPObject) {
				// TODO: new ImageEffectEvent();
				console.log("image_event", this, rpobject);
			}
		}); // image_event

		this.register("player_logged_on", new PlayerLoggedOnEvent());
		this.register("player_logged_out", new PlayerLoggedOutEvent());

		this.register("private_text", {
			soundTextEvents: {
				"privmsg": true,
				"support": true,
				"tutorial": true
			},

			execute: function(rpobject: RPObject) {
				const ttype = this["texttype"].toLowerCase();
				const msg = this["text"].replace(/\\r\\n/g, "\n").replace(/\\r/g, "\n");

				let profile;
				if (this.hasOwnProperty("profile")) {
					profile = this["profile"];
				} else if (ttype === "tutorial") {
					profile = "floattingladynpc";
				}

				if (ttype === "server" && msg.includes("\n")) {
					Chat.log(ttype, msg.split("\n"), undefined, profile);
				} else {
					// scene settings messages should not disturb playing, just create some atmosphere
					const headed = ttype !== "scene_setting";
					Chat.log(ttype, msg, undefined, profile, headed);
				}

				// play notification sound
				const notif = stendhal.config.get("chat.private.sound");
				if (notif && this.soundTextEvents[ttype]) {
					stendhal.sound.playGlobalizedEffect(notif);
				}
			}
		}); // private_text

		this.register("reached_achievement", {
			execute: function(rpobject: RPObject) {
				stendhal.ui.gamewindow.addAchievementNotif(this["category"], this["title"], this["description"]);
			}
		}); // reached_achievement

		this.register("show_item_list", {
			execute: function(rpobject: RPObject) {
				let title = "Items";
				let caption = "";
				let items = [];

				if (this.hasOwnProperty("title")) {
					title = this["title"];
				}
				if (this.hasOwnProperty("caption")) {
					caption = this["caption"];
				}
				if (this.hasOwnProperty("content")) {
					for (var obj in this["content"]) {
						if (this["content"].hasOwnProperty(obj)) {
							var slotObj = this["content"][obj];
							var data = this["content"][obj]["a"];
							const i = {
								clazz: data["class"],
								subclass: data["subclass"],
								img: data["class"] + "/" + data["subclass"] + ".png",
								price: data["price"],
								desc: data["description_info"]
							}

							// seller shops prefix prices with "-"
							if (i.price.startsWith("-")) {
								i.price = i.price.substr(1);
							}
							items.push(i);
						}
					}
				}

				const content = new class extends DialogContentComponent {} ("empty-div-template");
				content.componentElement.classList.add("shopsign");
				const captionElement = document.createElement("div");
				captionElement.className = "horizontalgroup shopcaption";
				captionElement.textContent = caption + "\nItem\t-\tPrice\t-\tDescription";
				content.componentElement.appendChild(captionElement);
				const itemList = document.createElement("div");
				itemList.className = "shoplist";
				content.componentElement.appendChild(itemList);

				// TODO: organize in columns & show item sprites
				for (const i of items) {
					const row = document.createElement("div");
					row.className = "horizontalgroup shoprow";
					const img = document.createElement("div");
					img.className = "shopcol";
					img.appendChild(stendhal.data.sprites.get(stendhal.paths.sprites + "/items/" + i.img));
					row.appendChild(img);
					const price = document.createElement("div");
					price.className = "shopcol";
					price.textContent = i.price;
					row.appendChild(price);
					const desc = document.createElement("div");
					desc.className = "shopcol shopcolr";
					desc.textContent = i.desc;
					row.appendChild(desc);
					itemList.appendChild(row);
				}

				stendhal.ui.globalInternalWindow.set(
						ui.createSingletonFloatingWindow(title, content, 20, 20));
			}
		}); // show_item_list

		this.register("show_outfit_list", {
			execute: function(rpobject: RPObject) {
				let title = "Outfits";
				let caption = "";
				let outfits = [];

				if (this.hasOwnProperty("title")) {
					title = this["title"];
				}
				if (this.hasOwnProperty("caption")) {
					caption = this["caption"];
				}
				if (this.hasOwnProperty("outfits")) {
					for (let o of this["outfits"].split(":")) {
						o = o.split(";");
						if (o.length > 2) {
							outfits.push([o[0], o[1], o[2]]);
						}
					}
				}
				if (this.hasOwnProperty("show_base")) {
					//Chat.log("normal", this["show_base"]);
				}

				const content = new class extends DialogContentComponent {} ("empty-div-template");
				content.componentElement.classList.add("shopsign");
				const captionElement = document.createElement("div");
				captionElement.className = "horizontalgroup shopcaption";
				captionElement.textContent = caption;
				content.componentElement.appendChild(captionElement);
				const itemList = document.createElement("div");
				itemList.className = "shoplist";
				content.componentElement.appendChild(itemList);

				// TODO: organize in columns & show outfit sprites
				for (const o of outfits) {
					const row = document.createElement("div");
					row.className = "horizontalgroup shoprow";
					row.textContent = o[0] + ": " + o[2];
					itemList.appendChild(row);
				}

				stendhal.ui.globalInternalWindow.set(
						ui.createSingletonFloatingWindow(title, content, 20, 20));
			}
		}); // show_outfit_list

		this.register("sound_event", new SoundEvent());

		this.register("text", {
			execute: function(entity: RPEntity) {
				if (this.hasOwnProperty("range")) {
					entity.say(this["text"], this["range"]);
				} else {
					entity.say(this["text"]);
				}
			}
		}); // text

		this.register("transition_graph", {
			execute: function(rpobject: RPObject) {
				// TODO: new TransitionGraphEvent();
			}
		}); // transition_graph

		this.register("view_change", new ViewChangeEvent());
	}

	/**
	 * Registers a new event.
	 *
	 * @param _type
	 *     String identifier.
	 * @param _event
	 *     Event to be registered.
	 */
	register(_type: string, _event: any) {
		if (_event instanceof RPEvent) {
			marauroa.rpeventFactory[_type] = _event;
		} else {
			marauroa.rpeventFactory[_type] =
					marauroa.util.fromProto(marauroa.rpeventFactory["_default"], _event);
		}
	}
}
