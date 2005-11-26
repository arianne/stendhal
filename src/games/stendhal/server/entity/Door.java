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

import java.awt.geom.Rectangle2D;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import org.apache.log4j.Logger;

public class Door extends Portal 
  {
  private boolean open;
  
  public static void generateRPClass()
    {
    RPClass door=new RPClass("door");
    door.isA("entity");
    door.add("open",RPClass.FLAG);
    }  

  public Door() throws AttributeNotFoundException
    {
    super();
    put("type","door");
    open=false;
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }

  public void update()
    {
    super.update();
    open=false;
    if(has("open")) open=true;
    }
  
  public void open()
    {
    this.open=true;
    put("open","");
    }
    
  public void close()
    {
    this.open=false;
    remove("open");
    }
  
  public boolean isOpen()
    {
    return open;
    }
  }
