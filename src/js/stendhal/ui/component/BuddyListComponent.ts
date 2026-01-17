/***************************************************************************
 *                (C) Copyright 2003-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";

import { Component } from "../toolkit/Component";

import { ActionContextMenu } from "../dialog/ActionContextMenu";
import { ChatInputComponent } from "./ChatInputComponent";
import { Paths } from "../../data/Paths";
import { stendhal } from "stendhal";


import { marauroa } from "marauroa"

/**
 * displays the player stats
 */
export class BuddyListComponent extends Component {

	private buddies: any[] = [];
	private current?: HTMLElement;
	private lastHtml?: string;

	constructor() {
		super("buddyList");

		this.componentElement.addEventListener("mouseup", (event) => {
			this.onMouseUp(event);
		});
		this.componentElement.addEventListener("contextmenu", (event) => {
			event.preventDefault();
		});

	}

	// TODO: don't rebuilt the buddylist completely on every turn,
	//       but implement an observer
	public update() {
		let data = marauroa.me["buddies"];
		let buddies = [];
		for (let buddy in data) {
			if (data.hasOwnProperty(buddy)) {
				let entry: any = {"name": buddy};
				if (data[buddy] == "true") {
					entry.isOnline = true;
					entry.status = "online";
				} else {
					entry.isOnline = false;
					entry.status = "offline";
				}
				buddies.push(entry);
			}
		}
		this.sort(buddies);

		let html = "";
		for (let i = 0; i < buddies.length; i++) {
			html += "<li class=" + buddies[i].status + "><img src=\"";
			if (buddies[i].status == "online") {
				html += Paths.gui + "/buddy_online.png";
			} else {
				html += Paths.gui + "/buddy_offline.png";
			}
			html += "\"> " + stendhal.ui.html.esc(buddies[i].name) + "</li>";
		}

		if (this.lastHtml !== html) {
			let buddyListUL = this.child("#buddyListUL")!;
			buddyListUL.innerHTML = html;
			this.lastHtml = html;
		}
		this.buddies = buddies;
	}

	/**
	 * sorts the buddy list
	 */
	sort(buddies: any[]) {
		buddies.sort(function compare(a, b) {
			// online buddies first
			if (a.isOnline) {
				if (!b.isOnline) {
					return -1;
				}
			} else {
				if (b.isOnline) {
					return 1;
				}
			}

			// sort by name
			if (a.name < b.name) {
				return -1;
			}
			if (a.name > b.name) {
				return 1;
			}
			return 0;
		});
	}

	setBuddyStatus(buddy: string, status: string) {
		this.removeBuddy(buddy);
		let newEntry = {"name": buddy, "status": status};
		this.buddies.push(newEntry);
		this.sort(this.buddies);
	}

	hasBuddy(buddy: string) {
    	let arrayLength = this.buddies.length;
    	for (let i = 0; i < arrayLength; i++) {
			if(this.buddies[i].name === buddy) {
				return true;
			}
    	}
    	return false;
	}

	removeBuddy(buddy: string) {
		let arrayLength = this.buddies.length;
		for (let i = 0; i < arrayLength; i++) {
			if (this.buddies[i].name === buddy) {
				this.buddies.splice(i, 1);
				return;
			}
		}
	}

	buildActions(actions: any) {
		if (!this.current) {
			return;
		}

		if (this.current.className === "online") {
			actions.push({
				title: "Talk",
				action: function(buddyListComponent: BuddyListComponent) {
					(ui.get(UIComponentEnum.ChatInput) as ChatInputComponent).setText("/msg "
							+ buddyListComponent.current!.textContent!.trim()
							+ " ");
				}
			});
			actions.push({
				title: "Where",
				action: function(buddyListComponent: BuddyListComponent) {
					let action = {
						"type": "where",
						"target": buddyListComponent.current!.textContent!.trim(),
						"zone": marauroa.currentZoneName
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
			// Invite
		} else {
			actions.push({
				title: "Leave Message",
				action: function(buddyListComponent: BuddyListComponent) {
					(ui.get(UIComponentEnum.ChatInput) as ChatInputComponent).setText("/storemessage "
							+ buddyListComponent.current!.textContent!.trim()
							+ " ");
				}
			});
		}
		actions.push({
			title: "Remove",
			action: function(buddyListComponent: BuddyListComponent) {
				let action = {
					"type": "removebuddy",
					"target": buddyListComponent.current!.textContent!.trim(),
					"zone": marauroa.currentZoneName
				};
				marauroa.clientFramework.sendAction(action);
				buddyListComponent.removeBuddy(buddyListComponent.current!.textContent!.trim());
			}
		});

	}

	onMouseUp(event: MouseEvent) {

		// get the <li> element. Ignore the click, if it was on the <ul> outside any <li>s.
		this.current = undefined;
		let target = event.target as HTMLElement;
		if (target.tagName === "LI") {
			this.current = target;
		} else if (target.tagName === "IMG") {
			this.current = target.parentElement!;
		}

		if (this.current) {
			stendhal.ui.actionContextMenu.set(ui.createSingletonFloatingWindow("Action",
				new ActionContextMenu(this), Math.max(10, event.pageX - 50), event.pageY - 5));
		}
	}
}
