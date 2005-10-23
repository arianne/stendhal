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
 * Button.java
 *
 * Created on 23. Oktober 2005, 09:47
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.Sprite;
import java.awt.Graphics;
import java.awt.Point;

/**
 * A simple button. It features a onClick() callback.
 * A button can have an image <b>or</b> some text, but not both.
 *
 * @author mtotz
 */
public class Button extends Panel
{
  /** size of the button-frame */
  private static final int FRAME_SIZE = 3;
  
  /** text for the button */
  private String text;
  /** image for the button */
  private Sprite image;
  
  /** */
  private Point dragPosition;
  
  /** Creates a new Button with text */
  public Button(String name, int width, int height, String text)
  {
    super(name, 0, 0, width, height);
    setMinimizeable(false);
    setTitleBar(false);
    setFrame(true);
    int clientHeight = (getClientHeight()-TextPanel.DEFAULT_FONT_SIZE)/2;
    TextPanel textPanel = new TextPanel(name+"text", 2,clientHeight, width, height, text);
    addChild(textPanel);
  }
  
  /** Creates a new Button with an image */
  public Button(String name, int width, int height, Sprite image)
  {
    super(name, 0, 0, width, height);
    this.image = image;
    setMinimizeable(false);
    setTitleBar(false);
    setFrame(true);
  }
  
  
  /** draws the button */
  public Graphics draw(Graphics g)
  {
    Graphics clientArea = super.draw(g);
    
    // draw the image if we have one
    if (image != null)
    {
      image.draw(clientArea,0,0);
    }
    
    return clientArea;
  }

  public boolean onMouseClick(Point p)
  {
    setEmboss(!isEmbossed());
    return true;
  }
  
  
}
