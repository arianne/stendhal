package games.stendhal.tools;

import java.util.Random;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.creature.*;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.rule.*;
import games.stendhal.server.rule.defaultruleset.*;
import marauroa.common.game.*;
import java.util.*;


public class BalanceRPGame 
  {
  static class BalanceWorld extends StendhalRPWorld
    {    
    public BalanceWorld() throws Exception 
      {
      super();
      }
    }
    
  public static void main(String[] args) throws Exception
    {
    BalanceWorld world=new BalanceWorld();
    StendhalRPZone area=new StendhalRPZone("test", world);
    world.addRPZone(area);

    Entity.setRPContext(null, world);

    List<DefaultCreature> creatures=CreatureXMLLoader.get().load("data/conf/creatures.xml");
    
    Collections.sort(creatures, new Comparator<DefaultCreature>()
      {
      public int compare(DefaultCreature o1, DefaultCreature o2) 
        {
        return o1.getLevel()-o2.getLevel();
        }
        
      public boolean equals(Object obj) 
        {
        return true;
        }
      });
    
    int levels=10;
    //                0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30
    int[] atkLevels={10,12,12,13,13,13,14,14,14,14,15,15,15,15,15,16,16,16,16,16,16,16,16,17,17,17,17,17,18,18,18};
    int[] defLevels={10,12,13,14,15,15,16,16,18,18,18,20,20,21,21,21,22,22,22,23,23,23,23,23,23,24,24,24,24,25,25};
    
    EntityManager em=DefaultEntityManager.getInstance();
    Item weapon=em.getItem("club");
    area.assignRPObjectID(weapon);
    
    Item shield=em.getItem("wooden_shield");
    area.assignRPObjectID(shield);

    Item armor=em.getItem("dress");
    area.assignRPObjectID(armor);

    Item helmet=em.getItem("leather_helmet");
    area.assignRPObjectID(helmet);

    Item legs=em.getItem("leather_legs");
    area.assignRPObjectID(legs);

    Item boots=em.getItem("leather_boots");
    area.assignRPObjectID(boots);

    
    Player player=new Player(new RPObject());
    player.addSlot(new RPSlot("lhand"));
    player.addSlot(new RPSlot("rhand"));
    player.addSlot(new RPSlot("armor"));
    player.addSlot(new RPSlot("head"));
    player.addSlot(new RPSlot("legs"));
    player.addSlot(new RPSlot("feet"));
    
    player.equip(weapon);
    player.equip(shield);
    player.equip(armor);
    player.equip(helmet);
    player.equip(legs);
    player.equip(boots);
    
//    System.out.println (player);
    
//      for(int level=0;level<levels;level++)
//        {
//        player.setBaseHP(100+10*level);
//        player.setATK(atkLevels[level]);
//        player.setDEF(defLevels[level]);      
//
//        equip(player,level);    
//        }
//        System.exit(0);
        
    
    for(DefaultCreature creature: creatures)
      {
      if(args.length>0)
        {
        if(!args[0].equals(creature.getCreatureName()))
          {
          continue;
          }
        }
        
      System.out.println ("-- "+creature.getCreatureName()+"("+creature.getLevel()+")");
      
      for(int level=0;level<levels;level++)
        {
        player.setBaseHP(100+10*level);
        player.setATK(atkLevels[level]);
        player.setDEF(defLevels[level]);      

        equip(player,level);    
        
        int playerOKs=0;
        int meanTurns=0;
        
        for(int i=0;i<100;i++)
          {
          Creature target=creature.getCreature();
          player.setHP(player.getBaseHP());
          
          boolean combatFinishedWinPlayer=false;
          int turns=0;
          
          while(!combatFinishedWinPlayer)
            {
            turns++;            

            if(StendhalRPAction.riskToHit(player,target))
              {
              int damage=StendhalRPAction.damageDone(player,target);
              if(damage<0) damage=0;
              target.setHP(target.getHP()-damage);
              }      
            
            if(target.getHP()<=0)
              {
              combatFinishedWinPlayer=true;
              break;
              }
  
            if(StendhalRPAction.riskToHit(target,player))
              {
              int damage=StendhalRPAction.damageDone(target,player);
              if(damage<0) damage=0;
              player.setHP(player.getHP()-damage);
              }     
              
            if(player.getHP()<=0)
              {
              break;
              }
            }
          
          if(combatFinishedWinPlayer)
            {
            int cl=creature.getLevel();
            
            if(cl>=00 && cl<05 && around(player,0.8,1)) playerOKs++;
            if(cl>=05 && cl<10 && around(player,0.6,1)) playerOKs++;
            if(cl>=10 && cl<15 && around(player,0.4,1)) playerOKs++;
            if(cl>=15 && cl<20 && around(player,0.2,1)) playerOKs++;
            if(cl>=20 && cl<30 && around(player,0.0,1)) playerOKs++;
            }
          
          meanTurns+=turns;
          }        

        System.out.print("Player("+level+") VS "+creature.getCreatureName()+":\t "+playerOKs+"\t Turns: "+meanTurns/100.0);
        if((playerOKs>80 && level>=creature.getLevel())||
            playerOKs<80 && level<creature.getLevel())
          {
          System.out.println ("\tOK");
          }
        else
          {
          System.out.println ("\tFAIL");
          }
        }
      }
    }
  
  static void equip(Player p, int level)
    {
    p.getWeapon().put("atk",7+level*2/3);
    if(level==0)
      {
      p.getShield().put("def",0);
      }
    else
      {
      p.getShield().put("def",14+level/4);
      }
    p.getArmor().put("def",1+level/4);    
    p.getHelmet().put("def",level/3);
    p.getLegs().put("def",level/3);
    p.getBoots().put("def",level/3);
    
//    System.out.println ("W: "+p.getWeapon().getAttack()+" \t"+
//                        "S: "+p.getShield().getDefense()+" \t"+
//                        "A: "+p.getArmor().getDefense()+" \t"+
//                        "H: "+p.getHelmet().getDefense()+" \t"+
//                        "L: "+p.getLegs().getDefense()+" \t"+
//                        "B: "+p.getBoots().getDefense()+" \t");
    }
  
  static boolean around(Player p, double val,double top)
    {
    if(p.getHP()>=val*p.getBaseHP() && p.getHP()<=p.getBaseHP()*(top))
      {
//      System.out.println (p.getBaseHP()*val+"<="+p.getHP()+"<="+p.getBaseHP()*(top));
      return true;
      }
    else
      {
//      System.out.println ("NOT "+p.getBaseHP()*val+"<="+p.getHP()+"<="+p.getBaseHP()*(top));
      return false;
      } 
    }
  }
