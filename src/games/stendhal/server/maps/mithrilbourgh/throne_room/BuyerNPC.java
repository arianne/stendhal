package games.stendhal.server.maps.mithrilbourgh.throne_room;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * Builds an NPC to buy previously un bought mainio weapons.
 * He is the 
 *
 * @author kymara
 */
public class BuyerNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Despot Halb Errvl") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("I hope you have disturbed me for a good reason?");
				addReply(ConversationPhrases.YES_MESSAGES, "Well state what you want then!");
				addReply(ConversationPhrases.NO_MESSAGES, "Then get out of my sight before I feed you to the dragons!");
				addJob("Isn't it clear by my title...?");
				addReply("mainio", "My advisors tell me the word means 'excellent' in some foreign language. If it is so, my men must wear it! I do not think Diehelm Brui is equipping them well enough!");
				addHelp("My army must have the best items. #Offer me some of the rare #mainio armor I have heard tell of, and I will pay you handsomely.");
				//addQuest("The Mithrilbourgh Army and I are not in need of your services at present.");
				new BuyerAdder().add(this, new BuyerBehaviour(shops.get("buymainio")), true);
 				addGoodbye("Bye.");
			}
			
			@Override
			protected void onGoodbye(Player player) {
				setDirection(Direction.DOWN);
			}
			
		};
		npc.setDescription("You see an impatient man. He has a military air about him.");
		npc.setEntityClass("blacklordnpc");
		npc.setPosition(19, 4);
		npc.initHP(100);
		zone.add(npc);

	}
}
