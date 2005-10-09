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
package games.stendhal.client.entity;

import java.util.StringTokenizer;
import marauroa.common.game.*;
import games.stendhal.client.*;
import games.stendhal.server.Path;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;

public abstract class Creature extends NPC
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Creature.class);
  
  /** Flag indicating that creatures are debugged */
  private static final boolean DEBUG_ENABLED = false;

  // some debug props
  /** should the path be hidden for this creature? */
  public boolean hidePath = false;
  /** display all debug messages for this creature in the game log */
  public boolean watch = false;
  
  
  /** creature is sleeping*/
  private boolean sleeping = false;
  /** creature has been attacked */
  private boolean attacked = false;
  /** objectid of the attacking rpobject */
  private int attackedBy = 0;
  /** true when this creature canceled the attack */
  private boolean cancelAttack = false;
  /** creature has choosen a new target */
  private boolean newTarget = false;
  /** id of the new target */
  private int newTargetId = 0;
  /** creature patrols along its path */
  private boolean patrol = false;
  /** the patrolpath */
  private List<Path.Node> patrolPath;
  /** target is out of reach */
  private boolean outOfReach = false;
  /** the target moved, so we'return trying to find a new path */
  private boolean targetMoved = false;
  /** new path to the target */
  private List<Path.Node> targetMovedPath;
  /** we're attacking */
  private boolean attacking = false;
  /** we're moving towards the target */
  private boolean moveToTarget = false;
  /** we're ran against a obstacle */
  private boolean moveToTargetBlocked = false;
  /** we're waiting for the path to clear */
  private boolean moveToTargetWaiting = false;
  /** searching new path to the target */
  private boolean moveToTargetNew = false;
  /** the path we got */
  private List<Path.Node> moveToTargetPath;
  
  public Creature(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {    
    super(gameObjects, object);
    }

  protected static String translate(String type)
    {
    return "sprites/monsters/"+type+".png";
    }
  
  public void drawPath(GameScreen screen, List<Path.Node> path, int delta)
  {
    Graphics g2d=screen.expose();
    Rectangle2D rect = getArea();
    Point2D p1 = screen.invtranslate(new Point.Double(getx(),gety()));

    for (Path.Node node : path)
    {
      Point2D p2 = screen.invtranslate(new Point.Double(node.x,node.y));

      g2d.drawLine((int) p1.getX()+delta, (int) p1.getY()+delta, (int) p2.getX()+delta, (int) p2.getY()+delta);
      p1 = p2;
    }
  }
  
  public void draw(GameScreen screen)
  {
    super.draw(screen);
    
    
    if (DEBUG_ENABLED && !hidePath)
    {
      Graphics g2d=screen.expose();

      if (targetMoved && targetMovedPath != null)
      {
        int delta = GameScreen.PIXEL_SCALE/2;
        g2d.setColor(Color.red);
        drawPath(screen,targetMovedPath, GameScreen.PIXEL_SCALE/2);
      }

      if (patrol && patrolPath != null)
      {
        g2d.setColor(Color.green);
        drawPath(screen,patrolPath, GameScreen.PIXEL_SCALE/2+1);
      }

      if ((moveToTarget || moveToTargetNew)  && moveToTargetPath != null)
      {
        g2d.setColor(Color.blue);
        drawPath(screen,moveToTargetPath, GameScreen.PIXEL_SCALE/2+2);
      }
    }
  }
  
  public List<Path.Node> getPath(String token)
  {
    String[] values = token.replace(',', ' ').replace('(', ' ').replace(')', ' ')
                           .replace('[', ' ').replace(']', ' ').split(" ");
    List<Path.Node> list = new ArrayList<Path.Node>();

    int x = 0;
    int pass = 1;
    
    for (String value : values)
    {
      if (value.trim().length() > 0)
      {
        int val = Integer.parseInt(value.trim());
        if (pass % 2 == 0)
        {
          list.add(new Path.Node(x, val));
        }
        else
        {
          x = val;
        }
        pass++;
      }
    }
    
    return list;
  }
  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
  {
    super.modifyAdded(object,changes);
    
    // Check if debug is enabled
    if(changes.has("debug") && DEBUG_ENABLED)
    {
      sleeping = false;
      attacked = false;
      cancelAttack = false;
      newTarget = false;
      patrol = false;
      outOfReach = false;
      targetMoved = false;
      attacking = false;
      moveToTarget = false;
      moveToTargetBlocked = false;
      moveToTargetWaiting = false;
      moveToTargetNew = false;

      String debug = changes.get("debug");

      if (watch)
      {
        StendhalClient.get().addEventLine(getID()+" - "+debug);
      }

      String[] actions = debug.split("\\|");
      // parse all actions
      for (String action : actions)
      {
        if (action.length() > 0)
        {
          StringTokenizer tokenizer = new StringTokenizer(action, ";");

          try
          {
            String token = tokenizer.nextToken();
            if (token.equals("sleep"))
            {
              sleeping = true;
              break;
            }
            else if (token.equals("attacked"))
            {
              attacked = true;
              attackedBy = Integer.parseInt(tokenizer.nextToken());
            }
            else if (token.equals("cancelattack"))
            {
              cancelAttack = true;
            }
            else if (token.equals("newtarget"))
            {
              newTarget = true;
              newTargetId = Integer.parseInt(tokenizer.nextToken());
            }
            else if (token.equals("patrol"))
            {
              patrol = true;
              patrolPath = getPath(tokenizer.nextToken());
            }
            else if (token.equals("outofreachstopped"))
            {
              outOfReach = true;
            }
            else if (token.equals("targetmoved"))
            {
              targetMoved = true;
              targetMovedPath = getPath(tokenizer.nextToken());
            }
            else if (token.equals("attacking"))
            {
              attacking = true;
            }
            else if (token.equals("movetotarget"))
            {
              moveToTarget = true;
              moveToTargetBlocked = false;
              moveToTargetWaiting = false;
              moveToTargetNew = false;
              String nextToken = tokenizer.nextToken();

              if (nextToken.equals("blocked"))
              {
                moveToTargetBlocked = true;
                nextToken = tokenizer.nextToken();
              }

              if (nextToken.equals("waiting"))
              {
                moveToTargetWaiting = true;
                nextToken = tokenizer.nextToken();
              }

              if (nextToken.equals("newpath"))
              {
                moveToTargetNew = true;
                nextToken = tokenizer.nextToken();
                if (nextToken.equals("blocked"))
                {
                  moveToTargetPath = null;
                }
                else
                {
                  moveToTargetPath = getPath(nextToken);
                }
              }
            }
          }
          catch (Exception e)
          {
            logger.warn("error parsing debug string '"+debug+"' actions ["+Arrays.asList(actions)+"] action '"+action+"'",e);
          }
        }
      } 
    }
  }
  
  public String[] offeredActions()
    {
    String[] superList = super.offeredActions();
    
    if (!DEBUG_ENABLED)
    {
      return superList;
    }
    
    String[] list = new String[superList.length+2];
    
    System.arraycopy(superList, 0, list, 0, superList.length);
    list[superList.length+0] = "["+(hidePath ? "show" : "hide")+" path]";
    list[superList.length+1] = "["+(watch ? "disable" : "enable")+" watch]";
    
    return list;
    }

  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("[show path]"))
      {
      hidePath = false;
      }
    else if(action.equals("[hide path]"))
      {
      hidePath = true;
      }
    else if(action.equals("[enable watch]"))
      {
      watch = true;
      }
    else if(action.equals("[disable watch]"))
      {
      watch = false;
      }
    else
      {
      super.onAction(client, action, params);
      }
    }
  

  }
