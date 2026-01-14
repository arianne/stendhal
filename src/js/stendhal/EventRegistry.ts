/***************************************************************************
 *                   (C) Copyright 2003-2026 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

var marauroa = (window as any).marauroa = (window as any).marauroa || {};

import { AttackEvent } from "event/AttackEvent";
import { BestiaryEvent } from "./event/BestiaryEvent";
import { ChatOptionsEvent } from "./event/ChatOptionsEvent";
import { ExamineEvent } from "./event/ExamineEvent";
import { GlobalVisualEffectEvent } from "event/GlobalVisualEffectEvent";
import { GroupChangeEvent } from "./event/GroupChangeEvent";
import { GroupInviteEvent } from "./event/GroupInviteEvent";
import { ImageEvent } from "event/ImageEvent";
import { PlayerLoggedOnEvent } from "./event/PlayerLoggedOnEvent";
import { PlayerLoggedOutEvent } from "./event/PlayerLoggedOutEvent";
import { PrivateTextEvent } from "event/PrivateTextEvent";
import { ProgressStatusEvent } from "./event/ProgressStatusEvent";
import { ReachedAchievementEvent } from "event/ReachedAchievementEvent";
import { ShowItemListEvent } from "./event/ShowItemListEvent";
import { ShowOutfitListEvent } from "./event/ShowOutfitListEvent";
import { SoundEvent } from "./event/SoundEvent";
import { TextEvent } from "event/TextEvent";
import { TradeEvent } from "./event/TradeEvent";
import { TransitionGraphEvent } from "event/TransitionGraphEvent";
import { ViewChangeEvent } from "./event/ViewChangeEvent";


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

		this.register("attack", AttackEvent);
		this.register("bestiary", BestiaryEvent);
		this.register("chat_options", ChatOptionsEvent);
		this.register("examine", ExamineEvent);
		this.register("global_visual_effect", GlobalVisualEffectEvent);
		this.register("group_change_event", GroupChangeEvent);
		this.register("group_invite_event", GroupInviteEvent);
		this.register("image_event", ImageEvent);
		this.register("player_logged_on", PlayerLoggedOnEvent);
		this.register("player_logged_out", PlayerLoggedOutEvent);
		this.register("private_text", PrivateTextEvent);
		this.register("progress_status_event", ProgressStatusEvent);
		this.register("reached_achievement", ReachedAchievementEvent);
		this.register("show_item_list", ShowItemListEvent);
		this.register("show_outfit_list", ShowOutfitListEvent);
		this.register("sound_event", SoundEvent);
		this.register("text", TextEvent);
		this.register("trade_state_change_event", TradeEvent);
		this.register("transition_graph", TransitionGraphEvent);
		this.register("view_change", ViewChangeEvent);
	}

	/**
	 * Registers a new event.
	 *
	 * @param type
	 *     String identifier.
	 * @param event
	 *     Event to be registered.
	 */
	register(type: string, event: any) {
		marauroa.rpeventFactory[type] = event;
	}
}
