/***************************************************************************
 *                   (C) Copyright 2023-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.events;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;

import games.stendhal.common.constants.Events;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Transition;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

public class ChatOptionsEvent extends RPEvent {

	private static final String NPC = "npc";
	private static final String OPTIONS = "options";

	public ChatOptionsEvent(SpeakerNPC npc, Player player, ConversationStates currentState) {
		super(Events.CHAT_OPTIONS);
		put(NPC, npc.getName());

		List<String> chatOptions = buildChatOptions(npc, player, currentState);
		put(OPTIONS, Joiner.on("|~|").join(chatOptions));
	}

	/**
	 * builds a list of the available chat options
	 *
	 * @param npc SpeakerNPC the player is talking to
	 * @param player player (to check conditions)
	 * @param currentState current state of the SpeakerNPC's state machine
	 * @return list of chat options
	 */
	private List<String> buildChatOptions(SpeakerNPC npc, Player player, ConversationStates currentState) {
		List<String> res = new LinkedList<>();
		Sentence sentence = ConversationParser.parse("");

		final List<Transition> transitions = npc.getTransitions();
		for (final Transition transition : transitions) {
			if (transition.getState() != currentState) {
				continue;
			}

			for(Expression expr : transition.getTriggers()) {
				if (transition.getCondition() != null) {
					if (!transition.getCondition().fire(player, sentence, npc)) {
						continue;
					}
				}

				String trigger = expr.getNormalized();
				String options = "";
				if (trigger.equals("buy") || trigger.equals("sell") || trigger.equals("")) {
					options = "params";
				}
				res.add(trigger + "|*|" + trigger + "|*|" + options);
				break;
			}
		}


		return res;
	}

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(Events.CHAT_OPTIONS);
		rpclass.addAttribute(NPC, Type.STRING);
		rpclass.addAttribute(OPTIONS, Type.VERY_LONG_STRING);
	}
}
