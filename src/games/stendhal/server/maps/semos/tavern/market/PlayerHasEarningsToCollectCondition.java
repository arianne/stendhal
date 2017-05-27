package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
/**
 * Check if a player has collectable earnings in the market managed by the manager NPC
 *
 * @author madmetzger
 */
public class PlayerHasEarningsToCollectCondition implements ChatCondition {

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		Market market = TradeCenterZoneConfigurator.getShopFromZone(npc.getZone());
		if(market != null) {
			return market.hasEarningsFor(player);
		}
		return false;
	}

}
