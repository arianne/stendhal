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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.Color;
import java.awt.Image;
import java.io.*;
import java.util.*;
import marauroa.common.*;
import games.stendhal.common.*;


/** It is class to get tiles from the tileset */
public class TileStore extends SpriteStore
  {
  private class RangeFilename
    {
    int base;
    int amount;
    String filename;
    boolean loaded;
    
    RangeFilename(int base, int amount, String filename)
      {
      this.base=base;
      this.amount=amount;
      this.filename=filename;
      this.loaded=false;
      }
    
    boolean isInRange(int i)
      {
      if(i>=base && i<base+amount)
        {
        return true;
        }
      
      return false;
      }
    
    String getFilename()
      {
      return filename;
      }
    
    public boolean isloaded()
      {
      return loaded;
      }
    
    public String toString()
      {
      return filename+"["+base+","+(base+amount)+"]";
      }
    
    public void load()
      {
      SpriteStore sprites;
      sprites=get();
      Sprite tiles=sprites.getSprite(filename);
      
      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      
      for(int j=0;j<tiles.getHeight()/TILE_HEIGHT;j++)
        {      
        for(int i=0;i<tiles.getWidth()/TILE_WIDTH;i++)
          {
          Image image = gc.createCompatibleImage(TILE_WIDTH,TILE_HEIGHT, Transparency.BITMASK);
          tiles.draw(image.getGraphics(),0,0,i*TILE_WIDTH,j*TILE_HEIGHT);
          
          // create a sprite, add it the cache then return it
          tileset.set(base+i+j*tiles.getWidth()/TILE_WIDTH,new Sprite(image));
          }
        }
      
      sprites.free(filename);
      
      loaded=true;
      }    
    }
    
  private static final int TILE_WIDTH=32;
  private static final int TILE_HEIGHT=32;
  
  private List<RangeFilename> rangesTiles;
  private Vector<Sprite> tileset;
  
  private static TileStore singleton;
  
  public static TileStore get()
    {
    if(singleton==null)
      {
      singleton=new TileStore();
      }
    
    return singleton;
    }
  
  public TileStore()
    {
    super();
    tileset=new Vector<Sprite>();
    rangesTiles=new LinkedList<RangeFilename>();
    }
  
  public void add(String ref, int amount)    
    {
    int base=tileset.size();    
    tileset.setSize(tileset.size()+amount);
    
    if(Debug.VERY_FAST_CLIENT_START)
      {
      rangesTiles.add(new RangeFilename(base,amount,ref));
      }
    else
      {
      SpriteStore sprites;
      sprites=get();
      Sprite tiles=sprites.getSprite(ref);
      
      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      
      for(int j=0;j<tiles.getHeight()/TILE_HEIGHT;j++)
        {      
        for(int i=0;i<tiles.getWidth()/TILE_WIDTH;i++)
          {
          Image image = gc.createCompatibleImage(TILE_WIDTH,TILE_HEIGHT, Transparency.BITMASK);
          tiles.draw(image.getGraphics(),0,0,i*TILE_WIDTH,j*TILE_HEIGHT);
          
          // create a sprite, add it the cache then return it
          tileset.set(base+i+j*tiles.getWidth()/TILE_WIDTH,new Sprite(image));
          }
        }
      }    
    }

  public Sprite getTile(int i) 
    {
    Sprite sprite=tileset.get(i);

    if(Debug.VERY_FAST_CLIENT_START && sprite==null)
      {
      for(RangeFilename range: rangesTiles)
        {
        if(range.isInRange(i) && !range.isloaded())
          {
          StendhalClient.get().addEventLine("Loading tileset "+range.getFilename(),Color.pink);
          range.load();
          
          sprite=tileset.get(i);
          break;
          }
        }
      }
    
    return sprite;
    }
  }