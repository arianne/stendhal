package games.stendhal.client.scripting;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.scripting.command.RecordCommand;
import games.stendhal.client.scripting.command.SlashCommand;
import games.stendhal.client.scripting.command.SlashCommandRepository;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import marauroa.common.game.RPAction;

/**
 * Parses the input in the chat box and invokes the appropriate action.
 */
public class ChatLineParser {
	private static ChatLineParser instance = null;
	private RecordCommand recordCommand = null;

	// hide constructor (Singleton)
	private ChatLineParser() {

		SlashCommandRepository.register();

		recordCommand = (RecordCommand) SlashCommandRepository.get("record");
		
	}


	/**
	 * returns the ChatLineParser
	 *
	 * @return ChatLineParser
	 */
	public static synchronized ChatLineParser get() {
		if (instance == null) {
			instance = new ChatLineParser();
		}
		return instance;
	}

	/**
	 * parses a chat/command line and processes the result
	 *
	 * @param input string to handle
	 *
	 * @return	<code>true</code> if command was valid enough to
	 *		process, <code>false</code> otherwise.
	 */
	public boolean parseAndHandle(String input) {
		CharacterIterator	ci;
		char		quote;
		char		ch;
		String		name;
		String []	params;
		String		remainder;
		StringBuffer	sbuf;
		int		minimum;
		int		maximum;
		SlashCommand	command;
		int		i;


		// get line
		String text = input.trim();

		if (text.length() == 0) {
			return false;
		}

		// record it (if recording)
		if (recordCommand.getRecorder() != null) {
			recordCommand.getRecorder().recordChatLine(text);
		}
		
		if (text.charAt(0) != '/') {
			// Chat command. The most frequent one.
			RPAction chat = new RPAction();

			chat.put("type", "chat");
			chat.put("text", text);

			StendhalClient.get().send(chat);

			return true;
		}


		/*
		 * Parse command
		 */
		ci = new StringCharacterIterator(text, 1);
		ch = ci.current();


		/*
		 * Must be non-space after slash
		 */
		if(Character.isWhitespace(ch)) {
			return false;
		}


		/*
		 * Extract command name
		 */
		if(Character.isLetterOrDigit(ch)) {
			/*
			 * Word command
			 */
			while ((ch != CharacterIterator.DONE) && !Character.isWhitespace(ch)) {
				ch = ci.next();
			}

			name = text.substring(1, ci.getIndex());
		} else {
			/*
			 * Special character command
			 */
			name = String.valueOf(ch);
			ch = ci.next();
		}


		/*
		 * Find command handler
		 */
		if((command = SlashCommandRepository.get(name)) != null) {
			minimum = command.getMinimumParameters();
			maximum = command.getMaximumParameters();
		} else {
			/*
			 * Server extention criteria
			 */
			minimum = 0;
			maximum = 1;
		}


		/*
		 * Extract parameters
		 * (ch already set to first character)
		 */
		params = new String[maximum];

		for(i = 0; i < maximum; i++) {
			/*
			 * Skip leading spaces
			 */
			while(Character.isWhitespace(ch)) {
				ch = ci.next();
			}

			/*
			 * EOL?
			 */
			if(ch == CharacterIterator.DONE) {
				/*
				 * Incomplete parameters?
				 */
				if(i < minimum) {
					return false;
				}

				break;
			}

			/*
			 * Grab parameter
			 */
			sbuf = new StringBuffer();
			quote = CharacterIterator.DONE;

			while(ch != CharacterIterator.DONE) {
				if(ch == quote) {
					// End of quote
					quote = CharacterIterator.DONE;
				} else if(quote != CharacterIterator.DONE) {
					// Quoted character
					sbuf.append(ch);
				} else if((ch == '"') || (ch == '\'')) {
					// Start of quote
					quote = ch;
				} else if(Character.isWhitespace(ch)) {
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
			if(quote != CharacterIterator.DONE) {
				return false;
			}

			params[i] = sbuf.toString();
		}


		/*
		 * Remainder text
		 */
		while(Character.isWhitespace(ch)) {
			ch = ci.next();
		}

		sbuf = new StringBuffer(ci.getEndIndex() - ci.getIndex() + 1);

		while(ch != CharacterIterator.DONE) {
			sbuf.append(ch);
			ch = ci.next();
		}

		remainder = sbuf.toString();


		/*
		 * Execute
		 */
		if(command != null) {
			return command.execute(params, remainder);
		} else {
			/*
			 * Server Extention
			 */
			RPAction extension = new RPAction();

			extension.put("type", name);

			if(params[0] != null) {
				extension.put("target", params[0]);
				extension.put("args", remainder);
			}

			StendhalClient.get().send(extension);

			return true;
		}
	}

}
