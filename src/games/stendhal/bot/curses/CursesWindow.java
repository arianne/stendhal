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

import java.util.StringTokenizer;

import jcurses.event.ActionEvent;
import jcurses.event.ActionListener;
import jcurses.system.CharColor;
import jcurses.widgets.Button;
import jcurses.widgets.GridLayoutManager;
import jcurses.widgets.TextArea;
import jcurses.widgets.TextField;
import jcurses.widgets.WidgetsConstants;
import jcurses.widgets.Window;

/**
 * an input window
 *
 * @author hendrik
 */
public class CursesWindow extends Window implements ActionListener {

    private int windowWidth;
    private TextArea chatLog;
    private TextField textField;
    private Button button;

    /**
     * creates a new input window
     *
     * @param x x-position
     * @param y y-position
     * @param width  width of the window
     * @param height height of the window
     * @param title title of window
     */
    public CursesWindow(int x, int y, int width, int height, String title) {
        super(x, y, width, height, false, title);
        this.windowWidth = width;
        chatLog = new TextArea();
        chatLog.setColors(new CharColor(CharColor.WHITE, CharColor.BLACK));
        textField = new TextField();
        button = new Button("Send");
        button.setShortCut('\n');
        button.addListener(this);

        int innerHeight = height - 5;
        GridLayoutManager manager = new GridLayoutManager(1, innerHeight);
        super.getRootPanel().setLayoutManager(manager);
        manager.addWidget(chatLog  , 0,  0, 1, innerHeight - 2, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
        manager.addWidget(textField, 0, innerHeight - 2, 1, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
        manager.addWidget(button   , 0, innerHeight - 1, 1, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
        
    }

    /**
     * handles the line typed by the user.
     */
    @Override
	public void actionPerformed(ActionEvent arg0) {
        String line = textField.getText();
        textField.setText("");
        ChatLineParser.parseAndHandle(line);
        this.repaint();
    }

    /**
     * adds a line to the chatlog
     *
     * @param line line to add
     */
    public void addChatLine(String line) {
        chatLog.setText(chatLog.getText() + splitLines(line, windowWidth - 5));
        chatLog.setCursorLocation(0, Integer.MAX_VALUE);
        this.repaint();
    }

    /**
     * wraps long lines so that they fit within "width" characters
     *
     * @param lines lines to split
     * @param width maximum width
     * @return split lines
     */
    private String splitLines(String lines, int width) {
        String lineSeparator = System.getProperty("line.separator");
        StringBuilder res = new StringBuilder();
        StringTokenizer st = new StringTokenizer(lines, "\r\n");
        while (st.hasMoreTokens()) {
            String line = st.nextToken();
            while (line.length() > width) {
                res.append(line.substring(0, width));
                res.append(lineSeparator);
                line = line.substring(width);
            }
            res.append(line);
            res.append(lineSeparator);
        }
        return res.toString();
    }
}
