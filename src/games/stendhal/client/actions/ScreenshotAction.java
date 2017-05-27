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
package games.stendhal.client.actions;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameScreen;
import games.stendhal.client.stendhal;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.common.NotificationType;

/**
 * Take a screenshot, and save it in the game directory.
 */
class ScreenshotAction implements SlashAction {
	/**
	 * Execute save a screenshot command.
	 *
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 *
	 * @return <code>true</code> if was handled.
	 */
	@Override
	public boolean execute(String[] params, String remainder) {
		ClientSingletonRepository.getUserInterface().addEventLine(new EventLine("", "Taking a screenshot...", NotificationType.CLIENT));
		// Drawing the image needs to be done in EDT.
		GameScreen screen = GameScreen.get();
		int width = screen.getWidth();
		int height = screen.getHeight();
		final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		screen.paintComponent(g);
		g.dispose();
		// Saving at least can be done outside the EDT.
		final String fileName = getFileName();
		new SwingWorker<String, Void>() {
			@Override
			protected String doInBackground() throws Exception {
				File file = new File(fileName);
				ImageIO.write(img, "png", file);
				return "Screenshot was saved to " + fileName;
			}

			@Override
			public void done() {
				String msg = "";
				try {
					msg = get();
				} catch (InterruptedException e) {
					Logger.getLogger(ScreenshotAction.class).error(e);
				} catch (ExecutionException e) {
					msg = "Failed to save screenshot to " + fileName + " : ";
		            Throwable cause = e.getCause();
		            Logger.getLogger(ScreenshotAction.class).error(e);
		            if (cause != null) {
		                msg = cause.getMessage();
		            } else {
		                msg = e.getMessage();
		            }
				}
				ClientSingletonRepository.getUserInterface().addEventLine(new EventLine("", msg, NotificationType.CLIENT));
			}
		}.execute();

		return true;
	}

	/**
	 * Get a nice descriptive file name for the screenshot image.
	 *
	 * @return File name
	 */
	private String getFileName() {
		String time = new SimpleDateFormat("-yyyy.MM.dd-HH.mm.ss").format(new Date());
		return stendhal.getGameFolder() + stendhal.GAME_NAME.toLowerCase() + time + ".png";
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 0;
	}
}
