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
package games.stendhal.server;

import marauroa.common.*;
import marauroa.common.net.*;
import marauroa.common.game.*;
import marauroa.server.game.*;

import games.stendhal.common.*;

import java.util.*;
import java.io.*;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class StendhalRPRuleProcessor implements IRPRuleProcessor
  {
  private RPServerManager rpman; 
  private RPWorld world;
  private TransferContent city_map_layer0;
  private TransferContent city_map_layer1;
  private CollisionDetection collisionMap;
  
  private LinkedList<RPObject> playersObject;
  
  private static Rectangle2D getCollisionArea(String type, double x, double y)
    {
    if(type.equals("player"))
      {
      return new Rectangle.Double(x,y,1,1);
      }
    else
      {
      return new Rectangle.Double(x,y,1,1);
      }
    }

  private static byte[] getBytesFromFile(String file) throws IOException 
    {
    InputStream is = new FileInputStream(file);
    
    long length = new File(file).length();
    byte[] bytes = new byte[(int)length];
    
    // Read in the bytes
    int offset = 0;
    int numRead;
    while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) 
      {
      offset += numRead;
      }
    
    if(offset < bytes.length) 
      {
      throw new IOException("Could not completely read file "+file);
      }
    
    // Close the input stream and return bytes
    is.close();
    return bytes;
    }

  public StendhalRPRuleProcessor() throws FileNotFoundException, IOException 
    {
    collisionMap=new CollisionDetection();
    city_map_layer0=new TransferContent();
    city_map_layer0.name="city_map_layer0";
    city_map_layer0.cacheable=true;
    city_map_layer0.timestamp=0;
    city_map_layer0.data=getBytesFromFile("games/stendhal/server/maps/city_layer0.txt");
    collisionMap.addLayer(new FileReader("games/stendhal/server/maps/city_layer0.txt"));

    city_map_layer1=new TransferContent();
    city_map_layer1.name="city_map_layer1";
    city_map_layer1.cacheable=true;
    city_map_layer1.timestamp=0;
    city_map_layer1.data=getBytesFromFile("games/stendhal/server/maps/city_layer1.txt");
    collisionMap.addLayer(new FileReader("games/stendhal/server/maps/city_layer1.txt"));
    
    playersObject=new LinkedList<RPObject>();
    }


  /**
   * Set the context where the actions are executed.
   *
   * @param rpman
   * @param world
   */
  public void setContext(RPServerManager rpman, RPWorld world)
    {
    try
      {
      this.rpman=rpman;
      this.world=world;
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::setContext","!",e);
      System.exit(-1);
      }
    }

  public void approvedActions(RPObject.ID id, List<RPAction> actionList)
    {
    }

  public RPAction.Status execute(RPObject.ID id, RPAction action)
    {
    Logger.trace("StendhalRPRuleProcessor::execute",">");

    RPAction.Status status=RPAction.Status.FAIL;

    try
      {
      if(action.get("type").equals("move"))
        {
        RPObject object=world.get(id);
        object.put("dx",action.getDouble("dx"));
        object.put("dy",action.getDouble("dy"));
        world.modify(object);
        }
      else if(action.get("type").equals("change"))
        {
        RPObject object=world.get(id);        
        world.changeZone(id.getZoneID(),action.get("dest"),object);
        }
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::execute","X",e);
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::execute","<");
      }

    return status;
    }
  
  private void move(RPObject object) throws Exception
    {
    Logger.trace("StendhalRPRuleProcessor::move",">");
    /** TODO: Code it */
    double x=object.getDouble("x");
    double y=object.getDouble("y");
    double dx=object.getDouble("dx");
    double dy=object.getDouble("dy");
    
    if(x>35)
      {
      object.put("dx",-0.5);
      }
    
    if(x<24)
      {
      object.put("dx",0.5);
      }
      
    Rectangle2D collisionArea=getCollisionArea(object.get("type"),x+dx,y+dy);
    if(collisionMap.collides(collisionArea)==false)
      {
      Logger.trace("StendhalRPRuleProcessor::move","D","Moving to ("+(x+dx)+","+(y+dy)+")");
      collisionMap.printaround((int)(x+dx),(int) (y+dy),5);
      object.put("x",x+dx);
      object.put("y",y+dy);
      }        
    else
      {
      /* Collision */
      Logger.trace("StendhalRPRuleProcessor::move","D","COLLISION!!! at ("+(x+dx)+","+(y+dy)+")");
      collisionMap.printaround((int)(x+dx),(int) (y+dy),5);
      object.put("dx",0);
      object.put("dy",0);
      }
    
    world.modify(object);
    Logger.trace("StendhalRPRuleProcessor::move","<");
    }

  /** Notify it when a new turn happens */
  synchronized public void nextTurn()
    {
    Logger.trace("StendhalRPRuleProcessor::nextTurn",">");
    try
      {
      for(RPObject object: playersObject)
        {
        move(object);
        }
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::nextTurn","X",e);
      }    
      
    Logger.trace("StendhalRPRuleProcessor::nextTurn","<");
    }

  synchronized public boolean onInit(RPObject object) throws RPObjectInvalidException
    {
    Logger.trace("StendhalRPRuleProcessor::onInit",">");
    try
      {
      object.put("zoneid","village");
      object.put("x",30);
      object.put("y",30);
      object.put("dx",0.5);
      object.put("dy",-0.5);
      world.add(object);
      
      try        
        {
        List<TransferContent> contents=new LinkedList<TransferContent>();
        contents.add(city_map_layer0);
        contents.add(city_map_layer1);
        rpman.transferContent(object.getID(),contents);
        }
      catch(AttributeNotFoundException e)
        {
        }        
      
      playersObject.add(object);
      return true;
      }
    catch(NoRPZoneException e)
      {
      Logger.thrown("StendhalRPRuleProcessor::onInit","X",e);
      return false;
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::onInit","<");
      }
    }

  synchronized public boolean onExit(RPObject.ID id)
    {
    Logger.trace("StendhalRPRuleProcessor::onExit",">");
    try
      {
      for(RPObject object: playersObject)
        {
        if(object.getID().equals(id))
          {
          boolean result=playersObject.remove(object);
          Logger.trace("StendhalRPRuleProcessor::onExit","D","Removed Player was "+result);
          break;
          }
        }

      return true;
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::onExit","X",e);
      return true;
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::onExit","<");
      }
    }

  synchronized public boolean onTimeout(RPObject.ID id)
    {
    Logger.trace("StendhalRPRuleProcessor::onTimeout",">");
    try
      {
      for(RPObject object: playersObject)
        {
        if(object.getID().equals(id))
          {
          playersObject.remove(object);
          break;
          }
        }
        
      return true;
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::onTimeout","X",e);
      return true;
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::onTimeout","<");
      }
    }
  }


