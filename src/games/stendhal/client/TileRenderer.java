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

import java.io.*;
import java.awt.Graphics;
import java.util.*;

import marauroa.common.*;

/** This is a helper class to render coherent tiles based on the tileset.
 *  This should be replaced by independent tiles as soon as possible . */
public class TileRenderer
  {
  private TileStore tiles;
  private int[] map;
  private int width;
  private int height;
  
  public TileRenderer(TileStore tiles)
    {
    this.tiles=tiles;
    map=null;
    width=height=0;    
    }
  
  /** Sets the data that will be rendered */
  public void setMapData(Reader reader) throws IOException
    {
    Logger.trace("TileRenderer::setMapData",">");
        
    BufferedReader file=new BufferedReader(reader);
    String text;
    
    text=file.readLine();
    String[] size=text.split(" ");
    width=Integer.parseInt(size[0]);
    height=Integer.parseInt(size[1]);
    
    System.out.println("Width="+width+"\tHeight="+height);
    
    map=new int[width*height];
    
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
        map[j]=Integer.parseInt(item);
        System.out.print(map[j]+",");
        j++;      
        }
      System.out.println();
      }

    Logger.trace("TileRenderer::setMapData","<");
    }

  /** Returns the widht in world units */
  public int getWidth()
    {
    return width;
    }
  
  /** Returns the height in world units */
  public int getHeight()
    {
    return height;
    }
  
  private int get(int x, int y)  
    {
    return map[y*width+x];
    }
  
  
  /** Render the data to screen. We assume that Gamescreen will clip.
   *  The data doesnt change, so we could cache it and get a boost in performance */
  public void draw(GameScreen screen) 
    {
    for(int j=0;j<getHeight();j++)
      {
      for(int i=0;i<getWidth();i++)
        {
        int value=get(i,j)-1;        
        if(value>=0)
          {
          screen.draw(tiles.getTile(value),i,j);
          }
        }
      }
    }
  }