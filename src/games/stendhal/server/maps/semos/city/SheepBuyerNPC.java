package games.stendhal.server.maps.semos.city;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.village.SheepSellerNPC;
import games.stendhal.server.util.Area;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.RPObject;

/**
 * A merchant (original name: Sato) who buys sheep from players.
 */
public class SheepBuyerNPC implements ZoneConfigurator {
	
	// The sheep pen where Sato moves what he buys
	/** Left X coordinate of the sheep pen */ 
	private static final int SHEEP_PEN_X = 39;
	/** Top Y coordinate of the sheep pen */
	private static final int SHEEP_PEN_Y = 24;
	/** Width of the sheep pen */
	private static final int SHEEP_PEN_WIDTH = 16;
	/** Height of the sheep pen */
	private static final int SHEEP_PEN_HEIGHT = 6;
	/** 
	 * The maximum number of sheep Sato keeps in his sheep pen.
	 */ 
	private static final int MAX_SHEEP_IN_PEN = 8;
	/** The area covering the sheep pen in Semos */
	private Area pen;

	public class SheepBuyerSpeakerNPC extends SpeakerNPC {

		public SheepBuyerSpeakerNPC(String name) {
			super(name);
			// HP needs to be > 0 for Sato to appear in the killer list for
			// the big bad wolf
			setBaseHP(100);
			setHP(100);
		}
		
		/**
		 * Get the area of the sheep pen where bought sheep 
		 * should be moved.
		 * 
		 * @param zone the zone of the sheep pen
		 * @return area of the sheep pen
		 */
		private Area getPen(StendhalRPZone zone) {
			if (pen == null) {
				Rectangle2D rect = new Rectangle2D.Double();
				rect.setRect(SHEEP_PEN_X, SHEEP_PEN_Y, SHEEP_PEN_WIDTH, SHEEP_PEN_HEIGHT);
				pen = new Area(zone, rect);
			}
			
			return pen;
		}
		
		/**
		 * Try to get rid of the big bad wolf that could have spawned in the pen,
		 * but miss it sometimes.
		 *  
		 * @param zone the zone to check
		 */
		private void killWolves(StendhalRPZone zone) {
			if (Rand.throwCoin() == 1) { 
				for (RPObject obj : zone) {
					if (obj instanceof Creature) {
						Creature wolf = (Creature) obj;
						
						if ("wolf".equals(wolf.get("subclass")) && getPen(zone).contains(wolf)) {
							wolf.onDamaged(this, wolf.getHP());
							return;
						}
					}
				}
			}
		}
		
		/**
		 * Get a list of the sheep in the pen.
		 * 
		 * @param zone the zone to check
		 * @return list of sheep in the pen
		 */
		private List<Sheep> sheepInPen(StendhalRPZone zone) {
			List<Sheep> sheep = new LinkedList<Sheep>();
			Area pen = getPen(zone);
			
			for (RPEntity entity : zone.getPlayerAndFriends()) {
				if (entity instanceof Sheep) {
					if (pen.contains(entity)) {
						sheep.add((Sheep) entity);
					}
				}
			}
			
			return sheep;
		}
		
		/**
		 * Move a bought sheep to the den if there's space, or remove it 
		 * from the zone otherwise. Remove old sheep if there'd be more
		 * than <code>MAX_SHEEP_IN_PEN</code> after the addition.
		 * 
		 * @param sheep the sheep to be moved
		 */
		public void moveSheep(Sheep sheep) {
			// The area of the sheed den.
			int x = Rand.randUniform(SHEEP_PEN_X, SHEEP_PEN_X + SHEEP_PEN_WIDTH - 1);
			int y = Rand.randUniform(SHEEP_PEN_Y, SHEEP_PEN_Y + SHEEP_PEN_HEIGHT - 1);
			StendhalRPZone zone = sheep.getZone();
			List<Sheep> oldSheep = sheepInPen(zone);
			
			killWolves(zone);
			/*
			 * Keep the amount of sheep reasonable. Sato's
			 * a business man and letting the sheep starve 
			 * would be bad for busines.
			 */
			if (oldSheep.size() >= MAX_SHEEP_IN_PEN) {
				// Sato sells the oldest sheep
				zone.remove(oldSheep.get(0));
			}
			
			if (!StendhalRPAction.placeat(zone, sheep, x, y)) {
				// there was no room for the sheep. Simply eat it
				sheep.getZone().remove(sheep);  
			}
		}
	}
	
	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		final SpeakerNPC npc = new SheepBuyerSpeakerNPC("Sato") {
			@Override
			public void createDialog() {
				addGreeting();
				addJob("I buy sheep here in Semos, then I send them up to Ados where they are exported.");
				addHelp("I purchase sheep, at what I think is a fairly reasonable price. Just say if you want to #sell #sheep, and I will set up a deal!");
				addGoodbye();
				addQuest("Hmm I need a present for my friend. Maybe you can ask Nishiya for a nice idea...");
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(40, 45));
				nodes.add(new Node(58, 45));
				nodes.add(new Node(58, 22));
				nodes.add(new Node(39, 22));
				nodes.add(new Node(39, 15));
				nodes.add(new Node(23, 15));
				nodes.add(new Node(23, 45));
				setPath(new FixedPath(nodes, true));
			}
		};
		final Map<String, Integer> buyitems = new HashMap<String, Integer>();
		buyitems.put("sheep", 150);
		new BuyerAdder().addBuyer(npc, new SheepBuyerBehaviour(buyitems), true);
		npc.setPosition(40, 45);
		npc.setEntityClass("buyernpc");
		npc.setDescription("You see Sato. He loves sheep.");
		npc.setSounds(Arrays.asList("hiccup-1", "sneeze-1"));
		zone.add(npc);
	}

	private static class SheepBuyerBehaviour extends BuyerBehaviour {
		SheepBuyerBehaviour(final Map<String, Integer> items) {
			super(items);
		}

		private int getValue(ItemParserResult res, final Sheep sheep) {
			return Math.round(getUnitPrice(res.getChosenItemName()) * ((float) sheep.getWeight() / (float) Sheep.MAX_WEIGHT));
		}

		@Override
		public int getCharge(ItemParserResult res, final Player player) {
			if (player.hasSheep()) {
				final Sheep sheep = player.getSheep();
				return getValue(res, sheep);
			} else {
				// npc's answer was moved to BuyerAdder. 
				return 0;
			}
		}

		@Override
		public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser seller, final Player player) {
			// res.getAmount() is currently ignored.

			final Sheep sheep = player.getSheep();

			if (sheep != null) {
				if (seller.getEntity().squaredDistance(sheep) > 5 * 5) {
					seller.say("I can't see that sheep from here! Bring it over so I can assess it properly.");
				} else if (getValue(res, sheep) < SheepSellerNPC.BUYING_PRICE) {
					// prevent newbies from selling their sheep too early
					seller.say("Nah, that sheep looks too skinny. Feed it with red berries, and come back when it has become fatter.");
				} else {
					seller.say("Thanks! Here is your money.");
					payPlayer(res, player);
					player.removeSheep(sheep);

					player.notifyWorldAboutChanges();
					if(seller.getEntity() instanceof SheepBuyerSpeakerNPC) {
						((SheepBuyerSpeakerNPC)seller.getEntity()).moveSheep(sheep);
					} else {
						// only to prevent that an error occurs and the sheep does not disappear
						sheep.getZone().remove(sheep);
					}

					return true;
				}
			} else {
				seller.say("You don't have any sheep, " + player.getTitle() + "! What are you trying to pull?");
			}

			return false;
		}
	}
}