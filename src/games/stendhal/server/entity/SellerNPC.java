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

public class SellerNPC extends NPC 
  {
  public static void generateRPClass()
    {
    try
      {
      RPClass npc=new RPClass("sellernpc");
      npc.isA("npc");
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("SellerNPC::generateRPClass","X",e);
      }
    }
    
  public SellerNPC() throws AttributeNotFoundException
    {
    super();
    put("type","sellernpc");
    }

  public boolean chat(Player player) throws AttributeNotFoundException
    {
    return false;
    }

  public boolean move()
    {    
    if(getdy()==0)
      {
      setdy(0.2);   
      }
      
    if(gety()<=28) 
      {
      setdy(0.1);   
      return true; 
      }
      
    if(gety()>=32) 
      {
      setdy(-0.1);    
      return true; 
      }

    return false; 
    }
  }
