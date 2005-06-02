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
package games.stendhal.server;

import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;

import games.stendhal.common.*;
import games.stendhal.server.entity.*;

import java.util.*;

public class Path
  {
  private static RPServerManager rpman;
  private static RPWorld world;

  public static class Node
    {
    public int x;
    public int y;

    public Node(int x, int y)
      {
      this.x=x;
      this.y=y;
      }
    }

  public static void initialize(RPServerManager rpman, RPWorld world)
    {
    Path.rpman=rpman;
    Path.world=world;
    }

  private static void moveto(RPEntity entity, int x, int y, double speed)
    {
    int rndx=x-entity.getx();
    int rndy=y-entity.gety();

    if(Math.abs(rndx)>Math.abs(rndy))
      {
      if(Math.signum(rndx)<0)
        {
        entity.setDirection(Direction.LEFT);
        entity.setSpeed(speed);
        }
      else
        {
        entity.setDirection(Direction.RIGHT);
        entity.setSpeed(speed);
        }
      }
    else
      {
      if(Math.signum(rndy)<0)
        {
        entity.setDirection(Direction.UP);
        entity.setSpeed(speed);
        }
      else
        {
        entity.setDirection(Direction.DOWN);
        entity.setSpeed(speed);
        }
      }

    world.modify(entity);
    }

  static class NavigableStendhalNode implements Navigable
    {
    private Entity entity;
    private Entity dest;
    private StendhalRPZone zone;
    private int radius;
    private Pathfinder.Node center;

    public NavigableStendhalNode(Entity entity, Entity dest, StendhalRPZone zone)
      {
      this.entity=entity;
      this.dest=dest;
      this.zone=zone;
      center = null;
      }

    public void setVisibility(int x, int y, int radius)
      {
      center = new Pathfinder.Node(x,y);
      this.radius = radius;
      }

    public boolean isValid(Pathfinder.Node node)
      {
      if(dest!=null && dest.distance(node.x,node.y)==0)
        {
        return true;
        }
      if((center != null) && (getDistance(node, center) > radius))
        {
        return false;
        }
      return !zone.collides(entity, node.getX(),node.getY());
      }

    public double getCost(Pathfinder.Node n1, Pathfinder.Node n2)
      {
      return Math.abs(n1.getX()-n2.getX())+Math.abs(n1.getY()-n2.getY());
      }

    public double getDistance(Pathfinder.Node n1, Pathfinder.Node n2)
      {
      return getCost(n1,n2);
      }

    public int createNodeID(Pathfinder.Node node)
      {
      return node.getY()*zone.getWidth()+node.getX();
      }

    public int maxNumberOfNodes()
      {
      return (zone.getWidth()*zone.getHeight())/10;
      }
    }

  public static List<Node> searchPath(Entity entity, int x, int y, int destx, int desty)
    {
    Logger.trace("Path::searchPath",">");
    Pathfinder path=new Pathfinder();
    NavigableStendhalNode navMap=new NavigableStendhalNode(entity, null, (StendhalRPZone)world.getRPZone(entity.getID()));
    navMap.setVisibility(entity.getx(), entity.gety(), 20);
    path.setNavigable(navMap);
    path.setEndpoints(x,y,destx,desty);

    path.init();
    // HACK: Time limited the A* search.
    while(path.getStatus()==Pathfinder.IN_PROGRESS && path.getClosed().size()<navMap.maxNumberOfNodes())
      {
      path.doStep();
      }

    if(path.getStatus()==Pathfinder.IN_PROGRESS)
      {
      return new LinkedList<Node>();
      }

    Logger.trace("Path::searchPath","D","Optimal route to ("+x+","+y+") OL:"+path.getOpen().size()+" CL:"+path.getClosed().size());
    List<Node> list=new LinkedList<Node>();
    Pathfinder.Node node=path.getBestNode();
    while(node!=null)
      {
      Logger.trace("Path::searchPath","D",node.toString());
      list.add(0,new Node(node.getX(),node.getY()));
      node=node.getParent();
      }


    Logger.trace("Path::searchPath","<");
    return list;
    }

  public static List<Node> searchPath(Entity entity, Entity dest)
    {
    Logger.trace("Path::searchPath",">");
    Pathfinder path=new Pathfinder();
    NavigableStendhalNode navMap=new NavigableStendhalNode(entity,dest,(StendhalRPZone)world.getRPZone(entity.getID()));
    navMap.setVisibility(entity.getx(), entity.gety(), 20);
    path.setNavigable(navMap);
    path.setEndpoints((int)entity.getx(),(int)entity.gety(),(int)dest.getx(),(int)dest.gety());

    path.init();
    // HACK: Time limited the A* search.
    while(path.getStatus()==Pathfinder.IN_PROGRESS && path.getClosed().size()<navMap.maxNumberOfNodes())
      {
      path.doStep();
      }

    if(path.getStatus()==Pathfinder.IN_PROGRESS)
      {
      return new LinkedList<Node>();
      }

    Logger.trace("Path::searchPath","D","Optimal route to ("+dest.getx()+","+dest.gety()+") OL:"+path.getOpen().size()+" CL:"+path.getClosed().size());
    List<Node> list=new LinkedList<Node>();
    Pathfinder.Node node=path.getBestNode();
    while(node!=null)
      {
      Logger.trace("Path::searchPath","D",node.toString());
      list.add(0,new Node(node.getX(),node.getY()));
      node=node.getParent();
      }


    Logger.trace("Path::searchPath","<");
    return list;
    }

  public static boolean followPath(RPEntity entity, double speed)
    {
    List<Node> path=entity.getPath();

    if(path.size()==0)
      {
      return true;
      }

    int pos=entity.getPathPosition();

    Node actual=path.get(pos);

    if(entity.distance(actual.x, actual.y)==0)
      {
      Logger.trace("Path::followPath","D","Completed waypoint("+pos+")("+actual.x+","+actual.y+") on Path");
      pos++;
      if(pos<path.size())
        {
        entity.setPathPosition(pos);
        actual=path.get(pos);
        Logger.trace("Path::followPath","D","Moving to waypoint("+pos+")("+actual.x+","+actual.y+") on Path from ("+entity.getx()+","+entity.gety()+")");
        moveto(entity,actual.x, actual.y,speed);
        return false;
        }
      else
        {
        if(entity.isPathLoop())
          {
          entity.setPathPosition(0);
          }
        else
          {
          entity.stop();
          }

        return true;
        }
      }
    else
      {
      Logger.trace("Path::followPath","D","Moving to waypoint("+pos+")("+actual.x+","+actual.y+") on Path from ("+entity.getx()+","+entity.gety()+")");
      moveto(entity,actual.x, actual.y,speed);
      return false;
      }
    }
  }
