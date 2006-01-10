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

import games.stendhal.client.gui.GameLogDialog;
import games.stendhal.client.gui.InGameGUI;
import games.stendhal.client.gui.OutfitDialog;
import games.stendhal.client.gui.wt.core.WindowManager;
import games.stendhal.common.Debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import marauroa.client.ariannexp;
import marauroa.client.net.DefaultPerceptionListener;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.game.Perception;
import marauroa.common.game.RPObject;
import marauroa.common.net.MessageS2CPerception;
import marauroa.common.net.TransferContent;
import org.apache.log4j.Logger;

/**
 * This class is the glue to Marauroa, it extends ariannexp and allow us to
 * easily connect to an marauroa server and operate it easily.
 */
public class StendhalClient extends ariannexp 
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(StendhalClient.class);
  
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
  
  private static final String LOG4J_PROPERTIES = "games/stendhal/client/log4j.properties";
  
  public static StendhalClient get()
    {
    if(client==null)
      {
      client=new StendhalClient(LOG4J_PROPERTIES);     
      }
      
    return client;
    }
    
  private StendhalClient(String loggingProperties)
    {
    super(loggingProperties);
    world_objects=new HashMap<RPObject.ID, RPObject>();
    staticLayers=new StaticGameLayers();
    gameObjects=new GameObjects(staticLayers);   
    handler=new PerceptionHandler(new StendhalPerceptionListener()); 
    gameDialog=null;
    gameGUI=null;
    
    try
      {
      // Create file.
      new File(stendhal.STENDHAL_FOLDER).mkdir();
      
      new File(stendhal.STENDHAL_FOLDER+"cache/").mkdir();
      new File(stendhal.STENDHAL_FOLDER+"cache/stendhal.cache").createNewFile();
      
      Configuration.setConfigurationFile(stendhal.STENDHAL_FOLDER+"cache/stendhal.cache");
      conf=Configuration.getConfiguration();
      }
    catch(Exception e)
      {
      logger.error("cannot create StendhalClient",e);
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
// int total_hairs, int total_heads, int total_bodies, int total_clothes) {
    return new OutfitDialog(frame, "Set outfit",13,11,11,13);
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
      Log4J.startMethod(logger,"onPerception");
      if(logger.isDebugEnabled())
        {
        logger.debug("message: "+message);
        }
        
      if(message.getTypePerception()==1/*Perception.SYNC*/)
        {
        logger.debug("UPDATING screen position");
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
        logger.debug("CLEANING static object list");
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
          
        getGameGUI().inspect(null,null);
        
        screen.place(x,y);
        screen.move(0,0);        
        }
        
      /** This code emulate a perception loss. */
      if(Debug.EMULATE_PERCEPTION_LOSS && message.getTypePerception() != Perception.SYNC && (message.getPerceptionTimestamp() % 30) == 0)
        {
        return;
        }

      handler.apply(message,world_objects);      
      }
    catch(Exception e)
      {
      logger.debug("error processing message "+message,e);
      System.exit(-1);
      }
    finally
      {
      Log4J.finishMethod(logger,"onPerception");
      }
    }
  
  protected List<TransferContent> onTransferREQ(List<TransferContent> items)
    {
    Log4J.startMethod(logger,"onTransferREQ");
    for(TransferContent item: items)
      {
      File file=new File(stendhal.STENDHAL_FOLDER+"cache/"+item.name);

      if(file.exists() && conf.has(item.name) && Integer.parseInt(conf.get(item.name))==item.timestamp)
        {
        logger.debug("File is on cache. We save transfer");
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
        logger.debug("File is NOT on cache. We have to transfer");
        item.ack=true;
        }
      }
    Log4J.finishMethod(logger,"onTransferREQ");
   
    return items;
    }
  
  private void contentHandling(String name, Reader reader) throws IOException 
    {
    staticLayers.addLayer(reader,name);
    GameScreen.get().setMaxWorldSize((int)staticLayers.getWidth(),(int)staticLayers.getHeight());
    }
    
  protected void onTransfer(List<TransferContent> items)
    {
    Log4J.startMethod(logger,"onTransfer");
    for(TransferContent item: items)
      {
      try
        {
        String data=new String(item.data);
        
        new File(stendhal.STENDHAL_FOLDER+"cache").mkdir();
        
        Writer writer=new BufferedWriter(new FileWriter(stendhal.STENDHAL_FOLDER+"cache/"+item.name));
        writer.write(data);
        writer.close();

        logger.debug("File cached. Timestamp: "+Integer.toString(item.timestamp));

        
        conf.set(item.name,Integer.toString(item.timestamp));
          
        contentHandling(item.name,new StringReader(data));
        }
      catch(java.io.IOException e)          
        {
        logger.fatal("onTransfer",e);
        System.exit(0);
        }
      }
    Log4J.finishMethod(logger,"onTransfer");
    }
  
  protected void onAvailableCharacters(String[] characters)
    {
    Log4J.startMethod(logger,"onAvailableCharacters");
    try
      {
      chooseCharacter(characters[0]);
      }
    catch(Exception e)
      {
      logger.error("StendhalClient::onAvailableCharacters",e);
      }
      
    Log4J.finishMethod(logger,"onAvailableCharacters");
    }
  
  protected void onServerInfo(String[] info)
    {
    //TODO: handle this info
    }
  
  protected void onError(int code, String reason)
    {
    logger.error("got error code: "+code+" reason"+reason);
    }

  public void requestLogout()
    {
    Log4J.startMethod(logger,"requestLogout");
    keepRunning=false;
    // try to save the window configuration
    WindowManager.getInstance().save();
    Log4J.finishMethod(logger,"requestLogout");
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
        logger.debug("Object("+object.getID()+") added to Game Objects container");
        gameObjects.add(object);
        }
      catch(Exception e)
        {
        logger.error("onAdded failed, object is "+object,e);
        }
      return false;
      }
      
    public boolean onModifiedAdded(RPObject object, RPObject changes)
      {
      // NOTE: We do handle the perception here ourselves. See that we return true 
      try
        {
        logger.debug("Object("+object.getID()+") modified in Game Objects container");
        gameObjects.modifyAdded(object, changes);
        object.applyDifferences(changes,null);
        }
      catch(Exception e)
        {
        logger.debug("onModifiedAdded failed, object is "+object+", changes is "+changes,e);
        }
      return true;
      }

    public boolean onModifiedDeleted(RPObject object, RPObject changes)
      {
      try
        {
        logger.debug("Object("+object.getID()+") modified in Game Objects container");
        logger.debug("Original("+object+") modified in Game Objects container");
        
        gameObjects.modifyRemoved(object, changes);
        object.applyDifferences(null,changes);
        
        logger.debug("Modified("+object+") modified in Game Objects container");
        logger.debug("Changes("+changes+") modified in Game Objects container");
        }
      catch(Exception e)
        {
        logger.error("onModifiedDeleted failed, object is "+object+", changes is "+changes,e);
        }
      return true;
      }
  
    public boolean onDeleted(RPObject object)
      {
      try
        {
        logger.debug("Object("+object.getID()+") removed from Static Objects container");
        gameObjects.remove(object.getID());
        }
      catch(Exception e)
        {
        logger.error("onDeleted failed, object is "+object,e);
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
        logger.error("onMyRPObject failed, changed="+changed+" object="+object,e);
        }
        
      return true;
      }

    public int onTimeout()
      {      
      logger.debug("Request resync");
      StendhalClient.get().addEventLine("Timeout: Requesting synchronization",Color.gray);
      resync();
      return 0;
      }
    
    public int onSynced()
      {
      logger.debug("Synced with server state.");
      StendhalClient.get().addEventLine("Synchronization completed",Color.gray);
      return 0;
      }
    
    public int onUnsynced()
      {
      logger.debug("Request resync");
      resync();
      return 0;
      }
   
    public int onException(Exception e, marauroa.common.net.MessageS2CPerception perception)      
      {
      logger.fatal("perception caused an error: "+perception,e);
      System.exit(-1);
      
      // Never executed 
      return -1;
      }
    }
  }
