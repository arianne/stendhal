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

import games.stendhal.client.*;
import games.stendhal.client.events.*;
import games.stendhal.common.Direction;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.DataLine;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;


public abstract class Entity implements MovementEvent, ZoneChangeEvent
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Entity.class);

  /** session wide instance identifier for this class */ 
  private byte[] ID_Token = new byte[0];

  /** The current x location of this entity */ 
  protected double x;
  /** The current y location of this entity */
  protected double y;
	
  private Direction direction;
  private double speed;
	
  /** The current speed of this entity horizontally (pixels/sec) */
  protected double dx;
  /** The current speed of this entity vertically (pixels/sec) */
  protected double dy;

  /** The arianne object associated with this game entity */
  protected RPObject rpObject;
  protected String type;
  protected String subtype;
  
  /** The object sprite. Animationless, just one frame */
  protected Sprite sprite;

  protected Rectangle2D area;
  protected Rectangle2D drawedArea;
  protected double audibleRange = Double.POSITIVE_INFINITY;

  protected GameObjects gameObjects;
  protected StendhalClient client;
  
  private int modificationCount;

  
  public Entity()
    {
    modificationCount=0;
    }
    
  /**
   * Construct a entity based on a sprite image and a location.
   * 
   * @param x The initial x location of this entity
   * @param y The initial y location of this entity
   */
  public Entity(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    String name = null; 
     
    this.gameObjects=gameObjects;
    this.client=StendhalClient.get();

    if(object.has("name"))
      {
      subtype = object.get("name");
      name = subtype;
      }

    type=object.get("type");
    
    String hstr = "- Entity type = " + type + (name == null ? "" : " / " + name );
    logger.debug( hstr );     
    
    rpObject = object;    
    x = 0.0;
    y = 0.0;
    dx = 0.0;
    dy = 0.0;
    direction=Direction.STOP;

    loadSprite(object);
    }  // constructor
  
  public byte[] get_IDToken ()
  {
     return ID_Token;
  }
  
  public String getType()
    {
    return type;
    }

  public String getSubType()
  {
  return subtype;
  }

  /** Returns the represented arianne object id */
  public RPObject.ID getID()
    {
    return rpObject != null ? rpObject.getID() : null;
    }
  
  public double getx()
    {
    return x;    
    }
  
  public double gety()
    {
    return y;
    }
  
  public Direction getDirection()
    {
    return direction;
    }
  
  /** the absolute position on the map of this entity */
  public Point2D getPosition ()
  {
     return new Point2D.Double( x, y );
  }
  
  public double getSpeed()
    {
    return speed;
    }
  
  public double distance(RPObject object)
    {
    return (object.getInt("x")-x)*(object.getInt("x")-x)+(object.getInt("y")-y)*(object.getInt("y")-y);
    }

  protected static String translate(String type)
    {
    return "data/sprites/"+type+".png";
    }
  
  public Sprite getSprite()
    {
    return sprite;
    }
    
  /** Returns the absolute world area (coordinates) to which audibility of 
   *  entity sounds is confined. Returns <b>null</b> if confines do
   *  not exist (audible everywhere).
   */
  public Rectangle2D getAudibleArea ()
  {
     if ( audibleRange == Double.POSITIVE_INFINITY )
        return null;

     double width = audibleRange*2;
     return new Rectangle2D.Double( getx()-audibleRange, gety()-audibleRange, width, width );
  }
  
  /** Sets the audible range as radius distance from this entity's position,
   *  expressed in coordinate units.
   *  This reflects an abstract capacity of this unit to emit sounds and influences
   *  the result of <code>getAudibleArea()</code>.
   *  
   *  @param range double audibility area radius in coordinate units
   */  
  public void setAudibleRange ( double range )
  {
     audibleRange = range;
  }
  
  /** Loads the sprite that represent this entity */
  protected void loadSprite(RPObject object)
    {
    SpriteStore store=SpriteStore.get();        
    sprite=store.getSprite(translate(object.get("type")));
    }

  // When rpentity moves, it will be called with the data.
  public void onMove(int x, int y, Direction direction, double speed)
    {
    this.dx=direction.getdx()*speed;
    this.dy=direction.getdy()*speed;
    
    this.x=x;
    this.y=y;
    }

  // When rpentity stops 
  public void onStop()
    {
    direction=Direction.STOP;
    speed=0;

    this.dx=0;
    this.dy=0;
    }


  // When rpentity reachs the [x,y,1,1] area.
  public void onEnter(int x, int y)
    {
    if(this instanceof Player && client.getPlayer()!=null && client.getPlayer().getID().equals(getID()))
      {
      WorldObjects.firePlayerMoved((Player)this);
      }
    }

  // When rpentity leaves the [x,y,1,1] area.
  public void onLeave(int x, int y)
    {
    }

  // Called when entity enters a new zone
  public void onEnterZone(String zone)
    {
    }
    
  // Called when entity leaves a zone
  public void onLeaveZone(String zone)
    {
    }
    


  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    modificationCount++;
//    if(changes.has("dir"))
//      {
//      direction=Direction.build(changes.getInt("dir"));
//      }
//    
//    if(changes.has("speed"))
//      {
//      if(object.has("speed")) speed=object.getDouble("speed");
//      if(changes.has("speed")) speed=changes.getDouble("speed");
//      }
//      
//    dx=direction.getdx()*speed;
//    dy=direction.getdy()*speed;
//    
//    double oldx=x, oldy=y;
//
//    if(object.has("x") && dx==0) x=object.getInt("x");
//    if(object.has("y") && dy==0) y=object.getInt("y");
//    if(changes.has("x")) x=changes.getInt("x");
//    if(changes.has("y")) y=changes.getInt("y");
//    
//    if( (oldx!=x || oldy!=y) && this instanceof Player )
//      {
//      if(this instanceof Player && client.getPlayer()!=null && client.getPlayer().getID().equals(getID()))
//        {
//        WorldObjects.firePlayerMoved((Player)this);
//        }
//      }
    }

  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    modificationCount++;
    }

  /** called when the server removes the entity */
  public void removed() throws AttributeNotFoundException
    {
     logger.debug("----- Entity removed = " + type );     
        SoundSystem.stopSoundCycle( ID_Token );
    }

  public void draw(GameScreen screen)
    {
    screen.draw(sprite,x,y);

    if(stendhal.SHOW_COLLISION_DETECTION)
      {
      Graphics g2d=screen.expose();
      Rectangle2D rect=getArea();      
      g2d.setColor(Color.green);    
      Point2D p=new Point.Double(rect.getX(),rect.getY());
      p=screen.invtranslate(p);
      g2d.drawRect((int)p.getX(),(int)p.getY(),(int)(rect.getWidth()*(float)GameScreen.SIZE_UNIT_PIXELS),(int)(rect.getHeight()*(float)GameScreen.SIZE_UNIT_PIXELS));
  
      g2d=screen.expose();
      rect=getDrawedArea();      
      g2d.setColor(Color.blue);    
      p=new Point.Double(rect.getX(),rect.getY());
      p=screen.invtranslate(p);
      g2d.drawRect((int)p.getX(),(int)p.getY(),(int)(rect.getWidth()*(float)GameScreen.SIZE_UNIT_PIXELS),(int)(rect.getHeight()*(float)GameScreen.SIZE_UNIT_PIXELS));
      }
    }
    
  public void move(long delta) 
    {
    // update the location of the entity based on move speeds
    x += (delta * dx) / 350;
    y += (delta * dy) / 350;
    }	
  
  public boolean stopped()
    {
    return dx==0 && dy==0;
    }
  
  /** Makes this entity play a sound on the map, at its current location. 
   *  The sound is audible to THE player in relation to distance and hearing 
   *  or audibility confines. Occurence of this soundplaying can be subject 
   *  to random (<code>chance</code>).
   *  
   * @param token sound library name of the sound to be played
   * @param volBot bottom volume (0..100)
   * @param volTop top volume (0..100)
   * @param chance chance of being performed (0..100)
   * @return the sound <code>DataLine</code> that is being played,
   *         or <b>null</b> if not performing
   */
  public DataLine playSound ( String token, int volBot, int volTop, int chance )
  {
     return SoundSystem.playMapSound( getPosition(), getAudibleArea(), token, volBot, volTop, chance );
  }
  
  /** Makes this entity play a sound on the map, at its current location. 
   *  The sound is audible to THE player in relation to distance and hearing 
   *  or audibility confines.
   *  
   * @param token sound library name of the sound to be played
   * @param volBot bottom volume (0..100)
   * @param volTop top volume (0..100)
   * @return the sound <code>DataLine</code> that is being played,
   *         or <b>null</b> if not performing
   */
  public DataLine playSound ( String token, int volBot, int volTop )
  {
     return SoundSystem.playMapSound( getPosition(), getAudibleArea(), token, volBot, volTop, 100 );
  }
  
  /** returns the number of slots this entity has */
  public int getNumSlots()
  {
    return rpObject.slots().size();
  }

  /** returns the slot with the specified name or null if the entity does not have
   * this slot */
  public RPSlot getSlot(String name)
  {
    if (rpObject.hasSlot(name))
    {
      return rpObject.getSlot(name);
    }
    return null;
  }

  /** returns a list of slots */
  public List<RPSlot> getSlots()
  {
    return new ArrayList<RPSlot>(rpObject.slots());
  }
  
  /** returns the modificationCount. This counter is increased each time a
   * perception is received from the server (so all serverside changes increases
   * the mod-count). This counters purpose is to be sure that this entity is
   * modified or not (ie for gui elements). 
   */
  public long getModificationCount()
  {
    return modificationCount;
  }
  
  /** Returns true when  the entity was modified since the <i>oldModificationCount</i>.
   * @param oldModificationCount the old modificationCount
   * @return true when the entity was modified, false otherwise 
   * @see #getModificationCount()
   * */
  public boolean isModified(long oldModificationCount)
  {
    return oldModificationCount != modificationCount;
  }
  
  abstract public Rectangle2D getArea();
  abstract public Rectangle2D getDrawedArea();

  public abstract String defaultAction();
  public abstract String[] offeredActions();
  public abstract void onAction(StendhalClient client, String action, String... params);
  
  abstract public int compare(Entity entity);
  }