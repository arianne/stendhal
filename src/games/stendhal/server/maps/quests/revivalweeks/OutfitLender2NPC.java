package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.Pair;

public class OutfitLender2NPC {
	private StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
	
	// outfits to last for 48 hours normally 
	public static final int endurance = 48 * 60 * 20 * 10;

	// this constant is to vary the price. N=1 normally but could be a lot smaller on special occasions 
	private static final double N = 0.05;

	private static HashMap<String, Pair<Outfit, Boolean>> outfitTypes = new HashMap<String, Pair<Outfit, Boolean>>();

	public void addToWorld() {
		initOutfits();
		buildBoutiqueArea(zone);
	}

	private void initOutfits() {
		// these outfits must be put on over existing outfit 
		// (what's null doesn't change that part of the outfit)	
		// so true means we put on over
		  final Pair<Outfit, Boolean> GLASSES = new Pair<Outfit, Boolean>(new Outfit(null, Integer.valueOf(86), null, null), true);
		  final Pair<Outfit, Boolean> GOBLIN_FACE = new Pair<Outfit, Boolean>(new Outfit(null, Integer.valueOf(88), null, null), true);
		  final Pair<Outfit, Boolean> THING_FACE = new Pair<Outfit, Boolean>(new Outfit(null, Integer.valueOf(87), null, null), true);

		// these outfits must replace the current outfit (what's null simply isn't there)
		  final Pair<Outfit, Boolean> PURPLE_SLIME = new Pair<Outfit, Boolean>(new Outfit(Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(93)), false);
		  final Pair<Outfit, Boolean> GREEN_SLIME = new Pair<Outfit, Boolean>(new Outfit(Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(89)), false);
		  final Pair<Outfit, Boolean> RED_SLIME = new Pair<Outfit, Boolean>(new Outfit(Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(88)), false);
		  final Pair<Outfit, Boolean> BLUE_SLIME = new Pair<Outfit, Boolean>(new Outfit(Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(91)), false);
		  final Pair<Outfit, Boolean> GINGERBREAD_MAN = new Pair<Outfit, Boolean>(new Outfit(Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(92)), false);
		 
		
			outfitTypes.put("glasses", GLASSES);
			outfitTypes.put("goblin face", GOBLIN_FACE);
			outfitTypes.put("thing face", THING_FACE);
			outfitTypes.put("purple slime", PURPLE_SLIME);
			outfitTypes.put("green slime", GREEN_SLIME);
			outfitTypes.put("red slime", RED_SLIME);
			outfitTypes.put("blue slime", BLUE_SLIME);
			outfitTypes.put("gingerbread man", GINGERBREAD_MAN);
	}
		
	

	private void buildBoutiqueArea(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Saskia") {
			@Override
			protected void createPath() {
			    final List<Node> nodes = new LinkedList<Node>();
			    nodes.add(new Node(89, 106));
			    nodes.add(new Node(97, 106));
			    nodes.add(new Node(97, 113));
			    nodes.add(new Node(89, 113));
			    setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				class SpecialOutfitChangerBehaviour extends OutfitChangerBehaviour {
					SpecialOutfitChangerBehaviour(final Map<String, Integer> priceList, final int endurance, final String wearOffMessage) {
						super(priceList, endurance, wearOffMessage);
					}

					@Override
					public void putOnOutfit(final Player player, final String outfitType) {
						
						final Pair<Outfit, Boolean> outfitPair = outfitTypes.get(outfitType);
						final Outfit outfit = outfitPair.first();
						final boolean type = outfitPair.second();
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
					// override transact agreed deal to only make the player rest to a normal outfit if they want a put on over type.
					@Override
						public boolean transactAgreedDeal(final SpeakerNPC seller, final Player player) {
						final String outfitType = chosenItemName;
						final Pair<Outfit, Boolean> outfitPair = outfitTypes.get(outfitType);
						final boolean type = outfitPair.second();
						if (type) {
							if (player.getOutfit().getBase() > 80
									&& player.getOutfit().getBase() < 99) {
								seller.say("You already have a magic outfit on which just wouldn't look good with another - could you please put yourself in something more conventional and ask again? Thanks!");
								return false;
							}
						}
						if (player.isEquipped("money", getCharge(seller, player))) {
							player.drop("money", getCharge(seller, player));
							putOnOutfit(player, outfitType);
							return true;
						} else {
							seller.say("Sorry, you don't have enough money!");
							return false;
						}
					}
					
					// These outfits are not on the usual OutfitChangerBehaviour's
					// list, so they need special care when looking for them
					@Override
					public boolean wearsOutfitFromHere(final Player player) {
						final Outfit currentOutfit = player.getOutfit();

						for (final Pair<Outfit, Boolean> possiblePair : outfitTypes.values()) {
							if (possiblePair.first().isPartOf(currentOutfit)) {
								return true;
							}
						}
						return false;
					}
				}
				final Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("glasses", (int) (N * 400));
				priceList.put("goblin face", (int) (N * 500));
				priceList.put("thing face", (int) (N * 500));
				priceList.put("slime", (int) (N * 3000));
				priceList.put("gingerbread man", (int) (N * 1200));
			    addGreeting("Hello, I hope you are enjoying looking around our gorgeous boutique.");
				addQuest("Just look fabulous!");
				add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Please tell me which outfit you would like, ask to #hire #glasses, #hire a #goblin #face, #hire a #thing #face, #hire a #slime outfit, or #hire a #gingerbread #man outfit.",
					new ExamineChatAction("outfits2.png", "Outfits", "Special offer."));
				addJob("I work with magic in a fun way! Ask about the #offer.");
				addHelp("I can cast a spell to dress you in a magical outfit. They wear off after some time. I hope I can #offer you something you like. If not Liliana in the Magic City also rents out from a different range.");
				addGoodbye("Bye!");
				final OutfitChangerBehaviour behaviour = new SpecialOutfitChangerBehaviour(priceList, endurance, "Your magical outfit has worn off.");
				new OutfitChangerAdder().addOutfitChanger(this, behaviour, "hire", false, false);
			}
		};

		npc.setEntityClass("wizardwomannpc");
		npc.setPosition(89, 106);
		npc.initHP(100);
		zone.add(npc);
	}
}

