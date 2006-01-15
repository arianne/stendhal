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

import games.stendhal.client.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;


/** This class is a link between client graphical objects and server attributes objects.<br>
 *  You need to extend this object in order to add new elements to the game. */
public abstract class RPEntity extends AnimatedEntity
  {
  private static Sprite hitted;
  private static Sprite blocked;
  private static Sprite missed;

  static
    {
    SpriteStore st=SpriteStore.get();

    hitted=st.getSprite("data/sprites/combat/hitted.png");
    blocked=st.getSprite("data/sprites/combat/blocked.png");
    missed=st.getSprite("data/sprites/combat/missed.png");
    }

  private static Sprite eating;
  private static Sprite poisoned;

  static
    {
    SpriteStore st=SpriteStore.get();
    
    eating=st.getSprite("data/sprites/ideas/eat.png");
    poisoned=st.getSprite("data/sprites/ideas/poisoned.png");
    }
    


  private enum Resolution
    {
    HITTED(0),
    BLOCKED(1),
    MISSED(2);

    private final int val;
    Resolution(int val)
      {
      this.val=val;
      }

    public int get()
      {
      return val;
      }
    };

  private String name;
  private int atk;
  private int def;
  private int xp;
  private int hp;
  private int base_hp;
  private float hp_base_hp;
  private int level;
  private boolean isEating;
  private boolean isPoisoned;

  private Sprite nameImage;

  private long combatIconTime;
  private java.util.List<Sprite> damageSprites;
  private java.util.List<Long> damageSpritesTimes;

  private boolean attacked;
  private boolean attacking;
  private RPObject.ID targetEntity;
  private Resolution resolution;
  private int atkXp;
  private int defXp;

  /** Create a new game entity based on the arianne object passed */
  public RPEntity(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(gameObjects,object);
    damageSprites=new LinkedList<Sprite>();
    damageSpritesTimes=new LinkedList<Long>();
    }

  public boolean isAttacking()
    {
    return attacking;
    }

  public String getName()
    {
    return name;
    }
  
  public int getLevel()
    {
    return level;
    }
  
  public int getHP()
    {
    return hp;
    }

  protected static Sprite setOutFitPlayer(SpriteStore store,RPObject object)
    {
    int outfit=object.getInt("outfit");
    
    Sprite player=store.getSprite("data/sprites/outfit/player_base_"+outfit%100+".png");
    player=player.copy();
    outfit/=100;

    if(outfit%100!=0)
      {
      Sprite dress=store.getSprite("data/sprites/outfit/dress_"+outfit%100+".png");
      dress.draw(player.getGraphics(),0,0);
      }
    outfit/=100;

    Sprite head=store.getSprite("data/sprites/outfit/head_"+outfit%100+".png");
    head.draw(player.getGraphics(),0,0);
    outfit/=100;

    if(outfit%100!=0)
      {
      Sprite hair=store.getSprite("data/sprites/outfit/hair_"+outfit%100+".png");
      hair.draw(player.getGraphics(),0,0);
      }
    
    return player;
    }

  protected void buildAnimations(RPObject object)
    {
    SpriteStore store=SpriteStore.get();

    sprites.put("move_up", store.getAnimatedSprite(translate(object.get("type")),0,4,1.5,2));
    sprites.put("move_right", store.getAnimatedSprite(translate(object.get("type")),1,4,1.5,2));
    sprites.put("move_down", store.getAnimatedSprite(translate(object.get("type")),2,4,1.5,2));
    sprites.put("move_left", store.getAnimatedSprite(translate(object.get("type")),3,4,1.5,2));

    sprites.get("move_up")[3]=sprites.get("move_up")[1];
    sprites.get("move_right")[3]=sprites.get("move_right")[1];
    sprites.get("move_down")[3]=sprites.get("move_down")[1];
    sprites.get("move_left")[3]=sprites.get("move_left")[1];
    }

  protected Sprite defaultAnimation()
    {
    animation="move_up";
    return sprites.get("move_up")[0];
    }

  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);

    if(changes.has("base_hp"))    base_hp=changes.getInt("base_hp");
    if(changes.has("hp"))         hp=changes.getInt("hp");
    if(changes.has("hp/base_hp")) hp_base_hp=(float)changes.getDouble("hp/base_hp");
    if(changes.has("atk"))        atk=changes.getInt("atk");
    if(changes.has("def"))        def=changes.getInt("def");
    if(changes.has("xp"))         xp=changes.getInt("xp");
    if(changes.has("level"))      level=changes.getInt("level");
    if(changes.has("atk_xp"))     atkXp = changes.getInt("atk_xp");
    if(changes.has("def_xp"))     defXp = changes.getInt("def_xp");
    
    if(changes.has("eating")) 
      {
      isEating=true;    
      }
//    else if(!object.has("eating"))
//      {
//      isEating=false;
//      }

    if(changes.has("name"))
      {
      name=changes.get("name");
      nameImage=GameScreen.get().createString(getName(),Color.white);
      }
    else if(name==null && changes.has("class"))
      {
      name=changes.get("class");
      nameImage=GameScreen.get().createString(getName(),Color.white);
      }
    else if(name==null && changes.has("type"))
      {
      name=changes.get("type");
      nameImage=GameScreen.get().createString(getName(),Color.white);
      }

    if(changes.has("xp") && object.has("xp"))
      {
      if(/*((changes.getInt("xp") - object.getInt("xp"))>0) &&*/  distance(client.getPlayer())<15*15)
        {
        damageSprites.add(GameScreen.get().createString("+"+Integer.toString(changes.getInt("xp") - object.getInt("xp")),Color.cyan));
        damageSpritesTimes.add(new Long(System.currentTimeMillis()));

        client.addEventLine(getName() + " earns " + (changes.getInt("xp") - object.getInt("xp")) + " experience points." , Color.blue);
        }
      }

    if(changes.has("hp") && object.has("hp"))
      {
      if(distance(client.getPlayer())<15*15)
        {
        int healing=changes.getInt("hp") - object.getInt("hp");
        if(healing>0)
          {
          damageSprites.add(GameScreen.get().createString("+"+Integer.toString(healing),Color.green));
          damageSpritesTimes.add(new Long(System.currentTimeMillis()));
 
          client.addEventLine(getName() + " heals " + healing + " health points." , Color.green);
          }
        }
      }

    if(changes.has("poisoned"))
      {
      if(getID().equals(client.getPlayer().getID()) || distance(client.getPlayer())<15*15)
        {
        isPoisoned=true;
        int poisoned=changes.getInt("poisoned");
  
        damageSprites.add(GameScreen.get().createString(Integer.toString(poisoned),Color.red));
        damageSpritesTimes.add(new Long(System.currentTimeMillis()));
  
        client.addEventLine(getName() + " is poisoned with " + poisoned + " health points." , Color.red);
        }
      }
    else if(!object.has("poisoned"))
      {
      isPoisoned=false;
      }

    if(changes.has("level") && object.has("level"))
      {
      if(getID().equals(client.getPlayer().getID()) || distance(client.getPlayer())<15*15)
        {
        String text=getName()+" reachs Level "+ getLevel();
        
        gameObjects.addText(this, GameScreen.get().createString(text,Color.green), 0);
        client.addEventLine(text,Color.green);
        }
      }

    /** Attack code */
    if(changes.has("target") && object.has("target"))
      {
      gameObjects.attackStop(this,targetEntity);
      targetEntity=null;
      }
      
    if(changes.has("target") || object.has("target"))
      {
      attacking=true;

      int risk=(changes.has("risk")?changes.getInt("risk"):0);
      int damage=(changes.has("damage")?changes.getInt("damage"):0);
      int target=(changes.has("target")?changes.getInt("target"):object.getInt("target"));

      targetEntity=new RPObject.ID(target,changes.get("zoneid"));
      gameObjects.attack(this,targetEntity,risk,damage);
      }

    /** Add text lines */
    if(changes.has("text") && client.getPlayer()!= null &&distance(client.getPlayer())<15*15)
      {
      String text=changes.get("text");
      client.addEventLine(getName(),text);

      gameObjects.addText(this, getName()+" says: "+text.replace("|",""), Color.yellow);
      }

    if(changes.has("private_text"))
      {
      client.addEventLine(changes.get("private_text"),Color.orange);
      gameObjects.addText(this, changes.get("private_text").replace("|",""), Color.orange);
      }

    if(changes.has("dead"))// && (stendhal.showEveryoneXPInfo || getID().equals(client.getPlayer().getID())))
      {
      client.addEventLine(getName()+" has died. "+getName()+"'s new level is "+getLevel());
      }

    }

  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyRemoved(object,changes);    
    if(changes.has("target"))
      {
      attacking=false;
      gameObjects.attackStop(this,targetEntity);
      targetEntity=null;
      }
    
    if(changes.has("eating")) isEating=false;    
    if(changes.has("poisoned")) isPoisoned=false;    
    }

  public void removed() throws AttributeNotFoundException
    {
    super.removed();
    
    if(attacking)
      {
      attacking=false;
      gameObjects.attackStop(this,targetEntity);
      targetEntity=null;
      }
    }


  public void onAttack(RPEntity source, int risk, int damage)
    {
    attacked=true;

    // This shows damage done by the player and to the player.
    boolean showAttackInfoForPlayer=client.getPlayer()!=null && (getID().equals(client.getPlayer().getID()) || source.getID().equals(client.getPlayer().getID()));
    showAttackInfoForPlayer=showAttackInfoForPlayer&(!stendhal.FILTER_ATTACK_MESSAGES);

    if(risk>0 && damage>0 && (stendhal.SHOW_EVERYONE_ATTACK_INFO || showAttackInfoForPlayer))
      {
      client.addEventLine(name+" loses with "+damage+" hitpoints due to an attack by "+source.getName(),Color.RED);
      }

    combatIconTime=System.currentTimeMillis();

    if(risk<=0)
      {
      resolution=Resolution.MISSED;
      }
    else if(damage<=0)
      {
      resolution=Resolution.BLOCKED;
      }
    else
      {
      playSound( "punch-mix", 20, 60, 80 ); 
      resolution=Resolution.HITTED;

      damageSprites.add(GameScreen.get().createString(Integer.toString(damage),Color.red));
      damageSpritesTimes.add(new Long(System.currentTimeMillis()));
      }
    }

  public void onAttackStop(RPEntity source)
    {
    attacked=false;
    }


  /** Draws this entity in the screen */
  public void draw(GameScreen screen)
    {
    if(attacked)
      {
      // Draw red box around
      Graphics g2d=screen.expose();
      Rectangle2D rect=getArea();

      g2d.setColor(Color.red);
      Point2D p=new Point.Double(rect.getX(),rect.getY());
      p=screen.invtranslate(p);
      g2d.drawRect((int)p.getX(),(int)p.getY(),(int)(rect.getWidth()*(float)GameScreen.SIZE_UNIT_PIXELS),(int)(rect.getHeight()*(float)GameScreen.SIZE_UNIT_PIXELS));
      g2d.setColor(Color.black);
      g2d.drawRect((int)p.getX()-1,(int)p.getY()-1,(int)(rect.getWidth()*(float)GameScreen.SIZE_UNIT_PIXELS)+2,(int)(rect.getHeight()*(float)GameScreen.SIZE_UNIT_PIXELS)+2);
      }

    super.draw(screen);

    if(nameImage!=null) screen.draw(nameImage,x,y-0.5);
      {
      Graphics g2d=screen.expose();

      Point2D p=new Point.Double(x,y);
      p=screen.invtranslate(p);
      
      if(hp_base_hp>1)
        {
        hp_base_hp=1;
        }
        
      if(hp_base_hp<0)
        {
        hp_base_hp=0;
        }

      float r=1-hp_base_hp;r*=2.0;
      float g=hp_base_hp;g*=2.0;

      g2d.setColor(Color.gray);
      g2d.fillRect((int)p.getX(),(int)p.getY()-3,32,3);
      g2d.setColor(new Color(r>1?1:r,g>1?1:g,0));
      g2d.fillRect((int)p.getX(),(int)p.getY()-3,(int)(hp_base_hp*32.0),3);
      g2d.setColor(Color.black);
      g2d.drawRect((int)p.getX(),(int)p.getY()-3,32,3);
      }
    
    if(isEating)
      {
      Rectangle2D rect=getArea();
      double sx=rect.getMaxX();
      double sy=rect.getMaxY();
      screen.draw(eating,sx-0.75,sy-0.25);
      }

    if(isPoisoned)
      {
      Rectangle2D rect=getArea();
      double sx=rect.getMaxX();
      double sy=rect.getMaxY();
      screen.draw(poisoned,sx-1.25,sy-0.25);
      }

    if(attacked && System.currentTimeMillis()-combatIconTime<4*300)
      {
      // Draw bottom right combat icon
      Rectangle2D rect=getArea();
      double sx=rect.getMaxX();
      double sy=rect.getMaxY();

      switch(resolution)
        {
        case BLOCKED:
          screen.draw(blocked,sx-0.25,sy-0.25);
          break;
        case MISSED:
          screen.draw(missed,sx-0.25,sy-0.25);
          break;
        case HITTED:
          screen.draw(hitted,sx-0.25,sy-0.25);
          break;
        }
      }

    if(damageSprites!=null && damageSprites.size()>0)  // Draw the damage done
      {
      long current=System.currentTimeMillis();

      int i=0;
      for(Sprite damageImage: damageSprites)
        {
        double tx=x+0.6-(damageImage.getWidth()/((float)GameScreen.SIZE_UNIT_PIXELS*2.0f));
        double ty=y-((current-damageSpritesTimes.get(i))/(6.0*300.0));
        screen.draw(damageImage,tx,ty);
        i++;
        }

      if(damageSpritesTimes.size()>0 && (current-damageSpritesTimes.get(0)>6*300))
        {
        damageSprites.remove(0);
        damageSpritesTimes.remove(0);
        }
      }
    }

  public String defaultAction()
    {
    return "Look";
    }

  public String[] offeredActions()
    {
    String[] list=null;
    if(client.getPlayer().has("target"))
      {
      list=new String[]{"Look","Attack","Stop attack"};
      }
    else
      {
      list=new String[]{"Look","Attack"};
      }
    return list;
    }

  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("Look"))
      {
      String text="You see " + getName() + "(Level " + level + ").";
      StendhalClient.get().addEventLine(text, Color.green);
      gameObjects.addText(this, text, Color.green);
      }
    else if(action.equals("Attack"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","attack");
      int id=getID().getObjectID();
      rpaction.put("target",id);
      client.send(rpaction);
      }
    else if(action.equals("Stop attack"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","stop");
      rpaction.put("attack","");
      client.send(rpaction);
      }
    else if(action.equals("Displace"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","displace");
      int id=getID().getObjectID();
      rpaction.put("baseitem",id);
      client.send(rpaction);
      }
    }

  public int compare(Entity entity)
    {
    if(entity instanceof PassiveEntity)
      {
      return 1;
      }
    
    double dx=getArea().getX()-entity.getArea().getX();
    double dy=getArea().getY()-entity.getArea().getY();

    if(dy<0) 
      {
      return -1;
      }
    else if(dy>0) 
      {
      return 1;
      }
    else if(dx!=0)
      {
      return (int)Math.signum(dx);
      }
    else
      {
      // Same tile...
      return 0;
      }
    }

  /**
   * @return Returns the atk.
   */
  public int getAtk()
  {
    return atk;
  }

  /**
   * @return Returns the def.
   */
  public int getDef()
  {
    return def;
  }

  /**
   * @return Returns the xp.
   */
  public int getXp()
  {
    return xp;
  }

  /**
   * @return Returns the base_hp.
   */
  public int getBase_hp()
  {
    return base_hp;
  }

  /**
   * @return the attack xp
   */
  public int getAtkXp()
  {
    return atkXp;
  }

  /**
   * @return the defence xp
   */
  public int getDefXp()
  {
    return defXp;
  }
  }
