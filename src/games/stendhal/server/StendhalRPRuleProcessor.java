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

public class StendhalRPRuleProcessor implements IRPRuleProcessor
  {
  private RPServerManager rpman; 
  private RPWorld world;
  
  private LinkedList<RPObject> playersObject;
  private LinkedList<RPObject> playersObjectRmText;
  
  public StendhalRPRuleProcessor() throws FileNotFoundException, IOException 
    {
    playersObject=new LinkedList<RPObject>();
    playersObjectRmText=new LinkedList<RPObject>();
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
      
      StendhalRPAction.initialize(rpman,world);
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::setContext","!",e);
      System.exit(-1);
      }
    }

  public void approvedActions(RPObject.ID id, List<RPAction> actionList)  
    {
    try
      {
      long maxTime=0;
      
      for(RPAction action: actionList)
        {
        long val=action.getInt("when");
        if(action.get("type").equals("moveto") && val>maxTime)
          {
          Logger.trace("StendhalRPRuleProcessor::approvedActions","D",action.toString());
          maxTime=val;
          }
        }
      
      Iterator it=actionList.iterator();
      while(it.hasNext())
        {
        RPAction action=(RPAction)it.next();
        if(action.get("type").equals("moveto") && action.getInt("when")<maxTime)
          {
          it.remove();
          }
        }
      }
    catch(AttributeNotFoundException e)
      {
      Logger.thrown("StendhalRPRuleProcessor::approvedActions","X",e);
      }
    }

  public RPAction.Status execute(RPObject.ID id, RPAction action)
    {
    Logger.trace("StendhalRPRuleProcessor::execute",">");

    RPAction.Status status=RPAction.Status.SUCCESS;

    try
      {
      RPObject object=world.get(id);
      
      if(action.get("type").equals("move"))
        {
        move(object,action);
        }
      else if(action.get("type").equals("moveto"))
        {
        status=moveTo(object,action);
        }
      else if(action.get("type").equals("change"))
        {
        changeZone(object,action);
        }
      else if(action.get("type").equals("chat"))
        {
        chat(object,action);
        }          
      else if(action.get("type").equals("face"))
        {
        face(object,action);
        }          
      }
    catch(Exception e)
      {
      Logger.trace("StendhalRPRuleProcessor::execute","X",action.toString());
      Logger.thrown("StendhalRPRuleProcessor::execute","X",e);
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::execute","<");
      }

    return status;
    }
  
  private RPAction.Status moveTo(RPObject object, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    RPAction.Status status=RPAction.Status.INCOMPLETE;
    
    double dsx=action.getDouble("x");
    double dsy=action.getDouble("y");
    double x=object.getDouble("x");
    double y=object.getDouble("y");
    
    if(!object.has("moving") || (object.getDouble("dx")==0 && object.getDouble("dy")==0))
      {
      object.put("moving",1);
      if(object.has("stopped")) object.remove("stopped");
      object.put("dx",((dsx-x)/10>1?1:(dsx-x)/10));
      object.put("dy",((dsy-y)/10>1?1:(dsy-y)/10));
      }
    
    if(Math.abs(dsx-x)<0.1 || Math.abs(dsy-y)<0.1)
      {
      if(object.has("moving")) object.remove("moving");
        
      object.put("dx",0);
      object.put("dy",0);
      
      if(Math.abs(dsx-x)<0.1 || Math.abs(dsy-y)<0.1)
        {
        status=RPAction.Status.SUCCESS;
        }
      }
      
    world.modify(object);
    return status;
    }
    
  private void move(RPObject object, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPRuleProcessor::move",">");
    if(action.has("dx")) 
      { 
      double dx=action.getDouble("dx");
      object.put("dx",dx<1?dx:1);
      }
      
    if(action.has("dy")) 
      {
      double dy=action.getDouble("dy");
      object.put("dy",dy<1?dy:1);
      }
    
    if(object.getDouble("dx")==0 && object.getDouble("dy")==0)
      {
      object.put("stopped","");
      }
    else
      {
      if(object.has("stopped")) object.remove("stopped");
      }
    
    StendhalRPAction.face(object,object.getDouble("dx"),object.getDouble("dy"));
    world.modify(object);
    
    Logger.trace("StendhalRPRuleProcessor::move","<");
    }
   
  private void changeZone(RPObject object, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPRuleProcessor::changeZone",">");
    if(action.has("dest") && !object.get("zoneid").equals(action.get("dest")))
      {
      StendhalRPAction.changeZone(object,action.get("dest"));
      StendhalRPAction.transferContent(object);
      }
    Logger.trace("StendhalRPRuleProcessor::changeZone","<");
    }
  
  private void chat(RPObject object, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPRuleProcessor::chat",">");
    if(action.has("text")) 
      {
      object.put("text",action.get("text"));
      world.modify(object);
      
      playersObjectRmText.add(object);
      }
    Logger.trace("StendhalRPRuleProcessor::chat","<");
    }

  private void face(RPObject object, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPRuleProcessor::face",">");
    if(action.has("dir")) 
      {
      object.put("dir",action.get("dir"));
      world.modify(object);
      }
    Logger.trace("StendhalRPRuleProcessor::face","<");
    }

  /** Notify it when a new turn happens */
  synchronized public void beginTurn()
    {
    Logger.trace("StendhalRPRuleProcessor::beginTurn",">");
    try
      {
      for(RPObject object: playersObjectRmText)
        {
        if(object.has("text"))
          {
          object.remove("text");
          world.modify(object);
          }
        }
      
      playersObjectRmText.clear();
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::beginTurn","X",e);
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::beginTurn","<");
      }
    }
    
  synchronized public void endTurn()
    {
    Logger.trace("StendhalRPRuleProcessor::endTurn",">");
    try
      {
      for(RPObject object: playersObject)
        {
        if(!object.has("stopped"))
          {
          StendhalRPAction.move(object);
          }
        }
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::endTurn","X",e);
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::endTurn","<");
      }
    }

  synchronized public boolean onInit(RPObject object) throws RPObjectInvalidException
    {
    Logger.trace("StendhalRPRuleProcessor::onInit",">");
    try
      {
      object.put("zoneid","village");
      object.put("dx",0);
      object.put("dy",0);
      world.add(object,true);      
      StendhalRPAction.transferContent(object);
      
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(object.getID());
      
      double x=object.getDouble("x");
      double y=object.getDouble("y");
      
      while(zone.collides(object,x,y))
        {
        x=x+(Math.random()*6-3);
        y=y+(Math.random()*6-3);
        object.put("x",x);
        object.put("y",y);
        }
      
      playersObject.add(object);
      return true;
      }
    catch(Exception e)
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
      
      world.remove(id);

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

      world.remove(id);
        
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


