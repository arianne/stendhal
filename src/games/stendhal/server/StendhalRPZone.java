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
import games.stendhal.server.entity.*;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import java.util.*;
import java.io.*;
import java.net.*;


public class StendhalRPZone extends MarauroaRPZone 
  {
  private List<TransferContent> contents;
  private List<String> entryPoints;
  private List<NPC> npcs;
  
  private CollisionDetection collisionMap;
  private int width;
  private int height;

  public StendhalRPZone(String name)
    {
    super(name);
    
    contents=new LinkedList<TransferContent>();
    entryPoints=new LinkedList<String>();
    npcs=new LinkedList<NPC>();
    
    collisionMap=new CollisionDetection();
    }
  
  public void onInit() throws Exception
    {
    }
    
  public void onFinish() throws Exception
    {
    }
  
  public List<NPC> getNPCList()
    {
    return npcs;
    }
    
  public void addZoneChange(String entry)
    {
    entryPoints.add(entry);
    }  

  public void addEntryPoint(String entry)
    {
    entryPoints.add(0,entry);
    }  
  
  public void placeObjectAtEntryPoint(RPObject object) throws NoEntryPointException
    {
    if(entryPoints.size()==0)
      {
      throw new NoEntryPointException();
      }
      
    String entryPoint=entryPoints.get(0);
    String[] components=entryPoint.split(",");
    object.put("x",components[0]);
    object.put("y",components[1]);
    }
  
  public void addLayer(String name, String filename) throws IOException
    {
    Logger.trace("StendhalRPZone::addLayer",">");
    TransferContent content=new TransferContent();
    content.name=name;
    content.cacheable=true;
    content.timestamp=(int)(new File(filename).lastModified()/1000);
    Logger.trace("StendhalRPZone::addLayer","D",Integer.toString(content.timestamp));
    content.data=getBytesFromFile(filename);
    
    contents.add(content);
    Logger.trace("StendhalRPZone::addLayer","<");
    }

  public void addCollisionLayer(String name, String filename) throws IOException
    {
    Logger.trace("StendhalRPZone::addCollisionLayer",">");
    TransferContent content=new TransferContent();
    content.name=name;
    content.cacheable=true;
    content.timestamp=(int)(new File(filename).lastModified()/1000);
    Logger.trace("StendhalRPZone::addLayer","D",Integer.toString(content.timestamp));
    content.data=getBytesFromFile(filename);
    
    contents.add(content);
    
    collisionMap.setCollisionData(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filename))); //new FileReader(filename));
    Logger.trace("StendhalRPZone::addCollisionLayer","<");    
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
        int value=Integer.parseInt(item)-480 /* Number of tiles at zelda_outside_chipset */;
        /** TODO: Change it by another way of not hardcoding the objects. */
        try
          {
          switch(value)
            {
            case 1: /* Entry point */
              {
              String entryPoint=new String(j%width+","+j/width);
              addEntryPoint(entryPoint);
              break;
              }            
            case 2: /* Sign */
              {
              Sign sign=new Sign();
              assignRPObjectID(sign);
              sign.setx(j%width);
              sign.sety(j/width);
              if(zoneid.getID().equals("village"))
                {
                if(sign.getx()==23 && sign.gety()==47) sign.setText("You are going to leave this area to move to plains.|You may grow up your sheep there.|Be careful wolves may attack you.");
                if(sign.getx()==26 && sign.gety()==27) sign.setText("Talk to Nishiya to buy a sheep!.|He will offer you a nice price.");
                if(sign.getx()==60 && sign.gety()==33) sign.setText("You are going to leave this area to move to city.|You may sell your sheep there. ");
                }
              else if(zoneid.getID().equals("city"))
                {
                if(sign.getx()==4 && sign.gety()==21) sign.setText("You are going to leave this area to move to village.|You may buy a new sheep there.");
                if(sign.getx()==8 && sign.gety()==25) sign.setText("This is our attack dummy.|Click on it to attack, another click to stop attacking it.| Be sure to learn how to attack correctly, it will be useful.");
                if(sign.getx()==8 && sign.gety()==33) sign.setText("Welcome to Stendhal!|Make sure you talk with Paco for hints|Please report problems at our webpage.");
                if(sign.getx()==43 && sign.gety()==26) sign.setText("Talk to Sato to sell your sheep!.|He won't give you a fair price but this is an small village...");
                if(sign.getx()==44 && sign.gety()==48) sign.setText("You are going to leave this area to move to plains.|You may grow up your sheep there.|Be careful wolves may attack you.");
                }
              else if(zoneid.getID().equals("plains"))
                {
                if(sign.getx()==118 && sign.gety()==43) sign.setText("You are going to leave this area to move to forest.|You may grow up your sheep faster there.|Be careful many wolves may attack you.");
                if(sign.getx()==38 && sign.gety()==3) sign.setText("You are going to leave this area to move to village.|You may buy a new sheep there.");
                if(sign.getx()==113 && sign.gety()==3) sign.setText("You are going to leave this area to move to city.|You may sell your sheep there. ");
                }
                
              add(sign);
  
              Logger.trace("StendhalRPZone::populate","D","Adding SIGN: "+sign);
              break;
              }
            case 3: /* Sheep */
              {
              break;
              }
            case 4: /* Wolf */
              {
              break;
              }
            case 5: /* NPC Seller */
              {
              SellerNPC npc=new SellerNPC();
              assignRPObjectID(npc);
              npc.setName("Nishiya");
              npc.setx(j%width);
              npc.sety(j/width);
              add(npc);
              
              npcs.add(npc);

              Logger.trace("StendhalRPZone::populate","D","Adding NPC seller: "+npc);
              break;
              }
            case 6: /* NPC Buyer */
              {
              BuyerNPC npc=new BuyerNPC();
              assignRPObjectID(npc);
              npc.setName("Sato");
              npc.setx(j%width);
              npc.sety(j/width);
              add(npc);

              npcs.add(npc);

              Logger.trace("StendhalRPZone::populate","D","Adding Buyer seller: "+npc);
              break;
              }
            case 7: /* Food */
              {
              break;
              }
            case 8: /* Zone change  */
              {
              String entryPoint=new String(j%width+","+j/width);
              addZoneChange(entryPoint);
              break;
              }
            case 9: /* Training dummy  */
              {              
              TrainingDummy dummy=new TrainingDummy();
              assignRPObjectID(dummy);
              dummy.setx(j%width);
              dummy.sety(j/width);
              dummy.setHP(100);
              add(dummy);


              Logger.trace("StendhalRPZone::populate","D","Adding Training dummy: "+dummy);
              break;
              }
            }
          }
        catch(AttributeNotFoundException e)
          {
          Logger.thrown("StendhalRPZone::populate","X",e);
          }
        
        j++;      
        }
      }

    Logger.trace("StendhalRPZone::populate","<");
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

  public boolean leavesZone(RPObject object, double x, double y) throws AttributeNotFoundException  
    {
    Rectangle2D area=getCollisionArea(object.get("type"),x,y);
    return collisionMap.leavesZone(area);
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
