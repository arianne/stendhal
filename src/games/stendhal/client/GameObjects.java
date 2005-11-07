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

import games.stendhal.client.entity.*;
import games.stendhal.common.Pair;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.*;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;
import org.apache.log4j.Logger;

/** This class stores the objects that exists on the World right now */
public class GameObjects implements Iterable<Entity>
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(GameObjects.class);
  
  private static Map<Pair<String,String>, Class> entityMap;
  
  static
    {
    entityMap=new HashMap<Pair<String,String>, Class>();
    register();
    }
  
  private static void register()
    {
    register("player",null,Player.class);
    
    register("creature","orc",NormalCreature.class);
    register("creature","troll",NormalCreature.class);
    register("creature","gargoyle",NormalCreature.class);
    register("creature","goblin",NormalCreature.class);
    register("creature","ogre",NormalCreature.class);
    register("creature","kobold",NormalCreature.class);
    register("creature","boar",NormalCreature.class);
    register("creature","cobra",NormalCreature.class);
    register("creature","wolf",NormalCreature.class);
    register("creature","caverat",SmallCreature.class);
    register("creature","rat",SmallCreature.class);
    register("sheep",null,Sheep.class);    
    
    register("npc",null,NPC.class);
    register("npc","beggarnpc",NPC.class);
    register("npc","buyernpc",NPC.class);
    register("npc","butchernpc",NPC.class);
    register("npc","journalistnpc",NPC.class);
    register("npc","welcomernpc",NPC.class);
    register("npc","orcbuyernpc",NPC.class);
    register("npc","sellernpc",NPC.class);
    register("npc","weaponsellernpc",NPC.class);
    register("npc","tavernbarmaidnpc",NPC.class);
    register("trainingdummy",null,TrainingDummy.class);
    
    register("food",null,Food.class);
    register("chest",null,Chest.class);
    
    register("corpse","player",Corpse.class);
    register("corpse","orc",Corpse.class);
    register("corpse","troll",Corpse.class);
    register("corpse","gargoyle",Corpse.class);
    register("corpse","goblin",Corpse.class);
    register("corpse","ogre",Corpse.class);
    register("corpse","kobold",Corpse.class);
    register("corpse","boar",Corpse.class);
    register("corpse","cobra",Corpse.class);
    register("corpse","wolf",Corpse.class);
    register("corpse","caverat",Corpse.class);
    register("corpse","rat",Corpse.class);
    register("corpse","sheep",Corpse.class);
    
    register("sign",null,Sign.class);
    register("item","shield",Item.class);
    register("item","club",Item.class);
    register("item","sword",Item.class);
    register("item","armor",Item.class);
    register("item","money",Money.class);

    register("portal",null,Portal.class);
    }

  public static void register(String type, String eclass, Class entityClass)
    {
    entityMap.put(new Pair<String,String>(type,eclass),entityClass);
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
  
  public Iterator<Entity> iterator()
    {
    return sortObjects.iterator();
    }
  
  /** Create a Entity of the correct type depending of the arianne object */
  public Entity entityType(RPObject object) 
    {
    try
      {
      if(object.get("type").equals("player"))
        {
        return new Player(this, object);
        }
      
      String type=object.get("type");
      String eclass=null;
      if(object.has("class"))
        {
        eclass=object.get("class");
        }
        
      Class entityClass=entityMap.get(new Pair<String,String>(type,eclass));
      java.lang.reflect.Constructor constr=entityClass.getConstructor(GameObjects.class, RPObject.class);
      return (Entity)constr.newInstance(this,object);
      }
    catch(Exception e)
      {
      logger.error("cannot create entity for object "+object,e);
      return null;
      }
    }

  public Sprite spriteType(RPObject object) 
    {
    try
      {
      String type=object.get("type");
      String eclass=null;
      if(object.has("class"))
        {
        eclass=object.get("class");
        }
        
      Class entityClass=entityMap.get(new Pair<String,String>(type,eclass));
      java.lang.reflect.Constructor constr=entityClass.getConstructor(GameObjects.class, RPObject.class);
      return ((Entity)constr.newInstance(this,object)).getSprite();
      }
    catch(Exception e)
      {
      logger.error("cannot create sprite for object "+object,e);
      return null;
      }
    }

  private void sort()
    {
    Collections.sort(sortObjects,new Comparator<Entity>()
      {
      public int compare(Entity o1, Entity o2) 
        {
        //return result(o1,o2);
        return o1.compare(o2);
        }      
      });    
    }
  
  /** Add a new Entity to the game */  
  public void add(RPObject object) throws AttributeNotFoundException
    {
    Log4J.startMethod(logger,"add");

    Entity entity=entityType(object);
    // HACK: The first time the object is EMPTY! 
    entity.modifyAdded(new RPObject(), object);
    
    objects.put(entity.getID(),entity);
    sortObjects.add(entity);
    
    logger.debug("added "+entity);
    Log4J.finishMethod(logger,"add");
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
    ListIterator<Entity> it=sortObjects.listIterator(sortObjects.size());
    while(it.hasPrevious())
      {
      Entity entity=it.previous();

      if(entity.getArea().contains(x,y))
        {
        return entity;
        }
      }

    // Maybe user clicked outside char but on the drawed area of it
    it=sortObjects.listIterator(sortObjects.size());
    while(it.hasPrevious())
      {
      Entity entity=it.previous();

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
    Log4J.startMethod(logger,"modifyAdded");
    Entity entity=objects.get(object.getID());
    if(entity!=null)
      {
      entity.modifyAdded(object, changes);
      }
      
    Log4J.finishMethod(logger,"modifyAdded");
    }

  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    Log4J.startMethod(logger,"modifyRemoved");
    Entity entity=objects.get(object.getID());
    if(entity!=null)
      {
      entity.modifyRemoved(object, changes);
      }
      
    Log4J.finishMethod(logger,"modifyRemoved");
    }

  public void attack(RPEntity source, RPObject.ID target, int risk, int damage) throws AttributeNotFoundException
    {
    Log4J.startMethod(logger,"attack");
    Entity entity=objects.get(target);
    if(entity!=null && entity instanceof RPEntity)
      {
      RPEntity rpentity=(RPEntity)entity;
      rpentity.onAttack(source,risk, damage);
      }
      
    Log4J.finishMethod(logger,"attack");
    }

  public void attackStop(RPEntity source, RPObject.ID target) throws AttributeNotFoundException
    {
    Log4J.startMethod(logger,"attackStop");
    Entity entity=objects.get(target);
    if(entity!=null && entity instanceof RPEntity)
      {
      RPEntity rpentity=(RPEntity)entity;
      rpentity.onAttackStop(source);
      }
      
    Log4J.finishMethod(logger,"attackStop");
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
    Log4J.startMethod(logger,"remove");
    logger.debug("removed "+id);

    Entity entity=objects.get(id);
    if(entity!=null)
      {
      entity.removed();
      }

    Entity object=objects.remove(id);
    sortObjects.remove(object);
    Log4J.finishMethod(logger,"remove");
    }
  
  /** Removes all the object entities */
  public void clear()
    {
    Log4J.startMethod(logger,"clear");
    objects.clear();
    sortObjects.clear();
    texts.clear();
    Log4J.finishMethod(logger,"clear");
    }
  
  private boolean collides(Entity entity)
    {
    Rectangle2D area=entity.getArea();

    for(Entity other: sortObjects)
      {
      if(!(other instanceof PassiveEntity))
        {
        if(area.intersects(other.getArea()) && !entity.getID().equals(other.getID()))
          {
          return true;
          }
        }
      }
    
    return false;
    }
  
  /** Move objects based on the lapsus of time ellapsed since the last call. */
  public void move(long delta)    
    {
    for(Entity entity: sortObjects)
      {
      if(!entity.stopped() && !collisionMap.collides(entity.getArea()) && !collides(entity))
        {
        entity.move(delta);
        }      
      }
    }
   
  /** Draw all the objects in game */
  public void draw(GameScreen screen)
    {
    sort();
    
    for(Entity entity: sortObjects)
      {
      entity.draw(screen);
      }
    }
    
  public void drawText(GameScreen screen)
    {
    texts.removeAll(textsToRemove);
    textsToRemove.clear();

    try
      {
      for(Text entity: texts)
        {
        entity.draw(screen);      
        }
      }
    catch(ConcurrentModificationException e)
      {
      logger.error("cannot draw text",e);
      }
    }
  }
  
