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
package games.stendhal.client;

import java.awt.geom.*;
import java.awt.event.*;
import java.awt.*;
import marauroa.common.*;
import marauroa.common.game.*;
import games.stendhal.client.entity.*;

public class StendhalMouseInputHandler implements MouseListener 
  {
  private StendhalClient client;
  private GameObjects gameObjects;
  private GameScreen screen;
  
  public StendhalMouseInputHandler(StendhalClient client)
    {
    this.client=client;
    this.gameObjects=client.getGameObjects();
    this.screen=GameScreen.get();
    }
    
  public void mouseClicked(MouseEvent e) 
    {
    Point2D point=screen.translate(e.getPoint());
    System.out.println(point);
    
    GameEntity entity=gameObjects.at(point.getX(),point.getY());
    if(entity!=null)
      {
      if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()>1)
        {
        entity.onDoubleClick();
        }
      else if(e.getButton()==MouseEvent.BUTTON1)
        {
        entity.onClick();
        }
      if(e.getButton()==MouseEvent.BUTTON3)
        {
        entity.onLeftClick();
        }

      System.out.println (entity.getClass());
      }

    if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()>1)
      {
      System.out.println ("Moving to "+point);
      RPAction action=new RPAction();
      action.put("type","moveto");
      action.put("x",point.getX()-0.9);
      action.put("y",point.getY()-1.6);
      client.send(action);
      } 
    }

  public void mousePressed(MouseEvent e) 
    {
    }

  public void mouseReleased(MouseEvent e) 
    {
    }

  public void mouseEntered(MouseEvent e) 
    {
    }

  public void mouseExited(MouseEvent e) 
    {
    }    
  }
