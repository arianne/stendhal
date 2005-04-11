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
import java.util.*;
import java.awt.*;
import java.awt.geom.*;


/** This class is a link between client graphical objects and server attributes objects.<br>
 *  You need to extend this object in order to add new elements to the game. */
public class GameEntity extends Entity  
  {
  /** The arianne object associated with this game entity */
  private RPObject.ID id;
  /** The object sprite. Animationless, just one frame */
  protected Sprite sprite;
  private java.util.List<Sprite> damageSprites;
  private java.util.List<Long> damageSpritesTimes;
  
  private boolean attacked;
  private enum RESOLUTION
    {
    HITTED(0),
    BLOCKED(1),
    MISSED(2);
    
    private final int val;
    RESOLUTION(int val)
      {
      this.val=val;
      }
     
    public int get()
      {
      return val;
      }
    };
  
  private RESOLUTION resolution;
  
  private static Sprite hitted;
  private static Sprite blocked;
  private static Sprite missed;
  
  static
    {
    SpriteStore st=SpriteStore.get();
    
    hitted=st.getSprite("sprites/hitted.gif");
    blocked=st.getSprite("sprites/blocked.gif");
    missed=st.getSprite("sprites/missed.gif");
    }
  
  protected GameObjects gameObjects;
  
  /** This methods returns the object graphical representation file from its type. */
  protected static String translate(String type)
    {
    return "sprites/"+type+".gif";
    }  

  /** Create a new game entity based on the arianne object passed */
  public GameEntity(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(0,0);       
    id=object.getID();    
    this.gameObjects=gameObjects;
    damageSprites=new LinkedList<Sprite>();
    damageSpritesTimes=new LinkedList<Long>();
    attacked=false;
    loadSprite(object.get("type"));
    }
    
  public Rectangle2D getArea()
    {
    return new Rectangle.Double(x,y,1,1);
    }
    
  public Rectangle2D getDrawedArea()
    {
    return new Rectangle.Double(x,y,1,1);
    }

  public void onClick()
    {
    }
    
  public void onDoubleClick()
    {
    }
    
  public void onLeftClick()
    {
    }
  
  public void onAttackStop(GameEntity source)  
    {
    System.out.println ("STOP ATTACKING");
    attacked=false;
    }
    
  public void onAttack(GameEntity source, int risk, int damage)
    {
    attacked=true;
    
    if(risk<=0)
      {
      resolution=RESOLUTION.MISSED;
      }
    else if(damage<=0)
      {
      resolution=RESOLUTION.BLOCKED;
      }    
    else
      {
      resolution=RESOLUTION.HITTED;
      
      GameScreen screen=GameScreen.get();      
      Graphics g2d=screen.expose();
      String damageString=Integer.toString(damage);

      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      Image image = gc.createCompatibleImage(g2d.getFontMetrics().stringWidth(damageString),16,Transparency.BITMASK);    
      Graphics g=image.getGraphics();
      g.setColor(Color.red);
      g.drawString(damageString,0,10);
      damageSprites.add(new Sprite(image));      
      damageSpritesTimes.add(new Long(System.currentTimeMillis()));
      }
    }
  
  /** Loads the sprite that represent this entity */
  protected void loadSprite(String type)
    {
    SpriteStore store=SpriteStore.get();        
    sprite=store.getSprite(translate(type));
    }
 
  /** This method is called to modify the propierties of the game entity when the object
   *  that it represent has changed. */
  public void modify(RPObject object) throws AttributeNotFoundException
    {
    Logger.trace("GameEntity::modify","D",object.toString());
    x=object.getDouble("x");
    y=object.getDouble("y");
    
    if(object.has("dx")) dx=object.getDouble("dx");
    if(object.has("dy")) dy=object.getDouble("dy");
    }

  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    }
  
  /** Returns the represented arianne object id */
  public RPObject.ID getID()
    {
    return id;
    }
 
  /** Draws this entity in the screen */
  public void draw(GameScreen screen)
    {
    if(attacked)
      {
      Graphics g2d=screen.expose();
      Rectangle2D rect=getArea();
      
      g2d.setColor(Color.red);    
      Point2D p=new Point.Double(rect.getX(),rect.getY());
      p=screen.invtranslate(p);
      g2d.drawRect((int)p.getX(),(int)p.getY(),(int)(rect.getWidth()*32.0),(int)(rect.getHeight()*32.0));
      }

    screen.draw(sprite,x,y);

    if(attacked)
      {
      Rectangle2D rect=getArea();
      double sx=rect.getMaxX();
      double sy=rect.getMaxY();
        
      switch(resolution)
        {
        case BLOCKED:          
          screen.draw(blocked,sx,sy);
          break;
        case MISSED:
          screen.draw(missed,sx,sy);
          break;
        case HITTED:
          screen.draw(hitted,sx,sy);
          break;
        }
      }
    
    if(damageSprites!=null && damageSprites.size()>0)  // Draw the damage done
      {
      long current=System.currentTimeMillis();

      int i=0;
      for(Sprite damageImage: damageSprites)
        {        
        double tx=x+0.6-(damageImage.getWidth()/(32.0f*2.0f));
        double ty=y-((current-damageSpritesTimes.get(i))/(6.0*300.0));
        screen.draw(damageImage,tx,ty);
        i++;
        }
      
      if(damageSpritesTimes.size()>0 && (current-damageSpritesTimes.get(0)>6*300))
        {        
        damageSprites.remove(0);
        damageSpritesTimes.remove(0); 
        }
      }
    }
  }
