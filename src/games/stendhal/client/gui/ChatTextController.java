package games.stendhal.client.gui;


import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;

import javax.swing.JTextField;

public class ChatTextController {
	private JTextField playerChatText = new JTextField("");
	public ChatTextController() {
		playerChatText.setFocusTraversalKeysEnabled(false);
		playerChatText.addKeyListener(new ChatTextKeyListener());
		
	}
	
	public Component getPlayerChatText() {
		
		return playerChatText;
	}

	public void setChatLine(final String text) {
		
		playerChatText.setText(text);
	}

	class ChatTextKeyListener extends KeyAdapter {
		

		
		
	}

	public void addActionListener(final ActionListener l) {
		playerChatText.addActionListener(l);
		
	}

	public String getText() {
		return playerChatText.getText();
	}
}
