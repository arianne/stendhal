/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
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
 * TextPanel.java
 *
 * Created on 22. Oktober 2005, 20:52
 */

package games.stendhal.client.gui.wt;

import games.stendhal.common.StringFormatter;
import java.awt.Color;
import java.awt.Graphics;

/**
 * A simple panel with text.
 * @author matthias
 */
public class TextPanel extends Panel
{
  /** default font size */
  private static final int DEFAULT_FONT_SIZE = 12;
  /** default font size */
  private static final Color DEFAULT_COLOR = Color.WHITE;

  /** the text to display */
  private StringFormatter formatter;
  /** the font size */
  private int fontSize;
  /** the font color */
  private Color color;
  
  /** Creates a new TextPanel */
  public TextPanel(String name, int x, int y, int width, int height)
  {
    this(name, x, y, width, height,"");
  }

  /** Creates a new TextPanel with the given StringFormatter. */
  public TextPanel(String name, int x, int y, int width, int height, String formatString)
  {
    super(name, x, y, width, height);
    this.formatter = new StringFormatter(formatString);
    this.fontSize = DEFAULT_FONT_SIZE;
    this.color = DEFAULT_COLOR;
  }
  
  /** sets the font size */
  public void setFontSize(int fontSize)
  {
    this.fontSize = fontSize;
  }
  
  /** sets the color */
  public void setColor(Color color)
  {
    this.color = color;
  }

  /** sets the StringFormatter. This will invalidate all values previously set */
  public void setFormat(String format)
  {
    this.formatter = new StringFormatter(format);
  }
  
  /** sets the value of a parameter */
  public void setValue(String param, String value)
  {
    formatter.set(param,value);
  }
  
  /** sets the value of a parameter */
  public void set(String param, int value)
  {
    formatter.set(param,value);
  }
  
  /** sets the value of a parameter */
  public void set(String param, String value)
  {
    formatter.set(param,value);
  }

  /** draws the String */
  public Graphics draw(Graphics g)
  {
    // draw frame/title bar
    Graphics clientArea = super.draw(g);
    
    if (isMinimized())
    {
      // don't draw minimized windows
      return clientArea;
    }
    
    // set font and color
    clientArea.setFont(clientArea.getFont().deriveFont((float) fontSize));
    clientArea.setColor(color);
    
    String text = formatter.toString();
    
    int index;
    int oldIndex = 0;
    int pos = fontSize;
    int lineHeight = (int) (fontSize * 1.2f);
    
    do
    {
      String string;
      index = text.indexOf('\n',oldIndex);
      // get next line from input string
      if (index >= 0)
      {
        string = text.substring(oldIndex, index);
        oldIndex = index+1;
      }
      else
      {
        string = text.substring(oldIndex);
      }

      clientArea.drawString(string, 0, pos);
      // next line
      pos += lineHeight;
    }
    while (index >= 0);

    
    return clientArea;
  }
  
  
  
}
