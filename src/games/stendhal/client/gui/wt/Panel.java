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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
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
  /** text of the title */
  private String titleText;
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
  /** true when the panel is closeable */
  private boolean closeable;
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
  
  /** all childs of this panel. */
  private LinkedList<Panel> childs;
  /** the parent of this panel */
  private Panel parent;
  /** List of registered CloseListener */
  protected List<CloseListener> closeListeners;
  /** List of registered ClickListener */
  protected List<ClickListener> clickListeners;
  
  /** chaches the titlebar/frame image */
  private BufferedImage cachedImage;
  /** true when the window is closed already */
  private boolean closed;


  
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
    this.titleText   = name;
    this.x           = x;
    this.y           = y;
    this.width       = width;
    this.height      = height;
    this.childs      = new LinkedList<Panel>();
    this.titleBar    = false;
    this.frame       = false;
    this.moveable    = false;
    this.resizeable  = false;
    this.closeable   = true;
    this.closed    = false;
    this.texture = 0;
    this.textureSprites = new ArrayList<Sprite>();
    this.closeListeners = new ArrayList<CloseListener>();
    this.clickListeners = new ArrayList<ClickListener>();

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
  
  /** 
   * Adds a CloseListener to this panel. All registered closelistener are
   * notified before the panel is closed
   */
  public void registerCloseListener(CloseListener listener)
  {
    // window is closed anyway. No more closelisteners
    // Note: this is necessary to avoid ConcurrentModificationExceptions
    if (isClosed())
      return;

    closeListeners.add(listener);
  }
  
  /** removes a (registered) closelistener */
  public void removeCloseListener(CloseListener listener)
  {
    // window is closed anyway and all listeners are notified
    // Note: this is necessary to avoid ConcurrentModificationExceptions
    if (isClosed())
      return;

    closeListeners.remove(listener);
  }
  
  /** 
   * Adds a ClickListener to this panel. All registered ClickListener are
   * notified when user clicks on the panel.
   * Note that not all panels must support/notify this type listener.
   * The default panel for example ignores all click events.
   */
  public void registerClickListener(ClickListener listener)
  {
    clickListeners.add(listener);
  }
  
  /** removes a (registered) ClickListener */
  public void removeClickListener(ClickListener listener)
  {
    clickListeners.remove(listener);
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
    
    int height = 0;
    if (hasTitleBar())
      height += TILLEBAR_SIZE;
    
    if (hasFrame())
      height += FRAME_SIZE;
    
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
  
  /** sets the text in the titlebar */
  public void setTitletext(String text)
  {
    this.titleText = text;
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
  
  /** returns wether the panel is closeable */
  public boolean isCloseable()
  {
    return closeable;
  }

  /** enables/disables closing the panel. Note: the panel must have a
   * title bar */
  public void setCloseable(boolean closeable)
  {
    this.closeable = closeable;
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
  public synchronized void addChild(Panel panel)
  {
    if (panel.hasParent())
    {
      logger.error("Panel "+panel.name+" cannot be added to "+name+" because it already is a child of "+panel.parent.name);
      return;
    }
    LinkedList<Panel> newChilds = new LinkedList<Panel>(childs);
    newChilds.addFirst(panel);
    this.childs = newChilds;
    panel.parent = this;
  }
  
  /** removes a child-panel from this panel */
  public synchronized void removeChild(Panel panel)
  {
    LinkedList<Panel> newChilds = new LinkedList<Panel>(childs);
    newChilds.remove(panel);
    this.childs = newChilds;
    // be sure to remove ourself from the other panel
    panel.parent = null;
  }
  
  /** returns true when the window is scheduled to be closed. */
  protected boolean isClosed()
  {
    return closed;
  }
  
  /** tells this panel (and all subpanels) to close */
  public void close()
  {
    if (closed)
      return;
    
    closed = true;
    
    // tell the childs to close too
    for (Panel child : childs)
    {
      child.close();
    }
    
    // clear the parent
    parent = null;
    
    // inform all listeners we're closed
    for (CloseListener listener : closeListeners)
    {
      listener.onClose(name);
    }
    
  }

  /** notifies all registered clicklisteners that this panel has been clicked */
  protected void notifyClickListeners(String name, boolean pressed)
  {
    for (ClickListener listener : clickListeners)
    {
      listener.onClick(name,pressed);
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
      for (int i = 0; i < FRAME_SIZE; i++)
      {
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
        
        panelGraphics.setColor(Color.BLACK);
        panelGraphics.fillRect(rect.x-FRAME_SIZE+1, rect.y-FRAME_SIZE+rect.height-3, rect.width-2, 2);
      }
      
      if (isCloseable())
      {
        // minimize button
        panelGraphics.setColor(lightColor);
        Rectangle rect = getCloseButton();
        panelGraphics.fillRect(rect.x-FRAME_SIZE, rect.y-FRAME_SIZE, rect.width, rect.height);
        
        panelGraphics.setColor(Color.BLACK);
        panelGraphics.drawLine(rect.x-FRAME_SIZE+1, rect.y-FRAME_SIZE+1, rect.x-FRAME_SIZE+rect.width-2, rect.y-FRAME_SIZE+rect.height-2);
        panelGraphics.drawLine(rect.x-FRAME_SIZE+2, rect.y-FRAME_SIZE+1, rect.x-FRAME_SIZE+rect.width-2, rect.y-FRAME_SIZE+rect.height-3);
        panelGraphics.drawLine(rect.x-FRAME_SIZE+1, rect.y-FRAME_SIZE+2, rect.x-FRAME_SIZE+rect.width-3, rect.y-FRAME_SIZE+rect.height-2);
        panelGraphics.drawLine(rect.x-FRAME_SIZE+rect.width-2  , rect.y-FRAME_SIZE+1  , rect.x-FRAME_SIZE+1  , rect.y-FRAME_SIZE+rect.height-2  );
        panelGraphics.drawLine(rect.x-FRAME_SIZE+rect.width-2-1, rect.y-FRAME_SIZE+1  , rect.x-FRAME_SIZE+1  , rect.y-FRAME_SIZE+rect.height-2-1);
        panelGraphics.drawLine(rect.x-FRAME_SIZE+rect.width-2  , rect.y-FRAME_SIZE+1+1, rect.x-FRAME_SIZE+1+1, rect.y-FRAME_SIZE+rect.height-2  );
      }

      //  the dark line under the title bar
      panelGraphics.setColor(darkColor);
      panelGraphics.drawLine(0,TILLEBAR_SIZE,width-(FRAME_SIZE*2), TILLEBAR_SIZE);
      panelGraphics.drawLine(0,TILLEBAR_SIZE+1,width-(FRAME_SIZE*2), TILLEBAR_SIZE+1);
      
      // panels title text
      panelGraphics.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));
      Font font = panelGraphics.getFont();
      panelGraphics.setFont(font.deriveFont(Font.BOLD, (float) TILLEBAR_FONT_SIZE));
      panelGraphics.drawString(titleText, 3,TILLEBAR_FONT_SIZE);

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
    // first scan for closed panels and remove them
    checkClosed();
    // are we closed too? then don't draw anything
    if (isClosed())
      return g;
    
    
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
  
  /** panels draw themselves */
  public void drawDragged(Graphics g)
  { }
  
  
  /** draws all childs
   * @param clientArea Graphics object clipped to the client region.
   */
  protected void drawChilds(Graphics clientArea)
  {
    for (int i = 0; i < childs.size(); i++)
    {
      childs.get(childs.size()-i-1).draw(clientArea);
    }
  }
  
  /** scans the child list for closed ones and removes them */
  private void checkClosed()
  {
    // remove all closed childs
    for (Iterator<Panel> childIt = childs.iterator(); childIt.hasNext();)
    {
      Panel child = childIt.next();
      if (child.isClosed())
      {
        childIt.remove();
      }
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
    int height = this.height;
    int width = this.width;
    
    if (isMinimized())
    {
      height = TILLEBAR_SIZE + (frame ? FRAME_SIZE*2 : 0);
    }
    
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
          // activate the panel
          focus(panel);
          return draggedObject;
        }
        
        // this child don't want to be dragged. Ignore all childs below this one
        return null;
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
  
  /** returns the rectangle for the close button */
  private Rectangle getCloseButton()
  {
    return new Rectangle(width-TILLEBAR_SIZE-FRAME_SIZE, FRAME_SIZE+1, TILLEBAR_SIZE-2,TILLEBAR_SIZE-2);
  }
  
  /** returns true when the point (x,y) is inside the close button */
  private boolean hitCloseButton(int x, int y)
  {
    return getCloseButton().contains(x,y);
  }
  
  /** callback for a mouse click. returns true when the click has been 
   * processed */
  public synchronized boolean onMouseClick(Point p)
  {
    // check if the minimize button has been clicked
    if (titleBar && minimizeable && hitMinimizeButton(p.x,  p.y))
    {
      // change minimized state
      setMinimized(!isMinimized());
      return true;
    }
    
    // check if the close button has been clicked
    if (titleBar && closeable && hitCloseButton(p.x,  p.y))
    {
      // close the window
      close();
      return true;
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
        focus(panel);
        Point point = p2.getLocation();
        point.translate(-panel.getX(), -panel.getY());
        // click the child
        panel.onMouseClick(point);
        
        // bail out when we're closed during a callback
        if (isClosed())
          break;
        
        // click processed
        return true;
      }
    }
    // click not processed
    return false;
  }
  
  /** callback for a doubleclick */
  public synchronized void onMouseDoubleClick(Point p)
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

  /** moves the child panel on top of all others */
  private void focus(Panel child)
  {
    if (!childs.remove(child))
      return;

    childs.addFirst(child);
  }
  
  
  /** toString */
  public String toString()
  {
    return super.toString()+": "+name+" at "+x+"x"+y+" size:"+width+"x"+height;
  }
}
