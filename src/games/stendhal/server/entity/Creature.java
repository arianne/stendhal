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
    init();
    }

  public Creature() throws AttributeNotFoundException
    {
    super();
    init();
    }

  private void init()
    {
    patrolPath=new LinkedList<Path.Node>();
    patrolPath.add(new Path.Node(0,0));
    patrolPath.add(new Path.Node(-6,0));
    patrolPath.add(new Path.Node(-6,6));
    patrolPath.add(new Path.Node(0,6));
    }

    private static double remaind(int atk, int def, int hp, int level)
      {
      double patk = (2.0 + level)/3;
      double pdef = patk;
      double playerHp = 100.0 + (10.0 * level)/3;
      double maxHp = playerHp;
      double creatureHP = hp;
      double damageCreature = StendhalRPAction.averageDamageAttack(atk, def, patk, pdef);
      double damagePlayer = StendhalRPAction.averageDamageAttack(patk, pdef, atk, def);
      while(playerHp > 0)
        {
        creatureHP -= damagePlayer;
        playerHp -= damageCreature;
        }
      return (playerHp / maxHp);
      }

    public static int getInitialXP(int atk, int def, int hp) {
      int level, minLevel, maxLevel;
      minLevel = 0;
      maxLevel = Level.maxLevel();
      if(remaind(atk, def, hp, minLevel) >= 0.1)
        {
        return ((int) Math.round((1.0 - remaind(atk, def, hp, minLevel)) * (double) Level.getXP(minLevel + 1)));
        }
      else if(remaind(atk, def, hp, maxLevel) <= 0.1)
        {
        return Level.getXP(maxLevel);
        }
      else
        {
        while(maxLevel - minLevel > 1)
          {
          level = minLevel + ((maxLevel - minLevel)/2);
          if(remaind(atk, def, hp, level) < 0.1) minLevel = level;
          else maxLevel = level;
          }
          double r1 = remaind(atk, def, hp, minLevel);
          double r2 = remaind(atk, def, hp, maxLevel);
          if(r1 == 0.1) return Level.getLevel(minLevel);
          if(r2 == 0.1) return Level.getLevel(maxLevel);
          return Level.getXP(minLevel) + ((int) Math.round((0.1 - r1)/ (r2-r1) *((long) Level.getXP(minLevel))));
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
        int fx=player.getx();
        int fy=player.gety();

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
      target=this.getAttackSource(0);
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
