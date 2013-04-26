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
package games.stendhal.bot.textclient;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.UserInterface;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.sound.facade.SoundSystemFacade;
import games.stendhal.client.sound.nosound.NoSoundFacade;
import games.stendhal.common.NotificationType;

/**
 * the text user interface
 *
 * @author hendrik
 */
public class TextUI implements UserInterface {
	private static final String ESC_COLOR_RESET = "\u001B[m";
	private static final String ESC_COLOR_INPUT = "\u001B[32m";

	private SoundSystemFacade soundSystemFacade;

	/**
	 * creates a TextUI
	 */
	public TextUI() {
		ClientSingletonRepository.setUserInterface(this);
		soundSystemFacade = new NoSoundFacade();
	}

	/**
	 * just output a line on stdout
	 *
	 * @param line to print
	 */
	@Override
	public void addEventLine(EventLine line) {
		System.out.println(ESC_COLOR_RESET + line + ESC_COLOR_INPUT);
	}

	/**
	 * adds a text box on the screen
	 *
	 * @param x  x
	 * @param y  y
	 * @param text text to display
	 * @param type type of text
	 * @param isTalking chat?
	 */
	@Override
	public void addGameScreenText(double x, double y, 
			String text, NotificationType type,
			boolean isTalking) {
		// ignored
		
	}

	/**
	 * gets the sound system
	 *
	 * @return SoundSystemFacade
	 */
	@Override
	public SoundSystemFacade getSoundSystemFacade() {
		return soundSystemFacade;
	}

	@Override
	public void addAchievementBox(String title, String description,
			String category) {
		// ignored
	}

}
