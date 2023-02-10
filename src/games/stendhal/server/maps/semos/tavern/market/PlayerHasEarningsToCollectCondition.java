/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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
