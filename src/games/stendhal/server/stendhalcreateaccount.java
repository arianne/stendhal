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
package games.stendhal.server;

import marauroa.common.game.*;
import marauroa.server.game.*;
import marauroa.server.Configuration;

/** The mapacmancreateaccount extends the createaccount class of marauroa package
 *  so that it defines the specific behaviour for an account of mapacman */
public class stendhalcreateaccount extends marauroa.server.createaccount
  {
  public static void main (String[] args)
    {
    stendhalcreateaccount instance=new stendhalcreateaccount();
    System.exit(instance.run(args));
    }
  
  public stendhalcreateaccount()
    {
    super();
    }
    
  public RPObject populatePlayerRPObject(IPlayerDatabase playerDatabase) throws Exception
    {
    RPObject object=new RPObject(RPObject.INVALID_ID);
    object.put("type","player");
    object.put("name",get("character"));
    object.put("x",0);
    object.put("y",0);
    object.put("xp",0);
    object.put("base_hp",10);
    object.put("hp",10);
    object.put("atk",1);
    object.put("def",1);
    
    return object;
    }
  }

