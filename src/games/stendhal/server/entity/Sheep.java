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
import java.util.Random;
import games.stendhal.server.*;

public class Sheep extends NPC
  {
  private int weight;
  private int hungry;
  private Player owner;
  private int i;
  
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
    }
  
  public Sheep(RPObject object, Player owner) throws AttributeNotFoundException
    {
    super(object);
    this.owner=owner;

    hungry=0;
    
    update();
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
  
  private void moveto(double x, double y, double speed)
    {
    double rndx=x-getx();
    double rndy=y-gety();

    double max=Math.abs(Math.abs(rndx)>Math.abs(rndy)?rndx:rndy);
    rndx=(rndx/Math.abs(max));
    rndy=(rndy/Math.abs(max));
    
    setdx(speed*Math.signum(rndx));
    setdy(speed*Math.signum(rndy));

    world.modify(this);
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

  public void logic()
    {
    Logger.trace("Sheep::logic",">");
    if(i++%5==0)
      {
    if(has("eat")) 
      {
      remove("eat");
      world.modify(this);
      }
    
    hungry++;    
    Food food=null;
    
    if(owner!=null)
      {
      double distance=distance(owner);
      if(collided() || distance>15*15)
        {
        Logger.trace("Sheep::logic","D","Sheep("+getx()+","+gety()+") is moving randomly");
        setdx(Math.random()*0.4-0.2);
        setdy(Math.random()*0.4-0.2);
        world.modify(this);
        }
      else 
        {
        Logger.trace("Sheep::logic","D","Sheep("+getx()+","+gety()+") is following player: "+owner.getx()+","+owner.gety());      
        moveto(owner.getx(),owner.gety(),0.25);
        }
      }
    else 
      {
      Logger.trace("Sheep::logic","D","Sheep("+getx()+","+gety()+") is moving randomly");
      setdx(Math.random()*0.4-0.2);
      setdy(Math.random()*0.4-0.2);
      world.modify(this);
      }
    
    if(hungry>10 && (food=getNearestFood(this,6))!=null)
      {
      if(distance(food)<2.1*2.1) //Sheep biggest dimension
        {
        Logger.trace("Sheep::logic","D","Sheep("+getx()+","+gety()+") is eating food: "+food);
        eat(food);        
        }
      else if(!collided())
        {
        Logger.trace("Sheep::logic","D","Sheep("+getx()+","+gety()+") is moving to food: "+food);
        moveto(food.getx(),food.gety(),0.5);
        }      
      else
        {
        Logger.trace("Sheep::logic","D","Sheep("+getx()+","+gety()+") is moving randomly");
        setdx(Math.random()*0.4-0.2);
        setdy(Math.random()*0.4-0.2);
        world.modify(this);
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
