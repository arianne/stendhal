/**
 * 
 */
package games.stendhal.client.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

final class ChatCompletionHelper extends KeyAdapter {
	private final ChatTextController chatController;
	private final Vector<String> playersonline;

	ChatCompletionHelper(final ChatTextController chatTextController, final Vector<String> onlineplayers) {
		this.chatController = chatTextController;
		this.playersonline = onlineplayers;
	}

	public void keyPressed(final KeyEvent e) {
		
		final int keypressed = e.getKeyCode();
		if (keypressed == KeyEvent.VK_TAB) {
			final String[] strwords = chatController.getText()
					.split("\\s+");

			
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
}