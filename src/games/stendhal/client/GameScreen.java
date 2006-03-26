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

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;

/** This class is an abstraction of the game screen, so that we can think of it as
 *  a window to the world, we can move it, place it and draw object usings World 
 *  coordinates.
 *  This class is based on the singleton pattern. */
public class GameScreen 
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(GameScreen.class);
  
  /** One unit is 16 pixels */
  public final static int SIZE_UNIT_PIXELS=32;
  
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
  /** the singleton instance */
  private static GameScreen screen;
  /** the awt-component which this screen belongs to */
  private Component component;
  
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
  
  /** sets the awt-component which this screen belongs to */
  public void setComponent(Component component)
    {
    this.component= component;
    }
  
  /** returns the awt-component which this screen belongs to */
  public Component getComponent()
    {
    return component;
    }
  
  /** Returns screen width in world units */
  public double getWidth()
    {
    return sw/SIZE_UNIT_PIXELS;
    }

  /** Returns screen height in world units */
  public double getHeight()
    {
    return sh/SIZE_UNIT_PIXELS;
    }

  /** Returns screen width in pixels */
  public int getWidthInPixels()
    {
    return sw;
    }

  /** Returns screen height in pixels */
  public int getHeightInPixels()
    {
    return sh;
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
    Log4J.startMethod(logger,"nextFrame");
    
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
      
    Log4J.finishMethod(logger,"nextFrame");
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
  
  /** Translate to world coordinates the given screen coordinate */
  public Point2D translate(Point2D point)
    {
    double tx=point.getX()/(float)GameScreen.SIZE_UNIT_PIXELS+x;
    double ty=point.getY()/(float)GameScreen.SIZE_UNIT_PIXELS+y;
    return new Point.Double(tx,ty);
    }

  /** Translate to screen coordinates the given world coordinate */
  public Point2D invtranslate(Point2D point)
    {
    double tx=(point.getX()-x)*(float)GameScreen.SIZE_UNIT_PIXELS;
    double ty=(point.getY()-y)*(float)GameScreen.SIZE_UNIT_PIXELS;
    return new Point.Double(tx,ty);
    }
    
  /** Draw a sprite in screen given its world coordinates */
  public void draw(Sprite sprite, double wx, double wy)
    {
    int sx=(int)((wx-x)*(float)GameScreen.SIZE_UNIT_PIXELS);
    int sy=(int)((wy-y)*(float)GameScreen.SIZE_UNIT_PIXELS);
    
    float spritew=sprite.getWidth()+2;
    float spriteh=sprite.getHeight()+2;
    
    if((sx>=-spritew && sx<sw) && (sy>=-spriteh && sy<sh))
      {
      sprite.draw(g,sx,sy);
      }
    }

  public void drawInScreen(Sprite sprite, int sx, int sy)
    {
    sprite.draw(g,sx,sy);
    }
  
  public Sprite createString(String text, Color textColor)
    {
    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    Image image = gc.createCompatibleImage(g.getFontMetrics().stringWidth(text)+2,16,Transparency.BITMASK);    
    Graphics g2d=image.getGraphics();

    g2d.setColor(Color.black);
    g2d.drawString(text,0,9);
    g2d.drawString(text,0,11);
    g2d.drawString(text,2,9);
    g2d.drawString(text,2,11);

    g2d.setColor(textColor);
    g2d.drawString(text,1,10);
    return new Sprite(image);      
    }
    
  private int positionStringOfSize(String text, int width)
    {
    String[] words=text.split(" ");
    
    int i=1;
    String textUnderWidth=words[0];
    
    while(i<words.length && g.getFontMetrics().stringWidth(textUnderWidth+" "+words[i])<width)
      {
      textUnderWidth=textUnderWidth+" "+words[i];
      i++;
      }
    
    if(textUnderWidth.length()==0 && words.length>1)
      {
      textUnderWidth=words[1];
      }
      
    if(g.getFontMetrics().stringWidth(textUnderWidth)>width)
      {
      return (int)((float)width/(float)g.getFontMetrics().stringWidth(textUnderWidth)*textUnderWidth.length());
      }
    
    return textUnderWidth.length();
    }

  public Sprite createTextBox(String text, int width, Color textColor, Color fillColor)
    {
    java.util.List<String> lines=new java.util.LinkedList<String>();
    
    int i=0;
    text=text.trim();
    while(text.length()>0)
      {
      int pos=positionStringOfSize(text,width);
      lines.add(text.substring(0,pos).trim());
      text=text.substring(pos);
      i++;
      }
      
    int numLines=lines.size();    
    int lineLengthPixels=0;
    
    for(String line: lines)
      {
      int lineWidth=g.getFontMetrics().stringWidth(line);
      if(lineWidth>lineLengthPixels)
        {
        lineLengthPixels=lineWidth;
        }
      }    
    
    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    Image image = gc.createCompatibleImage(((lineLengthPixels<width)?lineLengthPixels:width)+4,16*numLines,Transparency.BITMASK);
    
    Graphics g2d=image.getGraphics();

    if(fillColor!=null)
      {
      g2d.setColor(textColor);
      g2d.drawRect(0,0,((lineLengthPixels<width)?lineLengthPixels:width)+4-1,16*numLines-1);
    
      g2d.setColor(fillColor);
      g2d.fillRect(1,1,((lineLengthPixels<width)?lineLengthPixels:width)+4-2,16*numLines-2);
      }

    i=0;
    for(String line: lines)
      {
      g2d.setColor(Color.black);
      g2d.drawString(line,0,i*16+9);
      g2d.drawString(line,0,i*16+11);
      g2d.drawString(line,2,i*16+9);
      g2d.drawString(line,2,i*16+11);

      g2d.setColor(textColor);
      g2d.drawString(line,1,i*16+10);
      i++;      
      }    
    
    return new Sprite(image);      
    }
  
  }
