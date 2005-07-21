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
package games.stendhal.server.entity.creature;

import games.stendhal.common.Level;
import java.awt.geom.Rectangle2D;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import org.apache.log4j.Logger;

public class OrcLord extends Creature
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(OrcLord.class);
  
  final private double SPEED=1;

  final private static int HP=70;
  final private static int ATK=25;
  final private static int DEF=14;
  final private static int XP=940; //getInitialXP(ATK,DEF,HP);

  public OrcLord() throws AttributeNotFoundException
    {
    super();
    put("class","orclord");
    put("x",0);
    put("y",0);

    setATK(ATK);
    setDEF(DEF);
    setXP(XP);
    setbaseHP(HP);
    setLevel(Level.getLevel(getXP()));

    stop();

    logger.debug("Created OrcLord: "+this);
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y+1,1,1);
    }

  public double getSpeed()
    {
    return SPEED;
    }
  }
