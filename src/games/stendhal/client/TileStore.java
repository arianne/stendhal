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
import java.awt.Image;
import java.awt.Transparency;


/** It is class to get tiles from the tileset */
public class TileStore extends SpriteStore
  {
  private static final int TILE_WIDTH=32;
  private static final int TILE_HEIGHT=32;
  
  private Sprite[][] tileset;
  
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
 
    tileset=new Sprite[tiles.getWidth()/TILE_WIDTH][];
    for(int i=0;i<tiles.getWidth()/TILE_WIDTH;i++)
      {
      tileset[i]=new Sprite[tiles.getHeight()/TILE_HEIGHT];
      }
    
    for(int i=0;i<tiles.getWidth()/TILE_WIDTH;i++)
      {      
      for(int j=0;j<tiles.getHeight()/TILE_HEIGHT;j++)
        {
        Image image = gc.createCompatibleImage(TILE_WIDTH,TILE_HEIGHT, Transparency.BITMASK);
        tiles.draw(image.getGraphics(),0,0,i*TILE_WIDTH,j*TILE_HEIGHT);
        
        // create a sprite, add it the cache then return it
        tileset[i][j] = new Sprite(image);        
        }
      }
    }
    
  public Sprite getTile(int i, int j) 
    {
    return tileset[i][j];
    }

  public Sprite getTile(int[] indexes) 
    {
    return tileset[indexes[0]][indexes[1]];
    }
  }