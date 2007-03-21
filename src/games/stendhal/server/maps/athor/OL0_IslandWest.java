package games.stendhal.server.maps.athor;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OL0_IslandWest implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 * 
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildBeachArea(zone, attributes);
	}

	private void buildBeachArea(StendhalRPZone zone,
			Map<String, String> attributes) {
		SpeakerNPC cyk = new SpeakerNPC("Cyk") {
			@Override
			protected void createPath() {
				// doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			public void say(String text) {
				// Cyk doesn't move around because he's "lying" on his towel.
				say(text, false);
			}
			
			@Override
			protected void createDialog() {
				addGreeting("Hey there!");
				add(ConversationStates.ATTENDING,
						ConversationPhrases.QUEST_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"I don't have a task for you, I'm perfectly happy!",
						null);
				addJob("Don't remind me of my job, I'm on holiday!");
				addHelp("In the desert there is dangerous quicksand.");
				addGoodbye("Bye! I'll stock up on some more sunshine.");
			}
		};
		npcs.add(cyk);
		
		zone.assignRPObjectID(cyk);
		cyk.put("class", "swimmer1npc");
		cyk.set(172, 39);
		cyk.setDirection(Direction.DOWN);
		cyk.initHP(100);
		zone.add(cyk);
		
		SpeakerNPC zara = new SpeakerNPC("Zara") {
			@Override
			protected void createPath() {
				// doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			public void say(String text) {
				// Zara doesn't move around because she's "lying" on her towel.
				say(text, false);
			}
			
			@Override
			protected void createDialog() {
				addGreeting("Nice to meet you!");
				addJob("I'm on holiday! Let's talk about anything else!");
				// TODO
				addHelp("Be careful! On this island is a desert where many adventurers found their death...");
				addGoodbye("I hope to see you soon!");
			}
		};
		npcs.add(zara);
		
		zone.assignRPObjectID(zara);
		zara.put("class", "swimmer8npc");
		zara.set(188, 32);
		zara.setDirection(Direction.DOWN);
		zara.initHP(100);
		zone.add(zara);
	
		SpeakerNPC enrique = new SpeakerNPC("Enrique") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// Enrique is swimming in the pool
				nodes.add(new Path.Node(195, 68));
				nodes.add(new Path.Node(195, 63));
				setPath(nodes, true);
			}
						
			@Override
			protected void createDialog() {
				addGreeting("Don't disturb me, I'm trying to establish a record!");
				add(ConversationStates.ATTENDING,
						ConversationPhrases.QUEST_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"I don't have a task for you, I'm too busy.",
						null);
				addJob("I am a swimmer!");
				addHelp("Try the diving board! It's fun!");
				addGoodbye("Bye!");
			}
		};
		npcs.add(enrique);
		
		zone.assignRPObjectID(enrique);
		enrique.put("class", "swimmer3npc");
		enrique.set(195, 63);
		enrique.setDirection(Direction.DOWN);
		enrique.initHP(100);
		zone.add(enrique);
		
		SpeakerNPC dorinel = new SpeakerNPC("Dorinel") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// Dorinel is swimming in the sea
				nodes.add(new Path.Node(169, 21));
				nodes.add(new Path.Node(169, 28));
				setPath(nodes, true);
			}
						
			@Override
			protected void createDialog() {
				addGreeting("Hallo, my friend!");
				add(ConversationStates.ATTENDING,
						ConversationPhrases.QUEST_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"No, thank you, I do not need help!",
						null);
				addJob("I am a diver, but I cannot see a single fish at the moment!");
				addHelp("I like the swimsuits which you can get in the dressing rooms at the beach.");
				addGoodbye("Bye!");
			}
		};
		npcs.add(dorinel);
		
		zone.assignRPObjectID(dorinel);
		dorinel.put("class", "swimmer2npc");
		dorinel.set(169, 28);
		dorinel.setDirection(Direction.DOWN);
		dorinel.initHP(100);
		zone.add(dorinel);
		
		SpeakerNPC john = new SpeakerNPC("John") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// John doesn't move
				setPath(nodes, true);
			}
						
			@Override
			public void say(String text) {
				// John doesn't move around because he's "lying" on his towel.
				say(text, false);
			}
			
			@Override
			protected void createDialog() {
				addGreeting("Hi!");
				add(ConversationStates.ATTENDING,
						ConversationPhrases.QUEST_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"We have no tasks, we're here on holiday.",
						null);
				addJob("I am a coachman, but on this island there are no carriages!");
				addHelp("Don't try to talk to my wife, she is very shy.");
				addGoodbye("Bye!");
			}
		};
		npcs.add(john);
		
		zone.assignRPObjectID(john);
		john.put("class", "swimmer5npc");
		john.set(155, 43);
		john.setDirection(Direction.DOWN);
		john.initHP(100);
		zone.add(john);
		
		SpeakerNPC jane = new SpeakerNPC("Jane") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// John doesn't move
				setPath(nodes, true);
			}
						
			@Override
			public void say(String text) {
				// Jane doesn't move around because she's "lying" on her towel.
				say(text, false);
			}
			
			@Override
			protected void createDialog() {
				addGreeting("Hi!");
				addGoodbye("Bye!");
			}
		};
		npcs.add(jane);
		
		zone.assignRPObjectID(jane);
		jane.put("class", "swimmer6npc");
		jane.set(156, 43);
		jane.setDirection(Direction.DOWN);
		jane.initHP(100);
		zone.add(jane);
		
		SpeakerNPC yan = new SpeakerNPC("Yan") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// Yan doesn't move
				setPath(nodes, true);
			}
						
			@Override
			protected void createDialog() {
				addGreeting("Hello stranger!");
				addQuest("I don't have a task right now, but in the next release I will get one...");
				addJob("Sorry, but on holiday I don't want to talk about work");
				addHelp("A cocktail bar will open on this island soon.");
				addGoodbye("See you later!");
			}
		};
		npcs.add(yan);
		
		zone.assignRPObjectID(yan);
		yan.put("class", "swimmer4npc");
		yan.set(190, 72);
		yan.setDirection(Direction.DOWN);
		yan.initHP(100);
		zone.add(yan);
		
		SpeakerNPC kelicia = new SpeakerNPC("Kelicia") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// Kelicia is walking along the coast
				nodes.add(new Path.Node(133, 49));
				nodes.add(new Path.Node(134, 48));
				nodes.add(new Path.Node(134, 47));
				nodes.add(new Path.Node(136, 47));
				nodes.add(new Path.Node(136, 46));
				nodes.add(new Path.Node(139, 46));
				nodes.add(new Path.Node(139, 45));
				nodes.add(new Path.Node(141, 45));
				nodes.add(new Path.Node(141, 44));
				nodes.add(new Path.Node(143, 44));
				nodes.add(new Path.Node(143, 43));
				nodes.add(new Path.Node(145, 43));
				nodes.add(new Path.Node(145, 42));
				nodes.add(new Path.Node(147, 42));
				nodes.add(new Path.Node(147, 41));
				nodes.add(new Path.Node(148, 41));
				nodes.add(new Path.Node(148, 40));
				nodes.add(new Path.Node(150, 40));
				nodes.add(new Path.Node(150, 39));
				nodes.add(new Path.Node(152, 39));
				nodes.add(new Path.Node(152, 38));
				nodes.add(new Path.Node(154, 38));
				nodes.add(new Path.Node(154, 37));
				nodes.add(new Path.Node(155, 37));
				nodes.add(new Path.Node(155, 36));
				nodes.add(new Path.Node(157, 36));
				nodes.add(new Path.Node(157, 35));
				nodes.add(new Path.Node(159, 35));
				nodes.add(new Path.Node(159, 34));
				nodes.add(new Path.Node(162, 34));
				nodes.add(new Path.Node(162, 33));
				nodes.add(new Path.Node(163, 33));
				nodes.add(new Path.Node(163, 32));
				nodes.add(new Path.Node(169, 32));
				nodes.add(new Path.Node(169, 31));
				nodes.add(new Path.Node(173, 31));
				nodes.add(new Path.Node(173, 30));
				nodes.add(new Path.Node(175, 30));
				nodes.add(new Path.Node(175, 29));
				nodes.add(new Path.Node(180, 29));
				nodes.add(new Path.Node(180, 28));
				nodes.add(new Path.Node(204, 28));
				
				// the same way back
                nodes.add(new Path.Node(180, 28));
                nodes.add(new Path.Node(180, 29));
                nodes.add(new Path.Node(175, 29));
                nodes.add(new Path.Node(175, 30));
                nodes.add(new Path.Node(173, 30));
                nodes.add(new Path.Node(173, 31));
                nodes.add(new Path.Node(169, 31));
                nodes.add(new Path.Node(169, 32));
                nodes.add(new Path.Node(163, 32));
                nodes.add(new Path.Node(163, 33));
                nodes.add(new Path.Node(162, 33));
                nodes.add(new Path.Node(162, 34));
                nodes.add(new Path.Node(159, 34));
                nodes.add(new Path.Node(159, 35));
                nodes.add(new Path.Node(157, 35));
                nodes.add(new Path.Node(157, 36));
                nodes.add(new Path.Node(155, 36));
                nodes.add(new Path.Node(155, 37));
                nodes.add(new Path.Node(154, 37));
                nodes.add(new Path.Node(154, 38));
                nodes.add(new Path.Node(152, 38));
                nodes.add(new Path.Node(152, 39));
                nodes.add(new Path.Node(150, 39));
                nodes.add(new Path.Node(150, 40));
                nodes.add(new Path.Node(148, 40));
                nodes.add(new Path.Node(148, 41));
                nodes.add(new Path.Node(147, 41));
                nodes.add(new Path.Node(147, 42));
                nodes.add(new Path.Node(145, 42));
                nodes.add(new Path.Node(145, 43));
                nodes.add(new Path.Node(143, 43));
                nodes.add(new Path.Node(143, 44));
                nodes.add(new Path.Node(141, 44));
                nodes.add(new Path.Node(141, 45));
                nodes.add(new Path.Node(139, 45));
                nodes.add(new Path.Node(139, 46));
                nodes.add(new Path.Node(136, 46));
                nodes.add(new Path.Node(136, 47));
                nodes.add(new Path.Node(134, 47));

                setPath(nodes, true);
			}
						
			@Override
			protected void createDialog() {
				addGreeting("Hi!");
				addQuest("I have no jobs for you, my friend");
				addJob("I'm just walking along the coast!");
				addHelp("I cannot help you...I'm just a girl...");
				addGoodbye("Bye!");
			}
		};
		npcs.add(kelicia);
		
		zone.assignRPObjectID(kelicia);
		kelicia.put("class", "swimmer7npc");
		kelicia.set(133, 48);
		kelicia.setDirection(Direction.DOWN);
		kelicia.initHP(100);
		zone.add(kelicia);
		
	}
}
