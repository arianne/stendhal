package games.stendhal.client.gui;

import games.stendhal.client.StendhalClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

@SuppressWarnings("serial")
public final class QuitDialog extends ClientPanel {

	private static final int WIDTH = 150;
	private static final int HEIGHT = 75;

	private boolean disableMovement = false;

	public QuitDialog() {
		super("quit", WIDTH, HEIGHT);

		initComponents();

		setVisible(false);
		setIconifiable(false);
	}

	@Override
	public void reshape(int x, int y, int width, int height) {
		// disallow window movement to simulate a system modal dialog
		//TODO find a better solution, may be using a JDialog or just use JOptionPane
		if (!disableMovement) {
			super.reshape(x, y, width, height);
		}
	}

	private JButton yesButton;

	/**
	 * Build the in-window quit dialog [panel].
	 */
	private void initComponents() {
		setLayout(null);

		yesButton = new JButton();
		yesButton.setText("Yes");
		yesButton.setBounds(20, 25, 55, 25);
		yesButton.addActionListener(new QuitConfirmCB());
		add(yesButton);
		
		JButton noButton = new JButton();
		noButton.setText("No");
		noButton.setBounds(90, 25, 55, 25);
		noButton.addActionListener(new QuitCancelCB());
		add(noButton);

		getRootPane().setDefaultButton(yesButton);
	}

	protected class QuitCancelCB implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			setVisible(false);

			disableMovement = false;
		}
	}

	protected class QuitConfirmCB implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			j2DClient.get().shutdown();
		}
	}

	/**
	 * Request quit confirmation from the user. This stops all player actions
	 * and shows a dialog in which the player can confirm that they really wants
	 * to quit the program. If so it flags the client for termination.
	 */

	public void requestQuit() {
		/*
		 * Stop the player
		 */
		StendhalClient.get().stop();

		/*
		 * Center dialog
		 */
		setBounds((j2DClient.get().getWidth() - WIDTH) / 2,
				(j2DClient.get().getHeight() - HEIGHT) / 2, WIDTH, HEIGHT);

		setVisible(true);
		disableMovement = true;

		yesButton.requestFocus();
	}
}
