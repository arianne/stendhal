/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import games.stendhal.client.gui.ScreenController;
import games.stendhal.client.gui.UserInterface;
import games.stendhal.client.sound.facade.SoundSystemFacade;
import marauroa.client.ClientFramework;

/**
 * keeps instances of singletons that may depend on the context
 *
 * @author hendrik
 */
public class ClientSingletonRepository {

	private static ClientFramework clientFramework;
	private static UserInterface userInterface;

	/**
	 * gets the ClientFramework
	 *
	 * @return ClientFramework
	 */
	public static ClientFramework getClientFramework() {
		return clientFramework;
	}


	/**
	 * sets the ClientFramework
	 *
	 * @param clientFramework ClientFramework
	 */
	public static void setClientFramework(ClientFramework clientFramework) {
		ClientSingletonRepository.clientFramework = clientFramework;
	}


	/**
	 * gets the user interface
	 *
	 * @return UserInterface
	 */
	public static UserInterface getUserInterface() {
		return userInterface;
	}

	/**
	 * sets the user interface
	 *
	 * @param userInterface UserInterface
	 */
	public static void setUserInterface(UserInterface userInterface) {
		ClientSingletonRepository.userInterface = userInterface;
	}

	/**
	 * Gets the screen controller.
	 *
	 * @return
	 *     ScreenController instance.
	 */
	public static ScreenController getScreenController() {
		return ScreenController.get();
	}

	/**
	 * gets the sound system
	 *
	 * @return SoundSystemFacade
	 */
	public static SoundSystemFacade getSound() {
		return userInterface.getSoundSystemFacade();
	}
}
