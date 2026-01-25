/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Client } from "./Client";
import { EventRegistry } from "./event/EventRegistry";
import { SlashActionRepo } from "./SlashActionRepo";

import { Zone } from "./entity/Zone";

import { ConfigManager } from "./util/ConfigManager";
import { DownloadUtil } from "./util/DownloadUtil";
import { FacingHandler } from "./util/FacingHandler";
import { KeyHandler } from "./util/KeyHandler";
import { SessionManager } from "./util/SessionManager";
import { WeatherRenderer } from "./util/WeatherRenderer";

import { CStatus } from "./data/CStatus";
import { CacheManager } from "./data/CacheManager";
import { EmojiStore } from "./data/EmojiStore";
import { GroupManager } from "./data/GroupManager";
import { OutfitStore } from "./data/OutfitStore";
import { Paths } from "./data/Paths";
import { SpriteStore, store } from "./data/SpriteStore";
import { TileStore } from "./data/TileStore";

import { LoopedSoundSourceManager } from "./data/sound/LoopedSoundSourceManager";
import { SoundManager } from "./data/sound/SoundManager";

import { ui } from "./ui/UI";
import { UIComponentEnum } from "./ui/UIComponentEnum";
import { HeldObjectManager } from "./ui/HeldObject";
import { HTMLManager } from "./ui/HTMLManager";
import { Inventory } from "./ui/Inventory";
import { SoftwareJoystickController } from "./ui/SoftwareJoystickController";
import { TouchHandler } from "./ui/TouchHandler";
import { UIUpdateObserver } from "./ui/UIUpdateObserver";
import { ViewPort } from "./ui/ViewPort";

import { ChatInputComponent } from "./ui/component/ChatInputComponent";

/**
 * @Deprecated use Class.get() directly to prevent dependency chain
 */
export class SingletonRepo {

	static getCStatus(): CStatus {
		return CStatus.get();
	}

	static getCacheManager(): CacheManager {
		return CacheManager.get();
	}

	static getChatInput(): ChatInputComponent {
		return ui.get(UIComponentEnum.ChatInput) as ChatInputComponent;
	}

	static getClient(): Client {
		return Client.get();
	}

	static getConfigManager(): ConfigManager {
		return ConfigManager.get();
	}

	static getDownloadUtil(): typeof DownloadUtil {
		return DownloadUtil;
	}

	static getEmojiStore(): EmojiStore {
		return EmojiStore.get();
	}

	static getEventRegistry(): EventRegistry {
		return EventRegistry.get();
	}

	static getFacingHandler(): FacingHandler {
		return FacingHandler.get();
	}

	static getGroupManager(): GroupManager {
		return GroupManager.get();
	}

	static getHeldObjectManager(): HeldObjectManager {
		return HeldObjectManager.get();
	}

	static getHTMLManager(): HTMLManager {
		return HTMLManager.get();
	}

	static getInventory(): Inventory {
		return Inventory.get();
	}

	static getJoystickController(): SoftwareJoystickController {
		return SoftwareJoystickController.get();
	}

	static getKeyHandler(): typeof KeyHandler {
		return KeyHandler;
	}

	static getLoopedSoundSourceManager(): LoopedSoundSourceManager {
		return LoopedSoundSourceManager.get();
	}

	static getOutfitStore(): OutfitStore {
		return OutfitStore.get();
	}

	static getPaths(): typeof Paths {
		return Paths;
	}

	static getSessionManager(): SessionManager {
		return SessionManager.get();
	}

	static getSlashActionRepo(): SlashActionRepo {
		return SlashActionRepo.get();
	}

	static getSoundManager(): SoundManager {
		return SoundManager.get();
	}

	static getSpriteStore(): SpriteStore {
		return store;
	}

	static getTileStore(): TileStore {
		return TileStore.get();
	}

	static getTouchHandler(): TouchHandler {
		return TouchHandler.get();
	}

	static getUIUpdateObserver(): UIUpdateObserver {
		return UIUpdateObserver.get();
	}

	static getViewPort(): ViewPort {
		return ViewPort.get();
	}

	static getWeatherRenderer(): WeatherRenderer {
		return WeatherRenderer.get();
	}

	static getZone(): Zone {
		return Zone.get();
	}
}

export const singletons = SingletonRepo;
