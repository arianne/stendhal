package games.stendhal.server.maps.deniran.cityoutside;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class DoctorNPC implements ZoneConfigurator {

	
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("John Smith") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(72, 8));
				nodes.add(new Node(83, 8));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Brilliant! I like a nice fence! *sniff* *snniff* War is immentent. No, don't like #wars.");
				addJob("I am inspector of fences. Or something.");
				addGoodbye("Be fantastic!");
				addReply(Arrays.asList("advice", "doctor", "promise", "kind", "cowardly", "pear", "pears"),
						"Never be cruel, never be cowardly. And never ever eat pears! Remember: Hate is always foolish and love is always wise.");
				addReply(Arrays.asList("faith", "love", "wedding", "marriage"),
						"Something I believe in, my faith: Love in all its forms is the most powerful weapon because love is a form of hope.");
				addReply(Arrays.asList("war", "army", "fight", "gun", "attack"),
						"When you fire that first shot, no matter how right you feel, you have no idea, who’s going to die! You don’t know, whose children are going to scream and burn! How many hearts will be broken! How many lives shattered! How much blood will spill, until everybody does, what they were always going to do from the very beginning! – Sit down and talk! ");
				addReply(Arrays.asList("tardis", "blue", "box"),
						"I stole it. Well, I borrowed it; I was always going to take it back. Oh, that box. Big and little at the same time, brand-new and ancient, and the bluest blue ever.");
				addReply(Arrays.asList("sonic", "screwdriver"),
						"It's a sonic screwdriver. See? It makes a noise.");
				addReply(Arrays.asList("regeneration", "death", "dying", "end", "change"),
						"We all change. We are all different people all through our lives, and that's okay, that's good. You've got to keep moving. So long as you remember all the people that you used to be.");
				addReply(Arrays.asList("good", "bad", "idiot", "mad"),
						"Am I a good man?");
				addReply(Arrays.asList("fact", "facts"),
						"The very powerful and the very stupid have one thing in common. Instead of altering their views to fit the facts, they alter the facts to fit their views... Which can be very uncomfortable, if you happen to be one of the facts, that needs altering.");
				addReply(Arrays.asList("9", "nine", "ninth", "Christopher", "Eccleston"),
						"Fantastic!");
				addReply(Arrays.asList("10", "ten", "David", "Tennant"),
						"Allons-y!");
				addReply(Arrays.asList("11", "eleven", "Matt", "Smith"),
						"Geronimo!");
				addReply(Arrays.asList("13", "thirteen", "Jodie", "Whittaker"),
						"Brilliant!");
				addReply("bow", "Bow ties are cool.");
				addReply(Arrays.asList("mate", "companion", "friend"),
						"Oh, friends! Friends are great! I have a lot of friends... and had. Some left me. Some got left behind. And some, not many but, some regrettably died.");
			}

		};
		npc.setOutfit(0, 14, 0, 0, 0, 0, 33, 0, 0);
		npc.setDescription("You see a man who seems to be very interested in the fence.");
		npc.setPosition(83, 8);
		npc.initHP(100);
		zone.add(npc);
		return npc;
	}

}
