package games.stendhal.tools;

import games.stendhal.server.rule.defaultruleset.*;
import games.stendhal.server.entity.creature.Creature;
import java.util.*;

public class GenerateBestiaryAndItems 
  {
  public static void main(String[] args) throws Exception
    {
    CreatureXMLLoader creatureLoader=CreatureXMLLoader.get();
    List<DefaultCreature> creatures=creatureLoader.load("data/conf/creatures.xml");

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
      
    int level=-1;
    
//    for(DefaultCreature creature: creatures)
//      {
//      System.out.print("\""+creature.getCreatureName()+"\",");
//      }
//    System.out.println ();
//    
//    System.exit(0);
    
    for(DefaultCreature creature: creatures)
      {
      if(creature.getLevel()!=level)
        {
        level=creature.getLevel();
        System.out.println ("= Level "+level+"=");
        }
       
      String name=creature.getCreatureName();
      System.out.println ("== "+name.replace("_"," ")+" ==");
      System.out.println ("{{Creature|");
      System.out.println ("|name= "+name.replace("_"," ")+"");
      System.out.println ("|image= "+name+"");
      System.out.println ("|hp= "+creature.getHP()+"");
      System.out.println ("|atk= "+creature.getATK()+"");
      System.out.println ("|def= "+creature.getDEF()+"");
      System.out.println ("|exp= "+creature.getXP()/20+"");
      System.out.println ("|behavior = '''(TODO)'''.");
      System.out.println ("|location = '''(TODO)'''.");
      System.out.println ("|strategy = '''(TODO)'''.");
      System.out.println ("|loot = ");
      
      for(Creature.DropItem item: creature.getDropItems())
        {
        System.out.println (item.min+"-"+item.max+" "+item.name+"<br>");
        }
        
      System.out.println ("}}");
      System.out.println ("");
      }
      

/**
= Monsters =
This monster list is sorted from weakest creature to most mighty one.
=Level 0=
== Rat ==
{{Creature|
  |name = Rat 
  |image= rat
  |hp   = 20
  |atk  = 6
  |def  = 2
  |exp  = 5
  |immunities = None.
  |behavior = Rats patrol dungeons and usually are found in packs of three or four creatures.
  |location = All around. They are a plague. You can find lots of them at forest.
  |strategy = Just hit first. Rats are not strong opponents.
  |loot = 0-7 GP
}}
 */    
    }
  }
