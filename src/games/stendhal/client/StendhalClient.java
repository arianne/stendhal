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

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JFrame;

import games.stendhal.client.gui.*;
import games.stendhal.client.entity.*;

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
  private GameLogDialog gameDialog;
  private InGameGUI gameGUI;
  private JFrame frame;
  private Configuration conf;
  
  private static StendhalClient client;  
  
  public static StendhalClient get()
    {
    if(client==null)
      {
      client=new StendhalClient(true);     
      }
      
    return client;
    }
    
  private StendhalClient(boolean logging)
    {
    super(logging);
    world_objects=new HashMap<RPObject.ID, RPObject>();
    staticLayers=new StaticGameLayers();
    gameObjects=new GameObjects(staticLayers);   
    handler=new PerceptionHandler(new StendhalPerceptionListener()); 
    gameDialog=null;
    gameGUI=null;
    
    try
      {
      // Create file.
      new File("cache/").mkdir();
      new File("cache/stendhal.cache").createNewFile();
      
      Configuration.setConfigurationFile("cache/stendhal.cache");
      conf=Configuration.getConfiguration();
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalClient::StendhalClient","X",e);
      }
    }

  protected String getGameName()
    {
    return "stendhal";
    }
    
  protected String getVersionNumber()
    {
    return stendhal.VERSION;
    }    
  
  public void setGameLogDialog(GameLogDialog gameDialog)
    {
    this.gameDialog=gameDialog;
    }
    
  public void setFrame(JFrame frame)
    {
    this.frame=frame;
    }

  public GameLogDialog getGameLogDialog()
    {
    return gameDialog;
    }
  
  public void setGameGUI(InGameGUI gui)
    {
    gameGUI=gui;
    }
  
  public InGameGUI getGameGUI()
    {
    return gameGUI;
    }

  public OutfitDialog getOutfitDialog()
    {
    return new OutfitDialog(frame, "Set outfit",11,9,5,11);
    }
  
  public void addEventLine(String text)
    {
    this.gameDialog.addLine(text);
    }
    
  public void addEventLine(String header, String text)
    {
    this.gameDialog.addLine(header,text);
    }

  public void addEventLine(String header, String text, Color color)
    {
    this.gameDialog.addLine(header,text,color);
    }

  public void addEventLine(String text,Color color)
    {
    this.gameDialog.addLine(text,color);
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
      if(Logger.loggable("StendhalClient::onPerception","D"))
        {
        Logger.trace("StendhalClient::onPerception","D",message.toString());
        }
        
      if(message.getTypePerception()==1/*Perception.SYNC*/)
        {
        Logger.trace("StendhalClient::onPerception","D","UPDATING screen position");
        GameScreen screen=GameScreen.get();
        
        
        /** Full object is normal object+hidden objects */
        RPObject hidden=message.getMyRPObject();
        RPObject object=null;
        
        for(RPObject search: message.getAddedRPObjects())
          {
          if(search.getID().equals(hidden.getID()))
            {
            object=(RPObject)search.copy();
            break;
            }
          }
        
        object.applyDifferences(hidden,null);
        
        /** We clean the game object container */
        Logger.trace("StendhalClient::onPerception","D","CLEANING static object list");
        gameObjects.clear();
        
        String zoneid=message.getRPZoneID().getID();
        staticLayers.setRPZoneLayersSet(zoneid);
        GameScreen.get().setMaxWorldSize((int)staticLayers.getWidth(),(int)staticLayers.getHeight());
        
        /** And finally place player in screen */
        Graphics2D g=screen.expose();
        g.setColor(Color.BLACK);
        g.fill(new Rectangle(0,0,640,480));
        
        double x=object.getDouble("x")-screen.getWidth()/2;
        double y=object.getDouble("y")-screen.getHeight()/2;
        
        if(x<0)
          {
          x=0;
          }
        else if(staticLayers.getWidth()!=0 && x+screen.getWidth()>staticLayers.getWidth())
          {
          x=staticLayers.getWidth()-screen.getWidth();
          }

        if(y<0)
          {
          y=0;
          }
        else if(staticLayers.getHeight()!=0 && y+screen.getHeight()>staticLayers.getHeight())
          {
          y=staticLayers.getHeight()-screen.getHeight();
          }
        
        screen.place(x,y);
        screen.move(0,0);        
        }

      handler.apply(message,world_objects);      
      }
    catch(Exception e)
      {
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
      File file=new File("cache/"+item.name);

      if(file.exists() && conf.has(item.name) && Integer.parseInt(conf.get(item.name))==item.timestamp)
        {
        Logger.trace("StendhalClient::onTransferREQ","D","File is on cache. We save transfer");
        item.ack=false;
        try
          {
          contentHandling(item.name,new FileReader(file));
          }
        catch(java.io.IOException e)          
          {
          e.printStackTrace();
          System.exit(0);
          }
        }
      else
        {
        Logger.trace("StendhalClient::onTransferREQ","D","File is NOT on cache. We have to transfer");
        item.ack=true;
        }
      }
    Logger.trace("StendhalClient::onTransferREQ","<");
   
    return items;
    }
  
  private void contentHandling(String name, Reader reader) throws IOException 
    {
    staticLayers.addLayer(reader,name);
    GameScreen.get().setMaxWorldSize((int)staticLayers.getWidth(),(int)staticLayers.getHeight());
    }
    
  protected void onTransfer(List<TransferContent> items)
    {
    Logger.trace("StendhalClient::onTransfer",">");
    for(TransferContent item: items)
      {
      try
        {
        String data=new String(item.data);
        
        new File("cache").mkdir();
        
        Writer writer=new BufferedWriter(new FileWriter("cache/"+item.name));
        writer.write(data);
        writer.close();

        Logger.trace("StendhalClient::onTransfer","D","File cached. Timestamp: "+Integer.toString(item.timestamp));
        long timestamp=item.timestamp;
        
        conf.set(item.name,Integer.toString(item.timestamp));
          
        contentHandling(item.name,new StringReader(data));
        }
      catch(java.io.IOException e)          
        {
        Logger.thrown("StendhalClient::onTransfer","X",e);
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
        Logger.thrown("StendhalClient::StendhalPerceptionListener::onAdded","X",e);
        for(StackTraceElement line: e.getStackTrace()) StendhalClient.get().addEventLine(line.toString(),Color.gray);
        }
      return false;
      }
      
    public boolean onModifiedAdded(RPObject object, RPObject changes)
      {
      // NOTE: We do handle the perception here ourselves. See that we return true 
      try
        {
        Logger.trace("StendhalClient::StendhalPerceptionListener::onModifiedAdded","D","Object("+object.getID()+") modified in Game Objects container");
        gameObjects.modifyAdded(object, changes);
        object.applyDifferences(changes,null);
        }
      catch(Exception e)
        {
        Logger.trace("StendhalClient::StendhalPerceptionListener::onModifiedAdded","X",changes.toString());
        Logger.thrown("StendhalClient::StendhalPerceptionListener::onModifiedAdded","X",e);
        for(StackTraceElement line: e.getStackTrace()) StendhalClient.get().addEventLine(line.toString(),Color.gray);
        }
      return true;
      }

    public boolean onModifiedDeleted(RPObject object, RPObject changes)
      {
      try
        {
        Logger.trace("StendhalClient::StendhalPerceptionListener::onModifiedDeleted","D","Object("+object.getID()+") modified in Game Objects container");

        Logger.trace("StendhalClient::StendhalPerceptionListener::onModifiedDeleted","D","Original("+object+") modified in Game Objects container");

        gameObjects.modifyRemoved(object, changes);
        object.applyDifferences(null,changes);
        
        Logger.trace("StendhalClient::StendhalPerceptionListener::onModifiedDeleted","D","Modified("+object+") modified in Game Objects container");
        Logger.trace("StendhalClient::StendhalPerceptionListener::onModifiedDeleted","D","Changes("+changes+") modified in Game Objects container");
        }
      catch(Exception e)
        {
        Logger.thrown("StendhalClient::StendhalPerceptionListener::onModifiedDeleted","X",e);
        for(StackTraceElement line: e.getStackTrace()) StendhalClient.get().addEventLine(line.toString(),Color.gray);
        }
      return true;
      }
  
    public boolean onDeleted(RPObject object)
      {
      try
        {
        Logger.trace("StendhalClient::StendhalPerceptionListener::onDeleted","D","Object("+object.getID()+") removed from Static Objects container");
        gameObjects.remove(object.getID());
        }
      catch(Exception e)
        {
        Logger.thrown("StendhalClient::StendhalPerceptionListener::onDeleted","X",e);
        for(StackTraceElement line: e.getStackTrace()) StendhalClient.get().addEventLine(line.toString(),Color.gray);
        }
      return false;
      }
    
    public boolean onMyRPObject(boolean changed,RPObject object)
      {
      try
        {
        if(changed)
          {          
          player=(RPObject)world_objects.get(object.getID());
          gameObjects.modifyAdded(player,object);
          player.applyDifferences(object,null);
          }
        }
      catch(Exception e)
        {
        Logger.thrown("StendhalClient::StendhalPerceptionListener::onMyRPObject","X",e);
        for(StackTraceElement line: e.getStackTrace()) StendhalClient.get().addEventLine(line.toString(),Color.gray);
        }
        
      return true;
      }

    public int onTimeout()
      {      
      Logger.trace("StendhalClient::StendhalPerceptionListener::onTimeout","W","Request resync");
      StendhalClient.get().addEventLine("Timeout: Requesting synchronization",Color.gray);
      resync();
      return 0;
      }
    
    public int onSynced()
      {
      Logger.trace("StendhalClient::StendhalPerceptionListener::onSynced","W","Synced with server state.");
      StendhalClient.get().addEventLine("Synchronization completed",Color.gray);
      return 0;
      }
    
    public int onUnsynced()
      {
      Logger.trace("StendhalClient::StendhalPerceptionListener::onUnsynced","W","Request resync");
      resync();
      return 0;
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
