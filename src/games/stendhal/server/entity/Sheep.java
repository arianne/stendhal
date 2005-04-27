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

import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import java.util.*;
import games.stendhal.server.*;

public class Sheep extends NPC
  {
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
  
  public Sheep(Player owner) throws AttributeNotFoundException
    {
    super();
    this.owner=owner;
    put("type","sheep");
    put("x",0);
    put("y",0);
    put("dx",0);
    put("dy",0);

    hungry=0;
    update();
    Logger.trace("Sheep::Sheep","D","Created Sheep: "+this.toString());
    }
  
  public Sheep(RPObject object, Player owner) throws AttributeNotFoundException
    {
    super(object);
    this.owner=owner;

    hungry=0;
    
    update();
    Logger.trace("Sheep::Sheep","D","Created Sheep: "+this.toString());
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

  private Food getNearestFood(Sheep sheep, double range)
    {
    double x=sheep.getx();
    double y=sheep.gety();
    
    double distance=range*range; // We save this way several sqrt operations
    Food chosen=null;
    
    for(Food food: rp.getFoodItems())
      {
      double fx=food.getx();
      double fy=food.gety();
      
      if(Math.abs(fx-x)<range && Math.abs(fy-y)<range && food.getAmount()>0)
        {
        if(this.distance(food)<distance)
          {
          chosen=food;
          distance=this.distance(food);
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
      put("eat","");
      food.setAmount(amount-1);
      world.modify(food);
      hungry=0;
      
      if(weight<100)
        {
        setWeight(weight+1);
        world.modify(this);
        }
      }
    }

  private int escapeCollision;
  private int hungry;
  
  public void setMovement(double x, double y, double min, double max)
    {
    if(distance(x,y)<min && this.hasPath())
      {
      clearPath();
      }

    if(distance(x,y)>max && !hasPath())
      {
      List<Path.Node> path=Path.searchPath(this,x,y);
      setPath(path,false);
      }
    }
    
  public void moveto(double speed)
    {
    if(escapeCollision>0) escapeCollision--;
    
    if(hasPath() && collided())
      {
      Logger.trace("Path::randomPath","D","Collision");
      setdx(Math.random()*speed*2-speed);
      setdy(Math.random()*speed*2-speed);
      escapeCollision=6;
      }
    else if(escapeCollision==0 && hasPath() && Path.followPath(this,speed))
      {
      clearPath();
      }
    }

  public void logicWithoutOwner()
    {
    if(escapeCollision>0) escapeCollision--;
      
    if(escapeCollision==0)
      {
      setIdea("random");
      Logger.trace("Sheep::logic","D","Sheep("+getx()+","+gety()+") is moving randomly");
      setdx(Math.random()*0.4-0.2);
      setdy(Math.random()*0.4-0.2);
      escapeCollision=6;
      }
    }

  public void logicWithOwner()
    {
    setIdea("following");
    setMovement(owner.getx(),owner.gety(),2*2,8*8);
    moveto(0.25);
    }
  
  
  public void logic()
    {
    Logger.trace("Sheep::logic",">");
    if(has("eat")) 
      {
      remove("eat");
      world.modify(this);
      }
    
    hungry++;    
    
    if(hungry<100)
      {
      if(owner!=null)
        {
        logicWithOwner();
        }
      else 
        {
        logicWithoutOwner();
        }
      }
    else
      {
      Food food=null;
    
      if(weight<100 && (food=getNearestFood(this,6))!=null)
        {
        if(distance(food)<2.1*2.1) //Sheep biggest dimension
          {
          setIdea("eat");
          Logger.trace("Sheep::logic","D","Sheep("+getx()+","+gety()+") is eating food: "+food);
          eat(food);        
          }
        else
          {
          setIdea("moveToFood");
          Logger.trace("Sheep::logic","D","Sheep("+getx()+","+gety()+") is moving to food: "+food);
          setMovement(food.getx(),food.gety(),2.1*2.1,0);
          moveto(0.25);
          }      
        }
      }

    if(!stopped())
      {
      StendhalRPAction.move(this);
      }
      
    Logger.trace("Sheep::logic","<");
    }
  }
