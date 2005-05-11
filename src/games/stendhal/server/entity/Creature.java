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

import games.stendhal.server.*;
import marauroa.common.game.*;

public abstract class Creature extends NPC 
  {
  private RespawnPoint point;
  
  public Creature(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    }

  public Creature() throws AttributeNotFoundException
    {
    super();
    }

  public void setRespawnPoint(RespawnPoint point)
    {
    this.point=point;
    }
  
  public RespawnPoint getRespawnPoint()
    {
    return point;
    }

  public void onDead(RPEntity who)
    {
    point.notifyDead(this);
    super.onDead(who);
    }
  }
