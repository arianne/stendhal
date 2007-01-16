package games.stendhal.server.maps.ados;

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.NPCOwnedChest;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.entity.spawner.CreatureRespawnPoint;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

public class AdosOutsideNorthWest {
	
	private NPCList npcs = NPCList.get();;
	private ShopList shops = ShopList.get();

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
					cryForHelp = "Katinka shouts: Help! " + Grammar.A_noun(killer.get("name")) + " is eating our " + Grammar.plural(this.get("name")) + ".";
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


	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();
		buildZooArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"0_ados_outside_nw")));
		buildZooSub1Area((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"-1_ados_outside_nw")));
		buildZooSub3Area((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"-3_ados_outside_nw")));
	}

	private void buildZooArea(StendhalRPZone zone) {
		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(53);
		sign.setY(48);
		sign.setText("Ados Wildlife Refuge\nHome for endangered animals");
		zone.add(sign);
		
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(67);
		portal.setY(24);
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
				addHelp("Can you keep a secret? Dr. Feelgood, our veterinarian, can sell you medicine that he doesn't need for the animals.");
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
						"Sorry, I'm only licensed to heal animals, not humans. (But... ssshh! I can make you an #offer.)");

				addJob("I'm the veterinarian.");
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
		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 65, 34, creature, 1);
		zone.addRespawnPoint(point);

		// 67, 29 bear
		point = new CreatureRespawnPoint(zone, 67, 29, creature, 1);
		zone.addRespawnPoint(point);
		
		// 67, 31 black_bear
		creature = new AdosAttackableCreature(manager.getCreature("black_bear"));
		point = new CreatureRespawnPoint(zone, 67, 31, creature, 1);
		zone.addRespawnPoint(point);

		// 67, 35 black_bear
		point = new CreatureRespawnPoint(zone, 67, 35, creature, 1);
		zone.addRespawnPoint(point);
	}

	private void buildZooSub1Area(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(4);
		portal.setY(23);
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

		Chest chest = new NPCOwnedChest(npc);
		zone.assignRPObjectID(chest);
		chest.set(17, 23);
		zone.add(chest);
	}

}
