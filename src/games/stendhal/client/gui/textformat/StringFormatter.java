/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.textformat;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A class for creating attributed strings from strings using the stendhal
 * style markup. The rules are:<ol>
 *
 * 	<li>Text starts in a defined font and color mode.</li>
 *
 * 	<li>A special character changes to a mode specific to that character.</li>
 *
 *  <li>If a new special mode is started while the previous is in effect, their
 * 	effects are combined, the definitions of the newer mode overriding the other
 * 	where they would be in conflict.</li>
 *
 * 	<li>If the markup character is followed by a single quote " ' ", the mode
 * 	ends at the next single quote. Otherwise the mode ends at the next white
 * 	space or punctuation character. Only the last active mode is terminated.</li>
 *
 * 	<li>If a markup character would start a new mode that is the same as the
 * 	current effective, the markup character is treated as if it was a normal
 * 	character.</li>
 *
 * 	<li>The backslash character '\' can be used to prevent the normal effect
 * 	of the special characters to allow printing them. Backslash can be printed
 * 	by escaping it with itself '\\'.</li>
 * </ol>
 *
 * @param <K> type of the TextFormatSet internal data
 * @param <T> type holding the text attributes
 */
public class StringFormatter<K, T extends FormatSet<K, T>> {
	/** Punctuation characters. */
	private static final Collection<Character> endMarkers = Arrays.asList(' ', '\n', ',', '.', '!', '?');

	private final Map<Character, T> coloringModes;

	public StringFormatter() {
		coloringModes = new HashMap<Character, T>();
	}

	/**
	 * Add a formatting style.
	 *
	 * @param c character for turning on the style
	 * @param attributes attributes to be used for the text in the style
	 */
	public void addStyle(Character c, T attributes) {
		coloringModes.put(c, attributes);
	}

	/**
	 * Format a string.
	 *
	 * @param s string which may contain formatting markup
	 * @param normalAttributes attributes for the normal text
	 * @param dest destination to write parsed and attributed output
	 */
	public void format(String s, T normalAttributes, AttributedTextSink<T> dest) {
		BaseState state = new BaseState(dest, normalAttributes, s, 0);

		state.parse();
	}

	/**
	 * Base class for the parser states.
	 */
	private abstract class AbstractParserState {
		/** Attributes to be used for the printable characters. */
		final T attrs;
		/** Beginning of the current parses scan. */
		int beginIndex;
		/** Index of the character currently examined. */
		int index;
		/** Output destination. */
		final AttributedTextSink<T> dest;
		/** Formatted input string. */
		final String string;

		/**
		 * Create a new AbstractParserState.
		 *
		 * @param builder AttributedTextSink for the use of adding
		 * 	processed output
		 * @param attributes text attributes for the state
		 * @param s raw format string
		 * @param index starting index for this parser instance
		 */
		AbstractParserState(AttributedTextSink<T> builder, T attributes, String s, int index) {
			attrs = attributes;
			beginIndex = index;
			this.index = index;
			this.dest = builder;
			this.string = s;
		}

		/**
		 * Insert scanned elements to the output string.
		 */
		void push() {
			int endIndex = Math.min(index, string.length());
			if (endIndex > beginIndex) {
				dest.append(string.substring(beginIndex, endIndex), attrs);
				beginIndex = index;
			}
		}

		/**
		 * Parse starting from the beginning index until the parser instance
		 * has finished its part.
		 *
		 * @return index where the parser stopped
		 */
		abstract int parse();
	}

	/**
	 * The normal, starting state of the parser.
	 */
	private class BaseState extends AbstractParserState {
		/**
		 * Character that should not be treated as a special markup character.
		 * Used by child classes to detect transitions to the same state.
		 */
		private char ignoreChar = '\0';
		/**
		 * A flag for detecting if the the state should terminate parsing at the
		 * next encountered single quote.
		 */
		boolean endAtQuote = false;

		/**
		 * Create a new BaseState.
		 *
		 * @param dest AttributedTextSink for the use of adding
		 * 	processed output
		 * @param attributes attributes for the state
		 * @param s raw format string
		 * @param index starting index for this parser instance
		 */
		BaseState(AttributedTextSink<T> dest, T attributes, String s, int index) {
			super(dest, attributes, s, index);
		}

		/**
		 * Read the next character. Shifts to another parser state if special
		 * characters are met.
		 *
		 * @return <code>true</code> if the parser should stay in current mode,
		 * 	<code>false</code> if a character that marks the end of this state
		 * 	was encountered
		 */
		boolean readNext() {
			char current = string.charAt(index);
			T newAttrs = coloringModes.get(current);

			AbstractParserState newState = null;
			// Avoid switching to copy of current state. Required for ## to work
			// as expected
			if ((newAttrs != null) && (ignoreChar != current)) {
				ColoringState s = new ColoringState(dest, attrs.union(newAttrs), string, index + 1);
				// Add the current attributes first, so that we get a nice
				// combined effect
				s.ignore(current);
				if (endAtQuote) {
					s.setForceEndAtQuote();
				}
				newState = s;
			} else if (current == '\\') {
				AbstractParserState s = new QuoteState(dest, attrs, string, index + 1);
				newState = s;
			}

			if (newState != null) {
				push();
				index = newState.parse();
				beginIndex = index;
			} else {
				index++;
			}

			// Nothing counts as the end character
			return true;
		}

		/**
		 * Tell the parser to ignore a certain character. Used by child classes
		 * to prevent transitions to identical state.
		 *
		 * @param ignoreChar character to be ignored. This is the markup
		 * 	character used to transition to this state
		 */
		void ignore(char ignoreChar) {
			this.ignoreChar = ignoreChar;
		}

		@Override
		int parse() {
			while (index < string.length()) {
				if (!readNext()) {
					return index;
				}
			}
			push();

			return index;
		}
	}

	/**
	 * A special parser state for the effects. This will terminate at either
	 * at the next punctuation character followed by white space, or at
	 * encountering a single quote if the initiating state transition character
	 * was followed by a single quote.
	 */
	private class ColoringState extends BaseState {
		/**
		 * Leave the state at an encountered single quote, even if this state
		 * was not started with a quoted marker. This is needed for situations
		 * where this is an inner state where the <em>outer</em> was quoted, and
		 * thus quote should end both the states.
		 */
		private boolean forceEndAtQuote;

		/**
		 * Create a new ColoringState.
		 *
 		 * @param sink AttributedTextSink for the use of adding
		 * 	processed output
		 * @param attributes text attributes for the state
		 * @param s raw format string
		 * @param index starting index for this parser instance
		 */
		ColoringState(AttributedTextSink<T> sink, T attributes, String s, int index) {
			super(sink, attributes, s, index);
			checkFirst();
		}

		/**
		 * Read the first following character to verify if the state should be
		 * ended at a quote, or at the next whitespace or punctuation.
		 */
		private void checkFirst() {
			if (index < string.length()) {
				char current = string.charAt(index);
				if (current == '\'') {
					endAtQuote = true;
					// eat quote
					index++;
					beginIndex = index;
				}
			}
		}

		@Override
		boolean readNext() {
			char current = string.charAt(index);
			if (current == '\'') {
				if (endAtQuote || forceEndAtQuote) {
					push();
					// eat end char
					if (endAtQuote) {
						index++;
					}
					return false;
				}
			} else if (!endAtQuote && endMarkers.contains(current)) {
				// Punctuation ends the colouring only if the next character is
				// whitespace
				if (Character.isWhitespace(current) || Character.isWhitespace(peekNext())) {
					push();
					return false;
				}
			}

			// The normal mode transitions are after the rest to keep handling
			// the quoting last; that is needed to make \' working like it
			// should.
			if (index >= string.length()) {
				return false;
			}
			super.readNext();

			return true;
		}

		/**
		 * Flag the state to end at the first encountered single quote. Used by
		 * outer states that need to end at the quoting.
		 */
		void setForceEndAtQuote() {
			forceEndAtQuote = true;
		}

		/**
		 * Get the value of the next character, if there is one.
		 *
		 * @return the next character, or space ' ' if there is no next
		 * 	character
		 */
		private char peekNext() {
			int nextIndex = index + 1;
			if (nextIndex < string.length()) {
				return string.charAt(nextIndex);
			}
			return ' ';
		}
	}

	/**
	 * A state for preventing the other states seeing special characters.
	 * {@link #parse()} simply inserts the next character to the output string
	 * using the current attributes and returns.
	 */
	private class QuoteState extends AbstractParserState {
		/**
		 * Create a new QuoteState.
		 *
		 * @param sink AttributedTextSink for the use of adding
		 * 	processed output
		 * @param attributes text attributes for the state
		 * @param s raw format string
		 * @param index starting index for this parser instance
		 */
		QuoteState(AttributedTextSink<T> sink, T attributes, String s, int index) {
			super(sink, attributes, s, index);
		}

		@Override
		int parse() {
			// Just pass through one character
			index++;
			push();
			return index;
		}
	}
}
