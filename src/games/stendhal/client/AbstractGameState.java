/*
 * Copyright Hans Häggström 2005
 * hans.haggstrom at iki dot fi
 */

package games.stendhal.client;

import javax.swing.*;

/**
 * Implements common functionality of the GameState interface.
 *
 * @author Hans Häggström
 */
public class AbstractGameState
        implements GameState
{

    //======================================================================
    // Public Methods

    //----------------------------------------------------------------------
    // Constructors

    /**
     * Creates a new AbstractGameState.
     */
    public AbstractGameState()
    {
    }


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
    public GameState runGameState( GameState previousGameState, JComponent mainView )
    {
        return null;  // TODO: Implement
    }
}


