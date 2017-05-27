/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.condition;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.parser.ConvCtxForMatchingSource;
import games.stendhal.common.parser.ConversationContext;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import games.stendhal.common.parser.SimilarExprMatcher;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Was one of these trigger phrases said exactly ignoring case? (Use with a ""-trigger in npc.add)
 */
@Dev(category=Category.CHAT, label="\"\"?")
public class TriggerExactlyInListCondition implements ChatCondition {
	private static final ConversationContext CONVERSION_CONTEXT = new ConvCtxForMatchingSource();

	private final List<Sentence> triggers = new LinkedList<Sentence>();


	/**
	 * Creates a new TriggerExactlyInListCondition.
	 *
	 * @param trigger list of triggers
	 */
	public TriggerExactlyInListCondition(final String... trigger) {
		this(Arrays.asList(trigger));
	}

	/**
	 * Creates a new TriggerExactlyInListCondition.
	 *
	 * @param triggers list of triggers
	 */
	@Dev()
	public TriggerExactlyInListCondition(final List<String> triggers) {
		SimilarExprMatcher matcher = new SimilarExprMatcher();
		for (String trigger : triggers) {
			final Sentence expected = ConversationParser.parse(trigger, matcher);
			this.triggers.add(expected);
		}
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {

		// TODO: lowercase "and" at the beginning of a sentence is ignored, even in full match mode: "and the other gold"

		final Sentence answer = ConversationParser.parse(sentence.getOriginalText(), CONVERSION_CONTEXT);
		for (Sentence trigger : triggers) {
			if (answer.matchesFull(trigger)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "trigger exactly <" + triggers.toString() + ">";
	}

	@Override
	public int hashCode() {
		return 5009 * triggers.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TriggerExactlyInListCondition)) {
			return false;
		}
		TriggerExactlyInListCondition other = (TriggerExactlyInListCondition) obj;
		return triggers.equals(other.triggers);
	}
}
