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
package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.Sentence;
import games.stendhal.common.parser.SimilarExprMatcher;
import games.stendhal.server.entity.npc.ChatAction;

/**
 * abstract class for handling offer numbers in sentences
 *
 * @author madmetzger
 */
public abstract class KnownOffersChatAction implements ChatAction {
	protected Integer getOfferNumberFromSentence(Sentence sentence) {
		final SimilarExprMatcher matcher = new SimilarExprMatcher();
		final int last = sentence.getExpressions().size();

		for (Expression expr : sentence.getExpressions().subList(1, last)) {
			if (matcher.match(expr, new Expression("number", "NUM"))) {
				/*
				 * The player wrote either "command number", "command number <number>",
				 * or something along those lines. Ignore the "number" parts until
				 * we get to the actual numeral.
				 */
			} else {
				return Integer.parseInt(expr.toString());
			}
		}

		throw new NumberFormatException("no number provided");
	}
}
