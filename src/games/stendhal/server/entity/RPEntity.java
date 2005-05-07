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

import java.util.*;
import games.stendhal.server.*;
import marauroa.common.*;
import marauroa.common.game.*;

public class RPEntity extends ActiveEntity 
  {
  private String name;

  private double dx;
  private double dy;
  private boolean collides;
  
  private int atk;
  private int def;
  private int base_hp;
  private int hp;
  private int xp;
  
  public static void generateRPClass()
    {
    try
      {
      RPClass entity=new RPClass("rpentity");
      entity.isA("entity");
      entity.add("name",RPClass.STRING);
      entity.add("dx",RPClass.FLOAT);
      entity.add("dy",RPClass.FLOAT); 
      entity.add("xp",RPClass.SHORT);
      entity.add("base_hp",RPClass.BYTE);
      entity.add("hp",RPClass.BYTE);
      entity.add("atk",RPClass.BYTE);
      entity.add("def",RPClass.BYTE);
      entity.add("risk",RPClass.BYTE);
      entity.add("damage",RPClass.BYTE);
      entity.add("target",RPClass.INT);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("RPEntity::generateRPClass","X",e);
      }
    }
  
  public RPEntity(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    }

  public RPEntity() throws AttributeNotFoundException
    {
    super();
    }
    
  public void update() throws AttributeNotFoundException
    {
    super.update();
    
    if(has("name")) name=get("name");
    if(has("dx")) dx=getDouble("dx");
    if(has("dy")) dy=getDouble("dy");
    if(has("atk")) atk=getInt("atk");
    if(has("def")) def=getInt("def");
    if(has("base_hp")) hp=getInt("base_hp");
    if(has("hp")) hp=getInt("hp");
    if(has("xp")) xp=getInt("xp");
    }

  public void setATK(int atk)
    {
    this.atk=atk;
    put("atk",atk);
    }
      
  public int getATK()
    {
    return atk;
    }
    
  public void setDEF(int def)
    {
    this.def=def;
    put("def",def);
    }
      
  public int getDEF()
    {
    return def;
    }
    
  public void setbaseHP(int hp)
    {
    this.base_hp=hp;
    put("base_hp",hp);
    this.hp=hp;
    put("hp",hp);
    }

  public void setHP(int hp)
    {
    this.hp=hp;
    put("hp",hp);
    }
      
  public int getHP()
    {
    return hp;
    }
    
  public void setXP(int xp)
    {
    this.xp=xp;
    put("xp",xp);
    }
      
  public int getXP()
    {
    return xp;
    }
  
  public void onDamage(RPEntity who, int damage)
    {
    }

  public void setName(String name)
    {
    this.name=name;
    put("name",name);   
    }
  
  public String getName()
    {
    return name;
    }
  
  public void setdx(double dx)
    {
    this.dx=dx;
    put("dx",dx);
    }
  
  public double getdx()
    {
    return dx;
    }
  
  public void setdy(double dy)
    {
    this.dy=dy;
    put("dy",dy);
    }

  public double getdy()
    {
    return dy;
    }

  public void stop()
    {
    // HACK: FIXME
    setx(getx());
    sety(gety());
    
    setdx(0);
    setdy(0);
    }
    
  public boolean stopped()
    {
    return dx==0 && dy==0;
    }
  
  public void collides(boolean val)
    {
    collides=val;    
    }
  
  public boolean collided()
    {
    return collides;
    }
  
  private List<Path.Node> path;
  private int pathPosition;
  private boolean pathLoop;
  
  public void setPath(List<Path.Node> path, boolean cycle)
    {
    this.path=path;
    this.pathPosition=0;
    this.pathLoop=cycle;
    }
    
  public boolean hasPath()
    {
    return path!=null;
    }
  
  public void clearPath()
    {
    this.path=null;
    }
    
  public List<Path.Node> getPath()  
    {
    return path;
    }
  
  public boolean isPathLoop()
    {
    return pathLoop;
    }
  
  public int getPathPosition()
    {
    return pathPosition;
    }
  
  public boolean pathCompleted()
    {
    return path!=null && pathPosition==path.size()-1;
    }
  
  public void setPathPosition(int pathPos)
    {
    this.pathPosition=pathPos;
    }
  }
