/***************************************************************************
 *                (C) Copyright 2003-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Compontent";

declare var stendhal: any;

export class ApplicationMenuDialog extends Component {

	private actions = [
			{
				title: "Account",
				children: [
					{
						title: "Change Password",
						action: "changepassword",
					},
					{
						title: "Select character",
						action: "characterselector",
					},
					{
						title: "Login History",
						action: "loginhistory",
					}
				]
			},
/*
			{
				title: "Tools",
				children: [
					{
						title: "Take Screenshot",
						action: "takescreenshot",
					},
					{
						title: "Settings",
						action: "settings",
					}
				]
			},
*/
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
				]
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
					}
				]
			},
		]

	constructor() {
		super("applicationmenudialog-template");

		var content = "";
		for (var i = 0; i < this.actions.length; i++) {
			content += "<div class=\"inlineblock\"><h4 class=\"menugroup\">" + stendhal.ui.html.esc(this.actions[i].title) + "</h4>"
			for (var j = 0; j < this.actions[i].children.length; j++) {
				content += "<button id=\"menubutton." + this.actions[i].children[j].action + "\">" + stendhal.ui.html.esc(this.actions[i].children[j].title) + "</button><br>";
			}
			content += "</div>";
		}
		this.componentElement.innerHTML = content;

		this.componentElement.addEventListener("click", (event) => {
			this.onClick(event);
		});
	}


	private onClick(event: Event) {
		var cmd = (event.target as HTMLInputElement).id?.substring(11);
		if (cmd) {
			stendhal.slashActionRepository.execute("/" + cmd);
		}
		this.componentElement.dispatchEvent(new Event("close"));
		event.preventDefault();

	}
}
