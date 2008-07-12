package games.stendhal.client.gui;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.stendhal;
import games.stendhal.client.scripting.ChatLineParser;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.JTextField;

import org.apache.log4j.Logger;

public class StendhalChatLineListener implements ActionListener, KeyListener {

	private static final String CHAT_LOG_FILE = System.getProperty("user.home")
			+ "/" + stendhal.STENDHAL_FOLDER + "chat.log";

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalChatLineListener.class);

	private final JTextField playerChatText;

	private final LinkedList<String> lines;
	private final Vector<String> playersonline;

	private int actual;

	@SuppressWarnings("unchecked")
	public StendhalChatLineListener(final StendhalClient client,
			final JTextField playerChatText) {
		super();
		this.playerChatText = playerChatText;
		lines = new LinkedList<String>();

		/* Enable Keyboard TAB events */
		final KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();

		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);

		client.whoplayers = new Vector<String>();
		playersonline = client.whoplayers;

		// Open chat log file
		try {
			// TODO: Create the file on the Stendhal home folder.
			final File chatfile = new File(CHAT_LOG_FILE);

			if (chatfile.exists()) {
				final FileInputStream fis = new FileInputStream(chatfile);
				final BufferedReader br = new BufferedReader(new InputStreamReader(
						fis));

				String line = null;
				while (null != (line = br.readLine())) {
					lines.add(line);
				}
				br.close();
				fis.close();
			}
		} catch (final Exception e) {
			logger.error(e, e);
		}

		actual = lines.size();
	}

	public void save() {
		// Save chat log file
		FileOutputStream fo;
		try {
			fo = new FileOutputStream(CHAT_LOG_FILE);
			final PrintStream ps = new PrintStream(fo);

			/*
			 * Keep size of chat.log in a reasonable size.
			 */
			while (lines.size() > 200) {
				lines.removeFirst();
			}

			final ListIterator<String> iterator = lines.listIterator();
			while (iterator.hasNext()) {
				ps.println(iterator.next());
			}
			ps.close();
			fo.close();
		} catch (final Exception ex) {
			logger.error(ex, ex);
		}
	}

	public void keyPressed(final KeyEvent e) {
		final int keypressed = e.getKeyCode();

		if (e.isShiftDown()) {
			if (keypressed == KeyEvent.VK_UP) {
				if (actual > 0) {
					playerChatText.setText(lines.get(actual - 1));
					actual--;
				}
			} else if (keypressed == KeyEvent.VK_DOWN) {
				if (actual < lines.size()) {
					playerChatText.setText(lines.get(actual));
					actual++;
				}
			}
		}

		if (keypressed == KeyEvent.VK_TAB) {
			final String[] strwords = playerChatText.getText().split("\\s+");

			for (int i = 0; i < playersonline.size(); i++) {
				if (playersonline.elementAt(i).startsWith(
						strwords[strwords.length - 1])) {
					String output = "";
					for (int j = 0; j < strwords.length - 1; j++) {
						output = output + strwords[j] + " ";
					}
					output = output + playersonline.elementAt(i) + " ";

					playerChatText.setText(output);
				}
			}
		}
	}

	public void keyReleased(final KeyEvent e) {
		// specified by interface but not used here
	}

	public void keyTyped(final KeyEvent e) {
		// specified by interface but not used here
	}

	public void actionPerformed(final ActionEvent e) {
		final String text = playerChatText.getText();

		logger.debug("Player wrote: " + text);

		if (ChatLineParser.parseAndHandle(text)) {
			lines.add(text);
			actual = lines.size();

			if (lines.size() > 50) {
				lines.remove(0);
				actual--;
			}

			playerChatText.setText("");
		}
	}
}
