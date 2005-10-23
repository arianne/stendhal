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
 * Panel.java
 * Created on 16. Oktober 2005, 10:55
 */
package games.stendhal.client.gui.wt;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.common.Debug;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
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
 *
 * @see http://www.grsites.com/ for the textures
 * @author mtotz
 */
public class Panel implements Draggable
{
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Panel.class);

  /** size of the titlebar */
  private static final int TILLEBAR_SIZE = 14;
  /** size of the titlebar font */
  private static final int TILLEBAR_FONT_SIZE = 12;
  /** thickness of the frame */
  private static final int FRAME_SIZE = 3;

  /** panel has a title bar */
  private boolean titleBar;
  /** panel has a frame */
  private boolean frame;
  /** is the frame embossed? */
  private boolean frameEmbossed;
  /** panel is moveable */
  private boolean moveable;
  /** panel can be resized */
  private boolean resizeable;
  /** panel can be minimized */
  private boolean minimizeable;
  /** true when the panel is minimized */
  private boolean minimized;
  /** x-position relative to its parent */
  private int x;
  /** y-position relative to its parent */
  private int y;
  /** the point where we were before the drag started */
  private Point dragPosition;
  /** width of the panel inclusive frames and title bar */
  private int width;
  /** height of the panel inclusive frames and title bar */
  private int height;
  /** name of the panel */
  private String name;
  
  /** all childs of this panel. TODO: sort this by z-order */
  private List<Panel> childs;
  /** the parent of this panel */
  private Panel parent;
  
  /** chaches the titlebar/frame image */
  private BufferedImage cachedImage;

  
  /////////////////
  // Debug stuff //
  /////////////////

  /** current texture */
  private int texture;
  /** list of textures */
  private List<Sprite> textureSprites;

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
    texture = 0;
    textureSprites = new ArrayList<Sprite>();

    // get texture sprite
    SpriteStore st = SpriteStore.get();

    textureSprites.add(st.getSprite("data/panelwood003.jpg"));
    if (Debug.CYCLE_PANEL_TEXTURES)
    {
      textureSprites.add(st.getSprite("data/panelwood006.jpg"));
      textureSprites.add(st.getSprite("data/panelwood032.gif"));
      textureSprites.add(st.getSprite("data/panelwood119.jpg"));
      textureSprites.add(st.getSprite("data/paneldrock009.jpg"));
      textureSprites.add(st.getSprite("data/paneldrock048.jpg"));
      textureSprites.add(st.getSprite("data/panelmetal003.gif"));
    }
  }

  /** returns x-position of the panel (relative to its parent) */
  public int getX()
  {
    return x;
  }

  /** returns y-position of the panel (relative to its parent) */
  public int getY()
  {
    return y;
  }

  /** returns width of the panel */
  public int getWidth()
  {
    return width;
  }

  /** returns height of the panel */
  public int getHeight()
  {
    return height;
  }
  
  /** returns width of the client area */
  protected int getClientWidth()
  {
    return (frame ? width - FRAME_SIZE*2 : width);
  }

  /** returns height of the panel */
  protected int getClientHeight()
  {
    int clientHeight = height;
    if (frame)
      clientHeight  -= FRAME_SIZE*2;
    
    if (titleBar)
      clientHeight  -= TILLEBAR_SIZE;

    return clientHeight;
  }
  
  /** returns x-pos of the client area */
  protected int getClientX()
  {
    return (frame ? FRAME_SIZE : 0);
  }

  /** returns y-pos of the client area */
  protected int getClientY()
  {
    int clienty = (frame ? FRAME_SIZE : 0);

    if (titleBar)
      clienty += TILLEBAR_SIZE;
    

    return clienty;
  }
  
  /**
   * Moves the panel by dx pixels to the right and dy pixels down.
   *
   * @param dx amount of pixels to move rights ( < 0 is allowed, will move left)
   * @param dy amount of pixels to move down ( < 0 is allowed, will move up)
   * @return true when the operation is allowed (panel is moved) or false if not
   *             (panel is not moved)
   */
  public boolean move(int dx, int dy)
  {
    return moveTo(x+dx,y+dy);
  }

  /**
   * Moves the panel to the given position.
   *
   * @param x x-coordinale
   * @param y y-coordinmate
   * @return true when the operation is allowed (panel is moved) or false if not
   *             (panel is not moved)
   */
  public boolean moveTo(int x, int y)
  {
    this.x = x;
    this.y = y;
    
    // check if we are inside the bounds of our parent
    if (x < 0)
      this.x = 0;
    
    if (hasParent() && parent.getWidth() - width < x)
      this.x = parent.getWidth() - width;

    if (y < 0)
      this.y = 0;
    
    if (hasParent() && parent.getHeight() - height < y)
      this.y = parent.getHeight() - height;

    return true;
  }
  
  /** sets the name */
  public void setName(String name)
  {
    this.name = name;
  }
  
  /** returns the name */
  public String getName()
  {
    return name;
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
    // refresh cached panel image
    cachedImage = null;
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
    // refresh cached panel image
    cachedImage = null;
  }

  /** returns wether the panel is moveable */
  public boolean isMoveable()
  {
    return moveable;
  }

  /** sets the embossed-state of then frame */
  public void setEmboss(boolean emboss)
  {
    this.frameEmbossed = emboss;
    cachedImage = null;
  }

  /** returns wether the panels frame is embossed */
  public boolean isEmbossed()
  {
    return frameEmbossed;
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
  
  /** returns wether the panel is minimizeable */
  public boolean isMinimizeable()
  {
    return minimizeable;
  }

  /** enables/disables minimizing the panel. Note: the panel must have a
   * title bar */
  public void setMinimizeable(boolean minimizeable)
  {
    this.minimizeable = minimizeable;
  }

  /** returns whether the panel is minimized */
  public boolean isMinimized()
  {
    return minimized;
  }

  /** sets the minimized state */
  public void setMinimized(boolean minimized)
  {
    this.minimized = minimized;
    // refresh cached panel image
    cachedImage = null;
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
  
  /** removes a child-panel from this panel */
  public void removeChild(Panel panel)
  {
    if (childs.remove(panel))
    {
      // be sure to remove ourself from the other panel
      panel.parent = null;
    }
  }

  /** returns an unmodifiable list this panels childs.
   * TODO: cache this
   */
  protected List<Panel> getChilds()
  {
    return Collections.unmodifiableList(childs);
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
    // refresh cached panel image
    cachedImage = null;
  }
  
  /** creates the image background as an image */
  private BufferedImage recreatePanelImage(Graphics g)
  {
    int localHeight = this.height;

    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    BufferedImage tempImage = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    Graphics panelGraphics = tempImage.createGraphics();

    // if this frame is minimized, reduce frame to enclose the title bar only
    if (isMinimized())
    {
      localHeight = TILLEBAR_SIZE+FRAME_SIZE*2;
    }
    
    // get texture sprite
    Sprite woodTexture = textureSprites.get(texture);

    int repeatx = width / woodTexture.getWidth() + 1;
    int repeaty = height / woodTexture.getHeight() + 1;
    
    for (int x = 0; x < repeatx; x++)
    {
      for (int y = 0; y < repeaty; y++)
      {
        woodTexture.draw(panelGraphics, x*woodTexture.getWidth(), y * woodTexture.getHeight());
      }
    }
    
    Color darkColor = new Color(0.0f, 0.0f, 0.0f, 0.5f);
    Color lightColor = new Color(1.0f, 1.0f, 1.0f, 0.5f);

    // draw frame
    if (frame)
    {
      int colSteps = 255 / (FRAME_SIZE);
      for (int i = 0; i < FRAME_SIZE; i++)
      {
        int col = colSteps * i;
        panelGraphics.setColor(frameEmbossed ? darkColor : lightColor);
        panelGraphics.drawLine(i, i, width-i-2, i);
        panelGraphics.drawLine(i, i, i, localHeight-i-2);
        
        panelGraphics.setColor(frameEmbossed ? lightColor : darkColor);
        panelGraphics.drawLine(width-i-1, i              , width-i-1, localHeight-i-1);
        panelGraphics.drawLine(      i  , localHeight-i-1, width-i-1, localHeight-i-1);
      }
      // update clipping to exclude the frame
      panelGraphics = panelGraphics.create(FRAME_SIZE,FRAME_SIZE,width-(FRAME_SIZE*2), localHeight-(FRAME_SIZE*2));
    }

    // draw title bar
    if (titleBar)
    {
      if (isMinimizeable())
      {
        // minimize button
        panelGraphics.setColor(lightColor);
        Rectangle rect = getMiminizeButton();
        panelGraphics.fillRect(rect.x-FRAME_SIZE, rect.y-FRAME_SIZE, rect.width, rect.height);
      }
      
      //  the dark line under the title bar
      panelGraphics.setColor(darkColor);
      panelGraphics.drawLine(0,TILLEBAR_SIZE,width-(FRAME_SIZE*2), TILLEBAR_SIZE);
      panelGraphics.drawLine(0,TILLEBAR_SIZE+1,width-(FRAME_SIZE*2), TILLEBAR_SIZE+1);
      
      // panels name
      panelGraphics.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));
      Font font = panelGraphics.getFont();
      panelGraphics.setFont(font.deriveFont(Font.BOLD, (float) TILLEBAR_FONT_SIZE));
      panelGraphics.drawString(name, 3,TILLEBAR_FONT_SIZE);

      // update clipping
      panelGraphics = panelGraphics.create(0,TILLEBAR_SIZE+2, width-(FRAME_SIZE*2), height-(FRAME_SIZE*2)-TILLEBAR_SIZE-2);
    }

    BufferedImage image = gc.createCompatibleImage(width, localHeight);
    image.createGraphics().drawImage(tempImage,0,0,null);

    return image;
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
    
    // only draw something when we have a title bar or a frame
    if (frame || titleBar)
    {
      BufferedImage image = cachedImage;
      // (re)create the image if it does not exist
      if (image == null)
      {
        image  = recreatePanelImage(g);
        cachedImage = image;
      }
      panelGraphics.drawImage(image,0,0,null);
    }
      
    if (frame)
      panelGraphics = panelGraphics.create(FRAME_SIZE,FRAME_SIZE,width-(FRAME_SIZE*2), height-(FRAME_SIZE*2));
    if (titleBar)
      panelGraphics = panelGraphics.create(0,TILLEBAR_SIZE+2, width-(FRAME_SIZE*2), height-(FRAME_SIZE*2)-TILLEBAR_SIZE-2);

    if (!minimized)
    {
      // now draw the childs
      drawChilds(panelGraphics);
    }

    return panelGraphics;
  }
  
  /** draws all childs
   * @param clientArea Graphics object clipped to the client region.
   */
  protected void drawChilds(Graphics clientArea)
  {
    for (Panel panel : childs)
    {
      panel.draw(clientArea);
    }
  }
  
  /** 
   * Checks if the Point p is inside the Panel. Note that the coordinates are
   * local to the parent, not local to this Panel.
   *
   * @param p point to check (in parents coordinate space)
   * @return true when the point is in this panel, false otherwise
   */
  public boolean isHit(Point p)
  {
    return isHit(p.x, p.y);
  }
  
  /** Checks if the point is inside the Panel. Note that the coordinates are
   * local to the parent, not local to this Panel.
   *
   * @param x x-coordinate to check (in parents coordinate space)
   * @param y y-coordinate to check (in parents coordinate space)
   * @return true when the point is in this panel, false otherwise
   */
  public boolean isHit(int x, int y)
  {
    if (x < this.x || y < this.y || x > this.x + width || y > this.y + height)
      return false;
    return true;
  }
  
  /** return true if the point is in the title */
  private boolean hitTitle(int x,int y)
  {
    // do we have a title
    if (!titleBar)
      return false;
    
    // 
    if (x < FRAME_SIZE || y < FRAME_SIZE || x > width-FRAME_SIZE || y > FRAME_SIZE+TILLEBAR_SIZE)
      return false;
    
    return true;
  }
  
  /** return a object for dragging which is at the position p or null */
  protected Draggable getDragged(Point p)
  {
    return getDragged(p.x,p.y);
  }
  
  /** return a object for dragging which is at the position (x,y) or null */
  protected Draggable getDragged(int x, int y)
  {
    // if the user drags our titlebar we return ourself
    if (hitTitle(x,y))
      return this;

    // translate point to client coordinates
    x -= getClientX();
    y -= getClientY();

    // check all childs
    for (Panel panel : childs)
    {
      // only if the point is inside the child
      if (panel.isHit(x,y))
      {
        Draggable draggedObject = panel.getDragged(x - panel.getX(), y-panel.getY());

        // did we get an object
        if (draggedObject != null)
        {
          return draggedObject;
        }
      }
    }

    // no more dragging allowed
    return null;
  }
  
  /** 
   * checks if there is a droptarget direct under the position (x,y) 
   * @param x x-coordinate in client space
   * @param y y-coordinate in client space
   * @param droppedObject the dropped object
   * @return true when this panel or a child panel is a droptarget and has
   *         received the object, false when there is no droptarget found
   */
  protected boolean checkDropped(int x, int y, Draggable droppedObject)
  {
    // are we ourself a drop target
    if (this instanceof DropTarget)
    {
      // yep, so cast ourself to the interface, call the callback and return
      DropTarget target = (DropTarget) this;
      target.onDrop(droppedObject);
      return true;
    }

    // translate point to client coordinates
    x -= getClientX();
    y -= getClientY();

    // now ask each child
    for (Panel panel : childs)
    {
      // only if the point is inside the child
      if (panel.isHit(x,y))
      {
        // the child checks itself
        if (panel.checkDropped(x - panel.getX(), y-panel.getY(), droppedObject))
          return true;
      }
    }
    // no drop target found
    return false;
  }
  
  /** returns the rectangle for the minimize button */
  private Rectangle getMiminizeButton()
  {
    return new Rectangle(width-(TILLEBAR_SIZE*2)-FRAME_SIZE, FRAME_SIZE+1, TILLEBAR_SIZE-2,TILLEBAR_SIZE-2);
  }
  
  /** returns true when the point (x,y) is inside the minimize button */
  private boolean hitMinimizeButton(int x, int y)
  {
    return getMiminizeButton().contains(x,y);
  }
  
  
  /** callback for a mouse click. returns true when the click has been 
   * processed */
  public boolean onMouseClick(Point p)
  {
    if (hitMinimizeButton(p.x,  p.y))
    {
      // is have a have a title bar and are minimizeable
      if (titleBar && minimizeable)
      {
        // change minimized state
        setMinimized(!minimized);
        return true;
      }
    }

    if (Debug.CYCLE_PANEL_TEXTURES && hitTitle(p.x,  p.y))
    {
      texture = (texture + 1) % textureSprites.size();
      cachedImage = null;
    }
    
    // translate point to client coordinates
    Point p2 = p.getLocation();
    p2.translate(-getClientX(), -getClientY());

    // be sure to inform all childs of the mouse click
    for (Panel panel : childs)
    {
      // only if the point is inside the child
      if (panel.isHit(p2.x,p2.y))
      {
        Point point = p2.getLocation();
        point.translate(-panel.getX(), -panel.getY());
        // click the child
        if (panel.onMouseClick(point))
          return true;
      }
    }
    // click not processed
    return false;
  }
  
  /** callback for a doubleclick */
  public void onMouseDoubleClick(Point p)
  {
  }
  
  /** ignored */
  public boolean dragStarted()
  {
    dragPosition = new Point(x,y);
    return true;
  }
  
  /** ignored */
  public boolean dragFinished(Point p)
  {
    return false;
  }

  /** move the frame to the requested position */
  public boolean dragMoved(Point p)
  {
    return moveTo(dragPosition.x+p.x, dragPosition.y+p.y);
  }

}
