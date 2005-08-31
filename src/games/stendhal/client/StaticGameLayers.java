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

import games.stendhal.common.CollisionDetection;
import java.awt.geom.Rectangle2D;
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
  
  static class Pair<X>
    {
    public String name;
    public X content;
    
    Pair(String name, X content)
      {
      this.name=name;
      this.content=content;      
      }
    }
 
  /** List of pair name, layer */
  private LinkedList<Pair<TileRenderer>> layers;

  /** List of pair name, layer */
  private LinkedList<Pair<CollisionDetection>> collisions;
  
  /** Tilestore contains the tiles to draw */
  private TileStore tilestore;
  
  /** Name of the layers set that we are rendering right now */
  private String area;
    
  public StaticGameLayers()
    {
    layers=new LinkedList<Pair<TileRenderer>>();
    collisions=new LinkedList<Pair<CollisionDetection>>();
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
    
    for(Pair<TileRenderer> p: layers)
      {
      if(area!=null && p.name.contains(area))
        {
        if(width<p.content.getWidth())
          {
          width=p.content.getWidth();
          }
        }
      }

    return width;
    }

  /** Returns the height in world units */
  public double getHeight()
    {
    double height=0;
    
    for(Pair<TileRenderer> p: layers)
      {
      if(area!=null && p.name.contains(area))
        {
        if(height<p.content.getHeight())
          {
          height=p.content.getHeight();
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
          if(layers.get(i).name.compareTo(name)==0)
            {
            /** Repeated layers should be ignored. */
            return;
            }
        
          if(layers.get(i).name.compareTo(name)>=0)
            {
            break;
            }
          }
        
        layers.add(i,new Pair<TileRenderer>(name, content));    
        }
      else
        {
        CollisionDetection collision=new CollisionDetection();
        collision.setCollisionData(reader);
        
        collisions.add(new Pair<CollisionDetection>(name, collision));        
        }
      }
    finally
      {
      Log4J.finishMethod(logger, "addLayer");
      }
    }

  public boolean collides(Rectangle2D shape)
    {
    for(Pair<CollisionDetection> p: collisions)
      {
      if(area!=null && p.name.equals(area+"_collision"))
        {
        if(p.content.collides(shape))
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
    Log4J.finishMethod(logger, "setRPZoneLayersSet");
    }
  
  public String getRPZoneLayerSet()
    {
    return area;
    }
    
  public void draw(GameScreen screen, String layer)
    {
    for(Pair<TileRenderer> p: layers)
      {
      if(p.name.equals(layer))
        {
        p.content.draw(screen);
        }
      }
    }

  /** Render the choosen set of layers */
  public void draw(GameScreen screen)
    {
    for(Pair<TileRenderer> p: layers)
      {
      if(area!=null && p.name.contains(area))
        {
        p.content.draw(screen);
        }
      }
    }
  }
