package games.stendhal.server.maps.ados;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.PersonalChest;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.util.WikipediaAccess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;

/**
 * Builds the inside of buildings in Ados City
 *
 * @author hendrik
 */
public class AdosCityInside implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

	
	/**
	 * build the city insides
	 */
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();
		buildBank((StendhalRPZone) world.getRPZone(new IRPZone.ID("int_ados_bank")));
		buildBakery((StendhalRPZone) world.getRPZone(new IRPZone.ID("int_ados_bakery")));
		buildTavern((StendhalRPZone) world.getRPZone(new IRPZone.ID("int_ados_tavern_0")));
		buildTempel((StendhalRPZone) world.getRPZone(new IRPZone.ID("int_ados_temple")));
		buildLibrary((StendhalRPZone) world.getRPZone(new IRPZone.ID("int_ados_library")));
		buildHauntedHouse((StendhalRPZone) world.getRPZone(new IRPZone.ID("int_ados_haunted_house")));
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		/*
		 * For now - Split to one class per zone
		 */
		build();
	}


	private void buildBank(StendhalRPZone zone) {

		// portal from bank to city
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(22);
		portal.setY(17);
		portal.setNumber(0);
		portal.setDestination("0_ados_city", 6);
		zone.addPortal(portal);

		// personal chest
		PersonalChest chest = new PersonalChest();
		zone.assignRPObjectID(chest);
		chest.set(3, 12);
		zone.add(chest);

		chest = new PersonalChest();
		zone.assignRPObjectID(chest);
		chest.set(5, 12);
		zone.add(chest);
		
		chest = new PersonalChest();
		zone.assignRPObjectID(chest);
		chest.set(10, 12);
		zone.add(chest);
	}

	private void buildLibrary(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Wikipedian") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(9, 8));
				nodes.add(new Path.Node(9, 26));
				nodes.add(new Path.Node(20, 26));
				nodes.add(new Path.Node(20, 9));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am the librarian");
				addHelp("Just ask me to #explain #something");
				add(ConversationStates.ATTENDING, "explain", null, ConversationStates.ATTENDING, null, new SpeakerNPC.ChatAction() {

					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						// extract the title
						int pos = text.indexOf(" ");
						if (pos < 0) {
							engine.say("What do you want to be explained?");
							return;
						}
						String title = text.substring(pos + 1).trim();

						WikipediaAccess access = new WikipediaAccess(title);
						Thread thread = new Thread(access);
						thread.setPriority(Thread.MIN_PRIORITY);
						thread.setDaemon(true);
						thread.start();
						TurnNotifier.get().notifyInTurns(10, new WikipediaWaiter(engine, access), null);
						engine.say("Please wait, while i am looking it up in the book called #Wikipedia!");
					}
					// TODO: implement pointer to authors, GFDL, etc...
				});
				addReply("wikipedia", "Wikipedia is an Internet based to create a #free encyclopedia");
				addReply("free", "The Wikipedia content may be used according to the rules specified in the GNU General Documentation License which can be found at http://en.wikipedia.org/wiki/Wikipedia:Text_of_the_GNU_Free_Documentation_License");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "investigatornpc");
		npc.set(9, 8);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	private void buildTavern(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(12);
		portal.setY(17);
		portal.setNumber(0);
		portal.setDestination("0_ados_city", 0);
		zone.addPortal(portal);
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(27);
		portal.setY(17);
		portal.setNumber(1);
		portal.setDestination("0_ados_city", 1);
		zone.addPortal(portal);

		
		SpeakerNPC tavernMaid = new SpeakerNPC("Coralia") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(17, 12));
				nodes.add(new Path.Node(17, 13));
				nodes.add(new Path.Node(16, 8));
				nodes.add(new Path.Node(13, 8));
				nodes.add(new Path.Node(13, 6));
				nodes.add(new Path.Node(13, 10));
				nodes.add(new Path.Node(25, 10));
				nodes.add(new Path.Node(25, 13));
				nodes.add(new Path.Node(25, 10));
				nodes.add(new Path.Node(17, 10));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am the bar maid for this fair tavern. We sell both imported and local beers, and fine food.");
				addHelp("This tavern is a great place to take a break and meet new people! Just ask if you want me to #offer you a drink.");
				addSeller(new SellerBehaviour(shops.get("food&drinks")));
				addGoodbye();
			}
		};
		npcs.add(tavernMaid);
		zone.assignRPObjectID(tavernMaid);
		tavernMaid.put("class", "maidnpc");
		tavernMaid.set(17, 12);
		tavernMaid.initHP(100);
		zone.addNPC(tavernMaid);

	}

	private void buildBakery(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(26);
		portal.setY(14);
		portal.setNumber(0);
		portal.setDestination("0_ados_city", 10);
		zone.addPortal(portal);

		SpeakerNPC baker = new SpeakerNPC("Arlindo") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// to the well
				nodes.add(new Path.Node(15, 2));
				// to a barrel
				nodes.add(new Path.Node(15, 7));
				// to the baguette on the table
				nodes.add(new Path.Node(13, 7));
				// around the table
				nodes.add(new Path.Node(13, 9));
				nodes.add(new Path.Node(10, 9));
				// to the sink
				nodes.add(new Path.Node(10, 11));
				// to the pizza/cake/whatever
				nodes.add(new Path.Node(7, 11));
				nodes.add(new Path.Node(7, 9));
				// to the pot
				nodes.add(new Path.Node(3, 9));
				// towards the oven
				nodes.add(new Path.Node(3, 3));
				nodes.add(new Path.Node(5, 3));
				// to the oven
				nodes.add(new Path.Node(5, 2));
				// one step back
				nodes.add(new Path.Node(5, 3));
				// towards the well
				nodes.add(new Path.Node(15, 3));
				
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				// addGreeting("Hi, most of the people are out of town at the moment.");
				addJob("I'm the local baker. Although we get most of our supplies from Semos City, there is still a lot of work to do.");
				addReply(Arrays.asList("flour", "meat", "carrot", "mushroom", "button_mushroom"), "Ados is short on supplies. We get most of our food from Semos City which is west of here.");
				addHelp("My wife is searching for that lost girl, too. So we cannot sell you anthing at the moment.");
				addGoodbye();

				// Arlindo makes pies if you bring him flour, meat, carrot and a mushroom
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("flour", new Integer(2));
				requiredResources.put("meat", new Integer(2));
				requiredResources.put("carrot", new Integer(1));
				requiredResources.put("button_mushroom", new Integer(1));

				ProducerBehaviour behaviour = new ProducerBehaviour(
						"arlindo_make_pie", "make", "pie", requiredResources, 7 * 60);

				addProducer(behaviour,
						"Hi! I bet you've heard about my famous pie and want me to #make one for you, am I right?");
			}
		};
		npcs.add(baker);
		zone.assignRPObjectID(baker);
		baker.put("class", "bakernpc");
		baker.setDirection(Direction.DOWN);
		baker.set(15, 2);
		baker.initHP(100);
		zone.addNPC(baker);
	}
    private void buildHauntedHouse(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(7);
		portal.setY(1);
		portal.setNumber(0);
		portal.setDestination("0_ados_city", 11);
		zone.addPortal(portal);

	}

	private void buildTempel(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(10);
		portal.setY(23);
		portal.setNumber(0);
		portal.setDestination("0_ados_city", 1);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(11);
		portal.setY(23);
		portal.setNumber(1);
		portal.setDestination("0_ados_city", 1);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(12);
		portal.setY(23);
		portal.setNumber(2);
		portal.setDestination("0_ados_city", 1);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(13);
		portal.setY(23);
		portal.setNumber(3);
		portal.setDestination("0_ados_city", 1);
		zone.addPortal(portal);
	}

	protected class WikipediaWaiter implements TurnListener {
		private WikipediaAccess access = null;
		private SpeakerNPC engine = null;

		public WikipediaWaiter(SpeakerNPC engine, WikipediaAccess access) {
			this.engine = engine;
			this.access = access;
		}

		public void onTurnReached(int currentTurn, String message) {
			if (!access.isFinished()) {
				TurnNotifier.get().notifyInTurns(3, new WikipediaWaiter(engine, access), null);
				return;
			}
			if (access.getError() != null) {
				engine.say("Sorry, I cannot access the bookcase at the moment");
				return;
			}

			if (access.getText() != null && access.getText().length() > 0) {
				String content = access.getProcessedText();
				engine.say(content);
			} else {
				engine.say("Sorry, this book has still to be written");
			}
		}
	}
}
