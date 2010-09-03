/* $Id$ */
/***************************************************************************
 *                 (C) Copyright 2003-2010 - Arianne Project               *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.bot.curses;

import jcurses.widgets.GridLayoutManager;
import jcurses.widgets.TextField;
import jcurses.widgets.WidgetsConstants;
import jcurses.widgets.Window;

/**
 * an input iwndow
 *
 * @author hendrik
 */
public class InputJCursesWindow extends Window {
    
    private TextField textArea;


    /**
     * creates a new input window
     *
     * @param x x-position
     * @param y y-position
     * @param width  width of the window
     * @param height height of the window
     */
    public InputJCursesWindow(int x, int y, int width, int height) {
        super(x, y, width, height, true, "Input");
        textArea = new TextField();
        GridLayoutManager manager = new GridLayoutManager(1, 1);
        super.getRootPanel().setLayoutManager(manager);
        manager.addWidget(textArea, 0, 0, 1, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
        

    }
}
