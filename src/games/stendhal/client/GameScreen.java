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

import marauroa.common.*;

/** This class is an abstraction of the game screen, so that we can think of it as
 *  a window to the world, we can move it, place it and draw object usings World 
 *  coordinates.
 *  This class is based on the singleton pattern. */
public class GameScreen 
  {
  /** One unit is 32 pixels */
  public final static int PIXEL_SCALE=32;
  
  private BufferStrategy strategy;
  private Graphics2D g;
  
  /** Actual rendering position of the leftmost top corner in world units*/
  private double x,y;
  /** Actual speed of the screen */
  private double dx,dy;
  /** Actual size of the screen in pixels */
  private int sw,sh;  
  /** Actual size of the world in world units */
  private int ww,wh;
  
  private static GameScreen screen;
  
  /** Create a screen with the given width and height */
  public static void createScreen(BufferStrategy strategy, int sw, int sh)
    {
    if(screen==null)
      {
      screen=new GameScreen(strategy,sw,sh);
      }
    }
  
  /** Returns the GameScreen object */
  public static GameScreen get()
    {
    return screen;
    }
  
  /** Returns screen width in world units */
  public double getWidth()
    {
    return sw/PIXEL_SCALE;
    }

  /** Returns screen height in world units */
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
  
  /** Prepare screen for the next frame to be rendered and move it if needed */
  public void nextFrame()
    {
    Logger.trace("GameScreen::nextFrame",">");
    
    g.dispose();
    strategy.show();
    
    g=(Graphics2D)strategy.getDrawGraphics();
    
    if(((x+dx/60.0>=0) && dx<0) || ((x+dx/60.0+getWidth()<ww) && dx>0))
      {
      x+=dx/60.0;
      }
    else
      {
      dx=0;
      }
      
    if((y+dy/60.0>=0 && dy<0) || (y+dy/60.0+getHeight()<wh && dy>0))
      {
      y+=dy/60.0;
      }
    else
      {
      dy=0;
      }
      
    Logger.trace("GameScreen::nextFrame","<");
    }
  
  /** Returns the Graphics2D object in case you want to operate it directly.
   *  Ex. GUI */
  public Graphics2D expose()
    {
    return g;
    }
  
  /** Indicate the screen windows to move at a dx,dy speed. */
  public void move(double dx, double dy)
    {
    this.dx=dx;
    this.dy=dy;
    }
  
  /** Returns the x rendering coordinate in world units */
  public double getX()
    {
    return x;
    }

  /** Returns the y rendering coordinate in world units */
  public double getY()
    {
    return y;
    }

  /** Returns the x speed of the movement */
  public double getdx()
    {
    return dx;
    }

  /** Returns the y speed of the movement */
  public double getdy()
    {
    return dy;
    }

  /** Place the screen at the x,y position of world in world units. */
  public void place(double x, double y)
    {
    this.x=x;
    this.y=y;
    }
  
  /** Sets the world size */
  public void setMaxWorldSize(int width, int height)
    {
    ww=width;
    wh=height;
    }
  
  public Point2D translate(Point2D point)
    {
    double tx=point.getX()/32f+x;
    double ty=point.getY()/32f+y;
    return new Point.Double(tx,ty);
    }
    
  /** Draw a sprite in screen given its world coordinates */
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
