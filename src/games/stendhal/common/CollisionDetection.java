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
  byte[][] blocked;
  
  public CollisionDetection()
    {
    }
  
  /** Add a new layer to the class.<br>
   *  A map is build of several layers. */
  public void addLayer(Reader data) throws IOException
    {
    Logger.trace("CollisionDetection::addLayer",">");
    ArrayList<String> map=new ArrayList<String>();
        
    BufferedReader file=new BufferedReader(data);
    String text;
    while((text=file.readLine())!=null)
      {
      map.add(text);
      }
    
    if(blocked==null)
      {
      blocked=new byte[map.size()][];
      int size=((String)map.get(0)).length();
      
      for(int i=0;i<map.size();i++)
        {
        blocked[i]=new byte[size];
        
        for(int j=0;j<size;j++) blocked[i][j]=0;
        }
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
        if(j==(int)y && i==(int)x)
          {
          System.out.print("O");
          }
        else if(blocked[j][i]==0)
          {
          System.out.print(".");
          }
        else
          {
          System.out.print("X");
          }          
        }
      System.out.println();
      }
    }
  
  private void buildCollisionData(ArrayList<String> map)
    {    
    Logger.trace("CollisionDetection::buildCollisionData",">");
    for(int j=0;j<map.size();j++)
      {
      for(int i=0;i<((String)map.get(0)).length();i++)
        {
        switch((map.get(j)).charAt(i))
          {
          case 'w':
          case 'F':
          case 'p':
            blocked[j][i]=1;
            break;
          case 'T':
            for(int k=0;k<5;k++)
              for(int m=0;m<4;m++)
                {
                if(j+k<getHeight() && i+m<getWidth())
                  {
                  blocked[j+k][i+m]=1;
                  }
                }
            break;
          case 't':
            for(int k=0;k<2;k++)
              for(int m=0;m<2;m++)
                {
                if(j+m<getHeight() && i+k<getWidth())
                  {
                  blocked[j+m][i+k]=1;
                  }
                }
            break;
          case 'H':
            /** BUG: Fix me later, I can access out of the array */
            blocked[j+0][i]=1;
            blocked[j+1][i]=1;
            blocked[j+2][i]=1;
            blocked[j+3][i]=1;
            blocked[j+4][i]=1;
            
            for(int k=0;k<5;k++)
              for(int m=1;m<7;m++)
                {
                if(j+k<getHeight() && i+m<getWidth())
                  {
                  blocked[j+k][i+m]=1;                
                  }
                }
            break;
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
    
    if(blocked[(int)y][(int)x]==1)
      {
      return true;
      }
      
    if(blocked[(int)(y+h)][(int)(x+w)]==1)
      {
      return true;
      }

    if(blocked[(int)y][(int)(x+w)]==1)
      {
      return true;
      }

    if(blocked[(int)(y+h)][(int)x]==1)
      {
      return true;
      }

    for(double i=x;i<=x+w;i+=1)
      {
      for(double j=y;j<=y+h;j+=1)
        {
        if(blocked[(int)j][(int)i]==1)
          {
          return true;
          }
        }
      }
    
    return false;
    }

  public int getWidth()
    {
    return blocked[0].length;
    }
  
  public int getHeight()
    {
    return blocked.length;
    }
  }
