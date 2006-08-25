package games.stendhal.server.maps;

import games.stendhal.common.Direction;
import games.stendhal.server.RespawnPoint;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

public class Ados implements IContent {
	private ShopList shops;
	private NPCList npcs;
	
	private static class AdosAttackableCreature extends AttackableCreature implements TurnListener {
		private static long lastShoutTime = 0;
		
		private String cryForHelp = null;

		/**
		 * An attackable creature that will cause Katinka to shout if it 
		 * is killed by a monster.
		 *
		 * @param copy template creature
		 */
		public AdosAttackableCreature(Creature copy) {
			super(copy);
		}

		@Override
		public void onDead(Entity killer) {
			super.onDead(killer);
			if (! (killer instanceof Player)) {
				long currentTime = System.currentTimeMillis();
				if (lastShoutTime + 5*60*1000 < currentTime) {
					lastShoutTime = currentTime;
					cryForHelp = "Katinka shouts: Help! A " + killer.get("name") + " is eating our " + this.get("name") + "s.";
					// HACK: we need to wait a turn because the message is lost otherwise
					TurnNotifier.get().notifyInTurns(0, this, null);
				}
			}
		}

		@Override
		public Creature getInstance() {
			return new AdosAttackableCreature(this);
		}

		public void onTurnReached(int currentTurn, String message) {
			// HACK: we need to wait a turn because the message is lost otherwise
			// sends the message to all players
			StendhalRPAction.shout(cryForHelp.replace('_', ' '));
		}
	}

	public Ados() {
		this.npcs = NPCList.get();
		this.shops = ShopList.get();
		
		StendhalRPWorld world = StendhalRPWorld.get();

		buildRockArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"0_ados_rock")));
		buildZooArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"0_ados_outside_nw")));
		buildZooSub1Area((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"-1_ados_outside_nw")));
		buildZooSub3Area((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"-3_ados_outside_nw")));
		buildMagicianHouseArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"int_ados_magician_house")));
	}


	private void buildRockArea(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Balduin") {
			@Override
			protected void createPath() {
				// NPC doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addHelp("There is a swamp east of this mountain where you might get some rare weapons.");
				addJob("I'm much too old for hard work. I'm just living here as a hermit.");
				addGoodbye("It was nice to meet you.");
			}
			// remaining behaviour is defined in maps.quests.WeaponsCollector.
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "oldwizardnpc");
		npc.set(16, 7);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	private void buildZooArea(StendhalRPZone zone) {
		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(53);
		sign.sety(48);
		sign.setText("Ados Wildlife Refuge\nHome for endangered animals");
		zone.add(sign);
		
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(67);
		portal.sety(24);
		portal.setNumber(0);
		portal.setDestination("-1_ados_outside_nw", 0);
		zone.addPortal(portal);
		
		SpeakerNPC npc = new SpeakerNPC("Katinka") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(41, 39));
				nodes.add(new Path.Node(51, 39));
				nodes.add(new Path.Node(51, 45));
				nodes.add(new Path.Node(58, 45));
				nodes.add(new Path.Node(58, 41));
				nodes.add(new Path.Node(51, 41));
				nodes.add(new Path.Node(51, 39));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addHelp("Can you keep a secret? Dr. Feelgood, our veterinary, can sell you medicine that he doesn't need for the animals.");
				addJob("I'm the keeper of this animal refuge.");
				addGoodbye("Goodbye!");
			}
			// remaining behaviour is defined in maps.quests.ZooFood.
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "woman_007_npc");
		npc.set(41, 39);
		//npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.addNPC(npc);

		npc = new SpeakerNPC("Dr. Feelgood") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(53, 27));
				nodes.add(new Path.Node(53, 39));
				nodes.add(new Path.Node(62, 39));
				nodes.add(new Path.Node(62, 31));
				nodes.add(new Path.Node(63, 31));
				nodes.add(new Path.Node(63, 39));
				nodes.add(new Path.Node(51, 39));
				nodes.add(new Path.Node(51, 27));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				//Behaviours.addHelp(this,
				//				   "...");
				
				addReply("heal",
						"Sorry, I'm licensed to heal animals only, not humans. But... ssshh! I want to make you an #offer.");

				addJob("I'm the veterinary.");
				addSeller(new SellerBehaviour(shops.get("healing")) {
					@Override
					public int getUnitPrice(String item) {
						// Player gets 20 % rebate
						return (int) (0.8f * priceList.get(item));
					}
				});

				addGoodbye("Bye!");
			}
			// remaining behaviour is defined in maps.quests.ZooFood.
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "doctornpc");
		npc.set(53, 27);
		//npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.addNPC(npc);

		// put special RespawnPoints
		// 65, 34 bear
		DefaultEntityManager manager = (DefaultEntityManager) StendhalRPWorld.get().getRuleManager().getEntityManager();
		Creature creature = new AdosAttackableCreature(manager.getCreature("bear"));
		RespawnPoint point = new RespawnPoint(zone, 65, 34, creature, 1);
		zone.addRespawnPoint(point);

		// 67, 29 bear
		point = new RespawnPoint(zone, 67, 29, creature, 1);
		zone.addRespawnPoint(point);
		
		// 67, 31 black_bear
		creature = new AdosAttackableCreature(manager.getCreature("black_bear"));
		point = new RespawnPoint(zone, 67, 31, creature, 1);
		zone.addRespawnPoint(point);

		// 67, 35 black_bear
		point = new RespawnPoint(zone, 67, 35, creature, 1);
		zone.addRespawnPoint(point);
	}

	private void buildZooSub1Area(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(4);
		portal.sety(23);
		portal.setNumber(0);
		portal.setDestination("0_ados_outside_nw", 0);
		zone.addPortal(portal);
	}

	private void buildZooSub3Area(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Bario") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// to stove
				nodes.add(new Path.Node(7, 43));
				// to table
				nodes.add(new Path.Node(7, 51));
				// around couch
				nodes.add(new Path.Node(14, 56));
				nodes.add(new Path.Node(22, 56));
				// into the floor
				nodes.add(new Path.Node(18, 49));
				nodes.add(new Path.Node(19, 41));
				// into the bathroom
				nodes.add(new Path.Node(39, 41));
				// into the floor
				nodes.add(new Path.Node(18, 41));
				// into the bedroom
				nodes.add(new Path.Node(18, 28));
				// to the chest
				nodes.add(new Path.Node(17, 23));
				// through the floor
				nodes.add(new Path.Node(18, 33));
				nodes.add(new Path.Node(18, 50));
				// back to the kitchen
				nodes.add(new Path.Node(7, 50));
				nodes.add(new Path.Node(4, 43));
				nodes.add(new Path.Node(4, 46));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addJob("There is a quite high unemployment rate down here.");
				addHelp("Those lazy developers have not yet finished me ;)");
				addGoodbye();
				// remaining behaviour is defined in maps.quests.CloaksForBario.
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "beardmannpc");
		npc.set(4, 46);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	private void buildMagicianHouseArea(StendhalRPZone zone) {
		StendhalRPZone zoneOutside = (StendhalRPZone) 
			StendhalRPWorld.get().getRPZone(new IRPZone.ID("0_ados_mountain_nw"));

		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(12);
		portal.sety(13);
		portal.setNumber(0);
		portal.setDestination("0_ados_mountain_nw", 0);
		zone.addPortal(portal);

		portal = new Portal();
		zoneOutside.assignRPObjectID(portal);
		portal.setx(75);
		portal.sety(51);
		portal.setNumber(0);
		portal.setDestination("int_ados_magician_house", 0);
		zoneOutside.addPortal(portal);

		SpeakerNPC npc = new SpeakerNPC("Haizen") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(7, 1));
				nodes.add(new Path.Node(7, 3));
				nodes.add(new Path.Node(13, 3));
				nodes.add(new Path.Node(13, 8));
				nodes.add(new Path.Node(9, 8));
				nodes.add(new Path.Node(9, 7));
				nodes.add(new Path.Node(9, 8));
				nodes.add(new Path.Node(2, 8));
				nodes.add(new Path.Node(2, 2));
				nodes.add(new Path.Node(7, 2));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am a wizard and i sell magic scrolls. Just ask me about my #offer");
				addHelp("You can use magic with the help of #magic #scrolls.");

				addSeller(new SellerBehaviour(shops.get("scrolls")));
				
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I do not have any task for you right now. If you need anything from me just say it.",
					null);
				add(ConversationStates.ATTENDING,
					new String[] { "magic", "scroll", "scrolls" },
					null,
					ConversationStates.ATTENDING,
					"I #offer scrolls that help you to travel faster: #home scrolls and #empty scrolls that can be #marked. For the advanced magicians i have #summon scrolls.",
					null);
				add(ConversationStates.ATTENDING,
					new String[] { "home" },
					null,
					ConversationStates.ATTENDING,
					"Home scrolls take you home immediately, a good way to escape danger!",
					null);
				add(ConversationStates.ATTENDING,
					new String[] { "empty", "marked" },
					null,
					ConversationStates.ATTENDING,
					"Empty scrolls are used to mark a position. Marked scrolls can take you back to that position. They are a little expensive, though.",
					null);
				add(ConversationStates.ATTENDING,
					new String[] { "summon"},
					null,
					ConversationStates.ATTENDING,
					"You can summon animals with summon_scrolls. Advanced magicians can summon stronger monsters but i think it is too dangerous to sell such scrolls.",
					null);
			
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "wisemannpc");
		npc.set(7, 1);
		npc.initHP(100);
		zone.addNPC(npc);
		
		Item item = addPersistentItem("summon_scroll", zone, 7, 6);
		// Just in case a player finds a way to get this scroll, fake it:
		item.setDescription("You see a summon scroll. It is marked with: blue_dragon");
		item.put("infostring", "rat");
		addPersistentItem("poison", zone, 4, 7); // need PlantGrower
	}
	
	private Item addPersistentItem(String name, StendhalRPZone zone, int x, int y) {
		Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(name);
		zone.assignRPObjectID(item);
		item.setx(x);
		item.sety(y);
		item.put("persistent", 1);
		zone.add(item);
		return item;
	}
}