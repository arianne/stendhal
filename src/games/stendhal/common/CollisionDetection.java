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

public class CollisionDetection 
  {
  byte[][] blocked;
  
  public CollisionDetection()
    {
    }
  
  public void addLayer(Reader data) throws IOException
    {
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
    }
  
  /** REMOVE ME LATER: Test stuff **/
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
                if(j+m<blocked.length && i+k<blocked[0].length)
                  {
                  blocked[j+m][i+k]=1;
                  }
                }
            break;
          case 't':
            for(int k=0;k<2;k++)
              for(int m=0;m<2;m++)
                {
                if(j+m<blocked.length && i+k<blocked[0].length)
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
                if(j+m<blocked.length && i+k<blocked[0].length)
                  {
                  blocked[j+m][i+k]=1;                
                  }
                }
            break;
          }    
        }
      }  
    }
  
  public boolean collides(Rectangle2D shape)
    {
    double x=shape.getX();
    double y=shape.getY();
    double w=shape.getWidth();
    double h=shape.getHeight();

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
  
  public static void main(String[] args)
    {
    try
      {
      double x=25,y=1;
      
      CollisionDetection collision=new CollisionDetection();
      collision.addLayer(new FileReader("maps/city_layer0.txt"));
      collision.addLayer(new FileReader("maps/city_layer1.txt"));
      
      for(int i=0;i<collision.blocked.length;i++)
        {
        for(int j=0;j<collision.blocked[0].length;j++)
          {
          if(i==(int)y && j==(int)x)
            {
            System.out.print("O");
            }
          else if(collision.blocked[i][j]==0)
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
      
      if(collision.collides(new Rectangle.Double(x,y,1,2)))
        {
        System.out.println("A collision exists");
        }
      else
        {
        System.out.println("No collision");
        }
      }
    catch(Exception e)
      {
      e.printStackTrace();
      }
    }
  }
