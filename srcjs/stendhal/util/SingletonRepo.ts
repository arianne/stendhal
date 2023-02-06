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

import { ConfigManager } from "./ConfigManager";
import { SessionManager } from "./SessionManager";
import { WeatherRenderer } from "./WeatherRenderer";

import { EventRegistry } from "../EventRegistry";
import { SlashActionRepo } from "../SlashActionRepo";

import { EmojiStore } from "../data/EmojiStore";

import { Animation } from "../data/tileset/Animation";

import { KeyHandler } from "../ui/KeyHandler";
import { LoopedSoundSourceManager } from "../ui/LoopedSoundSourceManager";
import { SoundManager } from "../ui/SoundManager";


export class SingletonRepo {

	static getConfigManager(): ConfigManager {
		return ConfigManager.get();
	}

	static getSessionManager(): SessionManager {
		return SessionManager.get();
	}

	static getEmojiStore(): EmojiStore {
		return EmojiStore.get();
	}

	static getEventRegistry(): EventRegistry {
		return EventRegistry.get();
	}

	static getKeyHandler(): KeyHandler {
		return KeyHandler;
	}

	static getLoopedSoundSourceManager(): LoopedSoundSourceManager {
		return LoopedSoundSourceManager.get();
	}

	static getSoundManager(): SoundManager {
		return SoundManager.get();
	}

	static getWeatherRenderer(): WeatherRenderer {
		return WeatherRenderer.get();
	}

	static getSlashActionRepository(): SlashActionRepo {
		return SlashActionRepo.get();
	}

	static getTileAnimation(): Animation {
		return Animation.get();
	}
}

export { SingletonRepo as default };
