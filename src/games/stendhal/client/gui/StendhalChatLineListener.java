package games.stendhal.client.gui;

import games.stendhal.client.stendhal;
import games.stendhal.client.scripting.ChatLineParser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;


import org.apache.log4j.Logger;

public class StendhalChatLineListener implements ActionListener {


	KeyAdapter keylistener = new KeyAdapter() {
	public void keyPressed(final KeyEvent e) {
		final int keypressed = e.getKeyCode();

		if (e.isShiftDown()) {
			if (keypressed == KeyEvent.VK_UP) {
				if (cache.hasPrevious()) {
					chatController.setChatLine(cache.current());
					cache.previous();
				}
				
			} else if (keypressed == KeyEvent.VK_DOWN) {
				if (cache.hasNext()) {
					chatController.setChatLine(cache.next());
				}
			}
			
		}

		if (keypressed == KeyEvent.VK_TAB) {
			final String[] strwords = chatController.getText().split("\\s+");

			for (int i = 0; i < playersonline.size(); i++) {
				if (playersonline.elementAt(i).startsWith(
						strwords[strwords.length - 1])) {
					String output = "";
					for (int j = 0; j < strwords.length - 1; j++) {
						output = output + strwords[j] + " ";
					}
					output = output + playersonline.elementAt(i) + " ";

					chatController.setChatLine(output);
				}
			}
		}
	}

	};
	
	private static final String CHAT_LOG_FILE = System.getProperty("user.home")
			+ "/" + stendhal.STENDHAL_FOLDER + "chat.log";
	ChatCache cache;
	/** the logger instance. */
	static final Logger logger = Logger.getLogger(StendhalChatLineListener.class);

	//private final JTextField playerChatText;

	
	private final Vector<String> playersonline;

	private final ChatTextController chatController;

	public StendhalChatLineListener(final ChatTextController chatText,
			final Vector<String> onlineplayers) {
		super();
		this.chatController = chatText;
		chatController.addActionListener(this);
		chatController.getPlayerChatText().addKeyListener(keylistener);

		playersonline = onlineplayers;
		cache = new ChatCache(CHAT_LOG_FILE);
		cache.loadChatCache();
	}

	public void actionPerformed(final ActionEvent e) {
		final String text = e.getActionCommand();

		logger.debug("Player wrote: " + text);

		if (ChatLineParser.parseAndHandle(text)) {
			cache.addlinetoCache(text);

			chatController.setChatLine("");
		}
	}

	public void save() {
		cache.save();
		
	}
}
