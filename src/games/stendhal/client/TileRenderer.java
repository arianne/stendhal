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

import games.stendhal.common.*;
import marauroa.common.*;

/** This is a helper class to render coherent tiles based on the tileset.
 *  This should be replaced by independent tiles as soon as possible . */
public class TileRenderer
  {
  private TileStore tiles;
  private int[] map;
  private int width;
  private int height;
  private int frame;
  private long delta;
  
  public TileRenderer(TileStore tiles)
    {
    this.tiles=tiles;
    map=null;
    frame=width=height=0;  
    animatedTiles=new HashMap<Integer,List<Integer>>();       
    createAnimateTiles();
    delta=System.currentTimeMillis();
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
        j++;      
        }
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
  
  private Map<Integer,List<Integer>> animatedTiles;
  
  private void addAnimatedTile(int tile, int[] tiles)
    {
    List<Integer> list=new LinkedList<Integer>();
    for(int num: tiles)
      {
      list.add(num);
      }
      
    animatedTiles.put(tile,list);
    }
  
  private void createAnimateTiles()
    {
    // Double daisy 
    addAnimatedTile(124,new int[]{124,154,184,214});
    addAnimatedTile(154,new int[]{154,184,214,124});
    addAnimatedTile(184,new int[]{184,214,124,154});
    addAnimatedTile(214,new int[]{214,124,154,184});

    // Single daisy
    addAnimatedTile(125,new int[]{125,155,185,215});
    addAnimatedTile(155,new int[]{155,185,215,125});
    addAnimatedTile(185,new int[]{185,215,125,155});
    addAnimatedTile(215,new int[]{215,125,155,185});
    
    // Waterfall    
    }
  
  /** Render the data to screen. We assume that Gamescreen will clip.
   *  The data doesnt change, so we could cache it and get a boost in performance */
  public void draw(GameScreen screen) 
    {
    if(System.currentTimeMillis()-delta>200)
      {
      delta=System.currentTimeMillis();
      frame++;
      }
    
    int x=(int)screen.getX();
    int y=(int)screen.getY();
    int w=(int)screen.getWidth();
    int h=(int)screen.getHeight();
    
    for(int j=y-1;j<y+h+1;j++)
      {
      for(int i=x-1;i<x+w+1;i++)
        {
        if(j>=0 && j<getHeight() && i>=0 && i<getWidth())
          {
          int value=get(i,j)-1;        
          
          if(animatedTiles.containsKey(value))
            {
            List<Integer> list=(animatedTiles.get(value));
            value=list.get(frame%list.size());
            }
          
          if(value>=0)
            {
            screen.draw(tiles.getTile(value),i,j);
            }
          }
        }
      }
    }
  }