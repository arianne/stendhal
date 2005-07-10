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

import games.stendhal.server.entity.item.*;

public abstract class RPEntity extends Entity
  {
  protected static Statistics stats;

  private String name;

  private int atk;
  private int atk_xp;
  private int def;
  private int def_xp;
  private int base_hp;
  private int hp;
  private int xp;
  private int level;

  public static void generateRPClass()
    {
    stats=Statistics.getStatistics();

    try
      {
      RPClass entity=new RPClass("rpentity");
      entity.isA("entity");
      entity.add("name",RPClass.STRING);
      entity.add("level",RPClass.SHORT);
      entity.add("xp",RPClass.INT,RPClass.HIDDEN);

      entity.add("hp/base_hp",RPClass.FLOAT, RPClass.VOLATILE);
      entity.add("base_hp",RPClass.SHORT,RPClass.HIDDEN);
      entity.add("hp",RPClass.SHORT,RPClass.HIDDEN);

      entity.add("atk",RPClass.BYTE,RPClass.HIDDEN);
      entity.add("atk_xp",RPClass.INT,RPClass.HIDDEN);
      entity.add("def",RPClass.BYTE,RPClass.HIDDEN);
      entity.add("def_xp",RPClass.INT,RPClass.HIDDEN);

      entity.add("risk",RPClass.BYTE, RPClass.VOLATILE);
      entity.add("damage",RPClass.SHORT, RPClass.VOLATILE);
      entity.add("target",RPClass.INT, RPClass.VOLATILE);
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
    damageReceived=new HashMap<RPEntity,Integer>();
    totalDamageReceived = 0;
    }

  public RPEntity() throws AttributeNotFoundException
    {
    super();
    attackSource=new LinkedList<RPEntity>();
    damageReceived=new HashMap<RPEntity,Integer>();
    totalDamageReceived = 0;
    }

  public void update() throws AttributeNotFoundException
    {
    super.update();

    if(has("name")) name=get("name");
    
    if(has("atk")) atk=getInt("atk");
    if(has("atk_xp")) atk_xp=getInt("atk_xp");
    
    if(has("def")) def=getInt("def");
    if(has("def_xp")) def_xp=getInt("def_xp");
    
    if(has("base_hp")) base_hp=getInt("base_hp");
    if(has("hp")) hp=getInt("hp");
    
    if(has("xp")) xp=getInt("xp");
    if(has("level")) level=getInt("level");

    if(base_hp!=0)
      {
      put("hp/base_hp",(double)hp/(double)base_hp);
      }
    else
      {
      put("hp/base_hp",1);
      }
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

  public void setLevel(int level)
    {
    this.level = level;
    put("level",level);
    }

  public int getLevel()
    {
    return level;
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
  
  public int incATKXP()
    {
    this.atk_xp++;
    put("atk_xp",atk_xp);

    int newLevel=Level.getLevel(atk_xp);
    int levels=newLevel-getATK();
    
    if(levels>0)
      {
      setATK(this.atk+1);
      }
    
    return atk_xp;
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

  public int incDEFXP()
    {
    this.def_xp++;
    put("def_xp",def_xp);

    int newLevel=Level.getLevel(def_xp);
    int levels=newLevel-getDEF();
    
    if(levels>0)
      {
      setDEF(this.def+1);
      }
    
    return def_xp;
    }

  public void setbaseHP(int hp)
    {
    this.base_hp=hp;
    put("base_hp",hp);

    //BUG: Not sure we want this here
    this.hp=hp;
    put("hp",hp);

    if(base_hp!=0)
      {
      put("hp/base_hp",(double)hp/(double)base_hp);
      }
    else
      {
      put("hp/base_hp",1);
      }
    }

  public int getbaseHP()
    {
    return base_hp;
    }

  public void setHP(int hp)
    {
    this.hp=hp;
    put("hp",hp);

    if(base_hp!=0)
      {
      put("hp/base_hp",(double)hp/(double)base_hp);
      }
    else
      {
      put("hp/base_hp",1);
      }
    }

  public int getHP()
    {
    return hp;
    }

  public void setXP(int newxp)
    {
    this.xp=newxp;
    put("xp",xp);
    }

  public void addXP(int newxp)
    {
    // Increment experience points
    this.xp += newxp;
    put("xp",xp);

    int newLevel=Level.getLevel(getXP());
    int levels=newLevel-getLevel();

    if(levels>0)
      {
      setbaseHP(getbaseHP()+10);
      setLevel(newLevel);
      }
    }

  public int getXP()
    {
    return xp;
    }

  private List<RPEntity> attackSource;
  private RPEntity attackTarget;

  private Map<RPEntity,Integer> damageReceived;
  private int totalDamageReceived;

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
      attackSource.clear(); //Not very sure if a clear is correct...
      }
    }

  /** This method is called when this entity has been attacked by RPEntity who and
   *  it has been damaged with damage points. */
  public void onDamage(RPEntity who, int damage)
    {
    Logger.trace("RPEntity::onDamage","D","Damaged "+damage+" points by "+who.getID());

    int leftHP=getHP()-damage;
    damage = (leftHP>=0 ? damage : getHP());
    
    totalDamageReceived += damage;
    if(damageReceived.containsKey(who))
      {
      damageReceived.put(who,damage+damageReceived.get(who));
      }
    else
      {
      damageReceived.put(who,damage);
      }

    if(leftHP>=0)
      {
      setHP(leftHP);
      }
    else
      {
      setHP(0);
      
      rp.killRPEntity(this,who);
      }
      
    world.modify(this);
    }

  /** This method is called when the entity has been killed ( hp==0 ). */
  public void onDead(RPEntity who)
    {
    onDead(who, true);
    }

  /** This method is called when the entity has been killed ( hp==0 ).
   * For almost wverything remove is true and the creature is removed
   * from the world, except for the players...
   */
  protected void onDead(RPEntity who, boolean remove)
    {
    stopAttack();
    who.stopAttack();

    // Establish how much xp points your are rewarded
    if(getXP()>0)
      {
      int xp_reward=(int)(this.getXP()*0.05);

      for(Map.Entry<RPEntity , Integer> entry : damageReceived.entrySet())
        {
        int damageDone = ((Integer) entry.getValue()).intValue();
        String name=(entry.getKey().has("name")?entry.getKey().get("name"):entry.getKey().get("type"));
        Logger.trace("RPEntity::onDead" , "D" ,name + " did " + damageDone + " of " + totalDamageReceived + ". Reward was " + xp_reward);
        entry.getKey().addXP((int) (xp_reward * ((float) damageDone / (float) totalDamageReceived)));
        }
      }
      
    damageReceived.clear();
    totalDamageReceived = 0;
    // Stats about dead
    if(has("class"))
      {
      stats.add("Killed "+get("class"),1);
      }
    else
      {
      stats.add("Killed "+get("type"),1);
      }

    // Add a corpse
    Corpse corpse=new Corpse(this);
    IRPZone zone=world.getRPZone(getID());
    zone.assignRPObjectID(corpse);    
    zone.add(corpse);

    rp.addCorpse(corpse);

    world.modify(who);
    if(remove)
      {
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
  
  public boolean equip(Item item)
    {
    if(item instanceof Armor)
      {
      if(hasSlot("armor"))
        {
        RPSlot slot=getSlot("armor");
        if(slot.size()==0)      
          {
          slot.assignValidID(item);
          slot.add(item);
          return true;
          }
        }
      }
    else if(item instanceof Shield || item instanceof Weapon)
      {
      if(hasSlot("rhand"))
        {
        RPSlot slot=getSlot("rhand");
        if(slot.size()==0)      
          {
          slot.assignValidID(item);
          slot.add(item);
          return true;
          }
        }
      
      if(hasSlot("lhand"))
        {
        RPSlot slot=getSlot("lhand");
        if(slot.size()==0)      
          {
          slot.assignValidID(item);
          slot.add(item);
          return true;
          }
        }
      }
    
    return false;
    }
  
  public boolean hasWeapon()
    {
    if(hasSlot("rhand"))
      {
      RPSlot slot=getSlot("rhand");
      if(slot.size()!=0)
        {
        Entity item=(Entity)slot.iterator().next();
        if(item instanceof Weapon)
          {
          return true;
          }
        }
      }
      
    if(hasSlot("lhand"))
      {
      RPSlot slot=getSlot("lhand");
      if(slot.size()!=0)
        {
        Entity item=(Entity)slot.iterator().next();
        if(item instanceof Weapon)
          {
          return true;
          }
        }
      }
      
    return false;
    }
  
  public Weapon getWeapon()
    {    
    if(hasSlot("rhand"))
      {
      RPSlot slot=getSlot("rhand");
      if(slot.size()!=0)
        {
        Entity item=(Entity)slot.iterator().next();
        if(item instanceof Weapon)
          {
          return (Weapon)item;
          }
        }
      }
      
    if(hasSlot("lhand"))
      {
      RPSlot slot=getSlot("lhand");
      if(slot.size()!=0)
        {
        Entity item=(Entity)slot.iterator().next();
        if(item instanceof Weapon)
          {
          return (Weapon)item;
          }
        }
      }
      
    return null;
    }
  
  public boolean hasShield()
    {
    if(hasSlot("rhand"))
      {
      RPSlot slot=getSlot("rhand");
      if(slot.size()!=0)
        {
        Entity item=(Entity)slot.iterator().next();
        if(item instanceof Shield)
          {
          return true;
          }
        }
      }
      
    if(hasSlot("lhand"))
      {
      RPSlot slot=getSlot("lhand");
      if(slot.size()!=0)
        {
        Entity item=(Entity)slot.iterator().next();
        if(item instanceof Shield)
          {
          return true;
          }
        }
      }
    
    return false;
    }
  
  public Shield getShield()
    {
    if(hasSlot("rhand"))
      {
      RPSlot slot=getSlot("rhand");
      if(slot.size()!=0)
        {
        Entity item=(Entity)slot.iterator().next();
        if(item instanceof Shield)
          {
          return (Shield)item;
          }
        }
      }
      
    if(hasSlot("lhand"))
      {
      RPSlot slot=getSlot("lhand");
      if(slot.size()!=0)
        {
        Entity item=(Entity)slot.iterator().next();
        if(item instanceof Shield)
          {
          return (Shield)item;
          }
        }
      }
      
    return null;
    }
    
  public boolean hasArmor()
    {
    if(hasSlot("armor"))
      {
      RPSlot slot=getSlot("armor");
      if(slot.size()!=0)
        {
        Entity item=(Entity)slot.iterator().next();
        if(item instanceof Armor)
          {
          return true;
          }
        }
      }
    
    return false;
    }
  
  public Armor getArmor()
    {
    return (Armor)getSlot("armor").iterator().next();
    }
  }
