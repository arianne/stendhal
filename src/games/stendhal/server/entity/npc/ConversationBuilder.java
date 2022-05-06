/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e.V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import games.stendhal.server.entity.npc.action.MultipleActions;

/**
 * A low level builder for npc interaction. This is designed to be used with
 * ConditionBuilder.
 * <br>
 * An example conversation with a response, conditions and a state change:
 * <pre>
 * {@code
 * conversation(actor(npc)
 *     .respondsTo(ConversationPhrases.GREETING_MESSAGES)
 *     .inState(ConversationStates.IDLE)
 *     .saying("I hope you didn't lose your goblet! Do you need another?")
 *     .when(greetingMatchesName(npc.getName())
 *         .and(questInState(QUEST_SLOT, "start"))
 *         .unless(playerCarriesItem("empty goblet")
 *             .or(playerCarriesItem("goblet"))))
 *     .changingStateTo(ConversationStates.QUESTION_1));
 * }
 * </pre>
 *
 * @see ConditionBuilder
 */
public class ConversationBuilder {
	private final SpeakerNPC npc;
	private final List<ConversationStates> initialStates = new ArrayList<>();
	private ConversationStates endState;
	private final Collection<String> triggers = new ArrayList<>();
	private ChatCondition condition;
	private String reply;
	private ChatAction action;

	private ConversationBuilder(SpeakerNPC npc) {
		this.npc = npc;
	}

	/**
	 * Creates a ConversationBuilder for the specified npc.
	 *
	 * @param npc SpeakerNPC for which the conversation is created
	 * @return ConversationBuilder for the specified npc
	 */
	public static ConversationBuilder actor(SpeakerNPC npc) {
		return new ConversationBuilder(npc);
	}

	/**
	 * Specifies an initial state of the NPC for which the conversation is used.
	 * <b>Every conversation must have at least one specified initial state.</b>
	 * <br>
	 * If there are no initial states set earlier, this is also used as the end
	 * state, unless a state change is specified with {@link #changingStateTo()}.
	 *
	 * @param state initial state
	 * @return the builder itself
	 */
	public ConversationBuilder inState(ConversationStates state) {
		initialStates.add(state);
		if (endState == null && initialStates.size() == 1) {
			endState = state;
		}

		return this;
	}

	/**
	 * Specifies multiple initial states of the NPC for which the conversation
	 * is used. <b>Every conversation must have at least one specified initial
	 * state.</b>
	 *
	 * @param states
	 * @return the builder itself
	 */
	public ConversationBuilder inStates(ConversationStates[] states) {
		Collections.addAll(initialStates, states);
		return this;
	}

	/**
	 * Add a trigger word to which the NPC responds to.
	 *
	 * @param trigger trigger word
	 * @return the builder itself
	 */
	public ConversationBuilder respondsTo(String trigger) {
		triggers.add(trigger);
		return this;
	}

	/**
	 * Add multiple trigger words to which the NPC responds to.
	 *
	 * @param triggers trigger words
	 * @return the builder itself
	 */
	public ConversationBuilder respondsTo(Collection<String> triggers) {
		this.triggers.addAll(triggers);
		return this;
	}

	/**
	 * Specify a verbal reply for the npc.
	 *
	 * @param reply
	 * @return the builder itself
	 */
	public ConversationBuilder saying(String reply) {
		this.reply = reply;
		return this;
	}

	/**
	 * Specify conditions for the conversation. Use the {@link ConditionBuilder}
	 * factory methods in the ChatConditions. This method can be called at most
	 * once.
	 *
	 * @param condition A condition builder for the conditions of the conversation
	 * @return the builder itself
	 */
	public ConversationBuilder when(ConditionBuilder condition) {
		if (this.condition != null) {
			throw new IllegalStateException("when() called more than once");
		}
		this.condition = condition.build();
		return this;
	}

	/**
	 * Specify additional actions for the conversation. For readability, use
	 * the static factory methods of the actions.
	 *
	 * @param actions
	 * @return the builder itself
	 */
	public ConversationBuilder doing(ChatAction ...actions ) {
		if (action != null) {
			throw new IllegalStateException("doing() called more than once");
		}
		switch (actions.length) {
			case 0:
				throw new IllegalArgumentException("doing() called without arguments");
			case 1: action = actions[0];
				break;
			default:
				action = new MultipleActions(actions);
		}
		return this;
	}

	/**
	 * Specify the final state of the npc after the conversation.
	 *
	 * @param endState
	 * @return the builder itself
	 * @see #inState(ConversationStates)
	 */
	public ConversationBuilder changingStateTo(ConversationStates endState) {
		this.endState = endState;
		return this;
	}

	/**
	 * Generates the conversation state transitions for the npc FSM. This is
	 * public mainly for the higher level builders. Most conversations made
	 * directly with ConversationBuilder should use
	 * {@link #conversation(ConversationBuilder)} instead.
	 */
	public void build() {
		if (initialStates.isEmpty()) {
			throw new IllegalStateException("NPC initial states have not been specified");
		}
		npc.add(initialStates.toArray(new ConversationStates[initialStates.size()]), triggers, condition, endState, reply, action);
	}

	/**
	 * Generates the conversation for the npc. This is a convenience wrapper
	 * around {@link #build()}.
	 *
	 * @param builder
	 */
	public static void conversation(ConversationBuilder builder) {
		builder.build();
	}
}
