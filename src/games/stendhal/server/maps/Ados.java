package games.stendhal.server.maps;

import games.stendhal.common.Direction;
import games.stendhal.server.RespawnPoint;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Portal;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

public class Ados implements IContent {
	//private StendhalRPWorld world;
	private ShopList shops;
	private NPCList npcs;
	private StendhalRPWorld world;
	
	private static class AdosAttackableCreature extends AttackableCreature {
		private static long lastShoutTime = 0;

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
		public void onDead(RPEntity who) {
			super.onDead(who);
			if (! (who instanceof Player)) {
				long currentTime = System.currentTimeMillis();
				if (lastShoutTime + 5*60*1000 < currentTime) {
					lastShoutTime = currentTime;
					String message = "Katinka shouts: Help! An " + who.get("name") + " is eating our " + this.get("name") + "s.";
					StendhalRPAction.shout(message.replace('_', ' '));
				}
			}
		}

		@Override
		public Creature getInstance() {
			return new AdosAttackableCreature(this);
		}
	}

	public Ados(StendhalRPWorld world) {
		this.npcs = NPCList.get();
		this.shops = ShopList.get();
		this.world = world;

		buildRockArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"0_ados_rock")));
		buildZooArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"0_ados_outside_nw")));
		buildZooSub1Area((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"-1_ados_outside_nw")));
		buildZooSub3Area((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"-3_ados_outside_nw")));
	}


	private void buildRockArea(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Balduin") {
			protected void createPath() {
				// NPC doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

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
		sign.setText("Ados Wildlife Refuge|Home for endangered animals");
		zone.add(sign);
		
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(67);
		portal.sety(24);
		portal.setNumber(0);
		portal.setDestination("-1_ados_outside_nw", 0);
		zone.addPortal(portal);
		
		SpeakerNPC npc = new SpeakerNPC("Katinka") {
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

			protected void createDialog() {
				//Behaviours.addHelp(this,
				//				   "...");
				
				addReply("heal",
						"Sorry, I'm licensed to heal animals only, not humans. But... ssshh! I want to make you an #offer.");

				addJob("I'm the veterinary.");
				addSeller(new SellerBehaviour(world, shops.get("healing")) {
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
		DefaultEntityManager manager = (DefaultEntityManager) world.getRuleManager().getEntityManager();
		Creature creature = new AdosAttackableCreature(manager.getCreature("bear"));
		RespawnPoint point = new RespawnPoint(65, 34, 2);
		point.set(zone, creature, 1);
		point.setRespawnTime(10*3); // TODO
		zone.addRespawnPoint(point);

		// 67, 29 bear
		point = new RespawnPoint(67, 29, 2);
		point.set(zone, creature, 1);
		point.setRespawnTime(creature.getRespawnTime());
		zone.addRespawnPoint(point);
		
		// 67, 31 black_bear
		creature = new AdosAttackableCreature(manager.getCreature("black_bear"));
		point = new RespawnPoint(67, 31, 2);
		point.set(zone, creature, 1);
		point.setRespawnTime(creature.getRespawnTime());
		zone.addRespawnPoint(point);

		// 67, 35 black_bear
		point = new RespawnPoint(67, 35, 2);
		point.set(zone, creature, 1);
		point.setRespawnTime(creature.getRespawnTime());
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
}
