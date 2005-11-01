package games.stendhal.server.maps;

import games.stendhal.server.Path;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.*;

public class village 
  {
  public village(StendhalRPZone zone)
    {
    Sign sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(23);
    sign.sety(61);
    sign.setText("You are about to leave this area and move to the plains.|You may fatten up your sheep there on the wild berries.|Be careful though, wolves roam these plains.");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(26);
    sign.sety(41);
    sign.setText("Talk to Nishiya to buy a sheep!.|He has the best prices for miles.");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(60);
    sign.sety(47);
    sign.setText("You are about to leave this area to move to the city.|You can sell your sheep there.");    
    zone.add(sign);
    
    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(16);
    sign.sety(35);
    sign.setText("[CLOSED]|The tavern has moved to a much|better and central house in town.|Come buy your weapons, find your|quests and hang out there instead.");
    zone.add(sign);
    
    NPC npc=new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(33,44));
        nodes.add(new Path.Node(33,42));
        nodes.add(new Path.Node(23,42));
        nodes.add(new Path.Node(23,44));
        setPath(nodes,true);
        }
      
      protected void createDialog()
        {
        Map<String,Integer> items=new HashMap<String,Integer>();
        items.put("shield",10);
        items.put("armor",20);
        items.put("sword",100);
        
        Behaviours.addGreeting(this);
        Behaviours.addGoodbye(this);
        Behaviours.addSeller(this,items);
        }
      };
      
    zone.assignRPObjectID(npc);
    npc.put("class","sellernpc");
    npc.setName("Nishiya");
    npc.setx(33);
    npc.sety(44);
    npc.setBaseHP(100);
    npc.setHP(npc.getBaseHP());
    zone.add(npc);    
    zone.addNPC(npc);

//    SellerNPC npc=new SellerNPC()
//      {
//      protected void createPath()
//        {
//        List<Path.Node> nodes=new LinkedList<Path.Node>();
//        nodes.add(new Path.Node(33,44));
//        nodes.add(new Path.Node(33,42));
//        nodes.add(new Path.Node(23,42));
//        nodes.add(new Path.Node(23,44));
//        setPath(nodes,true);
//        }
//      };
//      
//    zone.assignRPObjectID(npc);
//    npc.setName("Nishiya");
//    npc.setx(33);
//    npc.sety(44);
//    npc.setBaseHP(100);
//    npc.setHP(npc.getBaseHP());
//    zone.add(npc);    
//    zone.addNPC(npc);
    }
  }
