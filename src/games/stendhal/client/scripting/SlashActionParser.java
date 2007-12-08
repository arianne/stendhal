package games.stendhal.client.scripting;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import games.stendhal.client.actions.SlashAction;
import games.stendhal.client.actions.SlashActionRepository;

/**
 * Command line parser for the Stendhal client
 * @author Martin Fuchs
 */
public class SlashActionParser
{
	public static SlashActionCommand parse(String text)
    {
		SlashActionCommand command = new SlashActionCommand();

		if (text.length() < 1) {
			return command.setError();
		}

		/*
		 * Parse command
		 */
		CharacterIterator ci = new StringCharacterIterator(text, 1);
		char ch = ci.current();

		/*
		 * Must be non-space after slash
		 */
		if (Character.isWhitespace(ch)) {
			return command.setError();
		}

		/*
		 * Extract command name
		 */
		if (Character.isLetterOrDigit(ch)) {
			/*
			 * Word command
			 */
			while ((ch != CharacterIterator.DONE) && !Character.isWhitespace(ch)) {
				ch = ci.next();
			}

			command._name = text.substring(1, ci.getIndex());
		} else {
			/*
			 * Special character command
			 */
			command._name = String.valueOf(ch);
			ch = ci.next();
		}

		/*
		 * Find command handler
		 */
		SlashAction action = SlashActionRepository.get(command._name);

		int minimum, maximum;

		if (action != null) {
			minimum = action.getMinimumParameters();
			maximum = action.getMaximumParameters();
		} else {
			/*
			 * Server extension criteria
			 */
			minimum = 0;
			maximum = 1;
		}

		/*
		 * Extract parameters
		 * (ch already set to first character)
		 */
		command._params = new String[maximum];

		for(int i = 0; i < maximum; i++) {
			/*
			 * Skip leading spaces
			 */
			while (Character.isWhitespace(ch)) {
				ch = ci.next();
			}

			/*
			 * EOL?
			 */
			if (ch == CharacterIterator.DONE) {
				/*
				 * Incomplete parameters?
				 */
				if (i < minimum) {
					return command.setError();
				}

				break;
			}

			/*
			 * Grab parameter
			 */
			StringBuffer sbuf = new StringBuffer();
			char quote = CharacterIterator.DONE;

			while (ch != CharacterIterator.DONE) {
				if (ch == quote) {
					// End of quote
					quote = CharacterIterator.DONE;
				} else if (quote != CharacterIterator.DONE) {
					// Quoted character
					sbuf.append(ch);
				} else if ((ch == '"') || (ch == '\'')) {
					// Start of quote
					quote = ch;
				} else if (Character.isWhitespace(ch)) {
					// End of token
					break;
				} else {
					// Token character
					sbuf.append(ch);
				}

				ch = ci.next();
			}

			/*
			 * Unterminated quote?
			 */
			if (quote != CharacterIterator.DONE) {
				return command.setError();
			}

			command._params[i] = sbuf.toString();
		}

		/*
		 * Remainder text
		 */
		while (Character.isWhitespace(ch)) {
			ch = ci.next();
		}

		StringBuffer sbuf = new StringBuffer(ci.getEndIndex() - ci.getIndex() + 1);

		while (ch != CharacterIterator.DONE) {
			sbuf.append(ch);
			ch = ci.next();
		}

		command._remainder = sbuf.toString();

		return command;
    }
}
