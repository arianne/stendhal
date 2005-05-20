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
package games.stendhal.server.entity;

import java.awt.*;
import java.awt.geom.*;
import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import java.util.*;

import games.stendhal.common.*;
import games.stendhal.server.*;

public class Sheep extends Creature
  {
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
      sheep.isA("npc");
      sheep.add("weight",RPClass.BYTE);
      sheep.add("eat",RPClass.FLAG);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("Sheep::generateRPClass","X",e);
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
    setbaseHP(HP);

    hungry=0;
    update();
    Logger.trace("Sheep::Sheep","D","Created Sheep: "+this.toString());
    }
  
  public Sheep(RPObject object, Player owner) throws AttributeNotFoundException
    {
    super(object);
    put("type","sheep");
    this.owner=owner;

    hungry=0;
    
    update();
    Logger.trace("Sheep::Sheep","D","Created Sheep: "+this.toString());
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
    Logger.trace("Sheep::logic",">");
    
    hungry++;
    Food food=null;
    
    if(hungry>100 && (food=getNearestFood(6))!=null)
      {
      if(nextto(food,0.25))
        {
        Logger.trace("Sheep::logic","D","Sheep eats");
        setIdea("eat");
        eat(food);        
        clearPath();
        stop();
        }
      else
        {
        Logger.trace("Sheep::logic","D","Sheep moves to food");
        setIdea("food");
        setMovement(food,0,0);
        moveto(SPEED);
        }
      }
    else if(owner==null)
      {
      Logger.trace("Sheep::logic","D","Sheep(ownerless) moves randomly");
      setIdea("walk");
      moveRandomly(SPEED);
      }
    else if(owner!=null && !nextto(owner,0.25))
      {
      Logger.trace("Sheep::logic","D","Sheep(owner) moves to Owner");
      setIdea("follow");
      setMovement(owner,0,0);
      moveto(SPEED);
      }
    else
      {
      Logger.trace("Sheep::logic","D","Sheep has nothing to do");
      setIdea("stop");
      stop();
      clearPath();
      }

    if(owner!=null && owner.has("text") && owner.get("text").contains("sheep"))
      {
      Logger.trace("Sheep::logic","D","Sheep(owner) moves to Owner");
      setIdea("follow");
      clearPath();
      setMovement(owner,0,0);
      moveto(SPEED);
      }

    if(!stopped())
      {
      StendhalRPAction.move(this);
      }
      
    world.modify(this);
    Logger.trace("Sheep::logic","<");
    }
  }
