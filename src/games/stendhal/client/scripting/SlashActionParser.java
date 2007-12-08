package games.stendhal.client.scripting;

import games.stendhal.client.actions.SlashActionRepository;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * Command line parser for the Stendhal client
 * The parser recognises the registered slash action commands and
 * handles string quoting.
 * @author Martin Fuchs
 */
public class SlashActionParser
{
	/**
	 * Parse the given slash command. The text is supposed not to include the slash
	 * character but to start directly after the slash on the client command line.
	 * @param text
	 * @return SlashActionCommand object 
	 */
	public static SlashActionCommand parse(String text)
    {
		SlashActionCommand command = new SlashActionCommand();

		if (text.length() == 0) {
			return command.setError("missing slash command");
		}

		/*
		 * Parse command
		 */
		CharacterIterator ci = new StringCharacterIterator(text);
		char ch = ci.current();

		/*
		 * Must be non-space after slash
		 */
		if (Character.isWhitespace(ch)) {
			return command.setError("unexpected space after slash character");
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

			command._name = text.substring(0, ci.getIndex());
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
		command._action = SlashActionRepository.get(command._name);

		int minimum, maximum;

		if (command._action != null) {
			minimum = command._action.getMinimumParameters();
			maximum = command._action.getMaximumParameters();
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
					return command.setError("missing command parameter");
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
				return command.setError("unterminated quote");
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
