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

import static games.stendhal.server.entity.npc.condition.NotCondition.not;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;

/**
 * A builder for combining ChatConditions.
 *
 * An example combined condition generated with the static factory methods in
 * ChatConditions.
 * <pre>
 * {@code
 *     greetingMatchesName(npc.getName())
 *         .and(questInState(QUEST_SLOT, "start"))
 *         .unless(playerCarriesItem("empty goblet")
 *             .or(playerCarriesItem("goblet")))
 *         .build();
 * }
 * </pre>
 */
public class ConditionBuilder {
	private enum LogicType {
		UNSPECIFIED,
		AND,
		OR
	}

	private LogicType type;
	private final ChatCondition condition;
	private final List<ConditionBuilder> chainedBuilders = new ArrayList<>();

	private ConditionBuilder(LogicType type, ChatCondition condition) {
		this.type = type;
		this.condition = condition;
	}

	/**
	 * Create a new ConditionBuilder. This is meant primarily for the use of
	 * the static factory methods in ChatConditions.
	 *
	 * @param condition
	 */
	public ConditionBuilder(ChatCondition condition) {
		this(LogicType.UNSPECIFIED, condition);
	}

	/**
	 * Combine the condition represented by this builder with the child using
	 * logical AND.
	 *
	 * @param child
	 * @return the builder itself
	 * @see AndCondition
	 * @see #unless(ConditionBuilder)
	 */
	public ConditionBuilder and(ConditionBuilder child) {
		if (this.type == LogicType.OR) {
			throw new IllegalStateException("For readability, do not chain OR and AND conditions");
		}
		this.type = LogicType.AND;
		ConditionBuilder builder = new ConditionBuilder(LogicType.AND, child.build());
		chainedBuilders.add(builder);
		return this;
	}

	/**
	 * Combine the condition represented by this builder with the child using
	 * logical OR.
	 *
	 * @param child
	 * @return the builder itself
	 * @see OrCondition
	 */
	public ConditionBuilder or(ConditionBuilder child) {
		if (this.type == LogicType.AND) {
			throw new IllegalStateException("For readability, do not chain OR and AND conditions");
		}
		this.type = LogicType.OR;
		ConditionBuilder builder = new ConditionBuilder(LogicType.OR, child.build());
		chainedBuilders.add(builder);
		return this;
	}

	/**
	 * A convenience method for combining the condition represented by this
	 * builder with the child using logical AND NOT. This is equivalent to
	 * <code>builder.and(not(child))</code> which may be more appropriate in
	 * some cases.
	 *
	 * @param child
	 * @return the builder itself
	 * @see #and(ConditionBuilder)
	 * @see NotCondition
	 */
	public ConditionBuilder unless(ConditionBuilder child) {
		return and(not(child));
	}

	/**
	 * Build the ChatCondition represented by this builder.
	 *
	 * @return a ChatCondition
	 */
	public ChatCondition build() {
		if (chainedBuilders.isEmpty()) {
			return condition;
		}
		List<ChatCondition> allInGroup = new ArrayList<>(chainedBuilders.size() + 1);
		allInGroup.add(condition);
		for (ConditionBuilder builder : chainedBuilders) {
			allInGroup.add(builder.build());
		}
		ChatCondition[] conditionGroup = allInGroup.toArray(new ChatCondition[allInGroup.size()]);

		switch (type) {
			case AND: return new AndCondition(conditionGroup);
			case OR: return new OrCondition(conditionGroup);
			default:
				throw new IllegalStateException("Unspecified logic combination - this should not happen");
		}
	}
}
