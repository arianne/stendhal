package games.stendhal.server.entity.npc.parser;

import java.util.Iterator;

/**
 * This utility class is used to create string representations
 * of sentences by separating words by space characters.
 * 
 * @author Martin Fuchs
 */
public final class SentenceBuilder {
	private StringBuilder builder = new StringBuilder();
	private boolean first = true;
	private char space = ' ';

	public SentenceBuilder() {
		space = ' ';
	}

	public SentenceBuilder(char separator) {
		space = separator;
	}

	/**
	 * Append string separated by space.
	 *
	 * @param s
	 */
	public void append(final String s) {
		if (first) {
			first = false;
		} else {
			builder.append(space);
		}

		builder.append(s);
	}

	/**
	 * Directly append the given character.
	 *
	 * @param c
	 */
	public void append(char c) {
		builder.append(c);
	}

	/**
	 * Append a sequence of Expressions until we find a break flag or there is no more Expression.
	 *
	 * @param it Expression iterator
	 */
	public int appendUntilBreak(Iterator<Expression> it) {
		int count = 0;

		while (it.hasNext()) {
			Expression expr = it.next();

			append(expr.getNormalized());
			++count;

			// break on next sentence part
			if (expr.getBreakFlag()) {
				break;
			}
		}

		return count;
    }

	/**
	 * Check for empty buffer content.
	 *
	 * @return
	 */
	public boolean isEmpty() {
	    return builder.toString().length() == 0;
    }

	@Override
	public String toString() {
		return builder.toString();
	}
}
