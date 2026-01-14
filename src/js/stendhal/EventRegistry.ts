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

var marauroa = (window as any).marauroa = (window as any).marauroa || {};
import { stendhal } from "./stendhal";

import { RPEntity } from "./entity/RPEntity";
import { RPObject } from "./entity/RPObject";

import { BestiaryEvent } from "./event/BestiaryEvent";
import { ChatOptionsEvent } from "./event/ChatOptionsEvent";
import { ExamineEvent } from "./event/ExamineEvent";
import { GroupChangeEvent } from "./event/GroupChangeEvent";
import { GroupInviteEvent } from "./event/GroupInviteEvent";
import { PlayerLoggedOnEvent } from "./event/PlayerLoggedOnEvent";
import { PlayerLoggedOutEvent } from "./event/PlayerLoggedOutEvent";
import { ProgressStatusEvent } from "./event/ProgressStatusEvent";
import { RPEvent } from "./event/RPEvent";
import { ShowItemListEvent } from "./event/ShowItemListEvent";
import { ShowOutfitListEvent } from "./event/ShowOutfitListEvent";
import { SoundEvent } from "./event/SoundEvent";
import { TradeEvent } from "./event/TradeEvent";
import { ViewChangeEvent } from "./event/ViewChangeEvent";
import { Chat } from "./util/Chat";
import { AttackEvent } from "event/AttackEvent";
import { GlobalVisualEffectEvent } from "event/GlobalVisualEffectEvent";
import { ImageEvent } from "event/ImageEvent";
import { ReachedAchievementEvent } from "event/ReachedAchievementEvent";
import { TextEvent } from "event/TextEvent";
import { TransitionGraphEvent } from "event/TransitionGraphEvent";
import { PrivateTextEvent } from "event/PrivateTextEvent";


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

		this.register("chat_options", new ChatOptionsEvent());
		this.register("examine", new ExamineEvent());
		this.register("group_change_event", new GroupChangeEvent());
		this.register("group_invite_event", new GroupInviteEvent());
		this.register("progress_status_event", new ProgressStatusEvent());
		this.register("trade_state_change_event", new TradeEvent());

		this.register("attack", new AttackEvent());

		this.register("bestiary", new BestiaryEvent());

		this.register("global_visual_effect", new GlobalVisualEffectEvent());

		this.register("image_event", new ImageEvent());

		this.register("player_logged_on", new PlayerLoggedOnEvent());
		this.register("player_logged_out", new PlayerLoggedOutEvent());

		this.register("private_text", new PrivateTextEvent());

		this.register("reached_achievement", new ReachedAchievementEvent());

		this.register("show_item_list", new ShowItemListEvent());
		this.register("show_outfit_list", new ShowOutfitListEvent());

		this.register("sound_event", new SoundEvent());

		this.register("text", new TextEvent());

		this.register("transition_graph", new TransitionGraphEvent());

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
