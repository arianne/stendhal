package games.stendhal.client.gui;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.scripting.ChatLineParser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.JTextField;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

public class StendhalChatLineListener implements ActionListener, KeyListener {
	/** the logger instance. */
	private static final Logger logger = Log4J
			.getLogger(StendhalChatLineListener.class);

	private JTextField playerChatText;
	private LinkedList<String> lines;

	private int actual;

	public StendhalChatLineListener(StendhalClient client,
			JTextField playerChatText) {
		super();
		this.playerChatText = playerChatText;
		lines = new LinkedList<String>();
		actual = 0;
	}



	public void keyPressed(KeyEvent e) {
		if (e.isShiftDown()) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP: {
				if (actual > 0) {
					playerChatText.setText(lines.get(actual - 1));
					actual--;
				}
				break;
			}
			case KeyEvent.VK_DOWN: {
				if (actual < lines.size()) {
					playerChatText.setText(lines.get(actual));
					actual++;
				}
				break;
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
		
		if(ChatLineParser.get().parseAndHandle(text)) {
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
