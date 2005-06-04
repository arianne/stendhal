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
  private List<String> zoneChangePoints;
  private List<Portal> portals;

  private List<NPC> npcs;
  private List<RespawnPoint> respawnPoints;
  private List<Food> foodItems;
  
  private CollisionDetection collisionMap;
  private int width;
  private int height;

  public StendhalRPZone(String name)
    {
    super(name);
    
    contents=new LinkedList<TransferContent>();
    entryPoints=new LinkedList<String>();
    zoneChangePoints=new LinkedList<String>();
    portals=new LinkedList<Portal>();

    npcs=new LinkedList<NPC>();
    respawnPoints=new LinkedList<RespawnPoint>();
    foodItems=new LinkedList<Food>();
    
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
  
  public List<Portal> getPortals()
    {
    return portals;
    }
  
  public Portal getPortal(int number)
    {
    for(Portal portal: portals)
      {
      if(portal.getNumber()==number)
        {
        return portal;
        }
      }
    
    return null;
    }

  public List<RespawnPoint> getRespawnPointList()
    {
    return respawnPoints;
    }

  public List<Food> getFoodItemList()
    {
    return foodItems;
    }
    
  public void addZoneChange(String entry)
    {
    zoneChangePoints.add(entry);
    }  

  public void addEntryPoint(String entry)
    {
    entryPoints.add(0,entry);
    }  
  
  public void placeObjectAtEntryPoint(Entity object)
    {
    if(entryPoints.size()==0)
      {
      return;
      }
      
    String entryPoint=entryPoints.get(0);
    String[] components=entryPoint.split(",");

    object.setx(Integer.parseInt(components[0]));
    object.sety(Integer.parseInt(components[1]));
    }

  public void placeObjectAtZoneChangePoint(StendhalRPZone oldzone, Entity object)
    {
    if(zoneChangePoints.size()==0)
      {
      return;
      }
      
    String exitDirection=null;

    if(object.gety()<4)
      {
      exitDirection="N";
      }
    else if(object.gety()>oldzone.getHeight()-4)
      {
      exitDirection="S";
      }
    else if(object.getx()<4)
      {
      exitDirection="W";
      }
    else if(object.getx()>oldzone.getWidth()-4)
      {
      exitDirection="E";
      }
    else
      {
      // NOTE: If any of the above is true, then it just put object on the first zone change point.
      String[] components=zoneChangePoints.get(0).split(",");
      Logger.trace("StendhalRPZone::placeObjectAtZoneChangePoint","D","Player zone change default: "+components);
      object.setx(Integer.parseInt(components[0]));
      object.sety(Integer.parseInt(components[1]));
      return;
      }
    
    Logger.trace("StendhalRPZone::placeObjectAtZoneChangePoint","D","Player exit direction: "+exitDirection);
    
    int x=0;
    int y=0;
    int distance=Integer.MAX_VALUE;
    String minpoint=zoneChangePoints.get(0);
    
    if(exitDirection.equals("N"))
      {
      x=object.getx();
      y=getHeight();
      }
    else if(exitDirection.equals("S"))
      {
      x=object.getx();
      y=0;
      }
    else if(exitDirection.equals("W"))
      {
      x=getWidth();
      y=object.gety();
      }
    else if(exitDirection.equals("E"))
      {
      x=0;
      y=object.gety();
      }

    Logger.trace("StendhalRPZone::placeObjectAtZoneChangePoint","D","Player entry point: ("+x+","+y+")");

    for(String point: zoneChangePoints)
      {
      String[] components=point.split(",");
      int px=Integer.parseInt(components[0]);
      int py=Integer.parseInt(components[1]);
      
      if((px-x)*(px-x)+(py-y)*(py-y)<distance)
        {
        Logger.trace("StendhalRPZone::placeObjectAtZoneChangePoint","D","Best entry point: ("+px+","+py+") --> "+distance);
        distance=(px-x)*(px-x)+(py-y)*(py-y);
        minpoint=point;
        }
      }
      
    Logger.trace("StendhalRPZone::placeObjectAtZoneChangePoint","D","Choosen entry point: ("+minpoint+") --> "+distance);
    String[] components=minpoint.split(",");
    object.setx(Integer.parseInt(components[0]));
    object.sety(Integer.parseInt(components[1]));
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
        int value=Integer.parseInt(item)-(2401) /* Number of tiles at zelda_outside_chipset */;
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
            case 2: /* Zone change  */
              {
              String entryPoint=new String(j%width+","+j/width);
              addZoneChange(entryPoint);
              break;
              }
            case 3: /* Portal  */
              {
              Portal portal=new Portal();
              assignRPObjectID(portal);
              portal.setx(j%width);
              portal.sety(j/width);
              portal.setNumber(-1);
              
              if(zoneid.getID().equals("afterlive"))
                {
                if((portal.getx()==13 || portal.getx()==14) && portal.gety()==1) 
                  {
                  portal.setNumber(0);
                  portal.setDestination("city",0);
                  }
                }              
              else if(zoneid.getID().equals("city"))
                {
                if(portal.getx()==28 && portal.gety()==24) 
                  {
                  portal.setNumber(0);
                  portal.setDestination("dungeon_000",0);
                  }
                }              
              else if(zoneid.getID().equals("dungeon_000"))
                {
                if(portal.getx()==27 && portal.gety()==36) 
                  {
                  portal.setNumber(0);
                  portal.setDestination("city",0);
                  }
                else if(portal.getx()==42 && portal.gety()==43) 
                  {
                  portal.setNumber(1);
                  portal.setDestination("dungeon_001",0);
                  }
                }              
              else if(zoneid.getID().equals("dungeon_001"))
                {
                if(portal.getx()==5 && portal.gety()==7) 
                  {
                  portal.setNumber(0);
                  portal.setDestination("dungeon_000",1);
                  }
                else if(portal.getx()==67 && portal.gety()==118) 
                  {
                  portal.setNumber(1);
                  portal.setDestination("forest",0);
                  }
                }              
              else if(zoneid.getID().equals("forest"))
                {
                if(portal.getx()==103 && portal.gety()==65) 
                  {
                  portal.setNumber(0);
                  portal.setDestination("dungeon_001",1);
                  }
                }              
              else if(zoneid.getID().equals("tavern"))
                {
                if(portal.getx()==20 && portal.gety()==1) 
                  {
                  portal.setNumber(0);
                  portal.setDestination("village",0);
                  }
                }              
              else if(zoneid.getID().equals("village"))
                {
                if(portal.getx()==16 && portal.gety()==20) 
                  {
                  portal.setNumber(0);
                  portal.setDestination("tavern",0);
                  }
                }           
                   
              if(portal.getNumber()!=-1)
                {
                add(portal);
  
                portals.add(portal);
                }

              break;
              }
            case 11: /* Sheep */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new Sheep(),1);
              respawnPoints.add(point);
              
              break;
              }
            case 12: /* Rat */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new Rat(),1);
              respawnPoints.add(point);
              
              break;
              }
            case 13: /* Cave rat */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new CaveRat(),1);
              respawnPoints.add(point);
              
              break;
              }
            case 14: /* Wolf */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new Wolf(),1);
              respawnPoints.add(point);

              break;
              }
            case 15: /* Cobra */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new Cobra(),1);
              respawnPoints.add(point);

              break;
              }
            case 16: /* Orc */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new Orc(),1);
              respawnPoints.add(point);

              break;
              }
            case 17: /* Gargoyle */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new Gargoyle(),1);
              respawnPoints.add(point);

              break;
              }
            case 18: /* Ogre */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new Ogre(),1);
              respawnPoints.add(point);

              break;
              }
            case 19: /* Kobold */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new Kobold(),1);
              respawnPoints.add(point);

              break;
              }
            case 20: /* Boar */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new Boar(),1);
              respawnPoints.add(point);

              break;
              }
            case 21: /* Troll */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new Troll(),1);
              respawnPoints.add(point);

              break;
              }
            case 22: /* Goblin */
              {
              RespawnPoint point=new RespawnPoint(j%width,j/width,2);
              point.set(this, new Goblin(),1);
              respawnPoints.add(point);

              break;
              }
            case 71: /* NPC Beggar */
              {
              BeggarNPC npc=new BeggarNPC();
              assignRPObjectID(npc);
              npc.setName("Diogenes");
              npc.setx(j%width);
              npc.sety(j/width);
              npc.setbaseHP(100);
              add(npc);

              npcs.add(npc);

              Logger.trace("StendhalRPZone::populate","D","Adding NPC beggar: "+npc);
              break;
              }
            case 72: /* NPC Buyer */
              {
              BuyerNPC npc=new BuyerNPC();
              assignRPObjectID(npc);
              npc.setName("Sato");
              npc.setx(j%width);
              npc.sety(j/width);
              npc.setbaseHP(100);
              add(npc);

              npcs.add(npc);

              Logger.trace("StendhalRPZone::populate","D","Adding NPC buyer: "+npc);
              break;
              }
            case 73: /* NPC Journalist */
              {
              JournalistNPC npc=new JournalistNPC();
              assignRPObjectID(npc);
              npc.setName("Brian");
              npc.setx(j%width);
              npc.sety(j/width);
              npc.setbaseHP(100);
              add(npc);
              
              npcs.add(npc);

              Logger.trace("StendhalRPZone::populate","D","Adding NPC Journalist: "+npc);
              break;
              }
            case 74: /* NPC Seller */
              {
              SellerNPC npc=new SellerNPC();
              assignRPObjectID(npc);
              npc.setName("Nishiya");
              npc.setx(j%width);
              npc.sety(j/width);
              npc.setbaseHP(100);
              add(npc);
              
              npcs.add(npc);

              Logger.trace("StendhalRPZone::populate","D","Adding NPC seller: "+npc);
              break;
              }
            case 75: /* Welcomer NPC  */
              {              
              WelcomerNPC npc=new WelcomerNPC();
              assignRPObjectID(npc);
              npc.setName("Carmen");
              npc.setx(j%width);
              npc.sety(j/width);
              npc.setbaseHP(100);
              add(npc);

              npcs.add(npc);

              Logger.trace("StendhalRPZone::populate","D","Adding Welcomer NPC: "+npc);
              break;
              }
            case 76: /* Training dummy  */
              {              
              TrainingDummy dummy=new TrainingDummy();
              assignRPObjectID(dummy);
              dummy.setx(j%width);
              dummy.sety(j/width);
              dummy.setbaseHP(100);
              add(dummy);

              Logger.trace("StendhalRPZone::populate","D","Adding Training dummy: "+dummy);
              break;
              }
            case 77: /* Angel NPC  */
              {              
//              AngelNPC npc=new AngelNPC();
//              assignRPObjectID(npc);
//              npc.setName("Simon");
//              npc.setx(j%width);
//              npc.sety(j/width);
//              npc.setbaseHP(100);
//              add(npc);
//
//              npcs.add(npc);
//
//              Logger.trace("StendhalRPZone::populate","D","Adding Angel NPC: "+npc);
              break;
              }
            case 78: /* Tavern Main NPC  */
              {              
              TavernBarMaidNPC npc=new TavernBarMaidNPC();
              assignRPObjectID(npc);
              npc.setName("Margaret");
              npc.setx(j%width);
              npc.sety(j/width);
              npc.setbaseHP(100);
              add(npc);

              npcs.add(npc);

              Logger.trace("StendhalRPZone::populate","D","Adding Tavern Maid NPC: "+npc);
              break;
              }
            case 79: /* Butcher NPC  */
              {              
              ButcherNPC npc=new ButcherNPC();
              assignRPObjectID(npc);
              npc.setName("Paul");
              npc.setx(j%width);
              npc.sety(j/width);
              npc.setbaseHP(100);
              add(npc);

              npcs.add(npc);

              Logger.trace("StendhalRPZone::populate","D","Adding Butcher NPC: "+npc);
              break;
              }
            case 80: /* Old Orc NPC  */
              {              
              OrcBuyerNPC npc=new OrcBuyerNPC();
              assignRPObjectID(npc);
              npc.setName("Tor'Koom");
              npc.setx(j%width);
              npc.sety(j/width);
              npc.setbaseHP(100);
              add(npc);

              npcs.add(npc);

              Logger.trace("StendhalRPZone::populate","D","Adding Orc Buyer NPC: "+npc);
              break;
              }
            case 91: /* Sign */
              {
              Sign sign=new Sign();
              assignRPObjectID(sign);
              sign.setx(j%width);
              sign.sety(j/width);
              if(zoneid.getID().equals("village"))
                {
                if(sign.getx()==23 && sign.gety()==47) sign.setText("You are about to leave this area and move to the plains.|You may fatten up your sheep there on the wild berries.|Be careful though, wolves roam these plains.");
                if(sign.getx()==26 && sign.gety()==27) sign.setText("Talk to Nishiya to buy a sheep!.|He has the best prices for miles.");
                if(sign.getx()==60 && sign.gety()==33) sign.setText("You are about to leave this area to move to the city.|You can sell your sheep there.");
                }
              else if(zoneid.getID().equals("city"))
                {
                if(sign.getx()==4 && sign.gety()==21) sign.setText("You are about to leave this area to move to the village.|You can buy a new sheep there.");
                if(sign.getx()==8 && sign.gety()==33) sign.setText("Welcome to Stendhal!| Please report any problems and issues at our webpage.");
                if(sign.getx()==26 && sign.gety()==26) sign.setText("You are about to enter the Dungeons.|But Beware! This area is infested with rats and legend has |it that many Adventurers have died down there...");
                if(sign.getx()==43 && sign.gety()==26) sign.setText("Talk to Sato to sell your sheep!.|He probably won't give you a fair price but this is a small village...|The price he will offer you depends on the weight of your sheep.");
                if(sign.getx()==44 && sign.gety()==48) sign.setText("You are about to leave this area and move to the plains.|You may fatten up your sheep there on the wild berries.|Be careful though, wolves roam these plains.");
                }
              else if(zoneid.getID().equals("plains"))
                {
                if(sign.getx()==118 && sign.gety()==43) sign.setText("You are about to leave this area to move to the forest.|You may fatten up your sheep there on wild berries.|Be careful though, these forests crawl with wolves.");
                if(sign.getx()==38 && sign.gety()==3) sign.setText("You are about to leave this area to move to the village.|You can buy a new sheep there.");
                if(sign.getx()==113 && sign.gety()==3) sign.setText("You are about to leave this area to move to the city.|You can sell your sheep there.");
                }
              else if(zoneid.getID().equals("afterlive"))
                {
                if(sign.getx()==11 && sign.gety()==19) sign.setText("I regret to tell you that you have died!|You have lost some of your items and 10% of your eXPerience points.|Be more careful next time. On the up side you can now return to city.");
                }
                
              add(sign);
  
              Logger.trace("StendhalRPZone::populate","D","Adding SIGN: "+sign);
              break;
              }
            case 92: /* Food */
              {
              Food food=new Food();
              assignRPObjectID(food);
              food.setAmount(5);
              food.setx(j%width);
              food.sety(j/width);
              add(food);

              foodItems.add(food);
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
  
  public boolean leavesZone(Entity entity, double x, double y) throws AttributeNotFoundException  
    {
    Rectangle2D area=entity.getArea(x,y);
    return collisionMap.leavesZone(area);
    }
    
  public boolean simpleCollides(Entity entity, double x, double y) throws AttributeNotFoundException  
    {
    Rectangle2D area=entity.getArea(x,y);
    return collisionMap.collides(area);
    }
    
  public boolean collides(Entity entity, double x, double y) throws AttributeNotFoundException  
    {
    Rectangle2D area=entity.getArea(x,y);
    
    if(collisionMap.collides(area)==false)
      {
      Rectangle2D otherarea=new Rectangle.Double();
      for(RPObject other: objects.values())
        {
        Entity otherEntity=(Entity)other;
        otherEntity.getArea(otherarea,otherEntity.getx(),otherEntity.gety());
        if(area.intersects(otherarea) && !entity.getID().equals(otherEntity.getID()))
          {
          return true;
          }
        }
              
      return false;
      }   
    else
      {
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
