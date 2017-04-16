/***************************************************************************
 *                   (C) Copyright 2010-2017 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.parser;

import java.util.AbstractList;
import java.util.ArrayList;

/**
 * CompoundName is used to store compound names.
 *
 * @author Martin Fuchs
 */
public class CompoundName extends ArrayList<String> {
    private static final long serialVersionUID = 1L;

	private ExpressionType	type;

	public CompoundName(final Sentence parsed, String typeString) {
		assert(parsed.expressions.size() >= 2);
		boolean isDynamic = false;

		for(Expression e : parsed.expressions) {
			add(e.getOriginal().toLowerCase());

			if (e.isDynamic()) {
				isDynamic = true;
			}
		}

		if (isDynamic) {
			typeString += ExpressionType.SUFFIX_DYNAMIC;
		}

		type = new ExpressionType(typeString);
	}

	/**
	 * Compare this compound name with the words in the expressions list starting at index idx.
	 * @param expressions
	 * @param idx
	 * @return true for matching between expressions and the compound name
	 */
	public boolean matches(AbstractList<Expression> expressions, int idx) {
		for(int i=0; i<size(); ++idx,++i) {
			if (idx >= expressions.size()) {
				return false;
			}

			Expression curr = expressions.get(idx);
			String word = get(i);

	        // compare the current word in a case insensitive way
	        if (!curr.getOriginal().equalsIgnoreCase(word)) {
	        	return false;
	        }

	        // don't merge if the break flag is set in-between the compound name
	        if (i<size()-1 && curr.getBreakFlag()) {
	            return false;
	        }
		}

		return true;
	}

	public ExpressionType getType() {
		return type;
	}
}
