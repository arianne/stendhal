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
package games.stendhal.server.entity.npc.action;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.dbcommand.WriteHallOfFamePointsCommand;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.command.DBCommandQueue;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Write the difference between the players current age and the one stored in the quest slot
 * to the hall of fame database table.
 */
@Dev(category=Category.IGNORE, label="State")
public class SetHallOfFameToAgeDiffAction implements ChatAction {
	private final String questname;
	private final int index;
	private final String fametype;

	/**
	 * Creates a new SetHallOfFameToAgeDiffAction.
	 *
	 * @param questname
	 *            name of quest-slot to read the original age from
	 * @param fametype
	 * 			  the type in the hall of fame
	 */
	public SetHallOfFameToAgeDiffAction(final String questname, String fametype) {
		this.questname = questname;
		this.index = -1;
		this.fametype = fametype;
	}

	/**
	 * Creates a new SetHallOfFameToAgeDiffAction.
	 *
	 * @param questname
	 *            name of quest-slot to read the original age from
	 * @param index
	 *            index of sub state containing the age at start of the quest
	 * @param fametype
	 * 			  the type in the hall of fame
	 */
	public SetHallOfFameToAgeDiffAction(final String questname, final int index, String fametype) {
		this.questname = questname;
		this.index = index;
		this.fametype = fametype;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		String orgAge = null;
		if (index > -1) {
			orgAge = player.getQuest(questname, index);
		} else {
			orgAge = player.getQuest(questname);
		}

		int diff = player.getAge() - Integer.parseInt(orgAge);
		DBCommandQueue.get().enqueue(new WriteHallOfFamePointsCommand(player.getName(), fametype, diff, false));
	}

	@Override
	public String toString() {
		return "SetHallOfFameToAgeDiffAction<" + questname + "[" + index + "]," + fametype + ">";
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SetHallOfFameToAgeDiffAction.class);
	}
}
