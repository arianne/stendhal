package games.stendhal.server.maps.semos.tavern.market;

import java.util.Map;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Offer;

public class ExamineOfferChatAction extends KnownOffersChatAction {
	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		if (sentence.hasError()) {
			npc.say("Sorry, I did not understand you. "
					+ sentence.getErrorString());
		} else if (sentence.getExpressions().iterator().next().toString().equals("examine")){
			handleSentence(player,sentence,npc);
		}
	}
	
	private void handleSentence(Player player, Sentence sentence, SpeakerNPC npc) {
		MarketManagerNPC manager = (MarketManagerNPC) npc;
		try {
			String offerNumber = getOfferNumberFromSentence(sentence).toString();
			Map<String,Offer> offerMap = manager.getOfferMap().get(player.getName());
			if (offerMap == null) {
				npc.say("Please take a look at the list of offers first.");
				return;
			}
			if(offerMap.containsKey(offerNumber)) {
				Offer o = offerMap.get(offerNumber);
				
				player.sendPrivateText(o.getItem().describe());
				return;
			}
			npc.say("Sorry, please choose a number from those I told you.");
		} catch (NumberFormatException e) {
			npc.say("Sorry, please say #accept #number");
		}
	}
}
