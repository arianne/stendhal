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

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameScreen;
import games.stendhal.client.stendhal;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.common.NotificationType;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 * Take a screenshot, and save it in the game directory.
 */
public class ScreenshotAction implements SlashAction {
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
		// Ming the image needs to be done in EDT.
		GameScreen screen = GameScreen.get();
		int width = screen.getWidth();
		int height = screen.getHeight();
		final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		screen.paintComponent(g);
		g.dispose();
		// Saving at least can be done outside the EDT.
		new Thread() {
			@Override
			public void run() {
				String fileName = getFileName();
				String message;
				try {
				    File file = new File(fileName);
				    ImageIO.write(img, "png", file);
				    message = "Screenshot was saved to " + fileName;
				} catch (IOException e) {
					message = "Failed to save screenshot to " + fileName;
					Logger.getLogger(ScreenshotAction.class).error(message, e);
				}
				final String msg = message;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						ClientSingletonRepository.getUserInterface().addEventLine(new EventLine("", msg, NotificationType.CLIENT));
					}
				});
			}
		}.start();
		
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
