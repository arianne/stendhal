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
import marauroa.common.*;
import marauroa.common.net.*;
import marauroa.common.game.*;

/** This class is the glue to Marauroa, it extends ariannexp and allow us to
 *  easily connect to an marauroa server and operate it easily. */
public class StendhalClient extends ariannexp 
  {
  private Map<RPObject.ID,RPObject> world_objects;
  private PerceptionHandler handler;    
   
  private RPObject player;
    
  private StaticGameLayers staticLayers;
  private GameObjects gameObjects;
  
  private boolean keepRunning=true;
  
  public StendhalClient(boolean logging)
    {
    super(logging);
    world_objects=new HashMap<RPObject.ID, RPObject>();
    staticLayers=new StaticGameLayers();
    gameObjects=new GameObjects(staticLayers);   
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
      Logger.trace("StendhalClient::onPerception",">");
      Logger.trace("StendhalClient::onPerception","D",message.toString());
      if(message.getTypePerception()==1/*Perception.SYNC*/)
        {
        Logger.trace("StendhalClient::onPerception","D","UPDATING screen position");
        GameScreen screen=GameScreen.get();
        RPObject object=message.getMyRPObject();
        screen.place(object.getDouble("x")-screen.getWidth()/2,object.getDouble("y")-screen.getHeight()/2);
        screen.move(0,0);
        
        Logger.trace("StendhalClient::onPerception","D","CLEANING static object list");
        gameObjects.clear();
        
        String zoneid=message.getRPZoneID().getID();
        staticLayers.setRPZoneLayersSet(zoneid);
        }

      handler.apply(message,world_objects);      
      }
    catch(Exception e)
      {
      // TODO: Request synchronization instead of exiting.
      Logger.thrown("StendhalClient::onPerception","!",e);
      System.exit(-1);
      }
    finally
      {
      Logger.trace("StendhalClient::onPerception","<");
      }
    }
  
  protected List<TransferContent> onTransferREQ(List<TransferContent> items)
    {
    Logger.trace("StendhalClient::onTransferREQ",">");
    for(TransferContent item: items)
      {
      // TODO: Cache items so that we can save the transfer
      item.ack=true;
      }
    Logger.trace("StendhalClient::onTransferREQ","<");
   
    return items;
    }
  
  protected void onTransfer(List<TransferContent> items)
    {
    Logger.trace("StendhalClient::onTransfer",">");
    for(TransferContent item: items)
      {
      for(byte ele: item.data) System.out.print((char)ele);
      
      try
        {        
        staticLayers.addLayer(new StringReader(new String(item.data)),item.name);
        GameScreen.get().setMaxWorldSize((int)staticLayers.getWidth(),(int)staticLayers.getHeight());
        }
      catch(java.io.IOException e)          
        {
        e.printStackTrace();
        System.exit(0);
        }
      }
    Logger.trace("StendhalClient::onTransfer","<");
    }
  
  protected void onAvailableCharacters(String[] characters)
    {
    Logger.trace("StendhalClient::onAvailableCharacters",">");
    try
      {
      chooseCharacter(characters[0]);
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalClient::onAvailableCharacters","X",e);
      onError(1,e.getMessage());
      }
      
    Logger.trace("StendhalClient::onAvailableCharacters","<");
    }
  
  protected void onServerInfo(String[] info)
    {
    //TODO: handle this info
    }
  
  protected void onError(int code, String reason)
    {
    Logger.trace("StendhalClient::onError","X",reason);
    }

  public void requestLogout()
    {
    Logger.trace("StendhalClient::requestLogout",">");
    keepRunning=false;
    Logger.trace("StendhalClient::requestLogout","<");
    }
  
  public boolean shouldContinueGame()
    {
    return keepRunning;
    }

  class StendhalPerceptionListener extends DefaultPerceptionListener
    {
    public boolean onAdded(RPObject object)
      {
      try
        {
        Logger.trace("StendhalClient::StendhalPerceptionListener::onAdded","D","Object("+object.getID()+") added to Game Objects container");
        gameObjects.add(object);
        }
      catch(Exception e)
        {
        }
      return false;
      }
      
    public boolean onModifiedAdded(RPObject object, RPObject changes)
      {
      // NOTE: We do handle the perception here ourselves. See that we return true 
      try
        {
        Logger.trace("StendhalClient::StendhalPerceptionListener::onModifiedAdded","D","Object("+object.getID()+") modified in Game Objects container");
        object.applyDifferences(changes,null);
        gameObjects.modify(object);
        }
      catch(Exception e)
        {
        }
      return true;
      }

    public boolean onModifiedDeleted(RPObject object, RPObject changes)
      {
      try
        {
        Logger.trace("StendhalClient::StendhalPerceptionListener::onModifiedDeleted","D","Object("+object.getID()+") added to Static Objects container");
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
        Logger.trace("StendhalClient::StendhalPerceptionListener::onDeleted","D","Object("+object.getID()+") added to Static Objects container");
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
      Logger.trace("StendhalClient::StendhalPerceptionListener::onException",perception.toString());
      Logger.thrown("StendhalClient::StendhalPerceptionListener::onException","X",e);
      System.exit(-1);
      
      // Never executed 
      return -1;
      }
    }
  }
