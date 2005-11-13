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
package games.stendhal.server.entity.creature;

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.entity.Food;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import java.awt.geom.Rectangle2D;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.apache.log4j.Logger;

public class Sheep extends Creature
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Sheep.class);
  
  final private double SPEED=0.25;
  
  final private static int HP=30;
  final private static int ATK=0;
  final private static int DEF=1;
  final private static int XP=0;
  
  private int weight;
  private Player owner;
  
  public static void generateRPClass()
    {    
    try
      {
      RPClass sheep=new RPClass("sheep");
      sheep.isA("creature");
      sheep.add("weight",RPClass.BYTE);
      sheep.add("eat",RPClass.FLAG);
      }
    catch(RPClass.SyntaxException e)
      {
      logger.error("cannot generate RPClass",e);
      }
    }
  
  public Sheep() throws AttributeNotFoundException
    {
    this(null);
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }  
    
  public Sheep(Player owner) throws AttributeNotFoundException
    {
    super();
    this.owner=owner;
    put("type","sheep");
    put("x",0);
    put("y",0);
    
    setATK(ATK);
    setDEF(DEF);
    setXP(XP);
    setBaseHP(HP);
    setHP(HP);

    hungry=0;
    update();
    logger.debug("Created Sheep: "+this);
    }
  
  public Sheep(RPObject object, Player owner) throws AttributeNotFoundException
    {
    super(object);
    put("type","sheep");
    this.owner=owner;

    hungry=0;
    
    update();
    logger.debug("Created Sheep: "+this);
    }
  
  public void setOwner(Player owner)
    {
    this.owner=owner;
    }

  public Player getOwner()
    {
    return owner;
    }

  public void update() throws AttributeNotFoundException
    {
    super.update();
    
    if(has("weight")) weight=getInt("weight");
    }
  
  public void onDead(RPEntity who)
    {
    if(owner!=null)
      {
      owner.removeSheep(this);
      }
    else
      {
      rp.removeNPC(this);
      }
    
      
    super.onDead(who);
    }

  public double getSpeed()
    {
    return SPEED;
    }

  public void setWeight(int weight)
    {
    this.weight=weight;
    put("weight",weight);
    }
  
  public int getWeight()
    {
    return weight;
    }

  private Food getNearestFood(double range)
    {
    int x=getx();
    int y=gety();
    
    double distance=range*range; // We save this way several sqrt operations
    Food chosen=null;
    
    for(Food food: rp.getFoodItems())
      {
      if(food.get("zoneid").equals(get("zoneid")))
        {
        int fx=food.getx();
        int fy=food.gety();
        
        if(Math.abs(fx-x)<range && Math.abs(fy-y)<range && food.getAmount()>0)
          {
          if(this.distance(food)<distance)
            {
            chosen=food;
            distance=this.distance(food);
            }
          }
        }
      }
    
    return chosen;
    }  
  
  private void eat(Food food)
    {
    int amount=food.getAmount();
    if(amount>0)
      {
      food.setAmount(amount-1);
      world.modify(food);
      hungry=0;
      
      if(weight<100)
        {
        setWeight(weight+1);
        }
      }
    }

  private int hungry;

  public void logic()
    {
    Log4J.startMethod(logger,"logic");

    if(getNearestPlayer(20)==null && owner==null) // if there is no player near and none will see us... 
      {
      stop();
      
      world.modify(this);
      return;
      }
    
    hungry++;
    Food food=null;
    
    if(hungry>100 && (food=getNearestFood(6))!=null)
      {
      if(nextto(food,0.25))
        {
        logger.debug("Sheep eats");
        setIdea("eat");
        eat(food);
        clearPath();
        stop();
        }
      else
        {
        logger.debug("Sheep moves to food");
        setIdea("food");
//        setMovement(food,0,0);
        setAsynchonousMovement(food,0,0);
        moveto(SPEED);
        }
      }
    else if(owner==null)
      {
      logger.debug("Sheep(ownerless) moves randomly");
      setIdea("walk");
      moveRandomly(SPEED);
      }
    else if(owner!=null && !nextto(owner,0.25))
      {
      logger.debug("Sheep(owner) moves to Owner");
      setIdea("follow");
//      setMovement(owner,0,0);
      setAsynchonousMovement(owner,0,0);
      moveto(SPEED);
      }
    else
      {
      logger.debug("Sheep has nothing to do");
      setIdea("stop");
      stop();
      clearPath();
      }

    if(owner!=null && owner.has("text") && owner.get("text").contains("sheep"))
      {
      logger.debug("Sheep(owner) moves to Owner");
      setIdea("follow");
      clearPath();
//      setMovement(owner,0,0);
      setAsynchonousMovement(owner,0,0);
      moveto(SPEED);
      }

    if(!stopped())
      {
      StendhalRPAction.move(this);
      // if we collided with something we stop and clear the path
      if (collided())
      {
        stop();
        clearPath();
      }
      }
      
    world.modify(this);
    Log4J.finishMethod(logger,"logic");
    }
  }
