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
package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;
import java.awt.*;
import java.awt.geom.*;

public abstract class PassiveEntity extends Entity
  {
  public PassiveEntity(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {    
    super(gameObjects, object);
    }

  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("Displace"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","displace");
      int id=getID().getObjectID();
      rpaction.put("target",id);
      rpaction.put("x",params[0]);
      rpaction.put("y",params[1]);
      client.send(rpaction);
      }
    }
  }
