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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

import marauroa.common.*;
import marauroa.common.game.*;

/** Handles key inputs for ingame events */
public class StendhalKeyInputHandler extends KeyAdapter 
  {
  private Map<Integer, Object> pressed;
  private StendhalClient client;
  private boolean pressedESC;
  private long delta;
  
  public StendhalKeyInputHandler(StendhalClient client)
    {
    super();
    delta=System.currentTimeMillis();
    this.client=client;
    pressed=new HashMap<Integer,Object>();
    }
    
  public boolean isExitRequested()
    {
    return pressedESC;
    }
     
  public void onKeyPressed(KeyEvent e)  
    {
    RPAction action;
    
    if(e.getKeyCode()==KeyEvent.VK_LEFT && e.isControlDown())
      {
      action=new RPAction();
      action.put("type","face");
      action.put("dir",0);
      client.send(action);
      }
    else if(e.getKeyCode()==KeyEvent.VK_RIGHT && e.isControlDown())
      {
      action=new RPAction();
      action.put("type","face");
      action.put("dir",1);
      client.send(action);
      }
    else if(e.getKeyCode()==KeyEvent.VK_UP && e.isControlDown())
      {
      action=new RPAction();
      action.put("type","face");
      action.put("dir",2);
      client.send(action);
      }
    else if(e.getKeyCode()==KeyEvent.VK_DOWN && e.isControlDown())
      {
      action=new RPAction();
      action.put("type","face");
      action.put("dir",3);
      client.send(action);
      }
    else if(e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_RIGHT)
      {
      RPObject object=client.getPlayer();
      
      double dx=0;
      try
        {
        dx=object.getDouble("dx");
        }
      catch(AttributeNotFoundException ex)
        {
        Logger.thrown("StendhalKeyInputHandler::onKeyPressed","X",ex);
        }
        
      if(dx==0) 
        {
        dx=(e.getKeyCode()==KeyEvent.VK_LEFT?-1:1)*0.4;
        }
      else if(Math.abs(dx)<1)
        {
        dx=dx+(e.getKeyCode()==KeyEvent.VK_LEFT?-1:1)*0.1;
        }
      else
        {
        dx=1;
        return;        
        }
        
      action=new RPAction();
      action.put("type","move");
      action.put("dx",dx);
      client.send(action);
      }
    else if(e.getKeyCode()==KeyEvent.VK_UP || e.getKeyCode()==KeyEvent.VK_DOWN)
      {
      RPObject object=client.getPlayer();
      
      double dy=0;
      try
        {
        dy=object.getDouble("dy");
        }
      catch(AttributeNotFoundException ex)
        {
        Logger.thrown("StendhalKeyInputHandler::onKeyPressed","X",ex);
        }
        
      if(dy==0) 
        {
        dy=(e.getKeyCode()==KeyEvent.VK_UP?-1:1)*0.4;
        }
      else if(Math.abs(dy)<1)
        {
        dy=dy+(e.getKeyCode()==KeyEvent.VK_UP?-1:1)*0.1;
        }
      else
        {
        dy=1;
        return;
        }
        
      action=new RPAction();
      action.put("type","move");
      action.put("dy",dy);
      client.send(action);
      }
    }
    
  public void onKeyReleased(KeyEvent e)  
    {
    RPAction action=new RPAction();
    action.put("type","move");
    
    switch(e.getKeyCode())
      {
      case KeyEvent.VK_LEFT:
      case KeyEvent.VK_RIGHT:
        action.put("dx",0);
        client.getPlayer().put("dx",0);
        try
          {
//          client.getGameObjects().modify(client.getPlayer());
          }
        catch(AttributeNotFoundException ex)
          {
          Logger.thrown("StendhalKeyInputHandler::onKeyReleased","X",ex);
          }
        break;
      case KeyEvent.VK_UP:
      case KeyEvent.VK_DOWN:      
        action.put("dy",0);
        client.getPlayer().put("dy",0);
        try
          {
//          client.getGameObjects().modify(client.getPlayer());
          }
        catch(Exception ex)
          {
          Logger.thrown("StendhalKeyInputHandler::onKeyReleased","X",ex);
          }
        break;
      }

    if(action.has("dx") || action.has("dy"))
      {
      Logger.trace("StendhalKeyInputHandler::onKeyReleased","D","Sending action "+action);
      client.send(action);
      }    
    }
    
  public void keyPressed(KeyEvent e) 
    {
//    if(!pressed.containsKey(new Integer(e.getKeyCode())))
    if(System.currentTimeMillis()-delta>100)
      {
      delta=System.currentTimeMillis();
      onKeyPressed(e);
      pressed.put(new Integer(e.getKeyCode()),null);
      }      
    }
      
  public void keyReleased(KeyEvent e) 
    {
    onKeyReleased(e);
    pressed.remove(new Integer(e.getKeyCode()));
    }

  public void keyTyped(KeyEvent e) 
    {
    // if we hit escape, then quit the game
    if (e.getKeyChar() == 27) 
      {
      client.requestLogout();
      }
    }
  }
