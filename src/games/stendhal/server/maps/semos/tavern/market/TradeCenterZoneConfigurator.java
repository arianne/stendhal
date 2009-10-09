package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.trade.Market;
import games.stendhal.server.trade.Offer;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
/**
 * adds a market to a zone
 * 
 * @author madmetzger
 *
 */
public class TradeCenterZoneConfigurator implements ZoneConfigurator {

	private static final String TRADE_ADVISOR_NAME = "Harold";
	private static final int COORDINATE_Y = 13;
	private static final int COORDINATE_X = 10;

	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		addShopToZone(zone);
		buildTradeCenterAdvisor(zone);
	}

	private void addShopToZone(StendhalRPZone zone) {
		if (!zoneContainsAShop(zone)) {
			Market shop = Market.createShop();
			shop.setVisibility(0);
			zone.add(shop, false);
		}
	}

	private boolean zoneContainsAShop(StendhalRPZone zone) {
		for (RPObject rpObject : zone) {
			if (rpObject.getRPClass().getName().equals(Market.MARKET_RPCLASS_NAME)) {
				return true;
			}
		}
		return false;
	}

	private void buildTradeCenterAdvisor(StendhalRPZone zone) {
		SpeakerNPC speaker = new MarketManagerNPC(TRADE_ADVISOR_NAME);
		speaker.setPosition(COORDINATE_X,COORDINATE_Y);
		speaker.setEntityClass("tradecenteradvisornpc");
		speaker.setOutfit(new Outfit(5, 1, 34, 1));
		speaker.initHP(100);
		zone.add(speaker);
	}
	
	public static Market getShopFromZone(StendhalRPZone zone) {
		for (RPObject rpObject : zone) {
			if(rpObject.getRPClass().getName().equals(Market.MARKET_RPCLASS_NAME)) {
				return (Market) rpObject;
			}
		}
		return null;
	}

}