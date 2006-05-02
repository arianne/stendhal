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
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import games.stendhal.client.events.*;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;


/** This class is a link between client graphical objects and server attributes objects.<br>
 *  You need to extend this object in order to add new elements to the game. */
public abstract class RPEntity extends AnimatedEntity implements TalkEvent, HPEvent, KillEvent, AttackEvent
  {
  private static Sprite hitted;
  private static Sprite blocked;
  private static Sprite missed;

  private static Map<String,Sprite[]> blade_strikes;
  private int frameBladeStrike;
  private boolean showBladeStrike;

  static
    {
    SpriteStore st=SpriteStore.get();

    hitted=st.getSprite("data/sprites/combat/hitted.png");
    blocked=st.getSprite("data/sprites/combat/blocked.png");
    missed=st.getSprite("data/sprites/combat/missed.png");
    
    blade_strikes=new HashMap<String,Sprite[]>();
    blade_strikes.put("move_up",st.getAnimatedSprite("data/sprites/combat/blade_strike.png",0,3,3,4));
    blade_strikes.put("move_right",st.getAnimatedSprite("data/sprites/combat/blade_strike.png",1,3,3,4));
    blade_strikes.put("move_down",st.getAnimatedSprite("data/sprites/combat/blade_strike.png",2,3,3,4));
    blade_strikes.put("move_left",st.getAnimatedSprite("data/sprites/combat/blade_strike.png",3,3,3,4));
    }

  private static Sprite pk;
  private static Sprite eating;
  private static Sprite poisoned;

  static
    {
    SpriteStore st=SpriteStore.get();
    
    pk=st.getSprite("data/sprites/ideas/pk.png");
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

  // Called when entity says text
  public void onTalk(String text)
    {
    if(client.getPlayer()!= null && distance(client.getPlayer())<15*15)
      {
      if(!(this instanceof Creature)) // We avoid logging creature noises.
        {
        client.addEventLine(getName(),text);
        }
      
      String line=text.replace("|","");
      
    // Allow for more characters and cut the text if possible at the nearest space etc. intensifly@gmx.com
      if(line.length() > 84)
        {
        line = line.substring(0,84);
        int l = line.lastIndexOf(" ");
        int ln = line.lastIndexOf("-");
        if(ln > l)
          l = ln;
        ln = line.lastIndexOf(".");
        if(ln > l)
          l = ln;
        ln = line.lastIndexOf(",");
        if(ln > l)
          l = ln;
        if(l > 0)
          line = line.substring(0,l);
        line = line + " ...";
        }
      gameObjects.addText(this, /*getName()+" says: "+*/line,Color.black,true);
      }
    }
    
  // Called when entity listen to text from talker
  public void onPrivateListen(String text)
    {
// Change text color for private messages. intensifly@gmx.com
    client.addEventLine(text,Color.darkGray);

    gameObjects.addText(this, text.replace("|",""), Color.darkGray,false);      
    }

  // When entity gets healed
  public void onHealed(int amount)
    {
    if(distance(client.getPlayer())<15*15)
      {
      damageSprites.add(GameScreen.get().createString("+"+Integer.toString(amount),Color.green));
      damageSpritesTimes.add(new Long(System.currentTimeMillis()));

      client.addEventLine(getName() + " heals " + amount + " health points." , Color.green);
      }
    }
    
  // When entity eats food 
  public void onEat(int amount)
    {
    isEating=true;
    }
    
  public void onEatEnd()
    {
    isEating=false;
    }
    
  // When entity is poisoned
  public void onPoisoned(int amount)
    {
    if(getID().equals(client.getPlayer().getID()) || distance(client.getPlayer())<15*15)
      {
      isPoisoned=true;
      damageSprites.add(GameScreen.get().createString(Integer.toString(amount),Color.red));
      damageSpritesTimes.add(new Long(System.currentTimeMillis()));
  
      client.addEventLine(getName() + " is poisoned with " + amount + " health points." , Color.red);
      }
    }
    
  public void onPoisonEnd()
    {
    isPoisoned=false;
    }

  // Called when entity kills another entity
  public void onKill(RPEntity killed)
    {
    }
 
  // Called when entity is killed by killer
  public void onDeath(RPEntity killer)
    {
    if(killer!=null)
      {
      client.addEventLine(getName()+ " has been killed by "+killer.getName());
      }
    
    if(getID().equals(client.getPlayer().getID()))
      {
      client.addEventLine(getName()+" has died. "+getName()+"'s new level is "+getLevel());
      }
    }    

  public void onChangedAdded(RPObject base, RPObject diff) throws AttributeNotFoundException
    {
    super.onChangedAdded(base,diff);

    if(diff.has("base_hp"))    base_hp=diff.getInt("base_hp");
    if(diff.has("hp"))         hp=diff.getInt("hp");
    if(diff.has("hp/base_hp")) hp_base_hp=(float)diff.getDouble("hp/base_hp");
    if(diff.has("atk"))        atk=diff.getInt("atk");
    if(diff.has("def"))        def=diff.getInt("def");
    if(diff.has("xp"))         xp=diff.getInt("xp");
    if(diff.has("level"))      level=diff.getInt("level");
    if(diff.has("atk_xp"))     atkXp = diff.getInt("atk_xp");
    if(diff.has("def_xp"))     defXp = diff.getInt("def_xp");
    
    Color nameColor=Color.white;
    
    if(diff.has("admin") || base.has("admin"))
      {
      nameColor=Color.yellow;
      }
    
    if(diff.has("name"))
      {
      name=diff.get("name");
      name=name.replace("_"," ");
      nameImage=GameScreen.get().createString(getName(),nameColor);
      }
    else if(name==null && diff.has("class"))
      {
      name=diff.get("class");
      name=name.replace("_"," ");
      nameImage=GameScreen.get().createString(getName(),nameColor);
      }
    else if(name==null && diff.has("type"))
      {
      name=diff.get("type");
      name=name.replace("_"," ");
      nameImage=GameScreen.get().createString(getName(),nameColor);
      }

    if(diff.has("xp") && base.has("xp"))
      {
      if(distance(client.getPlayer())<15*15)
        {
        damageSprites.add(GameScreen.get().createString("+"+Integer.toString(diff.getInt("xp") - base.getInt("xp")),Color.cyan));
        damageSpritesTimes.add(new Long(System.currentTimeMillis()));

        client.addEventLine(getName() + " earns " + (diff.getInt("xp") - base.getInt("xp")) + " experience points." , Color.blue);
        }
      }

    if(diff.has("level") && base.has("level"))
      {
      if(getID().equals(client.getPlayer().getID()) || distance(client.getPlayer())<15*15)
        {
        String text=getName()+" reachs Level "+ getLevel();
        
        gameObjects.addText(this, GameScreen.get().createString(text,Color.green),0);
        client.addEventLine(text,Color.green);
        }
      }
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

    if(isAttacking() && showBladeStrike)
      {
      Rectangle2D rect=getArea();
      double sx=rect.getMaxX();
      double sy=rect.getMaxY();
      
      if(frameBladeStrike<3)
        {
        screen.draw(blade_strikes.get(getAnimation())[frameBladeStrike],sx-1.5,sy-3.3);      
        }
      else
        {
        showBladeStrike=false;
        frameBladeStrike=0;
        }
      
      frameBladeStrike++;
      }  


    super.draw(screen);

    if(nameImage!=null) 
      {
      screen.draw(nameImage,x,y-0.5);      

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
    List<String> list=new LinkedList<String>();
    
    list.add("Look");
    list.add("Attack");
    
    if(client.getPlayer().has("target"))
      {
      list.add("Stop attack");
      }
      
    if(client.isAdmin())
      {
      list.add("(*)Inspect");
      list.add("(*)Destroy");
      }
    
    return list.toArray(new String[0]);
    }

  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("Attack"))
      {
// NOTE: Dunno about this feature...      
//      if(distance(client.getPlayer())>2)
//        {
//        RPAction rpaction = new RPAction();
//        rpaction.put("type","moveto");
//        rpaction.put("x",(int)getx());
//        rpaction.put("y",(int)gety());
//        StendhalClient.get().send(rpaction);
//        }
      
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
    else if(action.equals("(*)Inspect"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","inspect");
      int id=getID().getObjectID();
      rpaction.put("targetid",id);
      client.send(rpaction);
      }
    else if(action.equals("(*)Destroy"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","destroy");
      int id=getID().getObjectID();
      rpaction.put("targetid",id);
      client.send(rpaction);
      }
    else
      super.onAction(client, action, params);
    }

  public int compare(Entity entity)
    {
    if(entity instanceof PassiveEntity || entity instanceof Blood)
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


  // When this entity attacks target.
  public void onAttack(RPEntity target)
    {
    attacking=true;
    }

  // When attacker attacks this entity.
  public void onAttacked(RPEntity attacker)
    {
    attacked=true;
    }

  // When this entity stops attacking 
  public void onStopAttack()
    {
    attacking=false;
    }

  // When attacket stop attacking us
  public void onStopAttacked(RPEntity attacker)
    {
    attacked=false;
    }

  
  // When this entity causes damaged to adversary, with damage amount 
  public void onAttackDamage(RPEntity target, int damage)
    {
    showBladeStrike=true;
    }

  // When this entity's attack is blocked by the adversary 
  public void onAttackBlocked(RPEntity target)
    {
    showBladeStrike=true;
    }

  // When this entity's attack is missing the adversary 
  public void onAttackMissed(RPEntity target)
    {
    showBladeStrike=true;
    }


  // When this entity is damaged by attacker with damage amount
  public void onDamaged(RPEntity attacker, int damage)
    {
    combatIconTime=System.currentTimeMillis();
    resolution=Resolution.HITTED;

    playSound( "punch-mix", 20, 60, 80 ); 

    damageSprites.add(GameScreen.get().createString(Integer.toString(damage),Color.red));
    damageSpritesTimes.add(new Long(System.currentTimeMillis()));

    boolean showAttackInfoForPlayer=client.getPlayer()!=null && (getID().equals(client.getPlayer().getID()) || attacker.getID().equals(client.getPlayer().getID()));
    showAttackInfoForPlayer=showAttackInfoForPlayer&(!stendhal.FILTER_ATTACK_MESSAGES);

    if(stendhal.SHOW_EVERYONE_ATTACK_INFO || showAttackInfoForPlayer)
      {
      client.addEventLine(getName()+" loses with "+damage+" hitpoints due to an attack by "+attacker.getName(),Color.RED);
      }
    }

  // When this entity blocks the attack by attacker
  public void onBlocked(RPEntity attacker)
    {
    combatIconTime=System.currentTimeMillis();
    resolution=Resolution.BLOCKED;
    }

  // When this entity skip attacker's attack.
  public void onMissed(RPEntity attacker)
    {
    combatIconTime=System.currentTimeMillis();
    resolution=Resolution.MISSED;
    }
  }
