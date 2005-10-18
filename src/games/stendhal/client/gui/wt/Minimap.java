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
 * Minimap.java
 * Created on 16. Oktober 2005, 13:34
 */
package games.stendhal.client.gui.wt;

import games.stendhal.common.CollisionDetection;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;

/**
 * The minimap.
 *
 * @author mtotz
 */
public class Minimap extends Panel
{
  /** width of the minimap */
  private static final int MINIMAP_WIDTH = 129;
  /** height of the minimap */
  private static final int MINIMAP_HEIGTH = 129;
  /** minimum scale of the minimap */
  private static final int MINIMAP_MINIMUM_SCALE = 2;
  
  
  /** the area this map is for */
  private String area;
  /** scale of map */
  private int scale;
  /** width of (scaled) minimap */
  private int width;
  /** height of (scaled) minimap */
  private int height;
  /** minimap image */
  private BufferedImage image;
  /** x-position of the player */
  private double playerX;
  /** y-position of the player */
  private double playerY;
  
  
  /** Creates a new instance of Minimap */
  public Minimap(CollisionDetection cd, GraphicsConfiguration gc, String zone)
  {
    super("Minimap "+zone, 0, 0, 100, 100);
    
    // calculate size and scale
    int w = cd. getWidth();
    int h = cd.getHeight();

    // calculate scale
    scale = MINIMAP_MINIMUM_SCALE;
    while ((w * (scale+1) < MINIMAP_WIDTH) && (h * (scale+1) < MINIMAP_HEIGTH))
    {
      scale++;
    }
      
    // calculate size of map
    width  = (w * scale < MINIMAP_WIDTH) ? w * scale : MINIMAP_WIDTH;
    height = (h * scale < MINIMAP_HEIGTH) ? h * scale : MINIMAP_HEIGTH;

    // create the image for the minimap
    image = gc.createCompatibleImage(w*scale, h*scale);
    Graphics2D mapgrapics = image.createGraphics();
    Color freeColor    = new Color(0.8f, 0.8f, 0.8f);
//    Color freeColor    = new Color(0.0f, 1.0f, 0.0f);      
    Color blockedColor = new Color(1.0f, 0.0f, 0.0f);
    for (int x = 0; x < w; x++)
    {
      for (int y = 0; y < h; y++)
      {
          boolean walkable = cd.walkable((double) x, (double) y);
          mapgrapics.setColor(walkable ? freeColor : blockedColor);
          mapgrapics.fillRect(x*scale, y*scale, scale, scale);
      }
    }
    
    setTitleBar(true);
    setFrame(true);
    setMoveable(true);
    // now resize the panel to match the size of the map
    resizeToFitClientArea(width, height);
  }

  /** Draws the minimap.
   * @param g graphics object for the game main window
   * @param x x-position of the player (used to pan big maps)
   * @patam y y-position of the player (used to pan big maps)
   */
  public Graphics draw(Graphics g)
  {
    // draw frame and title
    Graphics clientg = super.draw(g);

    // now calculate how to pan the minimap
    int panx = 0;
    int pany = 0;

    int w = image.getWidth();
    int h = image.getHeight();

    int xpos = (int) (playerX * scale) - width / 2;
    int ypos = (int) (playerY * scale) - width / 2;

    if (w > width)
    {
      // need to pan width
      if ((xpos + width) > w)
      {
        // x is at the screen border
        panx = w - width;
      }
      else if (xpos > 0)
      {
        panx = xpos;
      }
    }

    if (h > height)
    {
      // need to pan height
      if ((ypos + height) > h)
      {
        // y is at the screen border
        pany = h - height;
      }
      else if (ypos > 0)
      {
        pany = ypos;
      }
    }

    // draw minimap
    clientg.drawImage(image,-panx, -pany,null);


    Color playerColor = Color.BLUE;
    // draw the player position
    drawCross(clientg,(int) (playerX*scale)-panx, (int) (playerY*scale)-pany, playerColor);

    return g;
  }

  /** draws a cross at the given position */
  private void drawCross(Graphics g, int x, int y, Color color)
  {
    int size = 2;
    g.setColor(color);
    g.drawLine(x-size,y, x+size,y);
    g.drawLine(x,y+size, x,y-size);
  }
  
  public void setPlayerPos(double x, double y)
  {
    playerX = x;
    playerY = y;
  }
}
