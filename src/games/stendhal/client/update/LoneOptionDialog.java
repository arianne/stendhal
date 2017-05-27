/***************************************************************************
 *                (C) Copyright 2003-2013 - Faiumoni e.V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.update;

import java.awt.MouseInfo;
import java.awt.Rectangle;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * JOptionPane wrapper for dialogs that have no parent. Places the dialogs
 * on the screen with the mouse cursor, unlike plain JOptionPane that always
 * places them on the first screen. This is package private because any later
 * situations should already have a proper main window to use as the parent.
 */
final class LoneOptionDialog {
	/**
	 * Get the bounds of the screen where the mouse cursor is located.
	 *
	 * @return screen bounds
	 */
	private static Rectangle mouseScreenBounds() {
		return MouseInfo.getPointerInfo().getDevice().getDefaultConfiguration().getBounds();
	}

	/**
	 * Place a dialog centered on the screen with the mouse cursor.
	 *
	 * @param dialog dialog to be centered
	 */
	private static void setLocation(JDialog dialog) {
		Rectangle r = mouseScreenBounds();
		dialog.setLocation(r.x + (r.width - dialog.getWidth()) / 2,
				r.y + (r.height - dialog.getHeight()) / 2);
	}

	/**
	 * Show a message dialog with specified, selectable message.
	 *
	 * @param message message string
	 * @param title title of the dialog window
	 * @param messageType type of the message. These are specified in
	 * 	JOptionDialog
	 */
	static void showMessageDialog(String message, String title, int messageType) {
		JOptionPane pane = new JOptionPane(new SelectableLabel(message), messageType);
		JDialog dialog = pane.createDialog(title);
		dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setLocation(dialog);
		dialog.setVisible(true);
		dialog.dispose();
	}

	/**
	 * Show a message dialog with specified, selectable message.
	 *
	 * @param message message string
	 * @param title title of the dialog window
	 */
	static void showMessageDialog(String message, String title) {
		showMessageDialog(message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Show a message dialog with specified, selectable message. The dialog
	 * will use "Message" as its title.
	 *
	 * @param message message string
	 */
	static void showMessageDialog(String message) {
		showMessageDialog(message, "Message");
	}

	/**
	 * Show a confirmation dialog with specified, selectable message.
	 *
	 * @param message message string
	 * @param title window title
	 * @param optionType type of option, These are specified in JOptionDialog
	 * @param messageType type of message, These are specified in JOptionDialog
	 *
	 * @return integer indicating the option selected by the user
	 */
static int showConfirmDialog(String message, String title, int optionType,
			int messageType) {
		JOptionPane pane = new JOptionPane(new SelectableLabel(message), messageType, optionType);
		JDialog dialog = pane.createDialog(title);
		dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setLocation(dialog);
		dialog.setVisible(true);
		dialog.dispose();

		Object        selectedValue = pane.getValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}

		Object[] options = pane.getOptions();
		if (options == null) {
			if (selectedValue instanceof Integer) {
				return (Integer) selectedValue;
			}
			return JOptionPane.CLOSED_OPTION;
		}

		for (int i = 0; i < options.length; i++) {
			if (options[i].equals(selectedValue)) {
				return i;
			}
		}

		return JOptionPane.CLOSED_OPTION;
	}
}
