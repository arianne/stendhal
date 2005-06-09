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

import java.util.*;
import games.stendhal.server.*;
import games.stendhal.common.*;
import marauroa.common.*;
import marauroa.common.game.*;

public abstract class Creature extends NPC
  {
  private RespawnPoint point;
  private List<Path.Node> patrolPath;
  private RPEntity target;

  public Creature(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    createPath();
    }

  public Creature() throws AttributeNotFoundException
    {
    super();
    createPath();
    }

  protected void createPath()
    {
    /** TODO: Creat paths in other way */
    patrolPath=new LinkedList<Path.Node>();
    patrolPath.add(new Path.Node(0,0));
    patrolPath.add(new Path.Node(-6,0));
    patrolPath.add(new Path.Node(-6,6));
    patrolPath.add(new Path.Node(0,6));
    }

    /**
     * This function compute how much percentage of hp (in average) a player will lose against a creature
     * @param atk Attack stat of the creature
     * @param def Defense stat of the creature
     * @param hp Health Point stat of the creature
     * @param level Level of the player
     * @return double Percentage of remaining hp.
     */
  private static double leftTargetHPAverageCombat(int atk, int def, int hp, int level)
    {
    double patk = 2.0 + level/3.0; // Atk stat of a player of level level in average = 2 + level/3
    double pdef = patk; // Def stat of a player of level level
    double playerHp = 100.0 + (10.0 * level)/3.0; // HP Stat of a player of level level.
    double maxHp = playerHp;
    double creatureHP = hp;
    double damageCreature = StendhalRPAction.averageDamageAttack(atk, def, patk, pdef); // Average damage dealed by the creature each turn
    double damagePlayer = StendhalRPAction.averageDamageAttack(patk, pdef, atk, def); // Average damage dealed by the player each turn
    /* We now compute how much hp the player will have once he killed the creature */
    while(creatureHP > 0)
      {
      creatureHP -= damagePlayer;
      playerHp -= damageCreature;
      }
    /* We return the percentage of remaining hp */
    return (playerHp / maxHp);
    }

    /**
     * This function return the number of xp a creature should be given base on its stats;
     * @param atk Attack stat of the creature
     * @param def Defense stat of the creature
     * @param hp Health Point stat of the creature
     * @return Number of xp of the creature
     */
  public static int getInitialXP(int atk, int def, int hp)
    {
    int level, minLevel, maxLevel;
    minLevel = 0;
    maxLevel = Level.maxLevel();

    if(leftTargetHPAverageCombat(atk, def, hp, minLevel) >= 0.1) // If the creature is level 0 or less...
      {
      return Level.getXP(minLevel + 1) -1;
      }
    else if(leftTargetHPAverageCombat(atk, def, hp, maxLevel) <= 0.1) // If the creature is level 99 or more...
      {
      return Level.getXP(maxLevel) + 1;
      }
    else
      {
      while(maxLevel - minLevel > 1) // We de a dichotomic search to find what is the level of the creature
        {
        level = minLevel + ((maxLevel - minLevel)/2);
        if(leftTargetHPAverageCombat(atk, def, hp, level) < 0.1) minLevel = level;
        else maxLevel = level;
        }
      /* Now minLevel is the level of the creature and maxLevel is minLevel + 1
       * We now give the XP corresponding to this level.
       */
      if(minLevel != Level.maxLevel())
        {
        return Level.getXP(minLevel + 1) - 1;
        }
      return Level.getXP(minLevel) + 1;
      }
    }

   public void setRespawnPoint(RespawnPoint point)
    {
    this.point=point;
    }

  public RespawnPoint getRespawnPoint()
    {
    return point;
    }

  public void addXP(int newxp)
    {
    int oldLevel = Level.getLevel(getXP());
    super.addXP(newxp);
    int newLevel = Level.getLevel(getXP());

    int levelsGained = newLevel - oldLevel;

    if(levelsGained > 0)
      {
      for(int i = 0; i < levelsGained; i++)
        {
        switch((new Random()).nextInt(3))
          {
          case 0:
            setATK(getATK() + 1);
            break;
          case 1:
            setDEF(getDEF() + 1);
            break;
          case 2:
            setbaseHP(getbaseHP() + 10);
            break;
          }
        }

      setLevel(newLevel);
      }
    }

  public void onDead(RPEntity who)
    {
    if(point!=null)
      {
      point.notifyDead(this);
      }

    super.onDead(who);
    }

  public abstract double getSpeed();

  private Player getNearestPlayer(double range)
    {
    int x=getx();
    int y=gety();

    double distance=range*range; // We save this way several sqrt operations
    Player chosen=null;

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
    Logger.trace("Creature::logic",">");
    if(!hasPath() && !isAttacking())
      {
      Logger.trace("Creature::logic","D", "Creating Path for this entity");
      List<Path.Node> nodes=new LinkedList<Path.Node>();

      int size=patrolPath.size();

      for(int i=0; i<size;i++)
        {
        Path.Node actual=patrolPath.get(i);
        Path.Node next=patrolPath.get((i+1)%size);

        nodes.addAll(Path.searchPath(this,actual.x+getx(),actual.y+gety(),next.x+getx(),next.y+gety()));
        }

      setPath(nodes,true);
      }

    if(isAttacked() && target==null)
      {
      clearPath();
      target=getNearestPlayer(8);
      
      if(target==null)
        {
        target=this.getAttackSource(0);
        }
        
      Logger.trace("Creature::logic","D","Creature("+get("type")+") has been attacked by "+target.get("type"));
      }
    else if(target==null || (!target.get("zoneid").equals(get("zoneid")) && world.has(target.getID())) || !world.has(target.getID()))
      {
      if(isAttacking())
        {
        target=null;
        clearPath();
        stopAttack();
        }

      target=getNearestPlayer(8);
      if(target!=null)
        {
        Logger.trace("Creature::logic","D","Creature("+get("type")+") gets a new target.");
        }
      }

    if(target==null)
      {
      Logger.trace("Creature::logic","D","Following path");
      if(hasPath()) Path.followPath(this,getSpeed());
      }
    else if(distance(target)>16*16)
      {
      Logger.trace("Creature::logic","D","Attacker is too far. Creature stops attack");
      target=null;
      clearPath();
      stopAttack();
      stop();
      }
    else if(!nextto(target,0.25) && !target.stopped())
      {
      Logger.trace("Creature::logic","D","Moving to target. Searching new path");
      clearPath();
      setMovement(target,0,0);
      moveto(getSpeed());
      }
    else if(nextto(target,0.25))
      {
      Logger.trace("Creature::logic","D","Next to target. Creature stops and attacks");
      stop();
      attack(target);
      }
    else
      {
      Logger.trace("Creature::logic","D","Moving to target. Creature attacks");
      if(collided()) clearPath();
      attack(target);
      setMovement(target,0,0);
      moveto(getSpeed());
      
      if(getPath()==null || getPath().size()==0) // If creature is blocked choose a new target
        {
        Logger.trace("Creature::logic","D","Blocked. Choosing a new target.");
        target=null;
        clearPath();
        stopAttack();
        stop();
        }
      }

    if(!stopped())
      {
      StendhalRPAction.move(this);
      }

    if(rp.getTurn()%5==0 && isAttacking())
      {
      StendhalRPAction.attack(this,getAttackTarget());
      }

    world.modify(this);
    Logger.trace("Creature::logic","<");
    }
  }
