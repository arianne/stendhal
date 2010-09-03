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

import games.stendhal.client.scripting.ChatLineParser;
import jcurses.event.ActionEvent;
import jcurses.event.ActionListener;
import jcurses.widgets.Button;
import jcurses.widgets.GridLayoutManager;
import jcurses.widgets.TextField;
import jcurses.widgets.WidgetsConstants;
import jcurses.widgets.Window;

/**
 * an input window
 *
 * @author hendrik
 */
public class InputJCursesWindow extends Window implements ActionListener {

    private TextField textArea;
    private Button button;

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
        button = new Button("Send");
        button.addListener(this);

        GridLayoutManager manager = new GridLayoutManager(1, 2);
        super.getRootPanel().setLayoutManager(manager);
        manager.addWidget(textArea, 0, 0, 1, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
        manager.addWidget(button, 0, 1, 1, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
        
    }


    public void actionPerformed(ActionEvent arg0) {
        String line = textArea.getText();
        textArea.setText("");
        ChatLineParser.parseAndHandle(line);
    }
}
