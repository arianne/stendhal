package games.stendhal.server.maps.orril;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Configure Orril River South Campfire (Outside/Level 0).
 */
public class OL0_RiverSouthCampfire implements ZoneConfigurator {

	private NPCList npcs;

	public OL0_RiverSouthCampfire() {
		this.npcs = NPCList.get();
	}

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildCampfireArea(zone);
		buildGoldSourceArea(zone);
	}

	private void buildCampfireArea(StendhalRPZone zone) {
		SpeakerNPC sally = new SpeakerNPC("Sally") {

			@Override
			protected void createPath() {
				// NPC does not move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				//addGreeting();
				addJob("Work? I'm just a little girl! I'm a scout, you know.");
				addHelp("You can find lots of useful stuff in the forest; wood and mushrooms, for example. But beware, some mushrooms are poisonous!");
				addGoodbye();
				// remaining behaviour is defined in maps.quests.Campfire.				
			}
		};
		npcs.add(sally);

		zone.assignRPObjectID(sally);
		sally.put("class", "littlegirlnpc");
		sally.set(40, 60);
		sally.setDirection(Direction.RIGHT);
		sally.initHP(100);
		zone.add(sally);
	}

	private void buildGoldSourceArea(StendhalRPZone zone) {

		SpeakerNPC bill = new SpeakerNPC("Bill") {

			@Override
			protected void createPath() {
				// NPC does not move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Howdy partner!");
				addJob("Once I was a very successful gold procpector, but with the age came the backache, so I'm a pensioner now. However I can still give advice to rookies!");
				add(ConversationStates.ATTENDING, ConversationPhrases.HELP_MESSAGES, null,
				        ConversationStates.INFORMATION_1,
				        "I can tell you the secrets of prospecting for gold, if you are interested. Are you?", null);

				add(
				        ConversationStates.INFORMATION_1,
				        ConversationPhrases.YES_MESSAGES,
				        null,
				        ConversationStates.ATTENDING,
				        "First you need a #gold_pan to separate the gold from the mud. Then you have to search for the right spot in the water. The flat water in this area is very rich of gold ressources. Just doubleclick on the lightblue water when you see something glittering. But don't give up too early, you need a lot of luck and patience.",
				        null);

				add(ConversationStates.INFORMATION_1, ConversationPhrases.NO_MESSAGES, null,
				        ConversationStates.ATTENDING,
				        "Oh, it doesn't matter, the less people know about the prospect secrets the better!", null);

				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING,
				        "I don't have a task for you, I'm just here to help new prospectors.", null);

				add(ConversationStates.ATTENDING, Arrays.asList("gold", "pan", "gold_pan"), null,
				        ConversationStates.ATTENDING,
				        "I don't have a gold pan, but maybe you could ask a blacksmith to forge you one.", null);

				addGoodbye("Seeya, get yer spurs on!");

			}
		};
		npcs.add(bill);

		zone.assignRPObjectID(bill);
		bill.put("class", "oldcowboynpc");
		bill.set(105, 57);
		bill.setDirection(Direction.DOWN);
		bill.initHP(100);
		zone.add(bill);
	}
}
