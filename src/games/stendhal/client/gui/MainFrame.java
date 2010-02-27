package games.stendhal.client.gui;

import games.stendhal.client.stendhal;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.client.update.ClientGameConfiguration;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class MainFrame {

	private final JFrame mainFrame = new JFrame();
	public MainFrame() {
		initialize();
	}

	 private void initialize() {
		 setTitle();
		setIcon();
		setDefaultCloseBehaviour();
	}



	private void setDefaultCloseBehaviour() {
		// When the user tries to close the window, don't close immediately,
		// but show a confirmation dialog.
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	private void setIcon() {
		final URL url = this.getClass().getClassLoader().getResource(
				ClientGameConfiguration.get("GAME_ICON"));
		getMainFrame().setIconImage(new ImageIcon(url).getImage());
	}

	private void setTitle() {
		mainFrame.setTitle(ClientGameConfiguration.get("GAME_NAME") + " "
				+ stendhal.VERSION
				+ " - a multiplayer online game using Arianne");
	}

	 JFrame getMainFrame() {
		return mainFrame;
	}

}
