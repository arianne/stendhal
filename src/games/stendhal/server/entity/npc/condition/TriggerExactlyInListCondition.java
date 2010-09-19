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

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.ConvCtxForMatchingSource;
import games.stendhal.server.entity.npc.parser.ConversationContext;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.npc.parser.SimilarExprMatcher;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Was one of theses trigger phrases said exactly ignoring case? (Use with a ""-trigger in npc.add)
 */
public class TriggerExactlyInListCondition implements ChatCondition {
	private static final ConversationContext CONVERSION_CONTEXT = new ConvCtxForMatchingSource();

	private List<Sentence> triggers = new LinkedList<Sentence>();


	/**
	 * Creates a new TriggerExactlyInListCondition.
	 * 
	 * @param trigger list of triggers
	 */
	public TriggerExactlyInListCondition(final String... trigger) {
		addTriggers(Arrays.asList(trigger));
	}

	/**
	 * Creates a new TriggerExactlyInListCondition.
	 * 
	 * @param triggers list of triggers
	 */
	public TriggerExactlyInListCondition(final List<String> triggers) {
		addTriggers(triggers);
	}

	private void addTriggers(Iterable<String> triggers) {
		SimilarExprMatcher matcher = new SimilarExprMatcher();
		for (String trigger : triggers) {
			final Sentence expected = ConversationParser.parse(trigger, matcher);
			this.triggers.add(expected);
		}
	}

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
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,	TriggerExactlyInListCondition.class);
	}
}
