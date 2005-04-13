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

import games.stendhal.common.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;

import marauroa.common.*;

/** This class stores the layers that make the floor and the buildings */
public class StaticGameLayers
  {
  static private class PairRender
    {
    public String name;
    public TileRenderer renderer;
    
    PairRender(String name, TileRenderer renderer)
      {
      this.name=name;
      this.renderer=renderer;      
      }
    }
  
  /**TODO: Use generics */
  static private class PairCollision
    {
    public String name;
    public CollisionDetection collisionMap;
    
    PairCollision(String name, CollisionDetection collision)
      {
      this.name=name;
      this.collisionMap=collision;      
      }
    }
  
  /** List of pair name, layer */
  private LinkedList<PairRender> layers;

  /** List of pair name, layer */
  private LinkedList<PairCollision> collisions;
  
  /** Tilestore contains the tiles to draw */
  private TileStore tilestore;
  
  /** Name of the layers set that we are rendering right now */
  private String area;
    
  public StaticGameLayers()
    {
    layers=new LinkedList<PairRender>();
    collisions=new LinkedList<PairCollision>();
    tilestore=TileStore.get("sprites/zelda_outside_chipset.gif");    
    area=null;
    }
  
  /** Returns width in world units */
  public double getWidth()
    {
    double width=0;
    
    for(PairRender p: layers)
      {
      if(area!=null && p.name.contains(area))
        {
        if(width<p.renderer.getWidth())
          {
          width=p.renderer.getWidth();
          }
        }
      }

    return width;
    }

  /** Returns the height in world units */
  public double getHeight()
    {
    double height=0;
    
    for(PairRender p: layers)
      {
      if(area!=null && p.name.contains(area))
        {
        if(height<p.renderer.getHeight())
          {
          height=p.renderer.getHeight();
          }
        }
      }

    return height;
    }

  /** Add a new Layer to the set */
  public void addLayer(Reader reader, String name) throws IOException
    {
    Logger.trace("StaticGameLayers::addLayer",">");
    try 
      {
      if(!name.contains("collision"))
        {
        TileRenderer renderer=new TileRenderer(tilestore);
        renderer.setMapData(reader);

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
        
        layers.add(i,new PairRender(name, renderer));    
        }
      else
        {
        CollisionDetection collision=new CollisionDetection();
        collision.setCollisionData(reader);
        
        collisions.add(new PairCollision(name, collision));        
        }
      }
    finally
      {
      Logger.trace("StaticGameLayers::addLayer","<");
      }
    }

  public boolean collides(Rectangle2D shape)
    {
    for(PairCollision p: collisions)
      {
      if(area!=null && p.name.contains(area))
        {
        if(p.collisionMap.collides(shape))
          {
          //p.collisionMap.printaround((int)shape.getX(),(int)shape.getY(),5);
          return true;          
          }          
        }
      }
    
    return false;
    }
  
  
  /** Removes all layers */
  public void clear()
    {
    Logger.trace("StaticGameLayers::clear",">");
    layers.clear();
    Logger.trace("StaticGameLayers::clear","<");
    }
  
  /** Set the set of layers that is going to be rendered */
  public void setRPZoneLayersSet(String area)
    {
    Logger.trace("StaticGameLayers::setRPZoneLayersSet",">");
    this.area=area;
    Logger.trace("StaticGameLayers::setRPZoneLayersSet","<");
    }
  
  public String getRPZoneLayerSet()
    {
    return area;
    }
    
  public void draw(GameScreen screen, String layer)
    {
    for(PairRender p: layers)
      {
      if(p.name.equals(layer))
        {
        p.renderer.draw(screen);
        }
      }
    }

  /** Render the choosen set of layers */
  public void draw(GameScreen screen)
    {
    for(PairRender p: layers)
      {
      if(area!=null && p.name.contains(area))
        {
        p.renderer.draw(screen);
        }
      }
    }
  }
