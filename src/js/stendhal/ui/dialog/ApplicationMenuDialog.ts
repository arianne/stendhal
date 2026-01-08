/***************************************************************************
 *                (C) Copyright 2003-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../../stendhal";

import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { singletons } from "../../SingletonRepo";

import { Debug } from "../../util/Debug";
import { ScreenCapture } from "../../util/ScreenCapture";


interface MenuAction {
	title: string,
	action: string,
	alt?: string,
	condition?: Function
}

export class ApplicationMenuDialog extends DialogContentComponent {

	private actions = [
			{
				title: "Account",
				children: [
					{
						title: "Select character",
						action: "characterselector"
					},
					{
						title: "Login History",
						action: "loginhistory"
					},
					{
						title: "Change Password",
						action: "changepassword"
					},
					{
						title: "Logout",
						action: "logout"
					}
				] as MenuAction[]
			},
			{
				title: "Tools",
				children: [
					{
						title: "Take Screenshot",
						action: "screenshot",
					},
					/*
					{
						title: "Capture Video",
						alt: "Stop Capture",
						condition: ScreenCapture.isActive,
						action: "screencap"
					},
					*/
					{
						title: "Settings",
						action: "settings",
					}
				] as MenuAction[]
			},
			{
				title: "Commands",
				children: [
					{
						title: "Atlas",
						action: "atlas",
					},
					{
						title: "Online Players",
						action: "who",
					},
					{
						title: "Hall of Fame",
						action: "halloffame",
					},
					{
						title: "Travel Log",
						action: "progressstatus",
					}
				] as MenuAction[]
			},
			{
				title: "Help",
				children: [
					{
						title: "Manual",
						action: "manual",
					},
					{
						title: "FAQ",
						action: "faq",
					},
					{
						title: "Beginners Guide",
						action: "beginnersguide",
					},
					{
						title: "Commands",
						action: "help",
					},
					{
						title: "Rules",
						action: "rules",
					},
					{
						title: "About",
						action: "about",
					}
				] as MenuAction[]
			},
		]

	constructor() {
		super("applicationmenudialog-template");

		if (Debug.isActive("screencap")) {
			this.actions[1].children.push({
				title: "Capture Video",
				alt: "Stop Capture",
				condition: ScreenCapture.isActive,
				action: "screencap"
			});
		}

		var content = "";
		for (var i = 0; i < this.actions.length; i++) {
			content += "<div class=\"inlineblock buttonColumn\"><h4 class=\"menugroup\">" + stendhal.ui.html.esc(this.actions[i].title) + "</h4>"
			for (var j = 0; j < this.actions[i].children.length; j++) {
				const action = this.actions[i].children[j];
				let title = action.title;
				if (action.alt && action.condition && action.condition()) {
					title = action.alt;
				}
				content += "<button id=\"menubutton." + action.action + "\" class=\"menubutton\">" + stendhal.ui.html.esc(title) + "</button><br>";
			}
			content += "</div>";
		}
		this.componentElement.innerHTML = content;

		this.componentElement.addEventListener("click", (event) => {
			this.onClick(event);
		});
	}

	public override getConfigId(): string {
		return "menu";
	}

	private onClick(event: Event) {
		var cmd = (event.target as HTMLInputElement).id?.substring(11);
		if (cmd) {
			singletons.getSlashActionRepo().execute("/" + cmd);
			this.componentElement.dispatchEvent(new Event("close"));
		}
		event.preventDefault();

	}
}
