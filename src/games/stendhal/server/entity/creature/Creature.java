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
package games.stendhal.server.entity.creature;

import games.stendhal.common.Level;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.npc.NPC;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import marauroa.common.Log4J;
import marauroa.common.game.*;
import org.apache.log4j.Logger;


/** 
 * Serverside representation of a creature.
 * <p>
 * A creature is defined as an entity which can move with certain speed,
 * has life points (HP) and can die.
 * <p>
 * Not all creatures have to be hostile, but at the moment the default behavior 
 * is to attack the player.
 */
public class Creature extends NPC
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Creature.class);
  
  /** Flag indicating that creatures are debugged */
  private static final boolean DEBUG_ENABLED = false;

  
  /** the number of rounds the creature should wait when the path to the target
   * is blocked and the target is not moving */
  protected static final int WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED = 5;
  
  private RespawnPoint point;
  private List<Path.Node> patrolPath;
  private RPEntity target;

  /** the number of rounds to wait for a path the target */
  private int waitRounds;

  /** the speed of this creature */
  private double speed;
  /** size in width of a tile */
  private int size; 
  
  
  public static void generateRPClass()
    {
    try
      {
      RPClass npc=new RPClass("creature");
      npc.isA("npc");
      if (DEBUG_ENABLED)
        npc.add("debug",RPClass.VERY_LONG_STRING, RPClass.VOLATILE);
      }
    catch(RPClass.SyntaxException e)
      {
      logger.error("cannot generate RPClass",e);
      }
    }

  public Creature(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    put("type","creature");
    createPath();
    }

  /** creates a new creature without properties. These must be set in the
   * deriving class
   */
  public Creature() throws AttributeNotFoundException
    {
    super();
    put("type","creature");
    createPath();
    }

  /** creates a new creature with the given properties
   */
  public Creature(String clazz, int hp, int attack, int defense, int xp, int size, double speed) throws AttributeNotFoundException
    {
    super();
    put("type","creature");
    createPath();
    
    this.speed = speed;
    this.size = size;

    put("class",clazz);
    put("x",0);
    put("y",0);

    setATK(attack);
    setDEF(defense);
    setXP(xp);
    setBaseHP(hp);
    setLevel(Level.getLevel(xp));

    stop();

    logger.debug("Created Orc: "+this);
    }
  
  protected void createPath()
    {
    /** TODO: Create paths in other way */
    patrolPath=new LinkedList<Path.Node>();
    patrolPath.add(new Path.Node(0,0));
    patrolPath.add(new Path.Node(-6,0));
    patrolPath.add(new Path.Node(-6,6));
    patrolPath.add(new Path.Node(0,6));
    }

  public void setRespawnPoint(RespawnPoint point)
    {
    this.point=point;
    }

  public RespawnPoint getRespawnPoint()
    {
    return point;
    }

  public void onDead(RPEntity who)
    {
    if(point!=null)
      {
      point.notifyDead(this);
      }

    super.onDead(who);
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y+size,1,1);
    }

  public double getSpeed()
    {
    return speed;
    }


  protected RPEntity getNearestPlayer(double range)
    {
    int x=getx();
    int y=gety();

    double distance=range*range; // We save this way several sqrt operations
    RPEntity chosen=null;

    for(NPC sheep: rp.getNPCs())
      {
      if(sheep instanceof Sheep && sheep.get("zoneid").equals(get("zoneid")))
        {
        java.awt.geom.Rectangle2D rect=sheep.getArea(sheep.getx(),sheep.gety());
        int fx=(int)rect.getX();
        int fy=(int)rect.getY();

        if(Math.abs(fx-x)<range && Math.abs(fy-y)<range)
          {
          if(distance(sheep)<distance)
            {
            chosen=sheep;
            distance=distance(sheep);
            }
          }
        }
      }
      
    for(Player player: rp.getPlayers())
      {
      if(player.get("zoneid").equals(get("zoneid")))
        {
        java.awt.geom.Rectangle2D rect=player.getArea(player.getx(),player.gety());
        int fx=(int)rect.getX();
        int fy=(int)rect.getY();

        if(Math.abs(fx-x)<range && Math.abs(fy-y)<range)
          {
          if(distance(player)<distance)
            {
            chosen=player;
            distance=distance(player);
            }
          }
        }
      }

    return chosen;
    }


  public void logic()
    {
    Log4J.startMethod(logger, "logic");

    if(getNearestPlayer(20)==null) // if there is no player near and none will see us... 
      {
      // sleep so we don't waste cpu resources
      stopAttack();
      stop();
      
      if (DEBUG_ENABLED)
        put("debug","action=sleep");
      
      world.modify(this);
      return;
      }
    
    // this will keep track of the logic so the client can display it
    StringBuilder debug = new StringBuilder();

    if(!hasPath() && !isAttacking())
      {
      logger.debug("Creating Path for this entity");
      List<Path.Node> nodes=new LinkedList<Path.Node>();

      int size=patrolPath.size();

      long time = System.nanoTime();
      for(int i=0; i<size;i++)
        {
        Path.Node actual=patrolPath.get(i);
        Path.Node next=patrolPath.get((i+1)%size);

        nodes.addAll(Path.searchPath(this,actual.x+getx(),actual.y+gety(),next.x+getx(),next.y+gety()));
        }
      long time2 = System.nanoTime()-time;
      
      if (DEBUG_ENABLED)
        debug.append("generatepatrolpath,").append(time2).append(",").append(nodes).append(";");

      setPath(nodes,true);
      }
    
    // are we attacked and we don't attack ourself?
    if(isAttacked() && target==null)
      {
      // Yep, we're attacked
      clearPath();
      
      // hit the attacker, but prefer players
      target = getNearestPlayer(8);
      if (target == null)
        {
        target = this.getAttackSource(0);
        }
      
      if (DEBUG_ENABLED)
        debug.append("attacked,").append(target.getID().getObjectID()).append(';');

      logger.debug("Creature("+get("type")+") has been attacked by "+target.get("type"));
      }
    else if(target==null || (!target.get("zoneid").equals(get("zoneid")) && world.has(target.getID())) || !world.has(target.getID()))
      {
      // no target or current target left the zone (or is dead)
      if(isAttacking())
        {
        if (DEBUG_ENABLED)
          debug.append("cancelattack;");
        target=null;
        clearPath();
        stopAttack();
        waitRounds = 0;
        }

      target = getNearestPlayer(8);
      if(target!=null)
        {
        logger.debug("Creature("+get("type")+") gets a new target.");
        if (DEBUG_ENABLED)
          debug.append("newtarget,").append(target.getID().getObjectID()).append(';');
        }
      }

    if(target==null)
      {
      // No target, so patrol along 
      logger.debug("Following path");
      if(hasPath()) Path.followPath(this,getSpeed());
      if (DEBUG_ENABLED)
        debug.append("patrol;");
      }
    else if(distance(target)>16*16)
      {
      // target out of reach
      logger.debug("Attacker is too far. Creature stops attack");
      target=null;
      clearPath();
      stopAttack();
      stop();
      if (DEBUG_ENABLED)
        debug.append("outofreachstopped;");
      }
    else if(!nextto(target,0.25) && !target.stopped())
      {
      // target not near but in reach and is moving
      logger.debug("Moving to target. Searching new path");
      clearPath();
      setMovement(target,0,0, 20.0);
      moveto(getSpeed());
      waitRounds = 0;
      if (DEBUG_ENABLED)
        debug.append("targetmoved,").append(getPath()).append(";");
      }
    else if(nextto(target,0.25))
      {
      if (DEBUG_ENABLED)
        debug.append("attacking;");
      // target is near
      logger.debug("Next to target. Creature stops and attacks");
      stop();
      attack(target);
      }
    else
      {
      // target in reach and not moving
      logger.debug("Moving to target. Creature attacks");
      if (DEBUG_ENABLED)
        debug.append("movetotarget");
      // our current Path is blocked...mostly by the target or another attacker
      if(collided())
        {
        if (DEBUG_ENABLED)
          debug.append(",blocked");
        // invalidate the path and stop
        clearPath();
        stop();
        // wait some rounds so the path can be cleared by other creatures
        // (either they move away or die)
        if (waitRounds > 0)
          {
          logger.error("waitRounds = "+waitRounds);
          }
        else
          {
          waitRounds = WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED;
          }
        }
      
      attack(target);
      // be sure to let the blocking creatures pass before trying to find a
      // new path
      if (waitRounds > 0)
        {
        if (DEBUG_ENABLED)
          debug.append(",waiting");
        waitRounds--;
        // HACK: remove collision flag (we'return not moving after all)
        collides(false);
        }
      else
        {
        setMovement(target,0,0, 20.0);
        moveto(getSpeed());
        if (DEBUG_ENABLED)
          debug.append(",newpath,").append(getPath());

        if (getPath() == null || getPath().size() == 0) // If creature is blocked choose a new target
          {
          if (DEBUG_ENABLED)
            debug.append(",blocked");
          logger.debug("Blocked. Choosing a new target.");
          target=null;
          clearPath();
          stopAttack();
          stop();
          }
        }
        if (DEBUG_ENABLED)
          debug.append(';');
      }

    if(!stopped())
      {
      StendhalRPAction.move(this);
      }

    if(rp.getTurn()%5==0  && isAttacking())
      {
      StendhalRPAction.attack(this,getAttackTarget());
      }

    if (DEBUG_ENABLED)
      put("debug",debug.toString());
    world.modify(this);
    Log4J.finishMethod(logger, "logic");
    }
  }
