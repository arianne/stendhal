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
  static private class Pair
    {
    public String name;
    public TileRenderer renderer;
    public CollisionDetection collisionMap;
    
    Pair(String name, TileRenderer renderer)
      {
      this.name=name;
      this.renderer=renderer;      
      }

    Pair(String name, TileRenderer renderer, CollisionDetection collisionMap)
      {
      this.name=name;
      this.renderer=renderer;      
      this.collisionMap=collisionMap;
      }
    }
  
  /** List of pair name, layer */
  private LinkedList<Pair> layers;
  /** Tilestore contains the tiles to draw */
  private TileStore tilestore;
  
  /** Name of the layers set that we are rendering right now */
  private String area;
    
  public StaticGameLayers()
    {
    layers=new LinkedList<Pair>();
    tilestore=TileStore.get("sprites/zelda_outside_chipset.gif");    
    area=null;
    }
  
  /** Returns width in world units */
  public double getWidth()
    {
    double width=0;
    
    for(Pair p: layers)
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
    
    for(Pair p: layers)
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
        
      layers.add(i,new Pair(name, renderer, renderer.createCollisionMap()));    
      }
    finally
      {
      Logger.trace("StaticGameLayers::addLayer","<");
      }
    }

  public boolean collides(Rectangle2D shape)
    {
    for(Pair p: layers)
      {
      if(area!=null && p.name.contains(area) && !p.name.contains("roof"))
        {
        if(p.collisionMap.collides(shape))
          {
          p.collisionMap.printaround((int)shape.getX(),(int)shape.getY(),5);
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
    for(Pair p: layers)
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
    for(Pair p: layers)
      {
      if(area!=null && p.name.contains(area))
        {
        p.renderer.draw(screen);
        }
      }
    }
  }
