package games.stendhal.server.maps.semos.tavern.marketChatActions;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.tavern.MarketManagerNPC;
import games.stendhal.server.maps.semos.tavern.TradeCenterZoneConfigurator;
import games.stendhal.server.trade.Market;
import games.stendhal.server.trade.Offer;

import java.util.HashMap;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * shows all current offers to the asking player
 * @author madmetzger
 *
 */
public class ShowOffersChatAction implements ChatAction {

	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		StringBuilder offersMessage = new StringBuilder();
		int counter = 0;
		RPSlot offersSlot = market.getSlot(Market.OFFERS_SLOT_NAME);
		MarketManagerNPC marketNPC = (MarketManagerNPC) npc;
		marketNPC.getOfferMap().put(player.getName(),new HashMap<String, Offer>());
		for (RPObject rpObject : offersSlot) {
			if(rpObject.getRPClass().getName().equals(Offer.OFFER_RPCLASS_NAME)) {
				Offer o = (Offer) rpObject;
				counter += 1;
				offersMessage.append(counter);
				offersMessage.append(" ");
				offersMessage.append(o.getItem().getName());
				offersMessage.append(" for ");
				offersMessage.append(o.getPrice());
				offersMessage.append(" money");
				offersMessage.append("\n");
				marketNPC.getOfferMap().get(player.getName()).put(Integer.valueOf(counter).toString(), o);
			}
		}
		if (counter > 0) {
			player.sendPrivateText(offersMessage.toString());
		}
		if (counter == 0) {
			player.sendPrivateText("There are currently no offers in the market.");
		}
	}
	
}
