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

import java.util.*;
import java.io.*;

import marauroa.client.*;
import marauroa.client.net.*;
import marauroa.common.net.*;
import marauroa.common.game.*;

public class StendhalClient extends ariannexp 
  {
  private Map<RPObject.ID,RPObject> world_objects;
  private PerceptionHandler handler;    
   
  private RPObject player;
    
  private StaticGameLayers staticLayers;
  private GameObjects gameObjects;
  
  private boolean keepRunning=true;
  
  public void requestLogout()
    {
    keepRunning=false;
    }
  
  public boolean shouldContinueGame()
    {
    return keepRunning;
    }

  public StendhalClient()
    {
    world_objects=new HashMap<RPObject.ID, RPObject>();
    staticLayers=new StaticGameLayers();
    gameObjects=new GameObjects();   
    handler=new PerceptionHandler(new StendhalPerceptionListener()); 
    }
  
  public StaticGameLayers getStaticGameLayers()
    {
    return staticLayers;
    }
  
  public GameObjects getGameObjects()
    {
    return gameObjects;
    }
  
  public RPObject getPlayer()
    {
    return player;
    }
  
  protected void onPerception(MessageS2CPerception message)
    {
    try
      {
      if(message.getTypePerception()==1/*Perception.SYNC*/)
        {
        System.out.println("UPDATING screen position");
        GameScreen screen=GameScreen.get();
        RPObject object=message.getMyRPObject();
        screen.place(object.getDouble("x")-screen.getWidth()/2,object.getDouble("y")-screen.getHeight()/2);
        
        System.out.println("CLEANING static object list");
        gameObjects.clear();
        
        String zoneid=message.getRPZoneID().getID();
        staticLayers.setRPZoneLayersSet(zoneid);
        }

      handler.apply(message,world_objects);      
      }
    catch(Exception e)
      {
      e.printStackTrace();
      System.exit(-1);
      }
    }
  
  protected List<TransferContent> onTransferREQ(List<TransferContent> items)
    {
    for(TransferContent item: items)
      {
      item.ack=true;
      }
   
    return items;
    }
  
  protected void onTransfer(List<TransferContent> items)
    {
    System.out.println("Transfering ----");
    for(TransferContent item: items)
      {
      System.out.println(item);
      for(byte ele: item.data) System.out.print((char)ele);
      
      try
        {        
        staticLayers.addLayer(new StringReader(new String(item.data)),item.name);
        }
      catch(java.io.IOException e)          
        {
        e.printStackTrace();
        System.exit(0);
        }
      }
    }
  
  protected void onAvailableCharacters(String[] characters)
    {
    chooseCharacter(characters[0]);
    }
  
  protected void onServerInfo(String[] info)
    {
    }
  
  protected void onError(int code, String reason)
    {
    System.out.println(reason);
    }

  class StendhalPerceptionListener extends DefaultPerceptionListener
    {
    public boolean onAdded(RPObject object)
      {
      try
        {
        System.out.println("Object("+object.getID()+") added to Static Objects container");            
        gameObjects.add(object);
        }
      catch(Exception e)
        {
        }
      return false;
      }
      
    public boolean onModifiedAdded(RPObject object, RPObject changes)
      {
      try
        {
        System.out.println("Object("+object.getID()+") modified in Static Objects container");            
        gameObjects.modify(object);
        }
      catch(Exception e)
        {
        }
      return false;
      }

    public boolean onModifiedDeleted(RPObject object, RPObject changes)
      {
      try
        {
        gameObjects.modify(object);
        }
      catch(Exception e)
        {
        }
      return false;
      }
  
    public boolean onDeleted(RPObject object)
      {
      try
        {
        gameObjects.remove(object.getID());
        }
      catch(Exception e)
        {
        }
      return false;
      }
    
    public boolean onMyRPObject(boolean changed,RPObject object)
      {
      if(changed)
        {
        player=object;
        }
        
      return false;
      }
      
    public int onException(Exception e, marauroa.common.net.MessageS2CPerception perception)      
      {
      e.printStackTrace();
      System.out.println(perception);
      System.exit(-1);
      return 0;
      }
    }
  }
