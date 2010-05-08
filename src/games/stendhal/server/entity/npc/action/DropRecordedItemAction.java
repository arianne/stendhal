package games.stendhal.server.entity.npc.action;

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

/**
 * Drops the specified item.
 */
public class DropRecordedItemAction implements ChatAction {
	private static Logger logger = Logger.getLogger(DropRecordedItemAction.class);
	
	private final String questname;
	private final int index;
	
	/**
	 * Creates a new DropRecordedItemAction.
	 * 
	 * @param questname
	 *            name of quest to get the item and quantity from
	 */
	public DropRecordedItemAction(final String questname) {
		this.questname = questname;
		this.index = -1;
	}

	/**
	 * Creates a new DropRecordedItemAction.
	 * 
	 * @param questname
	 *            name of quest to get the item and quantity from
	 */
	public DropRecordedItemAction(final String questname, final int index) {
		this.questname = questname;
		this.index = index;
	}

	
	public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
		final String questSubString = player.getQuest(questname, index);
		final String[] elements = questSubString.split("=");
		String itemname = elements[0];
		int amount = 1;
		
		// some older quests may have stored an item name but not the amount
		// so we use the initial value of 1 if the string can't be split
	    if(elements.length > 1) {
			amount=MathHelper.parseIntDefault(elements[1], 1);
	    }
		final boolean res = player.drop(itemname, amount);
		if (!res) {
			logger.error("Cannot drop " + amount + " " + itemname,
					new Throwable());
		}
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "drop recorded item from questslot <" + questname + ">";
	}

}
