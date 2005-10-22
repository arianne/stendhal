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
package games.stendhal.server.entity;

import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.common.*;
import games.stendhal.server.*;
import java.awt.*;
import java.awt.geom.*;

public abstract class Entity extends RPObject
  {
  private int x;
  private int y;
  private Direction direction;
  private double speed;
  private boolean collides;


  protected static StendhalRPRuleProcessor rp;
  protected static StendhalRPWorld world;

  public static void setRPContext(StendhalRPRuleProcessor rpContext,StendhalRPWorld worldContext)
    {
    rp = rpContext;
    world = worldContext;
    }

  public static void generateRPClass()
    {
    RPClass entity=new RPClass("entity");
    entity.add("x",RPClass.SHORT);
    entity.add("y",RPClass.SHORT);
    entity.add("dir",RPClass.BYTE, RPClass.VOLATILE);
    entity.add("speed",RPClass.FLOAT, RPClass.VOLATILE);
    }

  public Entity(RPObject object) throws AttributeNotFoundException
    {
    super(object);

    direction=Direction.STOP;
    speed=0;

    update();
    }

  public Entity() throws AttributeNotFoundException
    {
    super();
    }

  public void update() throws AttributeNotFoundException
    {
    if(has("x")) x=getInt("x");
    if(has("y")) y=getInt("y");
    if(has("speed")) speed=getDouble("speed");
    if(has("dir")) direction=Direction.build(getInt("dir"));
    }

  public void setx(int x)
    {
    if(x==this.x)
      {
      return;
      }

    this.x=x;
    put("x",x);
    }

  public int getx()
    {
    return x;
    }

  public void sety(int y)
    {
    if(y==this.y)
      {
      return;
      }

    this.y=y;
    put("y",y);
    }

  public int gety()
    {
    return y;
    }

  public void setDirection(Direction dir)
    {
    if(dir==this.direction)
      {
      return;
      }

    this.direction=dir;
    put("dir",direction.get());
    }

  public Direction getDirection()
    {
    return direction;
    }

  public void setSpeed(double speed)
    {
    if(speed==this.speed)
      {
      return;
      }

    this.speed=speed;
    put("speed",speed);
    }

  public double getSpeed()
    {
    return speed;
    }

  private int turnsToCompleteMove;

  public boolean isMoveCompleted()
    {
    ++turnsToCompleteMove;

    if(turnsToCompleteMove>=1.0/speed)
      {
      turnsToCompleteMove=0;
      return true;
      }

    return false;
    }

  public void stop()
    {
    setDirection(Direction.STOP);
    setSpeed(0);
    }

  public boolean stopped()
    {
    return direction==Direction.STOP;
    }

  public void collides(boolean val)
    {
    collides=val;
    }

  public boolean collided()
    {
    return collides;
    }
  
  public boolean isCollisionable()
    {
    return true;
    }

  /** This returns the manhattan distance.
   *  It is faster than real distance */
  public double distance(Entity entity)
    {
    return (x-entity.x)*(x-entity.x)+(y-entity.y)*(y-entity.y);
    }

  /** This returns the manhattan distance.
   *  It is faster than real distance */
  public double distance(int x, int y)
    {
    return (x-this.x)*(x-this.x)+(y-this.y)*(y-this.y);
    }

  public boolean nextto(int ex, int ey, double step)
    {
    Rectangle2D this_area=getArea(x,y);
    this_area.setRect(this_area.getX()-step,this_area.getY()-step,this_area.getWidth()+step,this_area.getHeight()+step);

    return this_area.contains(ex,ey);
    }

  public boolean nextto(Entity entity, double step)
    {
    Rectangle2D this_area=getArea(x,y);
    Rectangle2D other_area=entity.getArea(entity.x,entity.y);

    this_area.setRect(this_area.getX()-step,this_area.getY()-step,this_area.getWidth()+step,this_area.getHeight()+step);
    other_area.setRect(other_area.getX()-step,other_area.getY()-step,other_area.getWidth()+step,other_area.getHeight()+step);

    return this_area.intersects(other_area);
    }

  public boolean facingto(Entity entity)
    {
    Rectangle2D this_area=getArea(x,y);
    Rectangle2D other_area=entity.getArea(entity.x,entity.y);

    if(direction==Direction.UP && this_area.getX()==other_area.getX() && this_area.getY()-1==other_area.getY()) return true;
    if(direction==Direction.DOWN && this_area.getX()==other_area.getX() && this_area.getY()+1==other_area.getY()) return true;
    if(direction==Direction.LEFT && this_area.getY()==other_area.getY() && this_area.getX()-1==other_area.getX()) return true;
    if(direction==Direction.RIGHT && this_area.getY()==other_area.getY() && this_area.getX()+1==other_area.getX()) return true;

    return false;
    }
  
  public Direction directionTo(int px, int py)
    {
    if(Math.abs(px-x)>Math.abs(py-y))
      {
      if(px-x>0)
        {
        return Direction.RIGHT;
        }
      else
        {
        return Direction.LEFT;
        }
      }
    else
      {
      if(py-y>0)
        {
        return Direction.DOWN;
        }
      else
        {
        return Direction.UP;
        }
      }
    }

  public Rectangle2D getArea(double ex, double ey)
    {
    Rectangle2D rect=new Rectangle.Double();
    getArea(rect,ex,ey);
    return rect;
    }

  abstract public void getArea(Rectangle2D rect, double x, double y);
  }

