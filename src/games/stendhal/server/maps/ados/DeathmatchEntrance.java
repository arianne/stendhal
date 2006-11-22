package games.stendhal.server.maps.ados;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.scripting.ScriptingNPC;

import java.util.LinkedList;
import java.util.List;

/**
 * Entrance to Deathmatch
 */
public class DeathmatchEntrance {

	class JumpToDeathmatchAction extends SpeakerNPC.ChatAction {
		public void fire(Player player, String text, SpeakerNPC engine) {
			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("int_semos_deathmatch");
			player.teleport(zone, 17, 8, Direction.DOWN, null);			
			return;
		}
	}

	public void build() {
		String myZone = "0_semos_plains_n";
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("0_ados_swamp");
		ScriptingNPC npc=new ScriptingNPC("Deathmatch Recruiter");
		npc.put("class", "youngsoldiernpc");
		List<Path.Node> path = new LinkedList<Path.Node>();
		path.add(new Path.Node(40, 35));
		path.add(new Path.Node(40, 84));
		path.add(new Path.Node(53, 84));
		path.add(new Path.Node(53, 80));
		path.add(new Path.Node(84, 80));
		path.add(new Path.Node(84, 56));
		path.add(new Path.Node(89, 56));
		path.add(new Path.Node(89, 37));
		path.add(new Path.Node(72, 37));
		path.add(new Path.Node(72, 32));
		path.add(new Path.Node(50, 32));
		path.add(new Path.Node(50, 35));
		npc.setPath(path);

		npc.behave("greet", "Hey there. You look like a reasonable fighter.");
		npc.behave("job", "I'm recruiter for the Semos #deathmatch.");
		npc.behave("help", "Have you ever heard of the Semos #deathmatch.");
		npc.behave("deathmatch", "The deathmatch is the ultimate challenge for true #heroes.");
		npc.behave("heroes", "Are you such a hero? I can take you to the #challenge.");
		npc.behave("bye", "I hope you will enjoy the Semos #Deathmatch!");
		
		npc.add (1,"challenge",null,1,null, new JumpToDeathmatchAction());

		npc.set(40, 35);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}
