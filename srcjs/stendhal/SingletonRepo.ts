/***************************************************************************
 *                    Copyright © 2003-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { EventRegistry } from "./EventRegistry";
import { SlashActionRepo } from "./SlashActionRepo";

import { ConfigManager } from "./util/ConfigManager";
import { SessionManager } from "./util/SessionManager";
import { WeatherRenderer } from "./util/WeatherRenderer";

import { Animation } from "./data/Animation";
import { EmojiStore } from "./data/EmojiStore";
import { GroupManager } from "./data/GroupManager";
import { Paths } from "./data/Paths";
import { SpriteStore, store } from "./data/SpriteStore";

import { KeyHandler } from "./ui/KeyHandler";
import { LoopedSoundSourceManager } from "./ui/LoopedSoundSourceManager";
import { SoundManager } from "./ui/SoundManager";


export class SingletonRepo {

	static getConfigManager(): ConfigManager {
		return ConfigManager.get();
	}

	static getEmojiStore(): EmojiStore {
		return EmojiStore.get();
	}

	static getEventRegistry(): EventRegistry {
		return EventRegistry.get();
	}

	static getGroupManager(): GroupManager {
		return GroupManager.get();
	}

	static getKeyHandler(): KeyHandler {
		return KeyHandler;
	}

	static getLoopedSoundSourceManager(): LoopedSoundSourceManager {
		return LoopedSoundSourceManager.get();
	}

	static getPaths(): typeof Paths {
		return Paths;
	}

	static getSessionManager(): SessionManager {
		return SessionManager.get();
	}

	static getSlashActionRepository(): SlashActionRepo {
		return SlashActionRepo.get();
	}

	static getSoundManager(): SoundManager {
		return SoundManager.get();
	}

	static getSpriteStore(): SpriteStore {
		return store;
	}

	static getTileAnimation(): Animation {
		return Animation.get();
	}

	static getWeatherRenderer(): WeatherRenderer {
		return WeatherRenderer.get();
	}
}

export const singletons = SingletonRepo;
