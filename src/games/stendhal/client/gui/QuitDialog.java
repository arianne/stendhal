package games.stendhal.client.gui;


import games.stendhal.client.StendhalClient;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;

public class QuitDialog {
	private static final int PADDING = 12;
	InternalManagedWindow quitDialog;
	
	Component getQuitDialog() {
		return quitDialog;
	}
	
	public QuitDialog() {
		quitDialog = buildQuitDialog();
		quitDialog.setVisible(false);
		quitDialog.addHierarchyBoundsListener(new ParentResizeListener());
	}
	
	/**
	 * Build the in-window quit dialog.
	 * 
	 * @return the quit dialog
	 */
	private InternalManagedWindow buildQuitDialog() {
		// dialog contents
		JComponent content = new JComponent() {};
		content.setLayout(new GridLayout(1, 2, PADDING, PADDING));
		content.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

		// create "yes" button
		JButton yesButton = new JButton();
		yesButton.setText("Yes");
		yesButton.addActionListener(new QuitConfirmCB());
		content.add(yesButton);

		// create "no" button
		JButton noButton = new JButton();
		noButton.setText("No");
		noButton.addActionListener(new QuitCancelCB());
		content.add(noButton);
		
		// Beautify button sizes; ensure that they are equal
		Dimension yPref = yesButton.getPreferredSize();
		Dimension nPref = noButton.getPreferredSize();
		Dimension pref = new Dimension(Math.max(yPref.width, nPref.width),
				Math.max(yPref.height, nPref.height));
		yesButton.setPreferredSize(pref);
		noButton.setPreferredSize(pref);

		// Pack the whole thing in a managed window
		InternalManagedWindow window = new InternalManagedWindow("quit", "Quit");
		window.setContent(content);
		window.setMinimizable(false);
		window.setHideOnClose(true);
		window.setMovable(false);

		return window;
	}
	
	/**
	 * Call back at "No" answer to quit.
	 */
	protected class QuitCancelCB implements ActionListener {
		public void actionPerformed(final ActionEvent ev) {
			quitDialog.setVisible(false);
		}
	}

	/**
	 * Call back at quit confirmed.
	 */
	private static class QuitConfirmCB implements ActionListener {
		public void actionPerformed(final ActionEvent ev) {
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
		quitDialog.center();
		quitDialog.setVisible(true);
	}
	
	/**
	 * For keeping the dialog centered on game screen resizes.
	 */
	private class ParentResizeListener implements HierarchyBoundsListener {
		public void ancestorMoved(HierarchyEvent e) {
		}

		public void ancestorResized(HierarchyEvent e) {
			if (quitDialog.isVisible()) {
				if (e.getChanged().equals(quitDialog.getParent())) {
					quitDialog.center();
				}
			}
		}
	}
}
