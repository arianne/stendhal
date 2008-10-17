package games.stendhal.client.gui;

import games.stendhal.client.scripting.ChatLineParser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

public class StendhalChatLineListener implements ActionListener {
	
	/** the logger instance. */
	static final Logger logger = Logger
			.getLogger(StendhalChatLineListener.class);



	final ChatTextController chatController;

	public StendhalChatLineListener(final ChatTextController chatText) {
		super();
		this.chatController = chatText;
		
		
	}

	public void actionPerformed(final ActionEvent e) {
		final String text = e.getActionCommand();

		logger.debug("Player wrote: " + text);

		if (ChatLineParser.parseAndHandle(text)) {
			chatController.clearLine();
			
		}
	}

	
}
