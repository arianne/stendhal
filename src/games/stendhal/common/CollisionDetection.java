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
package games.stendhal.common;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;

import marauroa.common.*;

/** This class loads the map and allow you to determine if a player collides or 
 *  not with any of the non trespasable areas of the world */
public class CollisionDetection 
  {
  private boolean[] tileWalkable;
  private boolean[] blocked;
  private int width;
  private int height;
  
  public CollisionDetection(String filename)
    {
    blocked=null;
    setCollisionData(filename);
    }

  private int[] getLayerData(Reader reader) throws IOException
    {
    Logger.trace("CollisionDetection::getLayerData",">");
        
    BufferedReader file=new BufferedReader(reader);
    String text=file.readLine();
    String[] size=text.split(" ");
    width=Integer.parseInt(size[0]);
    height=Integer.parseInt(size[1]);
    
    int[] map=new int[width*height];
    
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

    Logger.trace("CollisionDetection::getLayerData",">");
    return map;
    }
  
  /** Add a new layer to the class.<br>
   *  A map is build of several layers. */
  public void addLayer(Reader data) throws IOException
    {
    Logger.trace("CollisionDetection::addLayer",">");
    int[] map=getLayerData(data);
    
    if(blocked==null)
      {
      blocked=new boolean[width*height];
      for(int i=0;i<width*height;i++) blocked[i]=false;
      }     
    
    buildCollisionData(map);
    Logger.trace("CollisionDetection::addLayer","<");
    }
  
  /** Print the area around the (x,y) useful for debugging */
  public void printaround(int x, int y, int size)
    {
    for(int j=y-size;j<y+size;j++)
      {
      for(int i=x-size;i<x+size;i++)
        {
        if(j>0 && j<height && i>0 && i<width)
          {
        if(j==(int)y && i==(int)x)
          {
          System.out.print("O");
          }
        else if(blocked[j*width+i]==false)
          {
          System.out.print(".");
          }
        else
          {
          System.out.print("X");
          }          
          }
        }
      System.out.println();
      }
    }
    
  private void setCollisionData(String filename)
    {
    Logger.trace("CollisionDetection::setCollisionData",">");
    try
      {
      BufferedReader file=new BufferedReader(new FileReader(filename));
      String text;
    
      text=file.readLine();
      String[] size=text.split(" ");
      int widthCollision=Integer.parseInt(size[0]);
      int heightCollision=Integer.parseInt(size[1]);
    
      tileWalkable=new boolean[widthCollision*heightCollision];
    
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
          tileWalkable[j]=(Integer.parseInt(item)==0);
          j++;      
          }
        }
      }
    catch(IOException e)
      {
      Logger.thrown("CollisionDetection::setCollisionData","X",e);
      System.exit(0);
      }
    finally
      {
      Logger.trace("CollisionDetection::setCollisionData","<");
      }
    }
  
  private void buildCollisionData(int[] map)
    {    
    Logger.trace("CollisionDetection::buildCollisionData",">");
    for(int i=0;i<width;i++)
      {
      for(int j=0;j<height;j++)
        {
        int value=map[j*width+i]-1;
        if(value>=0)
          {
          blocked[j*width+i]=!tileWalkable[value] || blocked[j*width+i];
          }        
        }
      }  
  
    Logger.trace("CollisionDetection::buildCollisionData","<");
    }
  
  /** Returns true if the shape enters in any of the non trespasable areas of the map */
  public boolean collides(Rectangle2D shape)
    {
    double x=shape.getX();
    double y=shape.getY();
    double w=shape.getWidth();
    double h=shape.getHeight();
    
    if(x<0 || x+w>getWidth())
      {
      return true;
      }

    if(y<0 || y+h>getHeight())
      {
      return true;
      }
    
    if(blocked[(int)y*width+(int)x])
      {
      return true;
      }
      
    if(blocked[(int)(y+h)*width+(int)(x+w)])
      {
      return true;
      }

    if(blocked[(int)y*width+(int)(x+w)])
      {
      return true;
      }

    if(blocked[(int)(y+h)*width+(int)x])
      {
      return true;
      }

    for(double i=x;i<=x+w;i+=1)
      {
      for(double j=y;j<=y+h;j+=1)
        {
        if(blocked[(int)j*width+(int)i])
          {
          return true;
          }
        }
      }
    
    return false;
    }

  public int getWidth()
    {
    return width;
    }
  
  public int getHeight()
    {
    return height;
    }
  }
