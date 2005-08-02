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
    addAnimatedTile(2623,new int[]{2623,2653,2683,2713});
    addAnimatedTile(2653,new int[]{2713,2623,2653,2683});
    addAnimatedTile(2683,new int[]{2683,2713,2623,2653});
    addAnimatedTile(2713,new int[]{2653,2683,2713,2623});
    
    // Waterfall middle
    addAnimatedTile(2624,new int[]{2624,2654,2684,2714});
    addAnimatedTile(2654,new int[]{2714,2624,2654,2684});
    addAnimatedTile(2684,new int[]{2684,2714,2624,2654});
    addAnimatedTile(2714,new int[]{2654,2684,2714,2624});

    // Waterfall end
    addAnimatedTile(2625,new int[]{2625,2655,2685,2715});
    addAnimatedTile(2655,new int[]{2715,2625,2655,2685});
    addAnimatedTile(2685,new int[]{2685,2715,2625,2655});
    addAnimatedTile(2715,new int[]{2655,2685,2715,2625});

    // Waterfall end left
    addAnimatedTile(2626,new int[]{2626,2656,2686,2716});
    addAnimatedTile(2656,new int[]{2716,2626,2656,2686});
    addAnimatedTile(2686,new int[]{2686,2716,2626,2656});
    addAnimatedTile(2716,new int[]{2656,2686,2716,2626});

    // Waterfall end right
    addAnimatedTile(2627,new int[]{2627,2657,2687,2717});
    addAnimatedTile(2657,new int[]{2717,2627,2657,2687});
    addAnimatedTile(2687,new int[]{2687,2717,2627,2657});
    addAnimatedTile(2717,new int[]{2657,2687,2717,2627});


    // Waterfall golden start
    addAnimatedTile(2628,new int[]{2628,2658,2688,2718});
    addAnimatedTile(2658,new int[]{2718,2628,2658,2688});
    addAnimatedTile(2688,new int[]{2688,2718,2628,2658});
    addAnimatedTile(2718,new int[]{2658,2688,2718,2628});

    // Waterfall golden middle
    addAnimatedTile(2629,new int[]{2629,2659,2689,2719});
    addAnimatedTile(2659,new int[]{2719,2629,2659,2689});
    addAnimatedTile(2689,new int[]{2689,2719,2629,2659});
    addAnimatedTile(2719,new int[]{2659,2689,2719,2629});

    // Waterfall golden end
    addAnimatedTile(2630,new int[]{2630,2660,2690,2720});
    addAnimatedTile(2660,new int[]{2720,2630,2660,2690});
    addAnimatedTile(2690,new int[]{2690,2720,2630,2660});
    addAnimatedTile(2720,new int[]{2660,2690,2720,2630});

    // Waterfall golden end left
    addAnimatedTile(2631,new int[]{2631,2661,2691,2721});
    addAnimatedTile(2661,new int[]{2721,2631,2661,2691});
    addAnimatedTile(2691,new int[]{2691,2721,2631,2661});
    addAnimatedTile(2721,new int[]{2661,2691,2721,2631});

    // Waterfall golden end right
    addAnimatedTile(2632,new int[]{2632,2662,2692,2722});
    addAnimatedTile(2662,new int[]{2722,2632,2662,2692});
    addAnimatedTile(2692,new int[]{2692,2722,2632,2662});
    addAnimatedTile(2722,new int[]{2662,2692,2722,2632});

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