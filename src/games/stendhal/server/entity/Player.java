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

public class Player extends ActiveEntity 
  {
  private int atk;
  private int def;
  private int hp;
  private int xp;
  private int leave;
  private boolean hasLeave;
  
  public static void generateRPClass()
    {
    try
      {
      RPClass player=new RPClass("player");
      player.isA("activeentity");
      player.add("xp",RPClass.SHORT);
      player.add("hp",RPClass.SHORT);
      player.add("atk",RPClass.SHORT);
      player.add("def",RPClass.SHORT);
      player.add("text",RPClass.STRING);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("Player::generateRPClass","X",e);
      }
    }
  
  public Player(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    put("type","player");
    update();
    hasLeave=false;
    }
    
  public void update() throws AttributeNotFoundException
    {
    super.update();
    
    atk=getInt("atk");
    def=getInt("def");
    hp=getInt("hp");
    xp=getInt("xp");
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
    
  public boolean hasLeave()
    {
    return hasLeave;
    }
    
  public void setLeave(int val)
    {
    if(val<0)
      {
      hasLeave=false;
      }
    else
      {
      hasLeave=true;
      }
    
    leave=val;
    }

  public int getLeave()
    {
    return leave;
    }
  }
