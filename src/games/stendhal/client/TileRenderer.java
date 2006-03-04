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
    // Outside_0 = 0 - 478
    // Outside_1 = 480 - 858
    // Dungeon_0 = 860 - 1438
    // Dungeon_1 = 1440 - 1818
    // Interior_0 = 1820 - 2388
    // Navigation = 2400 - 2400
    // Objects = 2401 - 2500
    // Collision = 2501 - 2502
    // Buildings_0 = 2503 - 2882
    // Outside_2 = 2883 - 3462
    // Interior_1 = 3463 - 3842



    // Double white daisy
    addAnimatedTile(22,new int[]{22,52,82,112,112,112,112,112,112,112});
    addAnimatedTile(52,new int[]{52,82,112,22,22,22,22,22,22,22});
    addAnimatedTile(82,new int[]{82,112,22,52,52,52,52,52,52,52});
    addAnimatedTile(112,new int[]{112,22,52,82,82,82,82,82,82,82});

    // Single white daisy
    addAnimatedTile(23,new int[]{23,53,83,113,113,113,113,113,113,113});
    addAnimatedTile(53,new int[]{53,83,113,23,23,23,23,23,23,23});
    addAnimatedTile(83,new int[]{83,113,23,53,53,53,53,53,53,53});
    addAnimatedTile(113,new int[]{113,23,53,83,83,83,83,83,83,83});

    // Double yellow daisy
    addAnimatedTile(24,new int[]{24,54,84,114,114,114,114,114,114,114});
    addAnimatedTile(54,new int[]{54,84,114,24,24,24,24,24,24,24});
    addAnimatedTile(84,new int[]{84,114,24,54,54,54,54,54,54,54});
    addAnimatedTile(114,new int[]{114,24,54,84,84,84,84,84,84,84});
    
    // Single yellow daisy
    addAnimatedTile(25,new int[]{25,55,85,115,115,115,115,115,115,115});
    addAnimatedTile(55,new int[]{55,85,115,25,25,25,25,25,25,25});
    addAnimatedTile(85,new int[]{85,115,25,55,55,55,55,55,55,55});
    addAnimatedTile(115,new int[]{115,25,55,85,85,85,85,85,85,85});
    
    // Double red daisy
    addAnimatedTile(26,new int[]{26,56,86,116,116,116,116,116,116,116});
    addAnimatedTile(56,new int[]{56,86,116,26,26,26,26,26,26,26,26,26});
    addAnimatedTile(86,new int[]{86,116,26,56,56,56,56,56,56,56});
    addAnimatedTile(116,new int[]{116,26,56,86,86,86,86,86,86,86});
    
    // Single red daisy
    addAnimatedTile(27,new int[]{27,57,87,117,117,117,117,117,117,117});
    addAnimatedTile(57,new int[]{57,87,117,27,27,27,27,27,27,27});
    addAnimatedTile(87,new int[]{87,117,27,57,57,57,57,57,57,57});
    addAnimatedTile(117,new int[]{117,27,57,87,87,87,87,87,87,87});

    // Double blue daisy
    addAnimatedTile(28,new int[]{28,58,88,118,118,118,118,118,118,118});
    addAnimatedTile(58,new int[]{58,88,118,28,28,28,28,28,28,28});
    addAnimatedTile(88,new int[]{88,118,28,58,58,58,58,58,58,58});
    addAnimatedTile(118,new int[]{118,28,58,88,88,88,88,88,88,88});

    // Single blue daisy
    addAnimatedTile(28,new int[]{28,58,88,118,118,118,118,118,118,118});
    addAnimatedTile(58,new int[]{58,88,118,28,28,28,28,28,28,28});
    addAnimatedTile(88,new int[]{88,118,28,58,58,58,58,58,58,58});
    addAnimatedTile(118,new int[]{118,28,58,88,88,88,88,88,88,88});



    // Green Water, Top Left 
    addAnimatedTile(2883,new int[]{2883,2886,2888,2886});
    addAnimatedTile(2886,new int[]{2883,2886,2888,2886});
    addAnimatedTile(2888,new int[]{2883,2886,2888,2886});

    // Green Water, Top
    addAnimatedTile(2884,new int[]{2884,2887,2880,2887});
    addAnimatedTile(2887,new int[]{2884,2887,2880,2887});
    addAnimatedTile(2880,new int[]{2884,2887,2880,2887});

    // Green Water, Top Right 
    addAnimatedTile(2885,new int[]{2885,2888,2881,2888});
    addAnimatedTile(2888,new int[]{2885,2888,2881,2888});
    addAnimatedTile(2881,new int[]{2885,2888,2881,2888});
    
    // Green Water, Left
    addAnimatedTile(3013,new int[]{3013,3016,3018,3016});
    addAnimatedTile(3016,new int[]{3013,3016,3018,3016});
    addAnimatedTile(3018,new int[]{3013,3016,3018,3016});

    // Green Water, pond
    addAnimatedTile(3014,new int[]{3014,3017,3020,3017});
    addAnimatedTile(3017,new int[]{3014,3017,3020,3017});
    addAnimatedTile(3020,new int[]{3014,3017,3020,3017});

    // Green Water, Right
    addAnimatedTile(3015,new int[]{3015,3018,3021,3018});
    addAnimatedTile(3018,new int[]{3015,3018,3021,3018});
    addAnimatedTile(3021,new int[]{3015,3018,3021,3018});
    
    // Green Water, Bottom Left
    addAnimatedTile(3043,new int[]{3043,3046,3048,3046});
    addAnimatedTile(3046,new int[]{3043,3046,3048,3046});
    addAnimatedTile(3048,new int[]{3043,3046,3048,3046});

    // Green Water, Bottom 
    addAnimatedTile(3044,new int[]{3044,3047,3050,3047});
    addAnimatedTile(3047,new int[]{3044,3047,3050,3047});
    addAnimatedTile(3050,new int[]{3044,3047,3050,3047});

    // Green Water, Bottom Right
    addAnimatedTile(3045,new int[]{3045,3048,3051,3048});
    addAnimatedTile(3048,new int[]{3045,3048,3051,3048});
    addAnimatedTile(3051,new int[]{3045,3048,3051,3048});

    // Green Water, Top Left Corner
    addAnimatedTile(3163,new int[]{3163,3165,3167,3165});
    addAnimatedTile(3165,new int[]{3163,3165,3167,3165});
    addAnimatedTile(3167,new int[]{3163,3165,3167,3165});

    // Green Water, Top Right Corner
    addAnimatedTile(3164,new int[]{3164,3166,3168,3166});
    addAnimatedTile(3166,new int[]{3164,3166,3168,3166});
    addAnimatedTile(3168,new int[]{3164,3166,3168,3166});

    // Green Water, Bottom Left Corner
    addAnimatedTile(3183,new int[]{3183,3185,3187,3185});
    addAnimatedTile(3185,new int[]{3183,3185,3187,3185});
    addAnimatedTile(3187,new int[]{3183,3185,3187,3185});

    // Green Water, Bottom Right Corner
    addAnimatedTile(3184,new int[]{3184,3186,3188,3186});
    addAnimatedTile(3186,new int[]{3184,3186,3188,3186});
    addAnimatedTile(3188,new int[]{3184,3186,3188,3186});

    // Green Water, Vertical Canal
    addAnimatedTile(3223,new int[]{3223,3224,3225,3224});
    addAnimatedTile(3224,new int[]{3223,3224,3225,3224});
    addAnimatedTile(3225,new int[]{3223,3224,3225,3224});

    // Green Water, Horizontal Canal
    addAnimatedTile(3253,new int[]{3253,3254,3255,3254});
    addAnimatedTile(3254,new int[]{3253,3254,3255,3254});
    addAnimatedTile(3255,new int[]{3253,3254,3255,3254});

    // Light Water, Center
    addAnimatedTile(3168,new int[]{3168,3170,3171,3170});
    addAnimatedTile(3170,new int[]{3168,3170,3171,3170});
    addAnimatedTile(3171,new int[]{3168,3170,3171,3170});


    
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
    addAnimatedTile(148,new int[]{148,178,208,238});
    addAnimatedTile(178,new int[]{178,208,238,148});
    addAnimatedTile(208,new int[]{208,238,148,178});
    addAnimatedTile(238,new int[]{238,148,178,208});


    // Waterfall golden start
    addAnimatedTile(265,new int[]{265,285,325,355});
    addAnimatedTile(285,new int[]{285,325,355,265});
    addAnimatedTile(325,new int[]{325,355,265,285});
    addAnimatedTile(355,new int[]{355,265,285,325});

    // Waterfall golden middle
    addAnimatedTile(266,new int[]{266,286,326,356});
    addAnimatedTile(286,new int[]{286,326,356,266});
    addAnimatedTile(326,new int[]{326,356,266,286});
    addAnimatedTile(356,new int[]{286,326,356,266});

    // Waterfall golden end
    addAnimatedTile(267,new int[]{267,287,327,357});
    addAnimatedTile(287,new int[]{287,327,357,267});
    addAnimatedTile(327,new int[]{327,357,267,287});
    addAnimatedTile(357,new int[]{357,267,287,327});

    // Waterfall golden end left
    addAnimatedTile(268,new int[]{268,288,328,358});
    addAnimatedTile(288,new int[]{288,328,358,268});
    addAnimatedTile(328,new int[]{328,358,268,288});
    addAnimatedTile(358,new int[]{358,268,288,328});

    // Waterfall golden end right
    addAnimatedTile(268,new int[]{268,288,328,358});
    addAnimatedTile(288,new int[]{288,328,358,268});
    addAnimatedTile(328,new int[]{328,358,268,288});
    addAnimatedTile(358,new int[]{358,268,288,328});


    
    // Golden Teleport 
    addAnimatedTile(1443,new int[]{1443,1444,1445});
    addAnimatedTile(1444,new int[]{1444,1445,1443});
    addAnimatedTile(1445,new int[]{1445,1443,1444});
    
    // White Teleport 
    addAnimatedTile(1473,new int[]{1473,1474,1475});
    addAnimatedTile(1474,new int[]{1474,1475,1473});
    addAnimatedTile(1475,new int[]{1475,1473,1474});

    // Gray Teleport 
    addAnimatedTile(1503,new int[]{1503,1504,1505});
    addAnimatedTile(1504,new int[]{1504,1505,1503});
    addAnimatedTile(1505,new int[]{1505,1503,1504});

    // Red Teleport 
    addAnimatedTile(1533,new int[]{1533,1534,1535});
    addAnimatedTile(1534,new int[]{1534,1535,1533});
    addAnimatedTile(1535,new int[]{1535,1533,1534});

    // Green Teleport 
    addAnimatedTile(1563,new int[]{1563,1564,1565});
    addAnimatedTile(1564,new int[]{1564,1565,1563});
    addAnimatedTile(1565,new int[]{1565,1563,1564});

    // Blue Teleport 
    addAnimatedTile(1583,new int[]{1583,1584,1585});
    addAnimatedTile(1584,new int[]{1584,1585,1583});
    addAnimatedTile(1585,new int[]{1585,1583,1584});

    
    // Interior_1 = 3463 - 3842

    // Blacksmith fire (small), Top
    addAnimatedTile(3560,new int[]{3560,3560,3561,3561,3562,3562,3561,3561});
    addAnimatedTile(3561,new int[]{3560,3560,3561,3561,3562,3562,3561,3561});
    addAnimatedTile(3562,new int[]{3560,3560,3561,3561,3562,3562,3561,3561});

    // Blacksmith fire (small), Bottom
    addAnimatedTile(3580,new int[]{3580,3580,3581,3581,3582,3582,3581,3581});
    addAnimatedTile(3581,new int[]{3580,3580,3581,3581,3582,3582,3581,3581});
    addAnimatedTile(3582,new int[]{3580,3580,3581,3581,3582,3582,3581,3581});

    
    // Blacksmith fire (large), Top Left
    addAnimatedTile(3557,new int[]{3557,3557,3647,3647,3737,3737,3647,3647});
    addAnimatedTile(3647,new int[]{3557,3557,3647,3647,3737,3737,3647,3647});
    addAnimatedTile(3737,new int[]{3557,3557,3647,3647,3737,3737,3647,3647});

    // Blacksmith fire (large), Top 
    addAnimatedTile(3558,new int[]{3558,3558,3648,3648,3738,3738,3648,3648});
    addAnimatedTile(3648,new int[]{3558,3558,3648,3648,3738,3738,3648,3648});
    addAnimatedTile(3738,new int[]{3558,3558,3648,3648,3738,3738,3648,3648});

    // Blacksmith fire (large), Top Right
    addAnimatedTile(3559,new int[]{3559,3559,3649,3649,3739,3739,3649,3649});
    addAnimatedTile(3649,new int[]{3559,3559,3649,3649,3739,3739,3649,3649});
    addAnimatedTile(3739,new int[]{3559,3559,3649,3649,3739,3739,3649,3649});

    // Blacksmith fire (large), Left
    addAnimatedTile(3587,new int[]{3587,3587,3677,3677,3767,3767,3677,3677});
    addAnimatedTile(3677,new int[]{3587,3587,3677,3677,3767,3767,3677,3677});
    addAnimatedTile(3767,new int[]{3587,3587,3677,3677,3767,3767,3677,3677});

    // Blacksmith fire (large), Center
    addAnimatedTile(3588,new int[]{3588,3588,3678,3678,3768,3768,3678,3678});
    addAnimatedTile(3678,new int[]{3588,3588,3678,3678,3768,3768,3678,3678});
    addAnimatedTile(3768,new int[]{3588,3588,3678,3678,3768,3768,3678,3678});

    // Blacksmith fire (large), Right
    addAnimatedTile(3588,new int[]{3588,3588,3678,3678,3768,3768,3678,3678});
    addAnimatedTile(3678,new int[]{3588,3588,3678,3678,3768,3768,3678,3678});
    addAnimatedTile(3768,new int[]{3588,3588,3678,3678,3768,3768,3678,3678});

    // Blacksmith fire (large), Bottom Left
    addAnimatedTile(3617,new int[]{3617,3617,3707,3707,3797,3797,3707,3707});
    addAnimatedTile(3707,new int[]{3617,3617,3707,3707,3797,3797,3707,3707});
    addAnimatedTile(3797,new int[]{3617,3617,3707,3707,3797,3797,3707,3707});

    // Blacksmith fire (large), Bottom
    addAnimatedTile(3618,new int[]{3618,3618,3708,3708,3798,3798,3708,3708});
    addAnimatedTile(3708,new int[]{3618,3618,3708,3708,3798,3798,3708,3708});
    addAnimatedTile(3798,new int[]{3618,3618,3708,3708,3798,3798,3708,3708});
    
    // Blacksmith fire (large), Bottom Right
    addAnimatedTile(3619,new int[]{3619,3619,3709,3709,3799,3799,3709,3709});
    addAnimatedTile(3709,new int[]{3619,3619,3709,3709,3799,3799,3709,3709});
    addAnimatedTile(3799,new int[]{3619,3619,3709,3709,3799,3799,3709,3709});

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