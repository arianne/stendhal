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
import marauroa.common.game.*;
import marauroa.common.net.*;
import marauroa.server.game.*;

import games.stendhal.common.*;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import java.util.*;
import java.io.*;
import java.net.*;


public class StendhalRPZone extends MarauroaRPZone 
  {
  private List<TransferContent> contents;
  private String entryPoint;
  private CollisionDetection collisionMap;
  private int width;
  private int height;
  
  public StendhalRPZone(String name)
    {
    super(name);
    
    contents=new LinkedList<TransferContent>();
    collisionMap=new CollisionDetection("games/stendhal/server/maps/zelda_outside_chipset.gif.collision");
    }
  
  public void setEntryPoint(String entryPoint)
    {
    this.entryPoint=entryPoint;
    }
  
  public void placeObjectAtEntryPoint(RPObject object)
    {
    String[] components=entryPoint.split(",");
    object.put("x",components[0]);
    object.put("y",components[1]);
    }
  
  public void addLayer(String name, String filename,boolean computeCollision) throws IOException
    {
    Logger.trace("StendhalRPZone::addLayer",">");
    TransferContent content=new TransferContent();
    content.name=name;
    content.cacheable=true;
    content.timestamp=(int)(new File(filename).lastModified()/1000);
    Logger.trace("StendhalRPZone::addLayer","D",Integer.toString(content.timestamp));
    content.data=getBytesFromFile(filename);
    
    contents.add(content);
    
    if(computeCollision) 
      {
      collisionMap.addLayer(new FileReader(filename));
      }
    Logger.trace("StendhalRPZone::addLayer","<");
    }
  
  public void populate(String filename) throws IOException, RPObjectInvalidException
    {
    Logger.trace("StendhalRPZone::populate",">");
    
    InputStream in=getClass().getClassLoader().getResourceAsStream(filename);
    BufferedReader file=new BufferedReader(new InputStreamReader(in));
    
    String text=file.readLine();
    String[] size=text.split(" ");
    int width=Integer.parseInt(size[0]);
    int height=Integer.parseInt(size[1]);
    
    int j=0;
    
    while((text=file.readLine())!=null)
      {
      if(text.trim().equals(""))
        {
        break;
        }
        
      String[] items=text.split(",");
      for(String item: items)
        {
        int value=Integer.parseInt(item)-481 /* Number of tiles at zelda_outside_chipset + 1 */;
        switch(value)
          {
          case 0: /* Entry point */
            break;
          case 1: /* Sign */
            {
            RPObject sign=new RPObject();
            assignRPObjectID(sign);
            sign.put("type","sign");
            sign.put("x",j%width);
            sign.put("y",j/width);
            sign.put("text","Welcome to Stendhal! Enjoy visiting this area!");
            add(sign);

            Logger.trace("StendhalRPZone::populate","D","Adding SIGN: "+sign);
            break;
            }            
          }
        
        j++;      
        }
      }

    Logger.trace("StendhalRPZone::populate","<");
    }
    
  public void addLayer(String name, String filename) throws IOException
    {
    addLayer(name,filename, true);
    }

  public int getWidth()
    {
    return collisionMap.getWidth();
    }
  
  public int getHeight()
    {
    return collisionMap.getHeight();
    }
  
  
  public List<TransferContent> getContents()
    {
    return contents;
    }
  
  private static Rectangle2D getCollisionArea(String type, double x, double y)
    {
    Rectangle2D rect=new Rectangle.Double();
    getCollisionArea(rect,type,x,y);
    return rect;
    }

  private static void getCollisionArea(Rectangle2D rect,String type, double x, double y)
    {
    if(type.equals("player"))
      {
      rect.setRect(x+0.5,y+1.3,0.87,0.6);
      }
    else
      {
      rect.setRect(x,y,1,2);
      }
    }

  public boolean collides(RPObject object, double x, double y) throws AttributeNotFoundException  
    {
    Rectangle2D area=getCollisionArea(object.get("type"),x,y);
    
    if(collisionMap.collides(area)==false)
      {
      Rectangle2D otherarea=new Rectangle.Double();
      for(RPObject other: objects.values())
        {
        if(!other.getID().equals(object.getID()))
          {
          getCollisionArea(otherarea,other.get("type"),other.getDouble("x"),other.getDouble("y"));
          if(area.intersects(otherarea))
            {
            return true;
            }
          }
        }
      
      return false;
      }   
    else
      {
      collisionMap.printaround((int)x,(int)y,3);     
      return true;
      }    
    }

  private static byte[] getBytesFromFile(String file) throws IOException 
    {
    ByteArrayOutputStream out=new ByteArrayOutputStream();
    InputStream is= StendhalRPZone.class.getClassLoader().getResourceAsStream(file);
    
    byte[] buffer=new byte[1024];
    int len=0;
    while((len=is.read(buffer,0,1024))!=-1)
      {
      out.write(buffer,0,len);
      }

    return out.toByteArray();
    }
  }
