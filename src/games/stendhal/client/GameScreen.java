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
import java.awt.font.*;
import java.text.*;
import java.util.*;
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
//    if(screen==null)
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
    // Bugfix: Prevent NPE for empty text      intensifly@gmx.com
    String textUnderWidth="";
    if(words!=null)
	textUnderWidth=words[0];
    
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

        // Added support formatted text displaying #keywords in another color    intensifly@gmx.com
	// ToDo: optimize the alghorithm, it's a little long ;)

	public AttributedString formatLine(String line, Font fn, Color textColor) {
		Font fh = fn.deriveFont(Font.ITALIC);
		
		ArrayList<String> list = new ArrayList<String>();
		for (int c = 0, j = 0, h=0; c < line.length(); c++) {
			if (line.charAt(c) == '#' || (h==1 && (line.charAt(c) == ' ' || line.charAt(c) == '.')) ) {
				if (c != j) {
					list.add(line.substring(j, c));
				}
				if(line.charAt(c) == '#')
					h=1;
				else
					h=0;
				j = c ;
			} else if (c == line.length() - 1) {
				list.add(line.substring(j, c + 1));
			}
		}
		int number = list.size();
		AttributedString aStyledText;
		
		for (int j = 0; j < number; j++) {
			String tok = list.get(j).toString();
			if(tok.charAt(0) == '#') {
				tok = tok.substring(1);
			}
		}
		line = "";
		for (int j = 0; j < number; j++) {
			String tok = list.get(j).toString();
			if(tok.charAt(0) == '#') {
				tok = tok.substring(1);
			}
			line += tok;
		}
		
		aStyledText = new AttributedString(line);
		Font f;
		Color col;
		
		for (int j = 0, s=0; j < number; j++) {
			String tok = list.get(j).toString();
			f = fn;
			col = textColor;
			if(tok.charAt(0) == '#') {
				tok = tok.substring(1);
				f = fh;
				col = Color.blue;
			}
			if(tok.length()>0) {
				aStyledText.addAttribute(TextAttribute.FONT, f, s, s+ tok.length());
				aStyledText.addAttribute(TextAttribute.FOREGROUND, col, s, s+ tok.length());
			}
			s += tok.length();
		}
		
		return(aStyledText);
		
		
	}

  public Sprite createTextBox(String text, int width, Color textColor, Color fillColor, boolean isTalking)
    {
    java.util.List<String> lines=new java.util.LinkedList<String>();
    
    int i=0;
    // Added support for speech balloons. If drawn, they take 10 pixels from the left.    intensifly@gmx.com

    int delta = 0;

    if(fillColor!=null)
        delta = 10;
    text=text.trim();
    while(text.length()>0)
      {
      int pos=positionStringOfSize(text,width-delta);
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
    Image image = gc.createCompatibleImage(((lineLengthPixels+delta<width)?lineLengthPixels+delta:width)+4,16*numLines,Transparency.BITMASK);
    
    Graphics2D g2d= (Graphics2D)image.getGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

    if(fillColor != null)
      {
      Composite xac = g2d.getComposite();
      AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.8f);
      g2d.setComposite(ac);
      g2d.setColor(fillColor);
      g2d.fillRoundRect(10,0,((lineLengthPixels < width) ? lineLengthPixels : width) + 3,16 * numLines - 1,4,4);
      g2d.setColor(textColor);
      if(isTalking)
        {
        g2d.drawRoundRect(10,0,((lineLengthPixels < width) ? lineLengthPixels : width) + 3,16 * numLines - 1,4,4);    
        }
      else
        {
        float dash[] = { 4, 2 };
        BasicStroke newStroke = new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER,1,dash,0);
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke( newStroke );
        g2d.drawRect(10,0,((lineLengthPixels < width) ? lineLengthPixels : width) + 3,16 * numLines - 1);        
        g2d.setStroke( oldStroke );
        }
      g2d.setComposite(xac);
      if(isTalking)
        {
        g2d.setColor(fillColor);
        Polygon p = new Polygon();
        p.addPoint(10,3);
        p.addPoint(0,16);
        p.addPoint(11,12);
        g2d.fillPolygon(p);
        g2d.setColor(textColor);
        p.addPoint(0,16);
        g2d.drawPolygon(p);
        }
      }

    i=0;
    for(String line: lines)
      {
      AttributedString aStyledText = formatLine(line,g2d.getFont(),textColor);

      if(fillColor == null)
        {
        g2d.setColor(Color.black);
        g2d.drawString(aStyledText.getIterator(),1,2 + i * 16 + 9);
        g2d.drawString(aStyledText.getIterator(),1,2 + i * 16 + 11);
        g2d.drawString(aStyledText.getIterator(),3,2 + i * 16 + 9);
        g2d.drawString(aStyledText.getIterator(),3,2 + i * 16 + 11);
        }
      g2d.setColor(textColor);

      g2d.drawString(aStyledText.getIterator(),2 + delta,2 + i * 16 + 10);
      i++;      
      }    
    
    return new Sprite(image);      
    }
  
  }
