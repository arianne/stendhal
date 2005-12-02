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

import games.stendhal.common.Debug;
import games.stendhal.common.Rand;
import games.stendhal.server.Path;
import games.stendhal.server.RespawnPoint;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.rule.EntityManager;
import games.stendhal.server.entity.npc.NPC;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;


/** 
 * Serverside representation of a creature.
 * <p>
 * A creature is defined as an entity which can move with certain speed,
 * has life points (HP) and can die.
 * <p>
 * Not all creatures have to be hostile, but at the moment the default behavior 
 * is to attack the player.
 * <p>
 * The ai
 */
public class Creature extends NPC
  {
  public static class DropItem
    {
    public String name;
    public double probability;
    public int min;
    public int max;
    
    public DropItem(String name, double probability, int min, int max)
      {
      this.name=name;
      this.probability=probability;
      this.min=min;
      this.max=max;
      }

    public DropItem(String name, double probability, int amount)
      {
      this.name=name;
      this.probability=probability;
      this.min=amount;
      this.max=amount;
      }
    }

  /** Enum classifying the possible (AI) states a creature can be in */
  private enum AiState
    {
    /** sleeping as there is no enemy in sight */
    SLEEP,
    /** doin' nothing */
    IDLE,
    /** patroling, watching for an enemy */
    PATROL,
    /** moving towards a moving target */
    APPROACHING_MOVING_TARGET,
    /** moving towards a stopped target*/
    APPROACHING_STOPPED_TARGET,
    /** attacking */
    ATTACKING;
    }
  
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Creature.class);

  /** the number of rounds the creature should wait when the path to the target
   * is blocked and the target is not moving */
  protected static final int WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED = 5;
  
  private RespawnPoint point;
  private List<Path.Node> patrolPath;
  private RPEntity target;

  /** the number of rounds to wait for a path the target */
  private int waitRounds;
  /** the current (logic)state */
  private AiState aiState;

  /** the speed of this creature */
  private double speed;

  /** size in width of a tile */
  private int width; 
  private int height; 

  /** Ths list of items this creature may drop */
  private List<Creature.DropItem> dropsItems;
  
  
  public static void generateRPClass()
    {
    try
      {
      RPClass npc=new RPClass("creature");
      npc.isA("npc");
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
  public Creature(String clazz, String subclass, String name, int hp, int attack, int defense, int level, int xp, int width, int height, double speed, List<DropItem> dropItems) throws AttributeNotFoundException
    {
    super();
    put("type","creature");
    createPath();
    
    this.speed = speed;
    this.width = width;
    this.height = height;
    
    this.dropsItems=dropItems;

    put("class",clazz);
    put("subclass",subclass);
    put("name",name);
    put("x",0);
    put("y",0);

    setATK(attack);
    setDEF(defense);
    setXP(xp);
    setBaseHP(hp);
    setHP(hp);

    setLevel(level);

    stop();
    logger.debug("Created "+clazz+":"+this);
    }
  
  protected void createPath()
    {
    /** TODO: Create paths in other way */
    patrolPath=new LinkedList<Path.Node>();
    patrolPath.add(new Path.Node(0,0));
    patrolPath.add(new Path.Node(-6,0));
    patrolPath.add(new Path.Node(-6,6));
    patrolPath.add(new Path.Node(0,6));
    aiState = AiState.IDLE;
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
    else
      {
      // Perhaps a summoned creature
      rp.removeNPC(this);
      }

    super.onDead(who);
    }

  protected void dropItemsOn(Corpse corpse)
    {
    for(Item item: createDroppedItems(world.getRuleManager().getEntityManager()))
      {
      corpse.add(item);
      }
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    if(width==1 && height==2)
      {
      // The size 1,2 is a bit special... :)
      rect.setRect(x,y+1,1,1);
      }
    else
      {
      rect.setRect(x,y,width,height);
      }
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
      if(player.has("invisible"))
        {
        continue;        
        }
        
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

  /** returns a string-repesentation of the path */
  private String pathToString()
    {
    int pos = getPathPosition();
    List<Path.Node> thePath = getPath();
    List<Path.Node> nodeList = thePath.subList(pos, thePath.size());
    
    return nodeList.toString();
    }
  
  /** need to recalculate the ai when we stop the attack */
  public void stopAttack()
    {
    aiState = AiState.IDLE;
    super.stopAttack();
    }


  public void logic()
    {
    Log4J.startMethod(logger, "logic");

    if (getNearestPlayer(20) == null) // if there is no player near and none will see us... 
      {
      // sleep so we don't waste cpu resources
      stopAttack();
      stop();

      if (Debug.CREATRUES_DEBUG_SERVER)
        put("debug","sleep");

      aiState = AiState.SLEEP;
      world.modify(this);
      return;
      }

    // this will keep track of the logic so the client can display it
    StringBuilder debug = new StringBuilder(100);

    // are we attacked and we don't attack ourself?
    if(isAttacked() && target == null)
      {
      // Yep, we're attacked
      clearPath();
      
      // hit the attacker, but prefer players
      target = getNearestPlayer(8);
      if (target == null)
        {
        target = this.getAttackSource(0);
        }
      
      if (Debug.CREATRUES_DEBUG_SERVER)
        debug.append("attacked;").append(target.getID().getObjectID()).append('|');

      logger.debug("Creature("+get("type")+") has been attacked by "+target.get("type"));
      }
    else if(target==null || (!target.get("zoneid").equals(get("zoneid")) && world.has(target.getID())) || !world.has(target.getID()))
      {
      // no target or current target left the zone (or is dead)
      if(isAttacking())
        {
        // stop the attack...
        if (Debug.CREATRUES_DEBUG_SERVER)
          debug.append("cancelattack|");
        target=null;
        clearPath();
        stopAttack();
        waitRounds = 0;
        }

      // ...and find another target
      target = getNearestPlayer(8);
      if(target!=null)
        {
        logger.debug("Creature("+get("type")+") gets a new target.");
        if (Debug.CREATRUES_DEBUG_SERVER)
          debug.append("newtarget;").append(target.getID().getObjectID()).append('|');
        }
      }

    
    // now we check our current target
    if (target == null)
      {
      // No target, so patrol along
      if (aiState != AiState.PATROL || !hasPath())
        {
        // Create a patrolpath
        logger.debug("Creating Path for this entity");
        List<Path.Node> nodes = new LinkedList<Path.Node>();

        int size = patrolPath.size();

        long time = System.nanoTime();
        for(int i=0; i<size;i++)
          {
          Path.Node actual=patrolPath.get(i);
          Path.Node next=patrolPath.get((i+1)%size);
          
          

          nodes.addAll(Path.searchPath(this,actual.x+getx(),actual.y+gety(),new Rectangle2D.Double(next.x+getx(),next.y+gety(),1.0,1.0)));
          }
        long time2 = System.nanoTime()-time;

        setPath(nodes,true);

        if (Debug.CREATRUES_DEBUG_SERVER)
          debug.append("generatepatrolpath;").append(time2).append("|");

        }
      logger.debug("Following path");
      if(hasPath()) Path.followPath(this,getSpeed());
      aiState = AiState.PATROL;
      if (Debug.CREATRUES_DEBUG_SERVER)
        debug.append("patrol;").append(pathToString()).append('|');
      }
    else if(distance(target)>16*16)
      {
      // target out of reach
      logger.debug("Attacker is too far. Creature stops attack");
      target=null;
      clearPath();
      stopAttack();
      stop();

      if (Debug.CREATRUES_DEBUG_SERVER)
        debug.append("outofreachstopped|");
      }
    else if(!nextto(target,0.25) && !target.stopped())
      {
      // target not near but in reach and is moving
      logger.debug("Moving to target. Searching new path");
      clearPath();
      setMovement(target,0,0, 20.0);
      moveto(getSpeed());
      waitRounds = 0; // clear waitrounds
      aiState = AiState.APPROACHING_MOVING_TARGET; // update ai state
      if (Debug.CREATRUES_DEBUG_SERVER)
        {
        List path = getPath();
        if (path != null)
          {
          debug.append("targetmoved;").append(pathToString()).append("|");
          }
        }
      }
    else if(nextto(target,0.25))
      {
      if (Debug.CREATRUES_DEBUG_SERVER)
        debug.append("attacking|");
      // target is near
      logger.debug("Next to target. Creature stops and attacks");
      stop();
      attack(target);
      aiState = AiState.ATTACKING;
      }
    else
      {
      // target in reach and not moving
      logger.debug("Moving to target. Creature attacks");
      if (Debug.CREATRUES_DEBUG_SERVER)
        debug.append("movetotarget");
      // our current Path is blocked...mostly by the target or another attacker
      if(collided())
        {
        if (Debug.CREATRUES_DEBUG_SERVER)
          debug.append(";blocked");
        // invalidate the path and stop
        clearPath();
        stop();
        // wait some rounds so the path can be cleared by other creatures
        // (either they move away or die)
        waitRounds = WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED;
        }

      aiState = AiState.APPROACHING_STOPPED_TARGET;
      attack(target);

      // be sure to let the blocking creatures pass before trying to find a
      // new path
      if (waitRounds > 0)
        {
        if (Debug.CREATRUES_DEBUG_SERVER)
          debug.append(";waiting");
        waitRounds--;
        // HACK: remove collision flag (we're not moving after all)
        collides(false);
        clearPath();
        }
      else
        {
        // Are we still patrol'ing?
        if (isPathLoop() || aiState == AiState.PATROL)
          {
            // yep, so clear the patrol path
            clearPath();
          }

        setMovement(target,0,0, 20.0);
        moveto(getSpeed());
        if (Debug.CREATRUES_DEBUG_SERVER)
          debug.append(";newpath");

        if (getPath() == null || getPath().size() == 0) // If creature is blocked choose a new target
          {
          if (Debug.CREATRUES_DEBUG_SERVER)
            debug.append(";blocked");
          logger.debug("Blocked. Choosing a new target.");
          target=null;
          clearPath();
          stopAttack();
          stop();
          waitRounds = WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED;
          }
        else
          {
          if (Debug.CREATRUES_DEBUG_SERVER)
            debug.append(';').append(getPath());
          }
        }

        if (Debug.CREATRUES_DEBUG_SERVER)
          debug.append(";dummy|");
      }

    if(!stopped())
      {
      StendhalRPAction.move(this);
      }

    if(rp.getTurn()%5==0  && isAttacking())
      {
      StendhalRPAction.attack(this,getAttackTarget());
      }

    if (Debug.CREATRUES_DEBUG_SERVER)
      put("debug",debug.toString());
    world.modify(this);
    Log4J.finishMethod(logger, "logic");
    }

  
  private List<Item> createDroppedItems(EntityManager manager)
    {
    List<Item> list=new LinkedList<Item>();
    
    for(Creature.DropItem dropped: dropsItems)
      {
      int probability=Rand.roll1D100();
      if(dropped.probability>=probability)
        {
        Item item=manager.getItem(dropped.name);
        list.add(item);
        }
      }
    
    return list;
    }
  }
