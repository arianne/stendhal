/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;


import java.awt.Component;
import java.awt.Container;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;

import games.stendhal.client.entity.User;

@SuppressWarnings("serial") class QuitDialog {
	private static final int PADDING = 10;
	/** Quit dialog window. */
	private InternalManagedWindow quitDialog;
	private JButton yesButton;

	/**
	 * Get the dialog component.
	 *
	 * @return quit dialog component
	 */
	Component getQuitDialog() {
		return quitDialog;
	}

	/**
	 * Create a new QuitDialog.
	 */
	QuitDialog() {
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
		JComponent content = new JComponent() { };
		content.setLayout(new GridLayout(1, 2, PADDING, PADDING));
		content.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
		// Limit keyboard focus handling to the dialog until the user makes some
		// decision
		content.setFocusCycleRoot(true);
		content.setFocusTraversalPolicy(new LimitingFocusTraversalPolicy());

		// create "yes" button
		yesButton = new JButton();
		yesButton.setText("Yes");
		yesButton.setMnemonic(KeyEvent.VK_Y);
		yesButton.addActionListener(new QuitConfirmCB());
		content.add(yesButton);

		// create "no" button
		JButton noButton = new JButton();
		noButton.setText("No");
		noButton.setMnemonic(KeyEvent.VK_N);
		noButton.addActionListener(new QuitCancelCB());
		content.add(noButton);

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
	private class QuitCancelCB implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent ev) {
			quitDialog.setVisible(false);
		}
	}

	/**
	 * Call back at quit confirmed.
	 */
	private static class QuitConfirmCB implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent ev) {
			j2DClient.get().shutdown();
		}
	}

	/**
	 * Request quit confirmation from the user. This stops all player actions
	 * and shows a dialog in which the player can confirm that they really wants
	 * to quit the program. If so it flags the client for termination.
	 */
	void requestQuit(final User user) {
		/*
		 * Stop the player
		 */
		if (user != null && !user.stopped()) {
			/* User.stopMovement() executes an AutoWalkAction which will stop
			 * the character's movement and remove auto-walk setting.
			 */
			user.stopMovement();
		}

		quitDialog.center();
		quitDialog.setVisible(true);
		yesButton.requestFocusInWindow();
	}

	/**
	 * For keeping the dialog centered on game screen resizes.
	 */
	private class ParentResizeListener implements HierarchyBoundsListener {
		@Override
		public void ancestorMoved(HierarchyEvent e) {
			// ignore
		}

		@Override
		public void ancestorResized(HierarchyEvent e) {
			if (quitDialog.isVisible()) {
				if (e.getChanged().equals(quitDialog.getParent())) {
					quitDialog.center();
				}
			}
		}
	}

	/**
	 * A FocusTraversalPolicy that keeps the keyboard focus within the
	 * container, instead of passing it to parent once the last component has
	 * been reached.
	 */
	private static class LimitingFocusTraversalPolicy extends ContainerOrderFocusTraversalPolicy {
		@Override
		public Component getFirstComponent(Container container) {
			// By default we'd get the container itself.
			Component[] components = container.getComponents();
			if (components.length > 0) {
				return components[0];
			}
			return null;
		}

		@Override
		public Component getComponentBefore(Container container,
                Component component) {
			/*
			 * Jump to the actual last component instead of returning the
			 * container itself when cycling backwards from the first component.
			 */
			Component before = super.getComponentBefore(container, component);
			if (before == container) {
				before = super.getLastComponent(container);
			}
			return before;
		}
	}
}
