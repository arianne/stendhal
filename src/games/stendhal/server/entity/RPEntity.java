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

public class RPEntity extends ActiveEntity 
  {
  private int atk;
  private int def;
  private int hp;
  private int xp;
  
  public static void generateRPClass()
    {
    try
      {
      RPClass entity=new RPClass("rpentity");
      entity.isA("activeentity");
      entity.add("xp",RPClass.SHORT);
      entity.add("hp",RPClass.BYTE);
      entity.add("atk",RPClass.BYTE);
      entity.add("def",RPClass.BYTE);
      entity.add("risk",RPClass.BYTE);
      entity.add("damage",RPClass.BYTE);
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
    
    if(has("atk")) atk=getInt("atk");
    if(has("def")) def=getInt("def");
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
  }
