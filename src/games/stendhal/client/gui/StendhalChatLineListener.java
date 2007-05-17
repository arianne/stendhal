package games.stendhal.client.gui;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.scripting.ChatLineParser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.JTextField;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

public class StendhalChatLineListener implements ActionListener, KeyListener {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(StendhalChatLineListener.class);

	private JTextField playerChatText;

	private LinkedList<String> lines;

	private int actual;

	public StendhalChatLineListener(StendhalClient client, JTextField playerChatText) {
		super();
		this.playerChatText = playerChatText;
		lines = new LinkedList<String>();
		
		//Open chat log file
		try {
			FileInputStream fis = new FileInputStream("chat.log");
			BufferedReader br=new BufferedReader(new InputStreamReader(fis));

			String line =null;
			while(null != (line = br.readLine())) {
				lines.add(line);
			}
			br.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		actual = lines.size();
	}

	public void save (){
		// Save chat log file
		FileOutputStream fo;
		try {
			fo = new FileOutputStream("chat.log");
			PrintStream ps=new PrintStream(fo);

			ListIterator< String > iterator = lines.listIterator();
			while ( iterator.hasNext() ) 
			{
				ps.println( iterator.next());                 
			}
			ps.close();
			fo.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
		
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
