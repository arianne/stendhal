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

	private JTextField playerChatText;

	private LinkedList<String> lines;
	private Vector<String> playersonline;

	private int actual;

	@SuppressWarnings("unchecked")
	public StendhalChatLineListener(StendhalClient client,
			JTextField playerChatText) {
		super();
		this.playerChatText = playerChatText;
		lines = new LinkedList<String>();

		/* Enable Keyboard TAB events */
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();

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
			// TODO: Create the file on the stendhal home folder.
			File chatfile = new File(CHAT_LOG_FILE);

			if (chatfile.exists()) {
				FileInputStream fis = new FileInputStream(chatfile);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fis));

				String line = null;
				while (null != (line = br.readLine())) {
					lines.add(line);
				}
				br.close();
				fis.close();
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		actual = lines.size();
	}

	public void save() {
		// Save chat log file
		FileOutputStream fo;
		try {
			fo = new FileOutputStream(CHAT_LOG_FILE);
			PrintStream ps = new PrintStream(fo);

			/*
			 * Keep size of chat.log in a reasonable size.
			 */
			while (lines.size() > 200) {
				lines.removeFirst();
			}

			ListIterator<String> iterator = lines.listIterator();
			while (iterator.hasNext()) {
				ps.println(iterator.next());
			}
			ps.close();
			fo.close();
		} catch (Exception ex) {
			logger.error(ex, ex);
		}
	}

	public void keyPressed(KeyEvent e) {
		int keypressed = e.getKeyCode();

		if (e.isShiftDown()) {
			switch (keypressed) {
			case KeyEvent.VK_UP:
				if (actual > 0) {
					playerChatText.setText(lines.get(actual - 1));
					actual--;
				}
				break;
			case KeyEvent.VK_DOWN:
				if (actual < lines.size()) {
					playerChatText.setText(lines.get(actual));
					actual++;
				}
				break;
			}
		}

		if (keypressed == KeyEvent.VK_TAB) {
			String[] strwords = playerChatText.getText().split("\\s+");

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

	public void keyReleased(KeyEvent e) {
		// specified by interface but not used here
	}

	public void keyTyped(KeyEvent e) {
		// specified by interface but not used here
	}

	public void actionPerformed(ActionEvent e) {
		String text = playerChatText.getText();

		logger.debug("Player wrote: " + text);

		if (ChatLineParser.get().parseAndHandle(text)) {
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
