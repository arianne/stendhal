package games.stendhal.server.maps.magic.clothing_boutique;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.Pair;

public class OutfitLenderNPC implements ZoneConfigurator {
	
	// outfits to last for 4 hours (in turns)
	// TODO : change to 24 before release
	public static final int endurance = 4 * 12000;  
	
	private static HashMap<String, Pair<Outfit, Boolean>> outfitTypes = new HashMap<String, Pair<Outfit, Boolean>>();
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		initOutfits();
		buildBoutiqueArea(zone);
	}

	private void initOutfits() {
		// these outfits must be put on over existing outfit 
		// (what's null doesn't change that part of the outfit)	
		// so true means we put on over
		  Pair<Outfit, Boolean> JUMPSUIT = new Pair<Outfit, Boolean>(new Outfit(null, null, Integer.valueOf(83), null), true);
		  Pair<Outfit, Boolean> DUNGAREES = new Pair<Outfit, Boolean>(new Outfit(null, null, Integer.valueOf(84), null), true);
		  Pair<Outfit, Boolean> BLACK_DRESS = new Pair<Outfit, Boolean>(new Outfit(null, null,	Integer.valueOf(85), null), true);

		  Pair<Outfit, Boolean> GOWN = new Pair<Outfit, Boolean>(new Outfit(null, null, Integer.valueOf(82), null), true);
		  Pair<Outfit, Boolean> NOOB = new Pair<Outfit, Boolean>(new Outfit(null, null, Integer.valueOf(80), null), true);
		  Pair<Outfit, Boolean> BUNNY = new Pair<Outfit, Boolean>(new Outfit(Integer.valueOf(00), null, Integer.valueOf(81), Integer.valueOf(98)), true);
		  Pair<Outfit, Boolean> GLASSES = new Pair<Outfit, Boolean>(new Outfit(null, Integer.valueOf(99), null, null), true);
		  Pair<Outfit, Boolean> GLASSES_2 = new Pair<Outfit, Boolean>(new Outfit(null, Integer.valueOf(79), null, null), true);
		  Pair<Outfit, Boolean> HAT = new Pair<Outfit, Boolean>(new Outfit(Integer.valueOf(99), null, null, null), true);

		// these outfits must replace the current outfit (what's null simply isn't there)
		  Pair<Outfit, Boolean> HORSE = new Pair<Outfit, Boolean>(new Outfit(Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(97)), false);
		  Pair<Outfit, Boolean> GIRL_HORSE = new Pair<Outfit, Boolean>(new Outfit(Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(96)), false);
		  Pair<Outfit, Boolean> ALIEN = new Pair<Outfit, Boolean>(new Outfit(Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(95)), false);

		
		
			outfitTypes.put("jumpsuit", JUMPSUIT);
			outfitTypes.put("dungarees", DUNGAREES);
			outfitTypes.put("black dress", BLACK_DRESS);
			outfitTypes.put("gown", GOWN);
			outfitTypes.put("orange", NOOB);
			outfitTypes.put("bunny", BUNNY);
			outfitTypes.put("glasses", GLASSES);
			outfitTypes.put("other glasses", GLASSES_2);
			outfitTypes.put("hat", HAT);
			outfitTypes.put("horse", HORSE);
			outfitTypes.put("girl horse", GIRL_HORSE);
			outfitTypes.put("alien", ALIEN);
	}
		
	

	private void buildBoutiqueArea(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Liliana") {
			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(16, 5));
				nodes.add(new Node(16, 16));
				nodes.add(new Node(26, 16));
				nodes.add(new Node(26, 5));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				class SpecialOutfitChangerBehaviour extends OutfitChangerBehaviour {
					SpecialOutfitChangerBehaviour(Map<String, Integer> priceList, int endurance, String wearOffMessage) {
						super(priceList, endurance, wearOffMessage);
					}

					@Override
					public void putOnOutfit(Player player, String outfitType) {
						
						Pair<Outfit, Boolean> outfitPair = outfitTypes.get(outfitType);
						Outfit outfit = outfitPair.first();
						boolean type = outfitPair.second();
						if (type) {
							player.setOutfit(outfit.putOver(player.getOutfit()), true);
						} else {
							player.setOutfit(outfit, true);
						}
						if (endurance != NEVER_WEARS_OFF) {
							// restart the wear-off timer if the player was still wearing
							// another temporary outfit.
							SingletonRepository.getTurnNotifier().dontNotify(new OutwearClothes(player));
							// make the costume disappear after some time
							SingletonRepository.getTurnNotifier().notifyInTurns(endurance,
									new OutwearClothes(player));
						}
					}
				}
				Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("jumpsuit", 500);
				priceList.put("dungarees", 500);
				priceList.put("black dress", 500);
				priceList.put("gown", 750);
				priceList.put("orange", 500);
				priceList.put("bunny", 800);
				priceList.put("glasses", 400);
				priceList.put("other glasses", 400);
				priceList.put("hat", 400);
				priceList.put("horse", 1200);
				priceList.put("girl horse", 1200);
				priceList.put("alien", 1200);	
				addGreeting("Hi! How many I help you?");
				addQuest("I can't think of anything for you, sorry.");
				add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Just tell me if you want to #hire a #gown, #hire a #black #dress, #hire #glasses, #hire #other #glasses, #hire a #hat, #hire an #alien suit, #hire a #horse outfit, #hire a #girl #horse outfit, #hire a #jumpsuit, #hire #dungarees, #hire a #bunny #suit or #hire an #orange outfit.",
					new ExamineChatAction("outfits.png", "Outfits", "Price varies"));
				addJob("I work in this clothes boutique. It's no ordinary shop, we use magic to put our clients into fantastic outfits. Ask about the #offer.");
				addHelp("Our hired outfits wear off after some time, but you can always come back for more!");
				addGoodbye("Bye!");
				OutfitChangerBehaviour behaviour = new SpecialOutfitChangerBehaviour(priceList, endurance, "Your magical outfit has worn off.");
				new OutfitChangerAdder().addOutfitChanger(this, behaviour, "hire", false, false);
			}
		};

		npc.setEntityClass("slim_woman_npc");
		npc.setPosition(16, 5);
		npc.initHP(100);
		zone.add(npc);
	}
}

