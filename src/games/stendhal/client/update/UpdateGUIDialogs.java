/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import java.text.NumberFormat;

import javax.swing.JOptionPane;

/**
 * Dialogboxes used during updating..
 *
 * @author hendrik
 */
class UpdateGUIDialogs {

	private static final String DIALOG_TITLE = ClientGameConfiguration.get("GAME_NAME") + " Update";

	/**
	 * Asks the user to accept an update.
	 *
	 * @param updateSize
	 *            size of the files to download
	 * @param update
	 *            true, if it is an update, false on first install
	 * @return true if the update should be performed, false otherwise
	 */
	static boolean askForDownload(final int updateSize, final boolean update) {
		// format number, only provide decimal digits on very small sizes
		float size = (float) updateSize / 1024;
		if (size > 10) {
			size = (int) size;
		}
		final String sizeString = NumberFormat.getInstance().format(size);

		// ask user
		int resCode;
		if (update) {
			resCode = LoneOptionDialog.showConfirmDialog("找到新版本，要下载 "
					+ sizeString + " KB.\r\n" + "的补丁"
					+ ClientGameConfiguration.get("GAME_NAME") + " 确认安装?",
					DIALOG_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		} else {
			resCode = LoneOptionDialog.showConfirmDialog("需要下载以下文件："
						+ sizeString + " KB.\r\n" 
					+ ClientGameConfiguration.get("GAME_NAME") + " 确认安装?",
					DIALOG_TITLE, JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
		}

		return (resCode == JOptionPane.YES_OPTION);
	}

	/**
	 * Displays a message box.
	 *
	 * @param message
	 *            message to display
	 */
	static void messageBox(final String message) {
		LoneOptionDialog.showMessageDialog(message, DIALOG_TITLE);
	}
}
