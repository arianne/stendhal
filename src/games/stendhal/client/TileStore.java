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

import java.awt.*;
import java.io.*;
import marauroa.common.*;


/** It is class to get tiles from the tileset */
public class TileStore extends SpriteStore
  {
  private static final int TILE_WIDTH=32;
  private static final int TILE_HEIGHT=32;
  
  private Sprite[] tileset;
  private boolean[] walkable;
  
  private static TileStore singleton;
  
  public static TileStore get(String ref)
    {
    if(singleton==null)
      {
      singleton=new TileStore(ref);
      }
    
    return singleton;
    }
  
  public TileStore(String ref)
    {
    super();
    SpriteStore sprites;
    sprites=get();
    Sprite tiles=sprites.getSprite(ref);
    
    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
 
    tileset=new Sprite[(tiles.getWidth()/TILE_WIDTH)*(tiles.getHeight()/TILE_HEIGHT)];
    
    for(int i=0;i<tiles.getWidth()/TILE_WIDTH;i++)
      {      
      for(int j=0;j<tiles.getHeight()/TILE_HEIGHT;j++)
        {
        Image image = gc.createCompatibleImage(TILE_WIDTH,TILE_HEIGHT, Transparency.BITMASK);
        tiles.draw(image.getGraphics(),0,0,i*TILE_WIDTH,j*TILE_HEIGHT);
        
        // create a sprite, add it the cache then return it
        tileset[i+j*(tiles.getWidth()/TILE_WIDTH)] = new Sprite(image);        
        }
      }
    
    //TODO: Unless you want to run collision detection on client too.
    //setCollisionData(ref+".collision");    
    }
    
  private void setCollisionData(String filename)
    {
    Logger.trace("TileStore::setCollisionData",">");
    try
      {
      BufferedReader file=new BufferedReader(new FileReader(filename));
      String text;
    
      text=file.readLine();
      String[] size=text.split(" ");
      int width=Integer.parseInt(size[0]);
      int height=Integer.parseInt(size[1]);
    
      walkable=new boolean[width*height];
    
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
          walkable[j]=(Integer.parseInt(item)==0);
          j++;      
          }
        System.out.println();
        }
      }
    catch(IOException e)
      {
      Logger.thrown("TileStore::setCollisionData","X",e);
      System.exit(0);
      }
    finally
      {
      Logger.trace("TileStore::setCollisionData","<");
      }
    }

  public Sprite getTile(int i) 
    {
    return tileset[i];
    }
  
  public boolean isWalkable(int i)
    {
    return walkable[i];
    }
  }