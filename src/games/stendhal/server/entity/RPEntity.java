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

import games.stendhal.common.Level;
import games.stendhal.common.Rand;
import games.stendhal.server.Path;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.rule.ActionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.Statistics;
import org.apache.log4j.Logger;


public abstract class RPEntity extends Entity
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(RPEntity.class);

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
  
  /** List of all attackers of this entity */
  private List<RPEntity> attackSource;
  /** current target */
  private RPEntity attackTarget;

  private Map<RPEntity,Integer> damageReceived;
  private int totalDamageReceived;
  
  
  /** the path */
  private List<Path.Node> path;
  /** current position in the path */
  private int pathPosition;
  /** true if the path is a loop */
  private boolean pathLoop;
  

  public static void generateRPClass()
    {
    stats=Statistics.getStatistics();

    try
      {
      RPClass entity=new RPClass("rpentity");
      entity.isA("entity");
      entity.add("name",RPClass.STRING);
      entity.add("level",RPClass.SHORT);
      entity.add("xp",RPClass.INT);

      entity.add("hp/base_hp",RPClass.FLOAT, RPClass.VOLATILE);
      entity.add("base_hp",RPClass.SHORT,RPClass.PRIVATE);
      entity.add("hp",RPClass.SHORT,RPClass.PRIVATE);

      entity.add("atk",RPClass.BYTE,RPClass.PRIVATE);
      entity.add("atk_xp",RPClass.INT,RPClass.PRIVATE);
      entity.add("def",RPClass.BYTE,RPClass.PRIVATE);
      entity.add("def_xp",RPClass.INT,RPClass.PRIVATE);

      entity.add("risk",RPClass.BYTE, RPClass.VOLATILE);
      entity.add("damage",RPClass.SHORT, RPClass.VOLATILE);
      entity.add("target",RPClass.INT, RPClass.VOLATILE);
      }
    catch(RPClass.SyntaxException e)
      {
      logger.error("cannot generateRPClass",e);
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
    int levels=newLevel-(getATK()-10);
    
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
    int levels=newLevel-(getDEF()-10);
    
    if(levels>0)
      {
      setDEF(this.def+1);
      }
    
    return def_xp;
    }

  public void setBaseHP(int hp)
    {
    this.base_hp=hp;
    put("base_hp",hp);

    if(base_hp!=0)
      {
      put("hp/base_hp",(double)hp/(double)base_hp);
      }
    else
      {
      put("hp/base_hp",1);
      }
    }

  public int getBaseHP()
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

    // In case we level up several levels at a single time.
    for(int i=0;i<levels;i++)
      {
      setBaseHP(getBaseHP()+10);
      setLevel(newLevel);
      }
    }

  public int getXP()
    {
    return xp;
    }

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

    if (attackTarget != null)
      {
      attackTarget.attackSource.remove(this);
      }

    attackTarget = null;
    }

  /** This method is called on each round when this entity has been attacked by
   *  RPEntity who and status is true to means keep attacking and false mean stop
   *  attacking. */
  public void onAttack(RPEntity who, boolean status)
    {
    if(status)
      {
      who.attackTarget = this;
      if(!attackSource.contains(who))
        {
        attackSource.add(who);
        }
      }
    else
      {
      if(who.has("target")) who.remove("target");
      who.attackTarget = null;
      attackSource.remove(who);
      }
    }

  /** This method is called when this entity has been attacked by RPEntity who and
   *  it has been damaged with damage points. */
  public void onDamage(RPEntity who, int damage)
    {
    logger.debug("Damaged "+damage+" points by "+who.getID());

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

    if(leftHP>0)
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
   * For almost everything remove is true and the creature is removed
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
        logger.debug(name + " did " + damageDone + " of " + totalDamageReceived + ". Reward was " + xp_reward);
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
    Corpse corpse=new Corpse(this,who);
    
    // Add some reward inside the corpse
    dropItemsOn(corpse);
    
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
  
  protected void dropItemsOn(Corpse corpse)
    {
    int amount=(int)(getXP()/100)+(Rand.roll1D6()-3);
    if(amount>0)
      {
      Money money=(Money)world.getRuleManager().getEntityManager().getItem("money");
      money.setQuantity(amount);
      corpse.add(money);
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

  /**
   * Set a path to follow for this entity. A previos path is cleared and the
   * entity starts at the first node (so the first node should be its position,
   * of course)
   * @param path list of connected nodes
   * @param cycle true, the entity will resume at the start of the path when
   *              finished; false, it will stop at the last node (and clear the
   *              path)
   */
  public void setPath(List<Path.Node> path, boolean cycle)
    {
    this.path=path;
    this.pathPosition=0;
    this.pathLoop=cycle;
    }

  /**
   * Adds some nodes to the path to follow for this entity.
   * The current path-position is kept.
   */
  public void addToPath(List<Path.Node> pathNodes)
  {
    if (path == null)
    {
      path = new ArrayList<Path.Node>();
    }
    
    path.addAll(pathNodes);
  }
  
  /**
   * Sets the loop-flag of the path. Note that the path should be closed.
   */
  public void setPathLoop(boolean loop)
  {
    this.pathLoop = loop;
  }

  public void clearPath()
    {
    this.path=null;
    this.pathPosition = 0;
    this.pathLoop = false;
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
  
  /** tries to equip the item in the appropriate slot. returns true if the item
   * can be equipped, else false 
   */
  public boolean equip(Item item)
  {
    ActionManager manager = world.getRuleManager().getActionManager();
    
    String slot = manager.canEquip(this, item);
    if (slot != null)
    {
      return manager.onEquip(this, slot, item);
    }
    // we cannot equip this item
    return false;
  }

  /** checks if an item of class <i>clazz</i> is equipped in slot <i>slot</i>
   * returns true if it is, else false
   */
  private boolean checkSlotForItem(String slot, String clazz)
  {
    if(hasSlot(slot))
    {
      // get slot if the this entity has one
      RPSlot rpslot = getSlot(slot );
      // traverse all slot items
      for (RPObject item : rpslot)
      {
        if ((item instanceof Item) && ((Item)item).isOfClass(clazz))
        {
          return true;
        }
      }
    }
    // no slot, free slot or wrong item type
    return false;
  }
  
  /** returns the first item of class <i>clazz</i> from the slot or <code>null</code>
   * if there is no item with the requested clazz
   * returns the item or null
   */
  private Item getFirstItemFromSlot(String slot, String clazz)
  {
    if(hasSlot(slot))
    {
      // get slot if the this entity has one
      RPSlot rpslot = getSlot(slot );
      // traverse all slot items
      for (RPObject object : rpslot)
      {
        // is it the right type
        if (object instanceof Item)
        {
          Item item = (Item) object;
          if (item.isOfClass(clazz))
          {
            return (Item) item;
          }
        }
      }
    }
    // no slot, free slot or wrong item type
    return null;
  }

  /** returns true if the entity has a weapon equipped */
  public boolean hasWeapon()
    {
    return checkSlotForItem("lhand", "club") || checkSlotForItem("rhand", "club");
    }
  
  public Item getWeapon()
    {
    Item item = getFirstItemFromSlot("lhand", "club");
    if (item != null)
      {
      return item;
      }
    else
      {
      return getFirstItemFromSlot("rhand", "club");
      }
    }
  
  /** returns true if the entity has a shield equipped */
  public boolean hasShield()
    {
    return checkSlotForItem("lhand", "shield") || checkSlotForItem("rhand", "shield");
    }
  
  public Item getShield()
    {
    Item item = getFirstItemFromSlot("lhand", "shield");
    if (item != null)
      {
      return item;
      }
    else
      {
      return getFirstItemFromSlot("rhand", "shield");
      }
    }
    
  public boolean hasArmor()
    {
    return checkSlotForItem("armor", "armor");
    }
  
  public Item getArmor()
    {
    return getFirstItemFromSlot("armor", "armor");
    }
  
  /** checks if the entity has at least one item of type <i>type</i> in one
   * of the given slots
   */
  public boolean hasItem(String[] slots, String type)
    {
    boolean retVal;
    for (String slot : slots)
      {
      retVal = checkSlotForItem(slot, type);
      if (retVal)
        {
        return true;
        }
      }
    return false;
    }
  
  }
