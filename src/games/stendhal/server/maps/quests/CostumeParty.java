package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * Puts the player into a funny constume
 */
public class CostumeParty extends AbstractQuest {

	private static final String QUEST_SLOT = "costume_party";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	private void createNPC() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("0_semos_mountain_n2"));
		SpeakerNPC npc = new SpeakerNPC("Fidorea") {
			@Override
			protected void createPath() {
				// TODO: implement path
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						int outfit = player.getInt("outfit");
						if (!player.has("outfit_org")) {
							player.put("outfit_org", outfit);
						}
						// hair head outfit body
						int randomHead = Rand.rand(5);
						int head = 80 + randomHead;
						outfit = 00 * 1000000 + head * 10000 + (outfit % 10000);
						player.put("outfit", outfit);
					}
				});
				addHelp("You have to stand next to a token in order to move it.");
				addJob("I am a makeup artist");
				addGoodbye("It was nice to meet you.");
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "girlnpc"); // TODO: change outfit
		npc.set(105, 113);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	public void addToWorld() {
		super.addToWorld();
		createNPC();
	}
}
