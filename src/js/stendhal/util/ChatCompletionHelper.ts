/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"
import { stendhal } from "../stendhal";

import { SlashActionRepo } from "../SlashActionRepo";

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";

import { ChatInputComponent } from "../ui/component/ChatInputComponent";


export class ChatCompletionHelper {

	/** Chat commands relating to players (tab completes player names). */
	private readonly playerCommands: string[] = [
		"add", "adminlevel", "alter", "alterkill", "alterquest",
		"ban",
		"gag",
		"ignore", "inspect", "inspectkill", "inspectquest",
		"jail",
		"msg",
		"profile",
		"remove",
		"summonat", "supporta", "supportanswer",
		"teleport", "teleportto", "tell",
		"unignore",
		"where"
	];

	/** Chat commands available only for admins/GMs. */
	private readonly adminCommands: string[] = [
		"adminnote", "alter", "altercreature", "alterkill", "alterquest",
		"ban",
		"destroy",
		"gag", "ghostmode",
		"inspect", "inspectkill", "inspectquest", "invisible",
		"jail", "jailreport",
		"script", "summon", "summonat", "supporta", "supportanswer",
		"teleclickmode", "teleport", "teleportto", "tellall"
	];

	/** Chat commands not explicitly registered in SlashActionRepo. */
	private readonly unlistedCommands: string[] = [
		"destroy",
		"ghostmode",
		"info", "inspect", "invisible",
		"jailreport",
		"markscroll",
		"name",
		//"removedetail", // don't include this to prevent accidental loss of detail layer
		"script",
		"teleclickmode",
		"unignore",
		"where", "who"
	];

	/** Available chat commands. */
	private readonly chatCommands: string[] = [];

	private commandPrefix?: string;
	private commandIndex = -1;
	private playerPrefix?: string;
	private playerIndex = -1;

	/** Singleton instance. */
	private static instance: ChatCompletionHelper;


	/**
	 * Retrieves the singleton instance.
	 */
	static get(): ChatCompletionHelper {
		if (!ChatCompletionHelper.instance) {
			ChatCompletionHelper.instance = new ChatCompletionHelper();
		}
		return ChatCompletionHelper.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Called when tab key is pressed while chat input has focus.
	 *
	 * FIXME: Sometimes fails to detect connected players. See FIXME note in
	 *        `event.PlayerLoggedOnEvent`. `entity.Player.Player` will add to list when created but
	 *        this is only a partial rememedy as only players on the same map as `entity.User.User`
	 *        instance will be added.
	 * NOTE:  So far have only noticed players on same map as user at login don't always register
	 *        a login event.
	 */
	onTabKey() {
		const parts: string[] = [];
		const chatInput = (ui.get(UIComponentEnum.ChatInput) as ChatInputComponent);
		let text = chatInput.getText();
		let iterFailsafe = 0;
		while (text.indexOf("  ") > -1) {
			// remove extra whitepace to allow matching
			text = text.replace(/  /, " ");
			iterFailsafe++;
			if (iterFailsafe > 999) {
				break;
			}
		}
		for (const p of text.split(" ")) {
			parts.push(p);
		}
		if (parts.length == 0 || !parts[0].startsWith("/")) {
			return;
		}
		this.parseChatCommands();
		if (this.commandPrefix == undefined) {
			// remove preceding forward slash
			this.commandPrefix = parts[0].substring(1, parts[0].length);
		}
		this.commandIndex++;
		if (parts.length < 2) {
			this.cycleNextCommand(parts);
			if (this.commandIndex < 0 && this.chatCommands.length > 1) {
				// restart from beginning of list
				this.commandIndex = 0;
				this.cycleNextCommand(parts);
			}
			chatInput.setText("/" + parts[0]);
			return;
		}
		if (!stendhal.players || parts.length > 2 || this.playerCommands.indexOf(this.commandPrefix) < 0) {
			return;
		}
		if (this.playerPrefix == undefined) {
			this.playerPrefix = parts[1].toLowerCase();
		}
		this.playerIndex++;
		this.cycleNextPlayer(parts);
		if (this.playerIndex < 0 && stendhal.players.length > 1) {
			// restart from beginning of list
			this.playerIndex = 0;
			this.cycleNextPlayer(parts);
		}
		chatInput.setText(parts.join(" "));
	}

	/**
	 * Iterates slash action commands for next matching instance.
	 *
	 * @param parts {string[]}
	 *   Content to update chat input.
	 */
	private cycleNextCommand(parts: string[]) {
		for (this.commandIndex; this.commandIndex < this.chatCommands.length + 1; this.commandIndex++) {
			if (this.commandIndex >= this.chatCommands.length) {
				// restart from beginning
				this.commandIndex = -1;
				break;
			}
			const cmd = this.chatCommands[this.commandIndex];
			if (cmd.startsWith(this.commandPrefix!)) {
				parts[0] = cmd;
				break;
			}
		}
	}

	/**
	 * Iterates player names for next matching instance.
	 *
	 * @param parts {string[]}
	 *   Content to update chat input.
	 */
	private cycleNextPlayer(parts: string[]) {
		if (!stendhal.players.length) {
			console.error("failed to detect available players");
			this.playerIndex--;
			return;
		}
		for (this.playerIndex; this.playerIndex < stendhal.players.length + 1; this.playerIndex++) {
			if (this.playerIndex >= stendhal.players.length) {
				// restart from beginning
				this.playerIndex = -1;
				break;
			}
			const name = stendhal.players[this.playerIndex];
			if (name.toLowerCase().startsWith(this.playerPrefix!)) {
				parts[parts.length-1] = name;
				break;
			}
		}
	}

	/**
	 * Extracts usable chat commands from slash action repository.
	 */
	private parseChatCommands() {
		if (this.chatCommands.length > 0) {
			return;
		}
		const excludes: string[] = ["/", "_default"];
		const admin = marauroa.me && marauroa.me.isAdmin();
		for (const cmd of [...Object.getOwnPropertyNames(SlashActionRepo.get()), ...this.unlistedCommands].sort()) {
			if (excludes.indexOf(cmd) > -1 || (this.adminCommands.indexOf(cmd) > -1 && !admin)) {
				continue;
			}
			this.chatCommands.push(cmd);
		}
	}

	/**
	 * Resets command & player prefixes to default values.
	 */
	reset() {
		this.commandPrefix = undefined;
		this.commandIndex = -1;
		this.playerPrefix = undefined;
		this.playerIndex = -1;
	}
}
