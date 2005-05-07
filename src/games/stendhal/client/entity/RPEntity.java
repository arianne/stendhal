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


/** This class is a link between client graphical objects and server attributes objects.<br>
 *  You need to extend this object in order to add new elements to the game. */
public abstract class RPEntity extends AnimatedEntity  
  {
  private static Sprite hitted;
  private static Sprite blocked;
  private static Sprite missed;
  
  static
    {
    SpriteStore st=SpriteStore.get();
    
    hitted=st.getSprite("sprites/combat/hitted.gif");
    blocked=st.getSprite("sprites/combat/blocked.gif");
    missed=st.getSprite("sprites/combat/missed.gif");
    }
  
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
  
  private String name;
  private boolean attacking;
  private int hp;
  private int base_hp;
  
  private Sprite nameImage;

  private long combatIconTime;
  private java.util.List<Sprite> damageSprites;
  private java.util.List<Long> damageSpritesTimes;
  
  private boolean attacked;
  private RESOLUTION resolution;

  /** Create a new game entity based on the arianne object passed */
  public RPEntity(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(gameObjects,object);       
    damageSprites=new LinkedList<Sprite>();
    damageSpritesTimes=new LinkedList<Long>();
    }
    
  public boolean isAttacking()
    {
    return attacking;
    } 
   
  public String getName()
    {
    return name;
    }

  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);
    
    if(changes.has("base_hp")) base_hp=changes.getInt("base_hp");
    if(changes.has("hp")) hp=changes.getInt("hp");
    
    if(changes.has("name"))
      {
      name=changes.get("name");
      nameImage=GameScreen.get().createString(getName(),Color.white);
      }
    else if(name==null && changes.has("type"))
      {
      name=changes.get("type");
      nameImage=GameScreen.get().createString(getName(),Color.white);
      }

    /** Attack code */  
    if(changes.has("target") || object.has("target"))
      {
      attacking=true;
            
      int risk=(changes.has("risk")?changes.getInt("risk"):0);
      int damage=(changes.has("damage")?changes.getInt("damage"):0);
      int target=(changes.has("target")?changes.getInt("target"):object.getInt("target"));
      
      // TODO: Change! Replace! Use new action system instead 
      gameObjects.attack(this,new RPObject.ID(target,changes.get("zoneid")),risk,damage);
      
      if(risk>0)
        {
        client.addEventLine(name+" striked and damaged with "+damage+" points to "+target,Color.RED);
        }
      else
        {
        client.addEventLine(name+" missed striking to "+target,Color.RED);
        }
      }
    }

  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyRemoved(object,changes);
    if(changes.has("target"))
      {     
      attacking=false;
      gameObjects.attackStop(this,new RPObject.ID(object.getInt("target"),object.get("zoneid")));
      }
    }
    
  
  public void onAttack(RPEntity source, int risk, int damage)
    {
    attacked=true;
    
    combatIconTime=System.currentTimeMillis();
    
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
      
      damageSprites.add(GameScreen.get().createString(Integer.toString(damage),Color.red));      
      damageSpritesTimes.add(new Long(System.currentTimeMillis()));
      }
    }

  public void onAttackStop(RPEntity source)  
    {
    attacked=false;
    }
  
  
  /** Draws this entity in the screen */
  public void draw(GameScreen screen)
    {
    super.draw(screen);

    if(nameImage!=null) screen.draw(nameImage,x,y-0.5);          
  
    if(base_hp>0)
      {
      if(hp<0) hp=0;
      
      Graphics g2d=screen.expose();

      Point2D p=new Point.Double(x,y);
      p=screen.invtranslate(p);
      
      g2d.setColor(new Color(1-(float)hp/(float)base_hp,((float)hp/(float)base_hp),0));
      g2d.fillRect((int)p.getX(),(int)p.getY()-3,(int)(((float)hp/(float)base_hp)*26.0),3);
      g2d.setColor(Color.black);
      g2d.drawRect((int)p.getX(),(int)p.getY()-3,26,3);
      }
    
    if(attacked)
      {
      // Draw red box around
      Graphics g2d=screen.expose();
      Rectangle2D rect=getArea();
      
      g2d.setColor(Color.red);    
      Point2D p=new Point.Double(rect.getX(),rect.getY());
      p=screen.invtranslate(p);
      g2d.drawRect((int)p.getX(),(int)p.getY(),(int)(rect.getWidth()*32.0),(int)(rect.getHeight()*32.0));
      }

    if(attacked && System.currentTimeMillis()-combatIconTime<4*300)
      {
      // Draw bottom right combat icon
      Rectangle2D rect=getArea();
      double sx=rect.getMaxX();
      double sy=rect.getMaxY();
        
      switch(resolution)
        {
        case BLOCKED:          
          screen.draw(blocked,sx-0.25,sy-0.25);
          break;
        case MISSED:
          screen.draw(missed,sx-0.25,sy-0.25);
          break;
        case HITTED:
          screen.draw(hitted,sx-0.25,sy-0.25);
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

  public String defaultAction()
    {
    return "Look";
    }
  
  public String[] offeredActions()
    {
    String[] list={"Look","Attack","Stop attack","Follow","Trade"};
    return list;
    }

  public void onAction(String action, StendhalClient client)
    {
    if(action.equals("Look"))
      {
      StendhalClient.get().addEventLine("You see "+getName()+".");
      }
    else if(action.equals("Attack"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","attack");
      int id=getID().getObjectID();
      rpaction.put("target",id);      
      client.send(rpaction);
      }
    else if(action.equals("Stop attack"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","stop");
      client.send(rpaction);
      }
    else if(action.equals("Follow"))
      {
      }
    else if(action.equals("Trade"))
      {
      }
    }
  }
