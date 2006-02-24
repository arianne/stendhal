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
import games.stendhal.server.entity.item.*;
import games.stendhal.server.pathfinder.Path;
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
  
  private int blood;
  
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
  
  private static int TURNS_WHILE_ATK_DEF_XP_INCREASE=40;
  

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

      entity.add("atk",RPClass.SHORT,RPClass.PRIVATE);
      entity.add("atk_xp",RPClass.INT,RPClass.PRIVATE);
      entity.add("def",RPClass.SHORT,RPClass.PRIVATE);
      entity.add("def_xp",RPClass.INT,RPClass.PRIVATE);

      entity.add("risk",RPClass.BYTE, RPClass.VOLATILE);
      entity.add("damage",RPClass.INT, RPClass.VOLATILE);
      entity.add("target",RPClass.INT, RPClass.VOLATILE);

      entity.addRPSlot("head",1,RPClass.PRIVATE);
      entity.addRPSlot("rhand",1,RPClass.PRIVATE);
      entity.addRPSlot("lhand",1,RPClass.PRIVATE);
      entity.addRPSlot("armor",1,RPClass.PRIVATE);
      entity.addRPSlot("legs",1,RPClass.PRIVATE);
      entity.addRPSlot("feet",1,RPClass.PRIVATE);
      entity.addRPSlot("bag",12,RPClass.PRIVATE);
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
  
  public void setATKXP(int atk)
    {
    this.atk_xp=atk;
    put("atk_xp",atk_xp);
    incATKXP();
    }

  public int getATKXP()
    {
    return atk_xp;
    }

  public int incATKXP()
    {
    this.atk_xp++;
    put("atk_xp",atk_xp);

    int newLevel=Level.getLevel(atk_xp);
    int levels=newLevel-(getATK()-10);
    
    // In case we level up several levels at a single time.
    for(int i=0;i<Math.abs(levels);i++)
      {
      setATK(this.atk+(int)Math.signum(levels)*1);
      rp.addGameEvent(getName(),"atk",Integer.toString(getATK()));
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

  public void setDEFXP(int def)
    {
    this.def_xp=def;
    put("def_xp",def_xp);    
    incDEFXP();
    }

  public int getDEFXP()
    {
    return def_xp;
    }

  public int incDEFXP()
    {
    this.def_xp++;
    put("def_xp",def_xp);

    int newLevel=Level.getLevel(def_xp);
    int levels=newLevel-(getDEF()-10);

    // In case we level up several levels at a single time.
    for(int i=0;i<Math.abs(levels);i++)
      {
      setDEF(this.def+(int)Math.signum(levels)*1);
      rp.addGameEvent(getName(),"def",Integer.toString(getDEF()));
      }
    
    return def_xp;
    }
    

  public void setBaseHP(int newhp)
    {
    this.base_hp=newhp;
    put("base_hp",newhp);

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

  public void subXP(int newxp)
    {
    addXP(-newxp);
    }
    
  public void addXP(int newxp)
    {
    // Increment experience points
    this.xp += newxp;
    put("xp",xp);

    rp.addGameEvent(getName(),"added xp",Integer.toString(newxp));
    rp.addGameEvent(getName(),"xp",Integer.toString(xp));
    
    int newLevel=Level.getLevel(getXP());
    int levels=newLevel-getLevel();

    // In case we level up several levels at a single time.
    for(int i=0;i<Math.abs(levels);i++)
      {
      setBaseHP(getBaseHP()+(int)Math.signum(levels)*10);
      setHP(getHP()+(int)Math.signum(levels)*10);
      
      setLevel(newLevel);
      }
    }

  public int getXP()
    {
    return xp;
    }
    
    
  
  /*****************************************************************************
   *                                                                           *
   * Attack handling code.                                                     *
   *                                                                           *
   ****************************************************************************/

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
  
  public void bloodHappens()
    {
    blood=TURNS_WHILE_ATK_DEF_XP_INCREASE;
    }
  
  public boolean stillHasBlood()
    {
    if(blood>0)
      {
      blood--;
      return true;
      }
      
    return false;
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

    rp.addGameEvent(who.getName(),"damaged",getName(),Integer.toString(damage));

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
      kill(who);
      }
      
    world.modify(this);
    }
  
  protected void kill(RPEntity who)
    {
    setHP(0);
    rp.killRPEntity(this,who);
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

    rp.addGameEvent(who.getName(),"killed",getName());

    // Establish how much xp points your are rewarded
    if(getXP()>0)
      {
      int xp_reward=(int)(this.getXP()*0.05);

      for(Map.Entry<RPEntity , Integer> entry : damageReceived.entrySet())
        {
        int damageDone = ((Integer) entry.getValue()).intValue();
        RPEntity entity=entry.getKey();

        String name=(entity.has("name")?entity.get("name"):entity.get("type"));
        logger.debug(name + " did " + damageDone + " of " + totalDamageReceived + ". Reward was " + xp_reward);
        
        int xp_earn=(int) (xp_reward * ((float) damageDone / (float) totalDamageReceived));
        
        /** We limit xp gain for up to eight levels difference */
        double gain_xp_limitation=1+((getLevel()-entity.getLevel())*(1.0/8.0));
        if(gain_xp_limitation<0)
          {
          gain_xp_limitation=0;
          }
          
        if(gain_xp_limitation>1)
          {
          gain_xp_limitation=1;
          }
        
        entity.addXP((int)(xp_earn*gain_xp_limitation));
        world.modify(entity);
        }
      }
      
    damageReceived.clear();
    totalDamageReceived = 0;
    
    // Stats about dead
    if(has("name"))
      {
      stats.add("Killed "+get("name"),1);
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
  
  abstract protected void dropItemsOn(Corpse corpse);

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



  /*****************************************************************************
   *                                                                           *
   * Path handling code.                                                     *
   *                                                                           *
   ****************************************************************************/

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
  


  /*****************************************************************************
   *                                                                           *
   * Equipment handling.                                                       *
   *                                                                           *
   ****************************************************************************/

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

  public boolean equip(String name,Item item)
    {
    if(hasSlot(name))
      {
      getSlot(name).add(item);
      return true;
      }
    
    return false;
    }

  public Item drop(String name)
    {
    ActionManager manager = world.getRuleManager().getActionManager();
    
    RPSlot bag=getSlot("bag");
    for(RPObject object: bag)
      {
      if(object instanceof Item)
        {
        Item item=(Item)object;
        if(item.getName().equals(name))
          {
          bag.remove(item.getID());
          return item;
          }
        }
      }
    
    for(RPSlot slot: slots())
      {
      for(RPObject object: slot)
        {
        if(object instanceof Item)
          {
          Item item=(Item)object;
          if(item.getName().equals(name))
            {
            slot.remove(item.getID());
            return item;
            }
          }
        }        
      }
    
    return null;
    }

  public boolean isEquipped(String name)
    {
    boolean found=false;
    for(RPSlot slot: this.slots())
      {
      for(RPObject object: slot)
        {
        if(object instanceof Item)
          {
          Item item=(Item)object;
          if(item.getName().equals(name))
            {
            found=true;
            break;
            }
          }
        }
      }
    
    return found;
    }

  public Item getEquipped(String name)
    {
    boolean found=false;
    for(RPSlot slot: this.slots())
      {
      for(RPObject object: slot)
        {
        if(object instanceof Item)
          {
          Item item=(Item)object;
          if(item.getName().equals(name))
            {
            return item;
            }
          }
        }
      }
    
    return null;
    }

  public Item dropItemClass(String[] slots, String clazz)
    {
    ActionManager manager = world.getRuleManager().getActionManager();
    
    for(String slotName: slots)
      {
      RPSlot slot=getSlot(slotName);
      
      for(RPObject object: slot)
        {
        if(object instanceof Item)
          {
          Item item=(Item)object;
          if(item.isOfClass(clazz))
            {
            slot.remove(item.getID());
            return item;
            }
          }
        }        
      }
    
    return null;
    }
  
  /** checks if an item of class <i>clazz</i> is equipped in slot <i>slot</i>
   * returns true if it is, else false
   */
  public boolean isEquippedItemClass(String slot, String clazz)
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
  public Item getEquippedItemClass(String slot, String clazz)
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
    String[] weaponsClasses={"club","sword","axe"};
    
    for(String weaponClass: weaponsClasses)    
      {
      if(isEquippedItemClass("lhand", weaponClass) || isEquippedItemClass("rhand", weaponClass))
        {
        return true;
        }
      }
    
    return false;
    }
  
  public Item getWeapon()
    {
    String[] weaponsClasses={"club","sword","axe","ranged"};
    
    for(String weaponClass: weaponsClasses)    
      {
      String[] slots={"lhand","rhand"};
      
      for(String slot: slots)
        {
        Item item=getEquippedItemClass(slot, weaponClass);
        if(item!=null)
          {
          return item;
          }
        }
      }
    
    return null;
    }
  
  public Item getProjectiles()
    {
    String[] slots={"lhand","rhand"};
    
    for(String slot: slots)
      {
      Item item=getEquippedItemClass(slot, "projectiles");
      if(item!=null)
        {
        return item;
        }
      }

    return null;
    }
  
  /** returns true if the entity has a shield equipped */
  public boolean hasShield()
    {
    return isEquippedItemClass("lhand", "shield") || isEquippedItemClass("rhand", "shield");
    }
  
  public Item getShield()
    {
    Item item = getEquippedItemClass("lhand", "shield");
    if (item != null)
      {
      return item;
      }
    else
      {
      return getEquippedItemClass("rhand", "shield");
      }
    }
    
  public boolean hasArmor()
    {
    return isEquippedItemClass("armor", "armor");
    }
  
  public Item getArmor()
    {
    return getEquippedItemClass("armor", "armor");
    }
  
  public boolean hasHelmet()
    {
    return isEquippedItemClass("head", "helmet");
    }
  
  public Item getHelmet()
    {
    return getEquippedItemClass("head", "helmet");
    }
  
  public boolean hasLegs()
    {
    return isEquippedItemClass("legs", "legs");
    }
  
  public Item getLegs()
    {
    return getEquippedItemClass("legs", "legs");
    }
  
  public boolean hasBoots()
    {
    return isEquippedItemClass("feet", "boots");
    }
  
  public Item getBoots()
    {
    return getEquippedItemClass("feet", "boots");
    }
  
  /** checks if the entity has at least one item of type <i>type</i> in one
   * of the given slots
   */
  public boolean hasItem(String[] slots, String type)
    {
    boolean retVal;
    for (String slot : slots)
      {
      retVal = isEquippedItemClass(slot, type);
      if (retVal)
        {
        return true;
        }
      }
    return false;
    }
  
  }
