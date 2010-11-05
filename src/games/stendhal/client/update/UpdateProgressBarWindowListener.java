package games.stendhal.client.update;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JOptionPane;

public class UpdateProgressBarWindowListener implements WindowListener {

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		int result = JOptionPane.showConfirmDialog(e.getWindow(),
				"Are you sure you want to cancel download?", "Confirmation",
				JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			e.getWindow().dispose();
			System.exit(1);
		}
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}
}
