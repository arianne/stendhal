package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Is this quest not in this state?
 */
public class QuestNotInStateCondition extends SpeakerNPC.ChatCondition {

	private String questname;
	private String state;

	/**
	 * Creates a new QuestNotInStateCondition
	 *
	 * @param questname name of quest-slot
	 * @param state state
	 */
	public QuestNotInStateCondition(String questname, String state) {
		this.questname = questname;
		this.state = state;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return (!player.hasQuest(questname) || !player.getQuest(questname).equals(state));
	}


	@Override
	public String toString() {
		return "QuestNotInState<" + questname + "," + state + ">";
	}
}