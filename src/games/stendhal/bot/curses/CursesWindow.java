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
import jcurses.widgets.List;
import jcurses.widgets.TextField;
import jcurses.widgets.WidgetsConstants;
import jcurses.widgets.Window;

/**
 * an input window
 *
 * @author hendrik
 */
public class CursesWindow extends Window implements ActionListener {

    private List chatLog;
    private TextField textField;
    private Button button;

    /**
     * creates a new input window
     *
     * @param x x-position
     * @param y y-position
     * @param width  width of the window
     * @param height height of the window
     */
    public CursesWindow(int x, int y, int width, int height) {
        super(x, y, width, height, true, "Stendhal");
        chatLog = new List();
        textField = new TextField();
        button = new Button("Send");
        button.addListener(this);

        GridLayoutManager manager = new GridLayoutManager(1, 3);
        super.getRootPanel().setLayoutManager(manager);
        manager.addWidget(chatLog, 0, 0, 1, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
        manager.addWidget(textField, 0, 1, 1, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
        manager.addWidget(button, 0, 2, 1, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
        
    }

    /**
     * handles the line typed by the user.
     */
    public void actionPerformed(ActionEvent arg0) {
        String line = textField.getText();
        textField.setText("");
        ChatLineParser.parseAndHandle(line);
    }

    /**
     * adds a line to the chatlog
     *
     * @param line line to add
     */
    public void addChatLine(String line) {
        chatLog.add(line);
    }
}
