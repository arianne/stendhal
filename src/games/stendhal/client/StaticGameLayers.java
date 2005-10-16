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

import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.wt.Minimap;
import games.stendhal.client.gui.wt.Panel;
import games.stendhal.common.CollisionDetection;
import games.stendhal.common.Pair;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;

/** This class stores the layers that make the floor and the buildings */
public class StaticGameLayers
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(StaticGameLayers.class);
  
  /** x-pos of the minimap */
  private static final int MINIMAP_X = 10;
  /** y-pos of the minimap */
  private static final int MINIMAP_Y = 10;
  /** width of the minimap */
  private static final int MINIMAP_WIDTH = 129;
  /** height of the minimap */
  private static final int MINIMAP_HEIGTH = 129;
  /** minimum scale of the minimap */
  private static final int MINIMAP_MINIMUM_SCALE = 2;
  /** size of the minimap frame */
  private static final int MINIMAP_FRAME_SIZE = 5;
  /** buffered minimap */
  private Minimap minimap;
 
  /** List of pair name, layer */
  private LinkedList<Pair<String, TileRenderer>> layers;

  /** List of pair name, layer */
  private LinkedList<Pair<String, CollisionDetection>> collisions;

  /** Tilestore contains the tiles to draw */
  private TileStore tilestore;

  /** Name of the layers set that we are rendering right now */
  private String area;

  public StaticGameLayers()
    {
    layers=new LinkedList<Pair<String, TileRenderer>>();
    collisions=new LinkedList<Pair<String, CollisionDetection>>();
    tilestore=TileStore.get();
    tilestore.add("tilesets/zelda_outside_0_chipset.png");
    tilestore.add("tilesets/zelda_outside_1_chipset.png");
    tilestore.add("tilesets/zelda_dungeon_0_chipset.png");
    tilestore.add("tilesets/zelda_dungeon_1_chipset.png");
    tilestore.add("tilesets/zelda_interior_0_chipset.png");
    tilestore.add("tilesets/zelda_navigation_chipset.png");
    tilestore.add("tilesets/zelda_objects_chipset.png");
    tilestore.add("tilesets/zelda_collision_chipset.png");
    tilestore.add("tilesets/zelda_building_0_tileset.png");
    tilestore.add("tilesets/zelda_outside_2_chipset.png");

    area=null;
    }
  
  /** Returns width in world units */
  public double getWidth()
    {
    double width=0;
    
    for(Pair<String, TileRenderer> p: layers)
      {
      if(area!=null && p.first().contains(area))
        {
        if(width<p.second().getWidth())
          {
          width=p.second().getWidth();
          }
        }
      }

    return width;
    }

  /** Returns the height in world units */
  public double getHeight()
    {
    double height=0;
    
    for(Pair<String, TileRenderer> p: layers)
      {
      if(area!=null && p.first().contains(area))
        {
        if(height<p.second().getHeight())
          {
          height=p.second().getHeight();
          }
        }
      }

    return height;
    }

  /** Add a new Layer to the set */
  public void addLayer(Reader reader, String name) throws IOException
    {
    Log4J.startMethod(logger, "addLayer");
    try 
      {
      if(!name.contains("collision"))
        {
        TileRenderer content=new TileRenderer(tilestore);
        content.setMapData(reader);

        int i;
        for( i=0;i<layers.size();i++)
          {
          if(layers.get(i).first().compareTo(name)==0)
            {
            /** Repeated layers should be ignored. */
            return;
            }
        
          if(layers.get(i).first().compareTo(name)>=0)
            {
            break;
            }
          }
        
        layers.add(i,new Pair<String, TileRenderer>(name, content));    
        }
      else
        {
        CollisionDetection collision=new CollisionDetection();
        collision.setCollisionData(reader);
        
        collisions.add(new Pair<String, CollisionDetection>(name, collision));        
        }
      }
    finally
      {
      Log4J.finishMethod(logger, "addLayer");
      }
    }

  public boolean collides(Rectangle2D shape)
    {
    for(Pair<String, CollisionDetection> p: collisions)
      {
      if(area!=null && p.first().equals(area+"_collision"))
        {
        if(p.second().collides(shape))
          {
          return true;          
          }          
        }
      }
    
    return false;
    }
  
  
  /** Removes all layers */
  public void clear()
    {
    Log4J.startMethod(logger, "clear");
    layers.clear();
    Log4J.finishMethod(logger, "clear");
    }
  
  /** Set the set of layers that is going to be rendered */
  public void setRPZoneLayersSet(String area)
    {
    Log4J.startMethod(logger, "setRPZoneLayersSet");
    this.area=area;
    // be sure to refresh the minimap too
    minimap = null;
    Log4J.finishMethod(logger, "setRPZoneLayersSet");
    }
  
  public String getRPZoneLayerSet()
    {
    return area;
    }
    
  public void draw(GameScreen screen, String layer)
    {
    for(Pair<String, TileRenderer> p: layers)
      {
      if(p.first().equals(layer))
        {
        p.second().draw(screen);
        }
      }
    }

  /** Render the choosen set of layers */
  public void draw(GameScreen screen)
    {
    for(Pair<String, TileRenderer> p: layers)
      {
      if(area!=null && p.first().contains(area))
        {
        p.second().draw(screen);
        }
      }
    }

//  /** draws a cross at the given position */
//  private void drawCross(Graphics g, int x, int y, Color color)
//  {
//    int size = 4;
//    g.setColor(color);
//    g.drawLine(x-size,y-size, x+size,y+size);
//    g.drawLine(x-size,y+size, x+size,y-size);
//  }
  
  /** Draws a minimap in the top left corner. Only the collision layer is
   * considered */
  public void drawMiniMap(GameScreen screen, GameObjects gameObjects)
    {
    for(Pair<String, CollisionDetection> cdp: collisions)
      {
      if (area != null && cdp.first().contains(area))
        {
        // create the map if there is none yet
        if (minimap == null)
          {
          minimap = new Minimap(cdp.second(), screen.expose().getDeviceConfiguration(), area);
          }
        
        Entity player = null;
        // find the player
        for (Entity entity : gameObjects)
          {
          if (entity.getType().equals("player"))
            {
            player = entity;
            break;
            }
          }

        if (player != null)
          {
          minimap.draw(screen.expose(), player.getx(),player.gety());
          }
        else
          {
          minimap.draw(screen.expose(), 0.0, 0.0);
          }
        
        }
      }
    }
  
//  /** Encapsulates a minimap. */
//  private class MiniMap
//  {
//    /** the area this map is for */
//    private String area;
//    /** scale of map */
//    private int scale;
//    /** width of (scaled) minimap */
//    private int width;
//    /** height of (scaled) minimap */
//    private int height;
//    /** minimap image */
//    private BufferedImage image;
//    /** minimap image */
//    private BufferedImage frame;
//    
//    /** creates a new minimap from the CollisionDetection-layer */
//    public MiniMap(CollisionDetection cd, Graphics2D g)
//    {
//      // calculate size and scale
//      int w = cd. getWidth();
//      int h = cd.getHeight();
//
//      // calculate scale
//      scale = MINIMAP_MINIMUM_SCALE;
//      while ((w * (scale+1) < MINIMAP_WIDTH) && (h * (scale+1) < MINIMAP_HEIGTH))
//      {
//        scale++;
//      }
//      
//      // calculate size of map
//      width  = (w * scale < MINIMAP_WIDTH) ? w * scale : MINIMAP_WIDTH;
//      height = (h * scale < MINIMAP_HEIGTH) ? h * scale : MINIMAP_HEIGTH;
//
//
//      // create frame image
//      frame = g.getDeviceConfiguration().createCompatibleImage(width + MINIMAP_FRAME_SIZE*2+1, height + MINIMAP_FRAME_SIZE*2+1);
//      Graphics2D framegrapics = frame.createGraphics();
//      int colSteps = 255 / (MINIMAP_FRAME_SIZE);
//      for (int i = 0; i < MINIMAP_FRAME_SIZE; i++)
//      {
//        int col = colSteps * i;
//
//        framegrapics.setColor(new Color(col,col,col));
//        framegrapics.drawRect(i, i,width+(MINIMAP_FRAME_SIZE-i)*2-1, height+(MINIMAP_FRAME_SIZE-i)*2-1);
//      }
//
//      // create the image for the minimap
//      image = g.getDeviceConfiguration().createCompatibleImage(w*scale, h*scale);
//      Graphics2D mapgrapics = image.createGraphics();
////      Color freeColor    = new Color(0.8f, 0.8f, 0.8f);
//      Color freeColor    = new Color(0.0f, 1.0f, 0.0f);      
//      Color blockedColor = new Color(1.0f, 0.0f, 0.0f);
//      for (int x = 0; x < w; x++)
//      {
//        for (int y = 0; y < h; y++)
//        {
//            boolean walkable = cd.walkable((double) x, (double) y);
//            mapgrapics.setColor(walkable ? freeColor : blockedColor);
//            mapgrapics.fillRect(x*scale, y*scale, scale, scale);
//        }
//      }
//    }
//    
//    /** Draws the minimap.
//     * @param g graphics object for the game main window
//     * @param x x-position of the player (used to pan big maps)
//     * @patam y y-position of the player (used to pan big maps)
//     */
//    public void draw(Graphics2D g, double x, double y)
//    {
//      // draw the frame
//      g.drawImage(frame, MINIMAP_X, MINIMAP_Y, null);
//
//
//      // now calculate how to pan the minimap
//      
//      int panx = 0;
//      int pany = 0;
//      
//      int w = image.getWidth();
//      int h = image.getHeight();
//      
//      int xpos = (int) (x * scale) - width / 2;
//      int ypos = (int) (y * scale) - width / 2;
//      
//      if (w > width)
//      {
//        // need to pan width
//        if ((xpos + width) > w)
//        {
//          // x is at the screen border
//          panx = w - width;
//        }
//        else if (xpos > 0)
//        {
//          panx = xpos;
//        }
//      }
//
//      if (h > height)
//      {
//        // need to pan height
//        if ((ypos + height) > h)
//        {
//          // y is at the screen border
//          pany = h - height;
//        }
//        else if (ypos > 0)
//        {
//          pany = ypos;
//        }
//      }
//      // draw minimap
//      Graphics mapg = g.create(MINIMAP_X+MINIMAP_FRAME_SIZE, MINIMAP_Y+MINIMAP_FRAME_SIZE, width,height);
//      mapg.drawImage(image,-panx, -pany,null);
//      
//      
//      Color playerColor = Color.BLUE;
//      // draw the player position
//      drawCross(mapg ,(int) (x*scale)-panx, (int) (y*scale)-pany, playerColor);
//      
//    }
//
//  }
  
  
  }
