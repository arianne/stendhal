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

import games.stendhal.common.*;
import marauroa.common.*;

/** This class loads the map and allow you to determine if a player collides or 
 *  not with any of the non trespasable areas of the world */
public class CollisionDetection 
  {
  private boolean[] blocked;
  private int width;
  private int height;
  
  public CollisionDetection()
    {
    blocked=null;
    }
  
  public void clear()
    {
    for(int i=0;i<width*height;i++) blocked[i]=false;
    }

  public void setCollisionData(Reader reader) throws IOException
    {
    BufferedReader file=new BufferedReader(reader);
    String text;
    
    text=file.readLine();
    String[] size=text.split(" ");
    width=Integer.parseInt(size[0]);
    height=Integer.parseInt(size[1]);
    
    blocked=new boolean[width*height];
    
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
        blocked[j]=(Integer.parseInt(item)-(480+100))==1;
        j++;      
        }
      }
    }
  
  /** Print the area around the (x,y) useful for debugging */
  public void printaround(int x, int y, int size)
    {
    for(int j=y-size;j<y+size;j++)
      {
      if(j>=0 && j<height) System.out.print(j+"\t");
      for(int i=x-size;i<x+size;i++)
        {
        if(j>=0 && j<height && i>=0 && i<width)
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
    
  public boolean walkable(double x, double y)
    {
    return !blocked[(int)y*width+(int)x];
    }
  
  public boolean leavesZone(Rectangle2D shape)
    {
    double x=shape.getX();
    double y=shape.getY();
    double w=shape.getWidth();
    double h=shape.getHeight();
    
    if(x<1 || x+w>=getWidth()-1)
      {
      return true;
      }

    if(y<1 || y+h>=getHeight()-1)
      {
      return true;
      }
    
    return false;
    }
    
  /** Returns true if the shape enters in any of the non trespasable areas of the map */
  public boolean collides(Rectangle2D shape)
    {
    double x=shape.getX();
    double y=shape.getY();
    double w=shape.getWidth();
    double h=shape.getHeight();
    
    if(x<0 || x+w>=getWidth())
      {
      return true;
      }

    if(y<0 || y+h>=getHeight())
      {
      return true;
      }
    
    for(int k=0;k<height;k++)
      {
      for(int i=0;i<width;i++)
        {
        if(blocked[k*width+i])
          {
          if(shape.intersects(i,k,1,1))
            {
            System.out.println (shape+"INTERSECTS: "+i+","+k+",1,1");
            return true;
            }
          }        
        }
      }
    
//    for(int i=(int)x;i<(int)x+w;i++)
//      {
//      for(int j=(int)y;j<(int)y+h;j++)
//        {
//        if(blocked[j*width+i])
//          {
//          return true;
//          }
//        }
//      }
//    
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
