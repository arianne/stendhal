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

public class Sheep extends NPC
  {
  private int weight;
  private Player owner;
  private int i;
  
  public static void generateRPClass()
    {
    try
      {
      RPClass sheep=new RPClass("sheep");
      sheep.isA("npc");
      sheep.add("weight",RPClass.BYTE);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("NPC::generateRPClass","X",e);
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
    }
  
  public Sheep(RPObject object, Player owner) throws AttributeNotFoundException
    {
    super(object);
    this.owner=owner;
    
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

  public boolean move(RPWorld world)
    {
    if(i++%5==0)
      {
      if(owner==null)
        {
        setdx(Math.random()*0.4-0.2);
        setdy(Math.random()*0.4-0.2);
        return true;
        }
      else
        {
        double rndx=owner.getx()-getx();
        double rndy=owner.gety()-gety();
  
        if(Math.abs(rndx)<2 && Math.abs(rndy)<2)
          {
          stop();
          return true;
          }
        else
          {
          double max=Math.abs(Math.abs(rndx)>Math.abs(rndy)?rndx:rndy);
          rndx=(rndx/Math.abs(max))*0.2;
          rndy=(rndy/Math.abs(max))*0.2;
          
          setdx(Math.abs(rndx)>0.2?0.2*Math.signum(rndx):rndx);
          setdy(Math.abs(rndy)>0.2?0.2*Math.signum(rndx):rndy);
          
          return true;      
          }
        }
      }

    return false;
    }
  }
