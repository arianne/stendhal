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

public class TrainingDummy extends Entity 
  {
  public static void generateRPClass()
    {
    try
      {
      RPClass dummy=new RPClass("trainingdummy");
      dummy.isA("entity");
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("Player::generateRPClass","X",e);
      }
    }
  
  public TrainingDummy() throws AttributeNotFoundException
    {
    super();
    put("type","trainingdummy");
    }
  }
