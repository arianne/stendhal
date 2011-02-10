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

import games.stendhal.server.core.engine.dbcommand.GetCharacterCreationDateCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnListenerDecorator;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Date;

import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the character's age greater than the specified age?
 */
public class CharacterAgeGreaterThanCondition implements ChatCondition, TurnListener {

	/** For identifying the results of this command */
	private ResultHandle handle = new ResultHandle();
	
	private final int age;

	/**
	 * Creates a new CharacterAgeGreaterThanCondition.
	 * 
	 * @param age
	 *            age 
	 */
	public CharacterAgeGreaterThanCondition(final int age) {
		this.age = age;
	}

	/**
	 * @return true if players age greater than age in condition
	 */
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		DBCommand command = new GetCharacterCreationDateCommand(player.getName());
		DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
		TurnNotifier.get().notifyInTurns(0, new TurnListenerDecorator(this));
		return false;
	}

	/**
	 * Completes handling the store message action, and
	 * Notifies the player who sent the message of the outcome
	 * 
	 * @param currentTurn ignored
	 */
	public void onTurnReached(int currentTurn) {
		GetCharacterCreationDateCommand checkcommand = DBCommandQueue.get().getOneResult(GetCharacterCreationDateCommand.class, handle);
		
		if (checkcommand == null) {
			TurnNotifier.get().notifyInTurns(0, new TurnListenerDecorator(this));
			return;
		}

		Date date = checkcommand.getCreationDate();

	}
	
	public String toString() {
		return "age > " + age + " ";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				CharacterAgeGreaterThanCondition.class);
	}

}
