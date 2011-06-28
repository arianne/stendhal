package games.stendhal.server.entity.npc.action;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.behaviour.impl.RepairerBehaviour;
import games.stendhal.server.entity.player.Player;
/**
 * Behaviour action for repairing npcs
 * 
 * @author madmetzger
 */
public class RepairingBehaviourAction extends AbstractBehaviourAction<RepairerBehaviour>{
	
	public RepairingBehaviourAction(RepairerBehaviour repairerBehaviour) {
		super(repairerBehaviour, "repair", "repair");
	}

	@Override
	public void fireRequestOK(ItemParserResult res, Player player,
			Sentence sentence, EventRaiser npc) {
		if (behaviour.getItemNames().contains(res.getChosenItemName())) {
			int price = behaviour.getPrice(res.getChosenItemName(), player);
			if(player.drop("money", price)) {
				behaviour.transactAgreedDeal(res, npc, player);
			} else {
				npc.say("You cannot afford to repair your "+res.getChosenItemName());
			}
		} else {
			npc.say("I am sorry, but I am not able to repair your " + res.getChosenItemName() + ".");
		}
	}

	@Override
	public void fireRequestError(ItemParserResult res, Player player,
			Sentence sentence, EventRaiser npc) {
		npc.say("I am sorry, I could not understand you.");
	}

}
