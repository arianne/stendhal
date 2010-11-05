package games.stendhal.client.update;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JOptionPane;

/**
 * Ask for confirmation and exists the JVM if the user closes the progress bar window.
 */
public class UpdateProgressBarWindowListener implements WindowListener {

	public void windowOpened(WindowEvent e) {
		// do nothing
	}

	/**
	 * Ask for confirmation and closes the JVM
	 */
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
		// do nothing
	}

	public void windowIconified(WindowEvent e) {
		// do nothing
	}

	public void windowDeiconified(WindowEvent e) {
		// do nothing
	}

	public void windowActivated(WindowEvent e) {
		// do nothing
	}

	public void windowDeactivated(WindowEvent e) {
		// do nothing
	}
}
