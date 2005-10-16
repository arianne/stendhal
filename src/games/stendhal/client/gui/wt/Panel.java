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
 * Panel.java
 * Created on 16. Oktober 2005, 10:55
 */
package games.stendhal.client.gui.wt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;

/**
 * Base class for all kinds of panels/windows/buttons.
 * <p>
 * Panels
 * <ul>
 *  <li>can have other panels as childs</li>
 *  <li>can have a border and a title bar. If they have a title bar they
 *   <ul>
 *    <li>can be moved</li>
 *    <li>can can be minimized (reduces the panel to the title bar)</li>
 *   </ul>
 *  </li>
 *  </ul>
 * <b>Note:</b> This class is not thread safe.
 *
 * TODO: try to cache the static panel content in BufferdImage
 * @author mtotz
 */
public class Panel
{
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Panel.class);

  /** size of the titlebar */
  private static final int TILLEBAR_SIZE = 14;
  /** size of the titlebar font */
  private static final int TILLEBAR_FONT_SIZE = 12;
  /** thickness of the frame */
  private static final int FRAME_SIZE = 5;

  /** panel has a title bar */
  private boolean titleBar;
  /** panel has a frame */
  private boolean frame;
  /** panel is moveable */
  private boolean moveable;
  /** panel can be resized */
  private boolean resizeable;
  /** x-position relative to its parent */
  private int x;
  /** y-position relative to its parent */
  private int y;
  /** width of the panel inclusive frames and title bar */
  private int width;
  /** height of the panel inclusive frames and title bar */
  private int height;
  /** name of the panel */
  private String name;
  
  /** all childs of this panel */
  private List<Panel> childs;
  /** the parent of this panel */
  private Panel parent;

  /**
   * Creates a new panel. The panel is not moveable or resizeable and has no
   * title bar or frame;
   */
  public Panel(String name, int x, int y, int width, int height)
  {
    this.name        = name;
    this.x           = x;
    this.y           = y;
    this.width       = width;
    this.height      = height;
    this.childs      = new ArrayList<Panel>();
    this.titleBar    = false;
    this.frame       = false;
    this.moveable    = false;
    this.resizeable  = false;
  }

  /** returns wether the panel has a title bar */
  public boolean hasTitleBar()
  {
    return titleBar;
  }

  /** enables/disables the title bar */
  public void setTitleBar(boolean titleBar)
  {
    this.titleBar = titleBar;
  }

  /** returns wether the panel has a frame */
  public boolean hasFrame()
  {
    return frame;
  }

  /** enables/disables the frame */
  public void setFrame(boolean frame)
  {
    this.frame = frame;
  }

  /** returns wether the panel is moveable */
  public boolean isMoveable()
  {
    return moveable;
  }

  /** enables/disables moving the panel. Note: the panel must have a title bar
   * to be moveable */
  public void setMoveable(boolean moveable)
  {
    this.moveable = moveable;
  }

  /** returns wether the panel is resizeable */
  public boolean isResizeable()
  {
    return resizeable;
  }

  /** enables/disables resizing the panel. Note: the panel must have a frame */
  public void setResizeable(boolean resizeable)
  {
    this.resizeable = resizeable;
  }
  
  /** returns the parent of the panel */
  public Panel getParent()
  {
    return parent;
  }

  /** returns wether the panel has a parent */
  public boolean hasParent()
  {
    return (parent != null);
  }

  /** adds a child-panel to this panel */
  public void addChild(Panel panel)
  {
    if (panel.hasParent())
    {
      logger.error("Panel "+panel.name+" cannot be added to "+name+" because it already is a child of "+panel.parent.name);
      return;
    }
    childs.add(panel);
    panel.parent = this;
  }
  
  /** resizes the panel so that the client area has the given width and height
   * @param width width of client area
   * @param height height of client area
   */
  public void resizeToFitClientArea(int width, int height)
  {
    this.width = width;
    this.height = height;
    
    // adjust size to include the frame
    if (frame)
    {
      this.width  += FRAME_SIZE * 2;
      this.height += FRAME_SIZE * 2;
    }
    
    // adjust size to include the title bar
    if (titleBar)
    {
      this.height += TILLEBAR_SIZE;
    }
    
  }
  
  /** draws the panel into the graphics object
   * @param g graphics where to render to
   * @return a graphics object for deriving classes to use. It is already 
   *         clipped to the correct client region
   */
  public Graphics draw(Graphics g)
  {
    // get correct clipped graphics
    Graphics panelGraphics = g.create(x,y,width, height);
    // draw frame
    if (frame)
    {
      int colSteps = 255 / (FRAME_SIZE);
      for (int i = 0; i < FRAME_SIZE; i++)
      {
        int col = colSteps * i;
        panelGraphics.setColor(new Color(col,col,col));
        panelGraphics.drawRect(i, i,width-(i*2)-1,height-(i*2)-1);
      }
      // update clipping to exclude the frame
      panelGraphics = panelGraphics.create(FRAME_SIZE,FRAME_SIZE,width-(FRAME_SIZE*2), height-(FRAME_SIZE*2));
    }
      
    // draw title bar
    if (titleBar)
    {
      panelGraphics.drawLine(0,TILLEBAR_SIZE,width-(FRAME_SIZE*2), TILLEBAR_SIZE);
      panelGraphics.drawLine(0,TILLEBAR_SIZE+1,width-(FRAME_SIZE*2), TILLEBAR_SIZE+1);

      panelGraphics.setColor(Color.BLUE);
      panelGraphics.fillRect(0,0,width-(FRAME_SIZE*2), TILLEBAR_SIZE);

      panelGraphics.setColor(Color.WHITE);
      Font font = panelGraphics.getFont();
      panelGraphics.setFont(font.deriveFont(Font.BOLD, (float) TILLEBAR_FONT_SIZE));
      panelGraphics.drawString(name, 3,TILLEBAR_FONT_SIZE);

      // update clipping
      panelGraphics = panelGraphics.create(0,TILLEBAR_SIZE+2, width-(FRAME_SIZE*2), height-(FRAME_SIZE*2)-TILLEBAR_SIZE-2);
    }

    // now draw the childs
    for (Panel panel : childs)
    {
      panel.draw(panelGraphics);
    }
    return panelGraphics;
  }
}
