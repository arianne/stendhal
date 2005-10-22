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
import games.stendhal.client.gui.wt.Frame;
import games.stendhal.client.gui.wt.Minimap;
import games.stendhal.common.CollisionDetection;
import games.stendhal.common.Debug;
import games.stendhal.common.Pair;
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

  /** buffered minimap */
  private Minimap minimap;
  /** the main frame */
  private Frame frame;
  
 
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
    if (frame != null && minimap != null)
    {
      frame.removeChild(minimap);
      minimap = null;
    }
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

  /** Draws a minimap in the top left corner. Only the collision layer is
   * considered */
  public void drawMiniMap(GameScreen screen, GameObjects gameObjects)
    {
    for(Pair<String, CollisionDetection> cdp: collisions)
      {
      if (area != null && cdp.first().contains(area))
        {
        // create the frame if it does not exists yet
        if (frame == null)
          {
          frame = new Frame(screen);
          if (Debug.SHOW_TEST_CHARACTER_PANEL)
            {
            frame.addChild(new games.stendhal.client.gui.wt.Character(gameObjects));
            }
          // register native event handler
          screen.getComponent().addMouseListener(frame);
          screen.getComponent().addMouseMotionListener(frame);
          }
        // create the map if there is none yet
        if (minimap == null)
          {
          minimap = new Minimap(cdp.second(), screen.expose().getDeviceConfiguration(), area);
          frame.addChild(minimap);
          minimap.moveTo(0,0);
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
          minimap.setPlayerPos(player.getx(),player.gety());
          }
        else
          {
          minimap.setPlayerPos(0.0, 0.0);
          }
        frame.draw(screen.expose());
        }
      }
    }
  }
