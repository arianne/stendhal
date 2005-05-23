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
import marauroa.server.game.*;

public abstract class RPEntity extends Entity
  {
  protected static Statistics stats;

  private String name;

  private int atk;
  private int def;
  private int base_hp;
  private int hp;
  private int xp;

  public static void generateRPClass()
    {
    stats=Statistics.getStatistics();

    try
      {
      RPClass entity=new RPClass("rpentity");
      entity.isA("entity");
      entity.add("name",RPClass.STRING);
      entity.add("xp",RPClass.INT);
      entity.add("base_hp",RPClass.SHORT);
      entity.add("hp",RPClass.SHORT);
      entity.add("atk",RPClass.BYTE);
      entity.add("def",RPClass.BYTE);
      entity.add("risk",RPClass.BYTE);
      entity.add("damage",RPClass.BYTE);
      entity.add("target",RPClass.INT);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("RPEntity::generateRPClass","X",e);
      }
    }

  public RPEntity(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    attackSource=new LinkedList<RPEntity>();
    }

  public RPEntity() throws AttributeNotFoundException
    {
    super();
    attackSource=new LinkedList<RPEntity>();
    }

  public void update() throws AttributeNotFoundException
    {
    super.update();

    if(has("name")) name=get("name");
    if(has("atk")) atk=getInt("atk");
    if(has("def")) def=getInt("def");
    if(has("base_hp")) base_hp=getInt("base_hp");
    if(has("hp")) hp=getInt("hp");
    if(has("xp")) xp=getInt("xp");
    }

  public void setName(String name)
    {
    this.name=name;
    put("name",name);
    }

  public String getName()
    {
    return name;
    }

  public void setATK(int atk)
    {
    this.atk=atk;
    put("atk",atk);
    }

  public int getATK()
    {
    return atk;
    }

  public void setDEF(int def)
    {
    this.def=def;
    put("def",def);
    }

  public int getDEF()
    {
    return def;
    }

  public void setbaseHP(int hp)
    {
    this.base_hp=hp;
    put("base_hp",hp);
    this.hp=hp;
    put("hp",hp);
    }

  public int getbaseHP()
    {
    return base_hp;
    }

  public void setHP(int hp)
    {
    this.hp=hp;
    put("hp",hp);
    }

  public int getHP()
    {
    return hp;
    }

  public void setXP(int newxp)
    {
    // Increment experience points
    int levels=Level.changeLevel(xp,newxp-xp);
    if(levels>0)
      {
      // Level up
      setATK(getATK()+levels);
      setDEF(getDEF()+levels);
      setbaseHP(getbaseHP()+10*levels);
      }

    this.xp=newxp;
    put("xp",xp);
    }

  public int getXP()
    {
    return xp;
    }
  
  private List<RPEntity> attackSource;
  private RPEntity attackTarget;

  /** Modify the entity to order to attack the target entity */
  public void attack(RPEntity target)
    {
    put("target",target.getID().getObjectID());
    attackTarget=target;
    }

  /** Modify the entity to stop attacking */
  public void stopAttack()
    {
    if(has("risk")) remove("risk");
    if(has("damage")) remove("damage");
    if(has("target")) remove("target");
    
    if(attackTarget!=null)
      {
      attackTarget.attackSource.remove(this);
      }
      
    attackTarget=null;    
    }

  /** This method is called on each round when this entity has been attacked by
   *  RPEntity who and status is true to means keep attacking and false mean stop
   *  attacking. */
  public void onAttack(RPEntity who, boolean status)
    {
    if(status)
      {
      who.attackTarget=this;
      if(attackSource.indexOf(who)==-1)
        {
        attackSource.add(who);
        }
      }
    else
      {
      if(who.has("target")) who.remove("target");
      who.attackTarget=null;
      attackSource.clear();
      }      
    }

  /** This method is called when this entity has been attacked by RPEntity who and
   *  it has been damaged with damage points. */
  public void onDamage(RPEntity who, int damage)
    {
    Logger.trace("RPEntity::onDamage","D","Damaged "+damage+" points by "+who.getID());
    int leftHP=getHP()-damage;
    if(leftHP>=0)
      {
      setHP(leftHP);
      }
    else
      {
      onDead(who);
      }

    world.modify(this);
    }

  /** This method is called when the entity has been killed ( hp==0 ). */
  public void onDead(RPEntity who) {
    onDead(who, true);
  }

  /** This method is called when the entity has been killed ( hp==0 ).
   * For almost wverything remove is true and the creature is removed
   * from the world, except for the players...
   */
  public void onDead(RPEntity who, boolean remove)
    {
    stopAttack();
    who.stopAttack();

    // Establish how much xp points your are rewarded
    who.setXP(who.getXP()+(int)(getXP()*0.05));

    // Stats about dead
    stats.add("Killed "+get("type"),1);

    // Add a corpse
    Corpse corpse=new Corpse(this);
    IRPZone zone=world.getRPZone(getID());
    zone.assignRPObjectID(corpse);
    zone.add(corpse);

    rp.addCorpse(corpse);

    world.modify(who);
    if(remove) {
      world.remove(getID());
      }
    }

  /** Return true if this entity is attacked */
  public boolean isAttacked()
    {
    return attackSource.size()>0;
    }
  
  /** Return the RPEntities that are attacking this character */ 
  public List<RPEntity> getAttackSources()
    {
    return attackSource;
    }

  /** Return the RPEntity that is attacking this character */ 
  public RPEntity getAttackSource(int pos)
    {
    try
      {
      return attackSource.get(pos);
      }
    catch(IndexOutOfBoundsException e)    
      {
      return null;
      }
    }

  /** Return true if this entity is attacking */
  public boolean isAttacking()
    {
    return attackTarget!=null;
    }

  /** Return the RPEntity that this entity is attacking. */
  public RPEntity getAttackTarget()
    {
    return attackTarget;
    }

  private List<Path.Node> path;
  private int pathPosition;
  private boolean pathLoop;


  /** Set a path to follow for this entity */
  public void setPath(List<Path.Node> path, boolean cycle)
    {
    this.path=path;
    this.pathPosition=0;
    this.pathLoop=cycle;
    }

  public void clearPath()
    {
    this.path=null;
    }

  public boolean hasPath()
    {
    return path!=null;
    }

  public List<Path.Node> getPath()
    {
    return path;
    }

  public boolean isPathLoop()
    {
    return pathLoop;
    }

  public int getPathPosition()
    {
    return pathPosition;
    }

  public boolean pathCompleted()
    {
    return path!=null && pathPosition==path.size()-1;
    }

  public void setPathPosition(int pathPos)
    {
    this.pathPosition=pathPos;
    }
  }
