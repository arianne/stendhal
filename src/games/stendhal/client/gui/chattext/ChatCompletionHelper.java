/**
 * 
 */
package games.stendhal.client.gui.chattext;

import games.stendhal.common.filter.CollectionFilter;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public final class ChatCompletionHelper extends KeyAdapter {
	private final ChatTextController chatController;
	private final Set<String> playersonline;
	private int  lastkeypressed;
	private Collection< ? extends String> resultset = Collections.emptyList();
	private int currentIndex;
	private String output;

	public ChatCompletionHelper(final ChatTextController chatTextController,
			final Set<String> list) {
		this.chatController = chatTextController;
		this.playersonline = list;
	}

	public void keyPressed(final KeyEvent e) {

		final int keypressed = e.getKeyCode();

		if (keypressed == KeyEvent.VK_TAB) {
			if (lastkeypressed != KeyEvent.VK_TAB) {
				currentIndex = 0;
				buildNames();

			} else {
				currentIndex++;
				if (currentIndex == resultset.size()) {
					currentIndex = 0;
				}
			}
			if (!resultset.isEmpty()) {

				chatController.setChatLine(output
						+ resultset.toArray()[currentIndex]);
			}
		}
		lastkeypressed = e.getKeyCode();
	}

	private void buildNames() {
		final String[] strwords = chatController.getText()
				.split("\\s+");

		final String prefix = strwords[strwords.length - 1];

		final CollectionFilter<String> filter = new StringPrefixFilter(
				prefix);
		output = "";
		for (int j = 0; j < strwords.length - 1; j++) {
			output = output + strwords[j] + " ";
		}

		resultset = filter.filterCopy(playersonline);
	}
	
}
