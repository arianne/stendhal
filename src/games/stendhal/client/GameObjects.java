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
import java.awt.Color;

/** This class stores the objects that exists on the World right now */
public class GameObjects 
  {
  private static Map<String, Class> entityMap;
  
  static
    {
    entityMap=new HashMap<String, Class>();
    register();
    }
  
  private static void register()
    {
    register("player",Player.class);
    
    register("orc",NormalCreature.class);
    register("troll",NormalCreature.class);
    register("gargoyle",NormalCreature.class);
    register("goblin",NormalCreature.class);
    register("ogre",NormalCreature.class);
    register("kobold",NormalCreature.class);
    register("boar",NormalCreature.class);
    register("cobra",NormalCreature.class);
    register("wolf",NormalCreature.class);
    register("caverat",SmallCreature.class);
    register("rat",SmallCreature.class);
    register("sheep",Sheep.class);
    
    register("angelnpc",NPC.class);
    register("beggarnpc",NPC.class);
    register("butchernpc",NPC.class);
    register("buyernpc",NPC.class);
    register("journalistnpc",NPC.class);
    register("orcbuyernpc",NPC.class);
    register("sellernpc",NPC.class);
    register("tavernbarmaidnpc",NPC.class);
    register("welcomernpc",NPC.class);
    register("trainingdummy",TrainingDummy.class);
    
    register("food",Food.class);
    register("corpse",Corpse.class);
    register("sign",Sign.class);

    register("portal",Portal.class);
    }

  public static void register(String type, Class entityClass)
    {
    entityMap.put(type,entityClass);
    }
  
  private HashMap<RPObject.ID, Entity> objects;
  private List<Text> texts;
  private List<Text> textsToRemove;
  
  private List<Entity> sortObjects;
  private StaticGameLayers collisionMap;
  
  public GameObjects(StaticGameLayers collisionMap)
    {
    objects=new HashMap<RPObject.ID, Entity>();
    texts=new LinkedList<Text>();
    textsToRemove=new LinkedList<Text>();
    sortObjects=new LinkedList<Entity>();
      
    this.collisionMap=collisionMap;
    }
  
  /** Create a Entity of the correct type depending of the arianne object */
  private Entity entityType(RPObject object) 
    {
    try
      {
      Class entityClass=entityMap.get(object.get("type"));
      java.lang.reflect.Constructor constr=entityClass.getConstructor(GameObjects.class, RPObject.class);
      return (Entity)constr.newInstance(this,object);
      }
    catch(Exception e)
      {
      Logger.trace("GameObjects::entityType","X",object.toString());
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
  
  public void addText(Entity speaker, String text, Color color)
    {
    Text entity=new Text(this,text, speaker.getx(), speaker.gety(), color);
    texts.add(entity);
    }

  public void addText(Entity speaker, Sprite sprite)
    {
    Text entity=new Text(this,sprite, speaker.getx(), speaker.gety());
    texts.add(entity);
    }
  
  public void removeText(Text entity)
    {
    textsToRemove.add(entity);
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
    
  public void drawText(GameScreen screen)
    {
    for(Text entity: texts)
      {
      entity.draw(screen);
      }
    
    texts.removeAll(textsToRemove);
    textsToRemove.clear();
    }
  }
  
