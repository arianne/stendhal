package games.stendhal.client.scripting;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.common.NotificationType;
import marauroa.common.game.RPAction;

/**
 * Parses the input in the chat box and invokes the appropriate action.
 */
public class ChatLineParser {
	private static ChatLineParser instance;

	// hide constructor (Singleton)
	private ChatLineParser() {

		SlashActionRepository.register();
	}

	/**
	 * returns the ChatLineParser.
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
	 * parses a chat/command line and processes the result.
	 * 
	 * @param input
	 *            string to handle
	 * 
	 * @return <code>true</code> if command was valid enough to process,
	 *         <code>false</code> otherwise.
	 */
	public boolean parseAndHandle(String input) {
		// get line
		String text = input.trim();

		if (text.length() == 0) {
			return false;
		}

		if (text.charAt(0) == '/') {
			SlashActionCommand command = SlashActionParser.parse(text.substring(1));
			String[] params = command.getParams();

			if (command.hasError()) {
				StendhalUI.get().addEventLine(command.getError(),
						NotificationType.ERROR);
				return false;
			}

			/*
			 * Execute
			 */
			if (command.getAction() != null) {
				return command.getAction().execute(params,
						command.getRemainder());
			} else {
				/*
				 * Server extension
				 */
				RPAction extension = new RPAction();

				extension.put("type", command.getName());

				if (params.length > 0 && params[0] != null) {
					extension.put("target", params[0]);
					extension.put("args", command.getRemainder());
				}

				StendhalClient.get().send(extension);

				return true;
			}
		} else {
			// Chat command. The most frequent one.
			RPAction chat = new RPAction();

			chat.put("type", "chat");
			chat.put("text", text);

			StendhalClient.get().send(chat);

			return true;
		}
	}

}
