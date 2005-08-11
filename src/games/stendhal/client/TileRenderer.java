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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;

/** This is a helper class to render coherent tiles based on the tileset.
 *  This should be replaced by independent tiles as soon as possible . */
public class TileRenderer
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(TileRenderer.class);
  
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
    Log4J.startMethod(logger, "setMapData");
        
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

    Log4J.finishMethod(logger, "setMapData");
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
    // Outside_0 = 0 - 479
    // Outside_1 = 480 - 959
    // Dungeon_0 = 960 - 1439
    // Dungeon_1 = 1440 - 1919
    // Interior_0 = 1920 - 2399
    // Navigation = 2400 - 2400
    // Objects = 2401 - 2500
    // Collision = 2501 - 2502
    // Outside_3 = 2503 - 2982


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

    // Waterfall start
    addAnimatedTile(145,new int[]{145,175,205,235});
    addAnimatedTile(175,new int[]{175,205,235,145});
    addAnimatedTile(205,new int[]{205,235,145,175});
    addAnimatedTile(235,new int[]{235,145,175,205});

    // Waterfall middle
    addAnimatedTile(146,new int[]{146,176,206,236});
    addAnimatedTile(176,new int[]{176,206,236,146});
    addAnimatedTile(206,new int[]{206,236,146,176});
    addAnimatedTile(236,new int[]{236,146,176,206});

    // Waterfall end
    addAnimatedTile(147,new int[]{147,177,207,237});
    addAnimatedTile(177,new int[]{177,207,237,147});
    addAnimatedTile(207,new int[]{207,237,147,177});
    addAnimatedTile(237,new int[]{237,147,177,207});

    // Waterfall end left
    addAnimatedTile(148,new int[]{148,178,208,238});
    addAnimatedTile(178,new int[]{178,208,238,148});
    addAnimatedTile(208,new int[]{208,238,148,178});
    addAnimatedTile(238,new int[]{238,148,178,208});

    // Waterfall end right
    addAnimatedTile(149,new int[]{149,179,209,239});
    addAnimatedTile(179,new int[]{179,209,239,149});
    addAnimatedTile(209,new int[]{209,239,149,179});
    addAnimatedTile(239,new int[]{239,149,179,209});


    // Waterfall golden start
    addAnimatedTile(265,new int[]{265,295,325,355});
    addAnimatedTile(295,new int[]{295,325,355,265});
    addAnimatedTile(325,new int[]{325,355,265,295});
    addAnimatedTile(355,new int[]{355,265,295,325});

    // Waterfall golden middle
    addAnimatedTile(266,new int[]{266,296,326,356});
    addAnimatedTile(296,new int[]{296,326,356,266});
    addAnimatedTile(326,new int[]{326,356,266,296});
    addAnimatedTile(356,new int[]{296,326,356,266});

    // Waterfall golden end
    addAnimatedTile(267,new int[]{267,297,327,357});
    addAnimatedTile(297,new int[]{297,327,357,267});
    addAnimatedTile(327,new int[]{327,357,267,297});
    addAnimatedTile(357,new int[]{357,267,297,327});

    // Waterfall golden end left
    addAnimatedTile(268,new int[]{268,298,328,358});
    addAnimatedTile(298,new int[]{298,328,358,268});
    addAnimatedTile(328,new int[]{328,358,268,298});
    addAnimatedTile(358,new int[]{358,268,298,328});

    // Waterfall golden end right
    addAnimatedTile(269,new int[]{269,299,329,359});
    addAnimatedTile(299,new int[]{299,329,359,269});
    addAnimatedTile(329,new int[]{329,359,269,299});
    addAnimatedTile(359,new int[]{359,269,299,329});

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