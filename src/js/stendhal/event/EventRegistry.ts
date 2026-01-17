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

import { marauroa } from "marauroa"

import { AttackEvent } from "event/AttackEvent";
import { BestiaryEvent } from "./BestiaryEvent";
import { ChatOptionsEvent } from "./ChatOptionsEvent";
import { ExamineEvent } from "./ExamineEvent";
import { GlobalVisualEffectEvent } from "event/GlobalVisualEffectEvent";
import { GroupChangeEvent } from "./GroupChangeEvent";
import { GroupInviteEvent } from "./GroupInviteEvent";
import { ImageEvent } from "event/ImageEvent";
import { PlayerLoggedOnEvent } from "./PlayerLoggedOnEvent";
import { PlayerLoggedOutEvent } from "./PlayerLoggedOutEvent";
import { PrivateTextEvent } from "event/PrivateTextEvent";
import { ProgressStatusEvent } from "./ProgressStatusEvent";
import { ReachedAchievementEvent } from "event/ReachedAchievementEvent";
import { ShowItemListEvent } from "./ShowItemListEvent";
import { ShowOutfitListEvent } from "./ShowOutfitListEvent";
import { SoundEvent } from "./SoundEvent";
import { TextEvent } from "event/TextEvent";
import { TradeEvent } from "./TradeEvent";
import { TransitionGraphEvent } from "event/TransitionGraphEvent";
import { ViewChangeEvent } from "./ViewChangeEvent";


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

		marauroa.rpeventFactory.register("attack", AttackEvent);
		marauroa.rpeventFactory.register("bestiary", BestiaryEvent);
		marauroa.rpeventFactory.register("chat_options", ChatOptionsEvent);
		marauroa.rpeventFactory.register("examine", ExamineEvent);
		marauroa.rpeventFactory.register("global_visual_effect", GlobalVisualEffectEvent);
		marauroa.rpeventFactory.register("group_change_event", GroupChangeEvent);
		marauroa.rpeventFactory.register("group_invite_event", GroupInviteEvent);
		marauroa.rpeventFactory.register("image_event", ImageEvent);
		marauroa.rpeventFactory.register("player_logged_on", PlayerLoggedOnEvent);
		marauroa.rpeventFactory.register("player_logged_out", PlayerLoggedOutEvent);
		marauroa.rpeventFactory.register("private_text", PrivateTextEvent);
		marauroa.rpeventFactory.register("progress_status_event", ProgressStatusEvent);
		marauroa.rpeventFactory.register("reached_achievement", ReachedAchievementEvent);
		marauroa.rpeventFactory.register("show_item_list", ShowItemListEvent);
		marauroa.rpeventFactory.register("show_outfit_list", ShowOutfitListEvent);
		marauroa.rpeventFactory.register("sound_event", SoundEvent);
		marauroa.rpeventFactory.register("text", TextEvent);
		marauroa.rpeventFactory.register("trade_state_change_event", TradeEvent);
		marauroa.rpeventFactory.register("transition_graph", TransitionGraphEvent);
		marauroa.rpeventFactory.register("view_change", ViewChangeEvent);
	}

}
