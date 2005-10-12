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
  private static final int MINIMAP_X = 0;
  /** y-pos of the minimap */
  private static final int MINIMAP_Y = 0;
  /** buffered minimap */
  private BufferedImage minimap;
 
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

  /** draws a cross at the given position */
  private void drawCross(Graphics g, int x, int y, Color color)
  {
    int size = 4;
    g.setColor(color);
    g.drawLine(x-size,y-size, x+size,y+size);
    g.drawLine(x-size,y+size, x+size,y-size);
  }
  
  /** Draws a minimap in the top left corner. Only the collision layer is
   * considered */
  public void drawMiniMap(GameScreen screen, GameObjects gameObjects)
    {
    for(Pair<String, CollisionDetection> cdp: collisions)
      {
      if (area != null && cdp.first().contains(area))
        {
          CollisionDetection cd = cdp.second();
          Graphics2D g = screen.expose();
          Graphics minimapGraphics = g.create(MINIMAP_X+1,MINIMAP_Y+1, cd.getWidth(), cd.getHeight());

          // draw frame
          g.setColor(Color.BLACK);
          g.drawRect(MINIMAP_X, MINIMAP_Y,MINIMAP_X + cd.getWidth()+2, MINIMAP_Y + cd.getHeight()+2);

          if (minimap == null)
          {
            System.out.println("redraw minimap");
            // create the minimap
            minimap = g.getDeviceConfiguration().createCompatibleImage(cd.getWidth(), cd.getHeight());
  
            int freeColor    = new Color(0.8f, 0.8f, 0.8f).getRGB();
            int blockedColor = new Color(1.0f, 0.0f, 0.0f).getRGB();

            for (int x = 0; x < cd.getWidth(); x++)
            {
              for (int y = 0; y < cd.getHeight(); y++)
              {
                boolean walkable = cd.walkable((double) x, (double) y);
                minimap.setRGB(x, y,  walkable ? freeColor : blockedColor);
//                g.setColor(walkable ? freeColor : blockedColor);
//                g.drawRect(x,y, 1,1);
              }
            }
          }
          
          // now draw the map in the upper left corner
          long time = System.currentTimeMillis();
          minimapGraphics.drawImage(minimap,MINIMAP_X,MINIMAP_Y, null);
          
          long biTime = System.currentTimeMillis() - time;

          Color playerColor = new Color(0.0f, 0.0f, 1.0f, 1.0f);
          Color otherColor = new Color(1.0f, 0.0f, 0.0f, 1.0f);
          
          time = System.currentTimeMillis();
          // The entities are drawn direct to the graphics canvas
          for (Entity entity : gameObjects)
          {
            Rectangle2D rect = entity.getArea();
            if (entity.getType().equals("player"))
            {
              drawCross(minimapGraphics, (int) rect.getMinX(), (int) rect.getMinY(), playerColor);
            }
          }
          long entityTime = System.currentTimeMillis() - time;
//          System.out.println("biTime="+biTime+" entityTime="+entityTime);
        }
      }
    }
  }
