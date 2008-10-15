package games.stendhal.client.gui;


import java.awt.event.KeyAdapter;

import javax.swing.JTextField;

public class ChatTextController {
	JTextField playerChatText = new JTextField("");
	public ChatTextController() {
		playerChatText.setFocusTraversalKeysEnabled(false);
		playerChatText.addKeyListener(new ChatTextKeyListener());
		
	}
	
	public JTextField getPlayerChatText() {
		
		return playerChatText;
	}

	public void setChatLine(final String text) {
		
		playerChatText.setText(text);
	}

	class ChatTextKeyListener extends KeyAdapter {
		

		
		
	}
}
