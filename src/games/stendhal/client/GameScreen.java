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
package games.stendhal.client;

import java.awt.image.BufferStrategy;
import java.awt.*;
import java.awt.geom.*;

public class GameScreen 
  {
  // One unit are 32 pixels 
  public final static int PIXEL_SCALE=32;
  
  private BufferStrategy strategy;
  private Graphics2D g;
  private double x,y;
  private double dx,dy;
  private int sw,sh;  
  private int ww,wh;
  private static GameScreen screen;
  
  public static void createScreen(BufferStrategy strategy, int sw, int sh)
    {
    if(screen==null)
      {
      screen=new GameScreen(strategy,sw,sh);
      }
    }
  
  public static GameScreen get()
    {
    return screen;
    }
  
  public double getWidth()
    {
    return sw/PIXEL_SCALE;
    }

  public double getHeight()
    {
    return sh/PIXEL_SCALE;
    }
   
  private GameScreen(BufferStrategy strategy, int sw, int sh)
    {
    this.strategy=strategy;
    this.sw=sw;
    this.sh=sh;
    x=y=0;
    dx=dy=0;
    g=(Graphics2D)strategy.getDrawGraphics();
    }
  
  public void nextFrame()
    {
    g.dispose();
    strategy.show();
    
    g=(Graphics2D)strategy.getDrawGraphics();
    
    if(x+dx/60.0>=0 && x+dx/60.0+getWidth()<ww)
      {
      x+=dx/60.0;
      }
      
    if(y+dy/60.0>=0 && y+dy/60.0+getHeight()<wh)
      {
      y+=dy/60.0;
      }
    }
  
  public Graphics2D expose()
    {
    return g;
    }
  
  public void move(double dx, double dy)
    {
    this.dx=dx;
    this.dy=dy;
    }
  
  public double getX()
    {
    return x;
    }

  public double getY()
    {
    return y;
    }

  public double getdx()
    {
    return dx;
    }

  public double getdy()
    {
    return dy;
    }

  public void place(double x, double y)
    {
    this.x=x;
    this.y=y;
    }
  
  public void setMaxWorldSize(int width, int height)
    {
    ww=width;
    wh=height;
    }
    
  public void draw(Sprite sprite, double wx, double wy)
    {
    int sx=(int)((wx-x)*32);
    int sy=(int)((wy-y)*32);
    
    if((sx>=-32 && sx<sw) && (sy>=-32 && sy<sh))
      {
      sprite.draw(g,sx,sy);
      }
    }
  }
