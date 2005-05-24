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

import marauroa.common.*;
import marauroa.common.game.*;
import games.stendhal.client.entity.*;
import games.stendhal.common.*;
import java.util.*;
import java.awt.Graphics;
import java.awt.geom.*;

/** This class stores the objects that exists on the World right now */
public class GameObjects 
  {
  private HashMap<RPObject.ID, Entity> objects;
  private List<Entity> sortObjects;
  private StaticGameLayers collisionMap;
  
  public GameObjects(StaticGameLayers collisionMap)
    {
    objects=new HashMap<RPObject.ID, Entity>();
    sortObjects=new LinkedList<Entity>();
      
    this.collisionMap=collisionMap;
    }
  
  /** Create a Entity of the correct type depending of the arianne object */
  private Entity entityType(RPObject object) 
    {
    try
      {
      /** TODO: Refactor this --> Factory pattern apply.
       *  OMG! It is a nice penalty each time we add an object using this... */
      if(object.get("type").equals("player"))
        {
        return new Player(this, object);
        }
      else if(object.get("type").equals("wolf"))
        {
        return new Creature(this, object);
        }
      else if(object.get("type").equals("rat"))
        {
        return new Creature(this, object);
        }
      else if(object.get("type").equals("caverat"))
        {
        return new Creature(this, object);
        }
      else if(object.get("type").equals("sheep"))
        {
        return new Sheep(this, object);
        }
      else if(object.get("type").equals("sign"))
        {
        return new Sign(this, object);
        }
      else if(object.get("type").equals("sellernpc"))
        {
        return new Player(this, object);
        }
      else if(object.get("type").equals("buyernpc"))
        {
        return new Player(this, object);
        }
      else if(object.get("type").equals("welcomernpc"))
        {
        return new Player(this, object);
        }
      else if(object.get("type").equals("beggarnpc"))
        {
        return new Player(this, object);
        }
      else if(object.get("type").equals("trainingdummy"))
        {
        return new TrainingDummy(this, object);
        }
      else if(object.get("type").equals("food"))
        {
        return new Food(this, object);
        }
      else if(object.get("type").equals("corpse"))
        {
        return new Corpse(this, object);
        }
      else if(object.get("type").equals("portal"))
        {
        return new Portal(this, object);
        }
      else
        {
        Logger.trace("GameObjects::entityType","X","Unknown entity type("+object.get("type")+")");
        return null;
        }
      }
    catch(AttributeNotFoundException e)
      {
      Logger.thrown("GameObjects::entityType","X",e);
      return null;
      }
    }
  
  private void sort()
    {
    Collections.sort(sortObjects,new Comparator<Entity>()
      {
      public int compare(Entity o1, Entity o2) 
        {
        double dx=o1.getArea().getX()-o2.getArea().getX();
        double dy=o1.getArea().getY()-o2.getArea().getY();
        
        if(dy<0) 
          {
          return -1;
          }
        else if(dy>0) 
          {
          return 1;
          }
        else if(dx!=0)
          {
          return (int)dx;
          }
        else
          {
          // Same tile...
          if(o1 instanceof Corpse)
            {
            return -1;
            }
          
          return 0;
          }
        }      
      });    
    }
  
  /** Add a new Entity to the game */  
  public void add(RPObject object) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::add",">");

    Entity entity=entityType(object);
    // HACK: The first time the object is EMPTY! 
    entity.modifyAdded(new RPObject(), object);
    
    objects.put(entity.getID(),entity);
    sortObjects.add(entity);
    
    Logger.trace("GameObjects::add","D",entity.toString());
    Logger.trace("GameObjects::add","<");
    }
  
  public Entity at(double x, double y)
    {
    for(Entity entity: sortObjects)
      {
      if(entity.getArea().contains(x,y))
        {
        return entity;
        }
      }

    // Maybe user clicked outside char but on the drawed area of it
    for(Entity entity: sortObjects)
      {
      if(entity.getDrawedArea().contains(x,y))
        {
        return entity;
        }
      }
    
    return null;
    }  

  /** Modify a existing Entity so its propierties change */  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::modifyAdded",">");
    Entity entity=objects.get(object.getID());
    if(entity!=null)
      {
      entity.modifyAdded(object, changes);
      }
      
    Logger.trace("GameObjects::modifyAdded","<");
    }

  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::modifyRemoved",">");
    Entity entity=objects.get(object.getID());
    if(entity!=null)
      {
      entity.modifyRemoved(object, changes);
      }
      
    Logger.trace("GameObjects::modifyRemoved","<");
    }

  public void attack(RPEntity source, RPObject.ID target, int risk, int damage) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::damage",">");
    Entity entity=objects.get(target);
    if(entity!=null && entity instanceof RPEntity)
      {
      RPEntity rpentity=(RPEntity)entity;
      rpentity.onAttack(source,risk, damage);
      }
      
    Logger.trace("GameObjects::damage","<");
    }

  public void attackStop(RPEntity source, RPObject.ID target) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::damage",">");
    Entity entity=objects.get(target);
    if(entity!=null && entity instanceof RPEntity)
      {
      RPEntity rpentity=(RPEntity)entity;
      rpentity.onAttackStop(source);
      }
      
    Logger.trace("GameObjects::damage","<");
    }
  
  public boolean has(Entity entity)
    {
    return objects.containsKey(entity.getID());
    }

  public Entity get(RPObject.ID id)
    {
    return objects.get(id);
    }
    
  /** Removes a Entity from game */
  public void remove(RPObject.ID id)
    {
    Logger.trace("GameObjects::remove",">");
    Logger.trace("GameObjects::remove","D",id.toString());

    Entity entity=objects.get(id);
    if(entity!=null)
      {
      entity.removed();
      }

    Entity object=objects.remove(id);
    sortObjects.remove(object);
    Logger.trace("GameObjects::remove","<");
    }
  
  /** Removes all the object entities */
  public void clear()
    {
    Logger.trace("GameObjects::clear",">");
    objects.clear();
    sortObjects.clear();
    Logger.trace("GameObjects::clear","<");
    }
  
  /** Move objects based on the lapsus of time ellapsed since the last call. */
  public void move(long delta)    
    {
    for(Entity entity: objects.values())
      {
      if(!entity.stopped() && !collisionMap.collides(entity.getArea()))
        {
        entity.move(delta);
        }      
      }

    sort();
    }
   
  /** Draw all the objects in game */
  public void draw(GameScreen screen)
    {
    for(Entity entity: sortObjects)
      {
      entity.draw(screen);
      }
    }
  }
  
