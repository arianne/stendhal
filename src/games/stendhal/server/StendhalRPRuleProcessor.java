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

import org.python.util.PythonInterpreter;
import org.python.core.*;

import marauroa.common.*;
import marauroa.common.net.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import java.util.*;
import java.io.*;

public class StendhalRPRuleProcessor implements IRPRuleProcessor
  {
  private RPServerManager rpman; 
  private RPWorld world;
  private TransferContent city_map_layer0;
  private TransferContent content_1;

  private static byte[] getBytesFromFile(String file) throws IOException 
    {
    InputStream is = new FileInputStream(file);
    
    long length = new File(file).length();
    byte[] bytes = new byte[(int)length];
    
    // Read in the bytes
    int offset = 0;
    int numRead = 0;
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
    city_map_layer0=new TransferContent();
    city_map_layer0.name="city_map_layer0";
    city_map_layer0.cacheable=true;
    city_map_layer0.timestamp=0;
    city_map_layer0.data=getBytesFromFile("games/stendhal/server/maps/city_layer0.txt");
    }


  /** Set the context where the actions are executed.
   *  @param zone The zone where actions happens. */
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
      if(action.get("type").equals("add"))
        {
        RPObject object=world.get(id);
        object.put("result",action.getInt("a")+action.getInt("b"));
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

  /** Notify it when a new turn happens */
  synchronized public void nextTurn()
    {
    Logger.trace("StendhalRPRuleProcessor::nextTurn",">");
    Logger.trace("StendhalRPRuleProcessor::nextTurn","<");
    }

  synchronized public boolean onInit(RPObject object) throws RPObjectInvalidException
    {
    Logger.trace("StendhalRPRuleProcessor::onInit",">");
    try
      {
      object.put("zoneid","village");
      world.add(object);
      
      try        
        {
        List<TransferContent> contents=new LinkedList<TransferContent>();
        contents.add(city_map_layer0);
        rpman.transferContent(object.getID(),contents);
        }
      catch(AttributeNotFoundException e)
        {
        }
        
      
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


