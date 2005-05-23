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

import marauroa.common.*;
import marauroa.common.game.*;
import games.stendhal.client.*;
import games.stendhal.common.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

public abstract class Entity
  {
	/** The current x location of this entity */ 
	protected double x;
	/** The current y location of this entity */
	protected double y;
	/** The current speed of this entity horizontally (pixels/sec) */
	protected double dx;
	/** The current speed of this entity vertically (pixels/sec) */
	protected double dy;

  /** The arianne object associated with this game entity */
  protected RPObject.ID id;
  protected String type;
  
  /** The object sprite. Animationless, just one frame */
  protected Sprite sprite;

  protected Rectangle2D area;
  protected Rectangle2D drawedArea;

  protected GameObjects gameObjects;
  protected StendhalClient client;


	/**
	 * Construct a entity based on a sprite image and a location.
	 * 
 	 * @param x The initial x location of this entity
	 * @param y The initial y location of this entity
	 */
  public Entity(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
	  {
	  this.gameObjects=gameObjects;
    this.client=StendhalClient.get();

    type=object.get("type");
    id=object.getID();    
    x=y=dx=dy=0;

    loadSprite(type);

    area=EntityAreas.getArea(object.get("type"),0,0);
    drawedArea=EntityAreas.getDrawedArea(object.get("type"),0,0);
    }

  /** Returns the represented arianne object id */
  public RPObject.ID getID()
    {
    return id;
    }
  
  public double getx()
    {
    return x;    
    }
  
  public double gety()
    {
    return y;
    }
  
  public double distance(RPObject object)
    {
    return (object.getInt("x")-x)*(object.getInt("x")-x)+(object.getInt("y")-y)*(object.getInt("y")-y);
    }
 
  protected static String translate(String type)
    {
    return "sprites/"+type+".gif";
    }
    
  /** Loads the sprite that represent this entity */
  protected void loadSprite(String type)
    {
    SpriteStore store=SpriteStore.get();        
    sprite=store.getSprite(translate(type));
    }

  final public Rectangle2D getArea()
    {
    return area;
    }

  final public Rectangle2D getDrawedArea()
    {
    return drawedArea;
    }

  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    if(changes.has("dir"))
      {
      double speed=1;
      
      if(changes.has("speed")) speed=changes.getDouble("speed");
      
      Direction dir=Direction.build(changes.getInt("dir"));
      dx=(int)dir.getdx()*speed;
      dy=(int)dir.getdy()*speed;
      }

    if(object.has("x") && dx==0) x=object.getInt("x");
    if(object.has("y") && dy==0) y=object.getInt("y");
    if(changes.has("x")) x=changes.getInt("x");
    if(changes.has("y")) y=changes.getInt("y");
    
    EntityAreas.getArea(area,type,x,y);
    drawedArea.setRect(x,y,drawedArea.getWidth(),drawedArea.getHeight());    
    }

  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    }

  public void removed() throws AttributeNotFoundException
    {
    }

  public void draw(GameScreen screen)
    {
    screen.draw(sprite,x,y);

    if(stendhal.showCollisionDetection)
      {
      Graphics g2d=screen.expose();
      Rectangle2D rect=getArea();      
      g2d.setColor(Color.green);    
      Point2D p=new Point.Double(rect.getX(),rect.getY());
      p=screen.invtranslate(p);
      g2d.drawRect((int)p.getX(),(int)p.getY(),(int)(rect.getWidth()*32.0),(int)(rect.getHeight()*32.0));
  
      g2d=screen.expose();
      rect=getDrawedArea();      
      g2d.setColor(Color.blue);    
      p=new Point.Double(rect.getX(),rect.getY());
      p=screen.invtranslate(p);
      g2d.drawRect((int)p.getX(),(int)p.getY(),(int)(rect.getWidth()*32.0),(int)(rect.getHeight()*32.0));
      }
    }
    
	public void move(long delta) 
	  {
		// update the location of the entity based on move speeds
		x += (delta * dx) / 300;
		y += (delta * dy) / 300;
//		
//		System.out.println (type+"(POS)-->"+x+","+y);
  	}	
  
  public boolean stopped()
    {
    return dx==0 && dy==0;
    }

  public abstract String defaultAction();
  public abstract String[] offeredActions();
  public abstract void onAction(String action, StendhalClient client);
  }