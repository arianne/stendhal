package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.player.Player;

public class PhotographerSellChatAction implements ChatAction {
	private String questSlot;

	public PhotographerSellChatAction(String questSlot) {
		this.questSlot = questSlot;
	}

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {

		if (!player.isEquipped("money", 1000)) {
			npc.say("I am sorry, you do not have enough money");
			return;
		}

		player.drop("money", 1000);

		int i = MathHelper.parseInt(player.getQuest(questSlot, 0));

		String url = PhotographerNPC.generateUrl(player, i);
		String caption = player.getName() + PhotographerNPC.CAPTIONS[i];

		final Item item = SingletonRepository.getEntityManager().getItem("picture in wooden frame");
		item.setItemData(url + "\tPicture\t" + caption);
		item.setState(i);
		item.setDescription("You see a §'picture in a wooden frame'. It shows " + caption + ".");
		player.equipOrPutOnGround(item);

		player.setQuest(questSlot, 0, "done");
		player.setQuest(questSlot, 1, Long.toString(System.currentTimeMillis()));
		new IncrementQuestAction(questSlot, 2, 1).fire(player, sentence, npc);

		npc.say("Here is your §'picture in a wooden frame'.");
	}

}
