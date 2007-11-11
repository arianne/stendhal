package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Sets the current state of a quest and modifies the karma of the player
 */
public class SetQuestAndModifyKarmaAction extends SpeakerNPC.ChatAction {

	private String questname;
	private String state;
	private double karmaDiff;

	/**
	 * Creates a new SetQuestAction
	 *
	 * @param questname name of quest-slot to change
	 * @param state new value
	 * @param karmaDiff amount of karma to add (negative numbers allowed)
	 */
	public SetQuestAndModifyKarmaAction(String questname, String state, double karmaDiff) {
		this.questname = questname;
		this.state = state;
		this.karmaDiff = karmaDiff;
	}

	@Override
	public void fire(Player player, String text, SpeakerNPC engine) {
		player.setQuest(questname, state);
		player.addKarma(karmaDiff);
	}

	@Override
	public String toString() {
		return "SetQuestAndModifyKarma<" + questname + ",\"" + state + "\"," + karmaDiff +">";
	}

}