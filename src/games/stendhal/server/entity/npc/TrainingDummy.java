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
package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.RPEntity;
import java.awt.geom.Rectangle2D;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import org.apache.log4j.Logger;


public class TrainingDummy extends RPEntity 
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(TrainingDummy.class);

  public static void generateRPClass()
    {
    try
      {
      RPClass dummy=new RPClass("trainingdummy");
      dummy.isA("rpentity");
      }
    catch(RPClass.SyntaxException e)
      {
      logger.error("cannot generate RPClass",e);
      }
    }
  
  public TrainingDummy() throws AttributeNotFoundException
    {
    super();
    put("type","trainingdummy");
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y+1,1,1);
    }  

  public void onDead(RPEntity who)
    {
    setHP(getBaseHP());
    world.modify(this);
    }
  }
