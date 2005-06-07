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
package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;
import java.awt.*;
import java.awt.geom.*;

public class Text extends Entity 
  {
  private final static int TEXT_PERSISTENCE_TIME=5000;

  private double x;
  private double y;
  private Sprite textImage;
  private long textImageTime;
  
  public Text(GameObjects gameObjects, Sprite text, double x, double y) throws AttributeNotFoundException  
    {    
    this.gameObjects=gameObjects;
    this.client=StendhalClient.get();
    
    textImage=text;
    textImageTime=System.currentTimeMillis();

    this.x=x+0.7-(textImage.getWidth()/((float)GameScreen.PIXEL_SCALE*2.0f));
    this.y=y-0.5;
    }
    
  public Text(GameObjects gameObjects, String text, double x, double y, Color color) throws AttributeNotFoundException
    {    
    this.gameObjects=gameObjects;
    this.client=StendhalClient.get();
    
    textImage=GameScreen.get().createTextBox(text,240,color,null);
    textImageTime=System.currentTimeMillis();

    this.x=x+0.7-(textImage.getWidth()/((float)GameScreen.PIXEL_SCALE*2.0f));
    this.y=y+1;
    
    drawedArea=new Rectangle.Double(x,y,textImage.getWidth()/GameScreen.PIXEL_SCALE, textImage.getHeight()/GameScreen.PIXEL_SCALE);
    }
  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    }

  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    }
    
  public String defaultAction()
    {
    return null;
    }

  public String[] offeredActions()
    {
    return null;
    }

  public void onAction(String action, StendhalClient client)
    {
    }

  public Rectangle2D getArea()
    {
    return null;
    }
    
  public Rectangle2D getDrawedArea()
    {
    return new Rectangle.Double(x,y,textImage.getWidth()/GameScreen.PIXEL_SCALE,textImage.getHeight()/GameScreen.PIXEL_SCALE);
    }  

  public void draw(GameScreen screen)
    {
    screen.draw(textImage,x,y);
    
    if(System.currentTimeMillis()-textImageTime>TEXT_PERSISTENCE_TIME) 
      {
      gameObjects.removeText(this);
      }
    }
  }
