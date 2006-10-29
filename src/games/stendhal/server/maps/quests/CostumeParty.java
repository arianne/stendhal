package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * Puts the player into a funny constume
 */
public class CostumeParty extends AbstractQuest {

	private static final String QUEST_SLOT = "costume_party";
	
	private class CostumeStripTimer implements TurnListener {
		public void onTurnReached(int currentTurn, String message) {
			List<Player> players = StendhalRPRuleProcessor.get().getPlayers();
			for (Player player : players) {
				if (player.hasQuest(QUEST_SLOT)) {
					if (!player.isQuestCompleted(QUEST_SLOT)) {
						long expireTime = Long.parseLong(player.getQuest(QUEST_SLOT));
						if (expireTime < System.currentTimeMillis()) {
							if (player.has("outfit_org")) {
								player.put("outfit", player.get("outfit_org"));
								player.remove("outfit_org");
								player.notifyWorldAboutChanges();
								player.sendPrivateText("My costume is wearing away.");
							}
							player.setQuest(QUEST_SLOT, "done");
						}
					}
				}
			}
			TurnNotifier.get().notifyInTurns(65*3, this, null);
		}
	}
	
	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	private void createNPC() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("0_semos_mountain_n2"));
		SpeakerNPC npc = new SpeakerNPC("Fidorea") {
			@Override
			protected void createPath() {
				// npc does not move
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
						// hair, head, outfit, body
						int randomHead = Rand.rand(5);
						int head = 80 + randomHead;
						outfit = 00 * 1000000 + head * 10000 + (outfit % 10000);
						player.put("outfit", outfit);
						engine.say("I hope, you like your costume.");
						player.setQuest(QUEST_SLOT, Long.toString(System.currentTimeMillis() + 30 * 60 * 1000));
					}
				});
				addHelp("If you don't like your costume, you can remove it by clicking on yourself and choosing Set Outfit.");
				addJob("I am a makeup artist living in Ados. But for the Semos Mine Town Revival Weeks i come here once a year.");
				addQuest("Just have fun.");
				add(ConversationStates.ATTENDING, Arrays.asList("offer"), ConversationStates.ATTENDING, "I will give you a costume free of charge.", null);
				addGoodbye("Come back to me, if you want another costume.");
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "woman_008_npc");
		npc.set(80, 108);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	public void addToWorld() {
		super.addToWorld();
		createNPC();
		TurnNotifier.get().notifyInTurns(65, new CostumeStripTimer(), null);
	}
}
