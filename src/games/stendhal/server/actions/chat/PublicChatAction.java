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
package games.stendhal.server.actions.chat;

import static games.stendhal.common.constants.Actions.TEXT;
import games.stendhal.common.Rand;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.validator.StandardActionValidations;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.StatusType;
import marauroa.common.game.RPAction;

/**
 * handles publicly said text .
 */
public class PublicChatAction implements ActionListener {

	@Override
	public void onAction(final Player player, final RPAction action) {
		if (!StandardActionValidations.CHAT.validateAndInformPlayer(player, action)) {
			return;
		}

		String text = QuoteSpecials.quote(action.get(TEXT));
		new GameEvent(player.getName(), "chat",  null, Integer.toString(text.length()), text.substring(0, Math.min(text.length(), 1000))).raise();

		if (player.getStatusList().countStatusByType(StatusType.DRUNK) >= 2) {
			text = applyDrunkEffect(text, player.getStatusList().countStatusByType(StatusType.DRUNK) - 1);
		}
		player.put("text", text);

		player.notifyWorldAboutChanges();
		SingletonRepository.getRuleProcessor().removePlayerText(player);
	}

	/**
	 * Apply random mutating effects to the text for slurry drunken speech.
	 * 
	 * @param text original text
	 * @param count number of modifications
	 * @return modified text.
	 */
	private String applyDrunkEffect(String text, int count) {
		while (count > 1) {
			switch (Rand.rand(3)) {
			case 0:
				text = swapLetters(text);
				break;
			case 1:
				text = removeLetter(text);
				break;
			case 2:
				text = duplicateLetter(text);
			}
			count--;
		}
		/*
		 * Place *hicks* always last, so that it does not get mangled. It is
		 * also always included, so that players notice the slurred speech
		 * is intentional.
		 */
		return text + " *hicks*";
	}
	
	/**
	 * Swap two adjacent letters at a random position.
	 * 
	 * @param text original text
	 * @return modified text
	 */
	private String swapLetters(String text) {
		if (text.length() < 2) {
			return text;
		}
		int low = Rand.rand(text.length() - 1);
		int high = low + 1;

		StringBuilder b = new StringBuilder();
		if (low > 0) {
			b.append(text.substring(0, low));
		}
		b.append(text.charAt(high));
		b.append(text.charAt(low));
		b.append(text.substring(high + 1));
		return b.toString();
	}
	
	/**
	 * Remove random letter from a string, if the string has at least length 2.
	 * 
	 * @param text original text
	 * @return modified text
	 */
	private String removeLetter(String text) {
		if (text.length() < 2) {
			return text;
		}
		int index = Rand.rand(text.length());
		if (index == 1) {
			return text.substring(index);
		}
		return text.substring(0, index) + text.substring(index + 1);
	}
	
	/**
	 * Duplicate a random letter in a string.
	 * 
	 * @param text original text
	 * @return modified text
	 */
	private String duplicateLetter(String text) {
		if (text.length() < 1) {
			return text;
		}
		int index = Rand.rand(text.length());
		StringBuilder b = new StringBuilder();
		if (index > 0) {
			b.append(text.substring(0, index));
		}
		b.append(text.charAt(index));
		b.append(text.substring(index));
		
		return b.toString();
	}
}
