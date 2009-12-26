package games.stendhal.server.maps.semos.tavern.market;

import java.util.Set;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Earning;
import games.stendhal.server.entity.trade.Market;
/**
 * chat action to let a player fetch his earnings from the market
 * 
 * @author madmetzger
 *
 */
public class FetchEarningsChatAction implements ChatAction {

	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		if (sentence.hasError()) {
			npc.say("Sorry, I did not understand you. "
					+ sentence.getErrorString());
			npc.setCurrentState(ConversationStates.ATTENDING);
		} else if (sentence.getExpressions().iterator().next().toString().equals("fetch")){
			handleSentence(player,sentence,npc);
		}
	}

	private void handleSentence(Player player, Sentence sentence, SpeakerNPC npc) {
		Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		Set<Earning> earnings = market.fetchEarnings(player);
		int collectedSum = 0;
		for (Earning earning : earnings) {
			collectedSum += earning.getValue().intValue();
		}
		player.sendPrivateText("You collected "+Integer.valueOf(collectedSum).toString()+" money.");
		npc.say("Your earnings have been paid to you");
		npc.setCurrentState(ConversationStates.ATTENDING);
	}

}
