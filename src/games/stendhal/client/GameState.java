/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * Copyright Hans Häggström 2005
 * hans.haggstrom at iki dot fi
 */

package games.stendhal.client;

import javax.swing.*;


/**
 * An interface for different high level states that the game can be in.
 * A game state affects the whole screen and user input.  Different states could for example be
 * the main menu, the server login and menu, a loading screen, and the game itself.
 *
 * @author Hans Häggström
 */
public interface GameState
{

    //======================================================================
    // Public Methods

    /**
     * Enters the game state, and returns when the game state has ended.
     * So implementations of this method should contain the main loop of a game state.
     *
     * @param previousGameState the previous game state.  Can be used for returning to the previous
     *                          game state from states such as paused or in game-menu.
     * @param mainView          the UI component that the game state can draw itself in.
     *
     * @return the next GameState to switch to, or null to exit the application.
     */
    GameState runGameState( GameState previousGameState, JComponent mainView );

}

