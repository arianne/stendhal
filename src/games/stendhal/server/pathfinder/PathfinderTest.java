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
package games.stendhal.server.pathfinder;

import games.stendhal.server.StendhalRPZone;

import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.Point;
import java.util.*;
import marauroa.common.Log4J;
import marauroa.server.game.RPWorld;
import org.apache.log4j.Logger;

import marauroa.common.game.*;
import games.stendhal.common.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.creature.*;


public class PathfinderTest 
  {
  public static void main(String[] args) throws Exception
    {
    int x0=0;
    int y0=0;
    int x1=0;
    int y1=0;
    
    StendhalRPWorld world=new StendhalRPWorld();
    world.onInit();
    
    System.out.println (x0+","+y0+" to "+x1+","+y1);
    
    RPEntity player=new Player(new RPObject());
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone("-1_semos_dungeon");

    if(args.length<4)
      {
      do
        {
        x0=Rand.rand(64);
        y0=Rand.rand(64);
        }
      while(zone.collides(x0,y0));
      
      do
        {
        x1=Rand.rand(64);
        y1=Rand.rand(64);
        }
      while(zone.collides(x1,y1));
      }
    else
      {
      x0=Integer.parseInt(args[0]);
      y0=Integer.parseInt(args[1]);
      x1=Integer.parseInt(args[2]);
      y1=Integer.parseInt(args[3]);      
      }
    
    
//    for(int j=0;j<zone.getHeight();j++)
//      {
//      for(int i=0;i<zone.getWidth();i++)
//        {
//        if(x0==i && y0==j)
//          {
//          System.out.print("O");          
//          }
//        else if(x1==i && y1==j)
//          {
//          System.out.print("D");          
//          }
//        else if(zone.collides(i,j))
//          {
//          System.out.print("*");          
//          }
//        else
//          {
//          System.out.print(".");          
//          }
//        }
//      System.out.println();          
//      }

    DijkstraPathfinder box=new DijkstraPathfinder(zone);
    
    long startTime = System.currentTimeMillis();
    
    
    List<Path.Node> nodes=null;
    
    for(int i=0;i<10000;i++) 
      {
      nodes=box.getPath(x0,y0,x1,y1);
      }

    long endTime = System.currentTimeMillis();
    
    for(int j=0;j<zone.getHeight();j++)
      {
      for(int i=0;i<zone.getWidth();i++)
        {
        boolean contained=false;
                
        for(Path.Node node: nodes)
          {
          if(node.x==i && node.y==j)
            {
            contained=true;
            break;
            }
          }
        
        if(contained)
          {
          System.out.print("P");
          }        
        else if(x0==i && y0==j)
          {
          System.out.print("O");          
          }
        else if(x1==i && y1==j)
          {
          System.out.print("D");          
          }
        else if(zone.collides(i,j))
          {
          System.out.print("*");          
          }
        else
          {
          System.out.print(".");          
          }
        }
      System.out.println();          
      }
    
    System.out.println ("STATUS: "+null+ "\t TIME: "+(endTime-startTime));
    }    
  
  public static class SplitBox
    {
    private Graph my_graph;
    private List<Rectangle> boxes;
    private StendhalRPZone zone;
    
    public SplitBox(StendhalRPZone zone)
      {
      this.zone=zone;
      boxes=new LinkedList<Rectangle>();      

      for(int j=0;j<zone.getHeight();j++)
        {
        for(int i=0;i<zone.getWidth();i++)
          {
          if(!zone.collides(i,j) && !isContained(i,j))
            {
            Rectangle rect=maxBox(i,j);
            addBox(rect);
            }          
          }
        }

      my_graph=buildGraph();
      }
    
    private void addBox(int x, int y, int width, int height)
      {
      boxes.add(new Rectangle(x,y,width,height));
      }

    private void addBox(Rectangle rect)
      {
      boxes.add(rect);
      }
     
    private boolean isContained(int x, int y)
      {
      for(Rectangle rect: boxes)
        {
        if(rect.contains(x,y))
          {
          return true;
          }
        }
      
      return false;
      }

    private String getContainedString(int x, int y)
      {
      int i=0;
      for(Rectangle rect: boxes)
        {
        if(rect.contains(x,y))
          {          
          if(i<10)
            {
            return "[ "+Integer.toString(i)+"]";          
            }
          else
            {
            return "["+Integer.toString(i)+"]";          
            }
          }
        
        i++;
        }
      
      return "[  ]";
      }

    private int getContainedInt(int x, int y)
      {
      int i=0;
      for(Rectangle rect: boxes)
        {
        if(rect.contains(x,y))
          {          
          return i;          
          }
        
        i++;
        }
      
      return -1;
      }
    
    private Rectangle getRectangle(int x, int y)
      {
      for(Rectangle rect: boxes)
        {
        if(rect.contains(x,y))
          {          
          return rect;          
          }
        }
      
      return null;
      }
    
    private Rectangle maxBox(int x,int y)
      {
      int width=zone.getWidth()-x;
      
      for(int i=0;i<width;i++)
        {
        if(zone.collides(x+i,y) || isContained(x+i,y))
          {
          width=i;
          break;
          }
        }
        
      int height=zone.getHeight()-y;
      
      for(int j=0;j<height;j++)
        {
        if(zone.collides(x,y+j) || isContained(x,y+j))
          {
          height=j;
          break;
          }
        }
      
      boolean firstStep=false;
      boolean secondStep=false;
        
      for(int i=0;i<width;i++)
        {
        for(int j=0;j<height;j++)
          {
          if(zone.collides(x+i,y+j) || isContained(x+i,y+j))
            {
            if(!firstStep)
              {
              firstStep=true;
              height=j;
              break;
              }
            else
              {
              secondStep=true;
              width=i;
              break;
              }
            }
          }
        
        if(secondStep)
          {
          break;
          }        
        }      
      
      return new Rectangle(x,y,width,height);
      }
    
    private Graph buildGraph()
      {
      Graph graph=new Graph();
      
      for(Rectangle rect: boxes)
        {
        graph.addElement(rect, (int)rect.getCenterX(), (int)rect.getCenterY());
        }
      

      for(Rectangle rect: boxes)
        {
        for(Rectangle neighbour: boxes)
          {
          if(rect!=neighbour)
            {
            if(rect.getX()==neighbour.getMaxX() && rect.getY()<=neighbour.getMaxY() && rect.getY()>=neighbour.getY() ||
               rect.getMaxX()==neighbour.getX() && rect.getY()<=neighbour.getMaxY() && rect.getY()>=neighbour.getY() ||
               rect.getY()==neighbour.getMaxY() && rect.getX()<=neighbour.getMaxX() && rect.getX()>=neighbour.getX() ||
               rect.getMaxY()==neighbour.getY() && rect.getX()<=neighbour.getMaxX() && rect.getX()>=neighbour.getX())
              {
              graph.addBiConnection(rect,neighbour);
              }
            }
          }
        }

      return graph;
      }
    
    public List<Path.Node> getPath(int x0,int y0, int x1, int y1)
      {
      int d[] = my_graph.dijkstra(my_graph.getItem(getRectangle(x0,y0)));
  
      int dest=getContainedInt(x1,y1);
      
      int next=0;
      
      List<Rectangle> list=new LinkedList<Rectangle>();
      
      while(next!=-1)
        {
        next=d[dest];        
        list.add(0,(Rectangle)my_graph.getItem(dest).getInfo());        
        dest=next;
        }
      
      List<Path.Node> result=new LinkedList<Path.Node>();
      
      int x=x0;
      int y=y0;
      
      for(int i=1;i<list.size();i++)        
        {
        Rectangle previous=list.get(i-1);
        Rectangle actual=list.get(i);
        
        int destx=x;
        int desty=y;
        
        if(actual.getY()==previous.getMaxY() || actual.getMaxY()==previous.getY())
          {
          if(actual.getWidth()<=previous.getWidth())
            {
            destx=(int)actual.getCenterX();
            desty=(int)actual.getY();
            }
          else
            {
            destx=(int)previous.getCenterX();
            desty=(int)actual.getY();
            }          
          }

        if(actual.getX()==previous.getMaxX() || actual.getMaxX()==previous.getX())
          {
          if(actual.getHeight()<=previous.getHeight())
            {
            desty=(int)actual.getCenterY();
            destx=(int)actual.getX();
            }
          else
            {
            desty=(int)previous.getCenterY();
            destx=(int)actual.getX();
            }          
          }

        Vector<Point> points=Line.renderLine(x,y,destx,desty);
        for(Point p: points)
          {
          result.add(new Path.Node(p.x, p.y));
          }
        
        x=destx;
        y=desty;
        }

      Vector<Point> points=Line.renderLine(x,y,x1,y1);
      for(Point p: points)
        {
        result.add(new Path.Node(p.x, p.y));
        }
      
      return result;
      }
    }
    
    
  }
