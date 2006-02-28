package games.stendhal.server.maps;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Portal;
import games.stendhal.server.entity.OneWayPortal;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import marauroa.common.game.IRPZone;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;


public class Orril implements IContent
  {
  private StendhalRPWorld world;
  private NPCList npcs;
  
  public Orril(StendhalRPWorld world) throws org.xml.sax.SAXException, java.io.IOException
    {
    this.npcs=NPCList.get();
    this.world=world;
    
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("0_orril_s"));

    SpeakerNPC npc=npcs.add("Jynath",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(24,6));
        nodes.add(new Path.Node(21,6));
        nodes.add(new Path.Node(21,8));
        nodes.add(new Path.Node(15,8));
        nodes.add(new Path.Node(15,11));
        nodes.add(new Path.Node(13,11));
        nodes.add(new Path.Node(13,26));
        nodes.add(new Path.Node(22,26));
        nodes.add(new Path.Node(13,26));
        nodes.add(new Path.Node(13,11));
        nodes.add(new Path.Node(15,11));
        nodes.add(new Path.Node(15,8));
        nodes.add(new Path.Node(21,8));
        nodes.add(new Path.Node(21,6));
        nodes.add(new Path.Node(24,6));
        setPath(nodes,true);
        }
      
      protected void createDialog()
        {        
        Behaviours.addGreeting(this);
        Behaviours.addJob(this,"*Do you really want to know?* I am a witch");
        Behaviours.addHelp(this,"You may want to buy some potions or do some #task for me.");
        Behaviours.addGoodbye(this);
        }
      });
    
    zone.assignRPObjectID(npc);
    npc.setOutfit("0");
    npc.set(9,20);
    npc.initHP(100);
    zone.add(npc);    
    zone.addNPC(npc);
    }
  }
