/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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
	 * @param amount amount of modifications
	 * @return modified text.
	 */
	private String applyDrunkEffect(String text, int amount) {
		// Make the effect relatively constant for different length sentences
		amount = Math.max(amount / 2 + 1, amount * (20 + text.length()) / 40);
		StringBuilder b = new StringBuilder(text);
		while (amount > 1) {
			switch (Rand.rand(3)) {
			case 0:
				swapLetters(b);
				break;
			case 1:
				removeLetter(b);
				break;
			case 2:
				duplicateLetter(b);
			}
			amount--;
		}
		/*
		 * Place *hicks* always last, so that it does not get mangled. It is
		 * also always included, so that players notice the slurred speech
		 * is intentional.
		 */
		b.append(" *hicks*");
		return b.toString();
	}

	/**
	 * Swap two adjacent letters at a random position.
	 *
	 * @param text original text
	 */
	private void swapLetters(StringBuilder text) {
		int length = text.codePointCount(0, text.length());
		if (length < 2) {
			return;
		}
		int index = Rand.rand(length - 1);
		int chrIndex = text.offsetByCodePoints(0, index);
		int chr = text.codePointAt(chrIndex);
		int toRemove = Character.charCount(chr);
		for (int i = 0; i < toRemove; i++) {
			text.deleteCharAt(chrIndex);
		}
		// There's no insertCodePointAt()
		text.insert(chrIndex + Character.charCount(text.codePointAt(chrIndex)),
				new StringBuilder().appendCodePoint(chr));
	}

	/**
	 * Remove random letter from a StringBuilder, if it has at least length 2.
	 *
	 * @param text original text
	 */
	private void removeLetter(StringBuilder text) {
		int length = text.codePointCount(0, text.length());
		if (length < 2) {
			return;
		}
		int index = Rand.rand(length);
		int chrIndex = text.offsetByCodePoints(0, index);
		int chr = text.codePointAt(chrIndex);
		int toRemove = Character.charCount(chr);
		for (int i = 0; i < toRemove; i++) {
			text.deleteCharAt(chrIndex);
		}
	}

	/**
	 * Duplicate a random letter in a StringBuilder.
	 *
	 * @param text original text
	 */
	private void duplicateLetter(StringBuilder text) {
		int length = text.codePointCount(0, text.length());
		if (length < 1) {
			return;
		}
		int index = Rand.rand(length);
		int chrIndex = text.offsetByCodePoints(0, index);
		int chr = text.codePointAt(chrIndex);
		// There's no insertCodePointAt()
		text.insert(chrIndex + Character.charCount(chr),
				new StringBuilder().appendCodePoint(chr));
	}
}
