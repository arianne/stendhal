package games.stendhal.tools;

import java.util.Random;
import games.stendhal.common.Rand;
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
  
  private static double ROUNDS=20.0;
    
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
    
    StringBuffer st=new StringBuffer();
    
    
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
      
      Creature target=creature.getCreature();
      
      int minlevel=creature.getLevel()-2;
      if(minlevel<0)
        {
        minlevel=0;
        }

      int maxlevel=creature.getLevel()+2;
        
      for(int level=minlevel;level<maxlevel;level++)
        {
        boolean balanced=false;
        
        while(balanced==false)
          {
          player.setBaseHP(100+10*level);
          player.setATK(atkLevels[level]);
          player.setDEF(defLevels[level]);      
  
          equip(player,level);    
          
          int playerOKs=0;
          int meanTurns=0;
          int leftHP=0;
          
          for(int i=0;i<20;i++)
            {
            target.setHP(target.getBaseHP());
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
            
            leftHP+=player.getHP();
            meanTurns+=turns;
            }        
  
          if(level==creature.getLevel())
            {
            int proposedXPValue=20*(int)((creature.getLevel()+1)*((meanTurns/ROUNDS)/2.0));
            System.out.println ("Proposed XP: "+proposedXPValue+"\t Actual XP: "+creature.getXP());
            creature.setLevel(creature.getLevel(),proposedXPValue);
            }          
            
          meanTurns=(int)(meanTurns/ROUNDS);
          int meanLeftHP=(int)(leftHP/ROUNDS);
          
          System.out.print("Player("+level+") VS "+creature.getCreatureName()+"\t Turns: "+meanTurns+"\tLeft HP:"+Math.round(ROUNDS*meanLeftHP/(1.0* player.getBaseHP())));
          System.out.print("\t");
          
          if(isCorrectResult(level-creature.getLevel(),meanTurns, meanLeftHP/(1.0* player.getBaseHP())))
            {
            balanced=true;
            }
          else
            {
            level=minlevel;
            balance(target, level-creature.getLevel(), meanTurns, meanLeftHP);
            System.out.println ("New ATK: "+target.getATK()+"/DEF: "+target.getDEF()+"/HP: "+target.getBaseHP());
            }
          }
        }

      System.out.println ("BALANCED: "+creature.getCreatureName()+"("+creature.getLevel()+")\tATK: "+target.getATK()+"/DEF: "+target.getDEF()+"/HP: "+target.getBaseHP());
      st.append("BALANCED: "+creature.getCreatureName()+"("+creature.getLevel()+")\tATK: "+target.getATK()+"/DEF: "+target.getDEF()+"/HP: "+target.getBaseHP()+"/XP: "+creature.getXP()+"\n");
      }
    
    System.out.println (st);
    }
  
  static private boolean isCorrectResult(int levelDiff,int meanTurns, double meanLeftHP)
    {
    if(levelDiff>0 && meanTurns>100)
      {
      System.out.println ("FAILED beacause takes too much time to kill");
      return false;
      }

    if(levelDiff==0 && meanTurns>50)
      {
      System.out.println ("FAILED beacause takes too much time to kill");
      return false;
      }
      
    if(levelDiff==0 && meanLeftHP>0.75)
      {
      System.out.println ("CORRECT");
      return true;
      }
    
    if(levelDiff<0 && meanLeftHP>0.75)
      {
      System.out.println ("FAILED beacause takes makes LITTLE damage to player at same level");
      return false;
      }    
    
    if(levelDiff>0 && meanLeftHP<0.75)
      {
      System.out.println ("FAILED beacause takes makes MUCH damage to player at same level");
      return false;
      }    
    
    System.out.println ("CORRECT: No reason");
    return true;
    }
  
  static private void balance(Creature target, int levelDiff, int meanTurns, double meanLeftHP)
    {
    if(meanLeftHP<0.8)
      {
      switch(Rand.roll1D6())
        {
        case 0:
          target.setATK(target.getATK()-1);
          break;
        case 1:
        case 2:
        case 3:
          target.setDEF(target.getDEF()-1);
          break;
        case 4:
        case 5:
          target.setBaseHP(target.getBaseHP()-3);
          break;
        }
      }
    else
      {
      switch(Rand.roll1D6())
        {
        case 0:
        case 1:
        case 2:
          target.setATK(target.getATK()+1);
          break;
        case 3:
        case 4:
          target.setDEF(target.getDEF()+1);
          break;
        case 5:
          target.setBaseHP(target.getBaseHP()+3);
          break;
        }
      }

    if(meanTurns>100)
      {
      target.setATK(target.getATK()+5);
      target.setDEF(target.getDEF()-5);
      return;
      }

    if(levelDiff==0 && meanTurns>40)
      {
      target.setATK(target.getATK()+1);
      target.setDEF(target.getDEF()-1);
      return;
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
