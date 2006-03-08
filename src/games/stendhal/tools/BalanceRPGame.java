package games.stendhal.tools;

import java.util.Random;
import games.stendhal.common.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.creature.*;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.rule.*;
import games.stendhal.server.rule.defaultruleset.*;
import marauroa.common.game.*;
import java.util.*;


/*** NOTE: AWFUL CODE FOLLOWS. YOU ARE NOT SUPPOSED TO READ THIS ;P ***/

public class BalanceRPGame 
  {
  static class BalanceWorld extends StendhalRPWorld
    {    
    public BalanceWorld() throws Exception 
      {
      super();
      }
    }
  
  private static int ROUNDS=10;
  
  public static Pair<Integer, Integer> combat(Player player, Creature target, int rounds)
    {
    int meanTurns=0;
    int meanLeftHP=0;
    
    for(int i=0;i<rounds;i++)
      {
      Pair<Integer,Integer> results=combat(player,target);
      meanTurns+=results.first();
      meanLeftHP+=results.second();
      }        
    
    meanTurns=(int)(meanTurns/(rounds*1.0));
    meanLeftHP=(int)(meanLeftHP/(rounds*1.0));

    return new Pair<Integer, Integer>(meanTurns, meanLeftHP);
    }
    
  public static Pair<Integer, Integer> combat(Player player, Creature target)
    {
    target.setHP(target.getBaseHP());
    player.setHP(player.getBaseHP());
    
    int damageDone=0;
    
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
        damageDone=+damage;
        player.setHP(player.getHP()-damage);
        }     
        
      if(player.getHP()<=0)
        {
        combatFinishedWinPlayer=true;
        break;
        }
      }
    
    return new Pair<Integer, Integer>(turns, player.getBaseHP()-damageDone);
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
    
    int[] atkLevels=new int[110];                     
    int[] defLevels=new int[110];   
    
    for(int i=0;i<atkLevels.length;i++)
      {
      atkLevels[i]=10+(int)Math.round(Math.log(i+1)/Math.log(10)*7);
      defLevels[i]=10+(int)Math.round(Math.log(i+1)/Math.log(10)*14);
      }                                       
                        
    
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

//    for(int level=0;level<100;level++)
//      {
//      player.setBaseHP(100+10*level);
//      player.setATK(atkLevels[level]);
//      player.setDEF(defLevels[level]);      
//      equip(player,level);    
//      System.out.println ("("+level+")\tATK: "+player.getATK()+"\tDEF: "+player.getDEF()+"\tHP: "+player.getBaseHP()+
//         "\tWeapon: "+player.getWeapon().getAttack()+"\tShield: "+player.getShield().getDefense()+"\tArmor: "+player.getArmor().getDefense()+
//         "\tHelmet: "+player.getHelmet().getDefense()+"\tLegs: "+player.getLegs().getDefense()+"\tBoots: "+player.getBoots().getDefense());
//      }
//    
//    System.exit(0);
  
    StringBuffer st=new StringBuffer("Creatures done: \n");
    
    boolean found=false;
    
    for(DefaultCreature creature: creatures)
      {      
      if(args.length>0)
        {  
        if(!args[0].equals(creature.getCreatureName()) && !found)
          {
          continue;
          }
//        else
//          {
//          found=true;
//          }
        }
        
      //OUTPUT: System.out.println ("-- "+creature.getCreatureName()+"("+creature.getLevel()+")");
      
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
          player.setLevel(level);
          player.setBaseHP(100+10*level);
          player.setATK(atkLevels[level]);
          player.setDEF(defLevels[level]);      
  
          equip(player,level);    
          
          Pair<Integer,Integer> results=combat(player,target,ROUNDS);
          int meanTurns=results.first();
          int meanLeftHP=results.second();
  
          if(level==creature.getLevel())
            {
            int proposedXPValue=1*(int)(0.37*(creature.getLevel()+1)*(creature.getLevel()+1)*(meanTurns));
            //OUTPUT: System.out.println ("Proposed XP: "+proposedXPValue+"\t Actual XP: "+creature.getXP());
            creature.setLevel(creature.getLevel(),proposedXPValue);
            }          
            
          //System.out.println("Player("+level+") VS "+creature.getCreatureName()+"\t Turns: "+meanTurns+"\tLeft HP:"+Math.round(100*meanLeftHP/(1.0* player.getBaseHP())));

          if(isCorrectResult(level, level-creature.getLevel(),meanTurns, meanLeftHP/(1.0* player.getBaseHP())))
            {
            balanced=true;
            }
          else
            {
            double best=Double.MAX_VALUE;
            Creature bestCreature=null;
            
            for(Creature child: children(target))
              {              
              results=combat(player,child,ROUNDS);
              
              int turns=results.first();
              int leftHP=results.second();
              
              double childScore=score(turns, leftHP/(1.0* player.getBaseHP()),level,child);
              //System.out.println ("Child ATK: "+child.getATK()+"/DEF: "+child.getDEF()+"/HP: "+child.getBaseHP()+"\t scored "+childScore+"\t Turns: "+turns+"\tLeft HP:"+Math.round(100*leftHP/(1.0* player.getBaseHP())));
              
              if(childScore<best)
                {
                best=childScore;
                bestCreature=child;
                }
              }
            
            target=bestCreature;            
            level=minlevel;
            
            
            //System.out.println ("New ATK: "+target.getATK()+"/DEF: "+target.getDEF()+"/HP: "+target.getBaseHP());
            }
          }
        }
      
      boolean changed=false;
      
      if(creature.getATK()!=target.getATK())
        {
        changed=true;
        }

      if(creature.getDEF()!=target.getDEF())
        {
        changed=true;
        }

      if(creature.getHP()!=target.getBaseHP())
        {
        changed=true;
        }

      System.out.println ("BALANCED: "+creature.getCreatureName()+"("+creature.getLevel()+")\t"+(changed?"*\t":" \t")+"ATK: "+target.getATK()+"\t\tDEF: "+target.getDEF()+"\t\tHP: "+target.getBaseHP()+"\t\tXP: "+creature.getXP());
      st.append("BALANCED: "+creature.getCreatureName()+"("+creature.getLevel()+")\tATK: "+target.getATK()+"\tDEF: "+target.getDEF()+"\tHP: "+target.getBaseHP()+"\tXP: "+creature.getXP()+"\n");
      }

    //OUTPUT: System.out.println (st);
    }
  
  static private double score(int turns, double leftHP, int level, Creature creature)
    {
    double score=0;
    
    int creatureLevel=creature.getLevel();
    
    if(level-creatureLevel<0)
      {
      // Weaker than creature.
      score=leftHP*100+(turns/(10+creatureLevel/3.0));
      }
    
    if(level-creatureLevel==0)
      {
      if(leftHP<0.1 && turns>(10+creatureLevel/3.0))
        {
        score=1000-leftHP*100+(turns/10.0);
        }
      if(leftHP<0.7 && turns>(10+creatureLevel/3.0))
        {
        score=500-leftHP*100+(turns/10.0);
        }
      if(leftHP>=0.7)
        {
        score=Math.abs(leftHP*100-80)+Math.abs(turns-(10+creatureLevel/3.0))/5.0;
        }
      }
      
    if(level-creatureLevel>0)
      {
      // Weaker than creature.
      score=(1-leftHP)*100+(turns/5.0);
      }
    
    return score;
    }
  
  static private Creature[] children(Creature creature)
    {
    Creature[] creatures=new Creature[9];
    
    int val=0;
    
    for(int i=0;i<9;i++)
      {
      creatures[i]=new Creature(creature);
      val=creature.getATK()+Rand.roll1D6()-3;
      if(val<1)
        {
        val=1;
        }        
      creatures[i].setATK(val);
      
      val=creature.getDEF()+Rand.roll1D6()-3;
      if(val<1)
        {
        val=1;
        }
      creatures[i].setDEF(val);

      val=creature.getBaseHP()+Rand.roll1D20()-10;
      if(val<1)
        {
        val=1;
        }        
      creatures[i].setBaseHP(val);
      }

    return creatures;
    }
  
  static private boolean isCorrectResult(int level, int levelDiff,int meanTurns, double meanLeftHP)
    {
    if(levelDiff==0 && meanTurns>10+level*1.5)
      {
      //OUTPUT: System.out.println ("FAILED beacause takes too much time to kill");
      return false;
      }

    if(levelDiff==0 && meanTurns<10-level*1.5)
      {
      //OUTPUT: System.out.println ("FAILED beacause takes too much time to kill");
      return false;
      }
      
    if(levelDiff==0 && meanLeftHP>=1-(level/100.0))
      {
      //OUTPUT: System.out.println ("CORRECT");
      return true;
      }
    
    if(levelDiff<0 && meanLeftHP>1-level/100.0)
      {
      //OUTPUT: System.out.println ("FAILED beacause takes makes LITTLE damage to player at same level");
      return false;
      }    
    
    if(levelDiff>0 && meanLeftHP<1-(level/100.0))
      {
      //OUTPUT: System.out.println ("FAILED beacause takes makes MUCH damage to player at same level");
      return false;
      }    
    
    //System.out.println ("FAILED: Any of the above reasons");
    return true;
    }
  
  static void equip(Player p, int level)
    {
    p.getWeapon().put("atk",7+level*2/6);
    if(level==0)
      {
      p.getShield().put("def",0);
      }
    else
      {
      p.getShield().put("def",12+level/8);
      }
    p.getArmor().put("def",1+level/4);    
    p.getHelmet().put("def",1+level/7);
    p.getLegs().put("def",1+level/7);
    p.getBoots().put("def",1+level/10);
    }
  }
