package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Sets the current state of a quest and modifies the karma of the player.
 */
public class SetQuestAndModifyKarmaAction extends SpeakerNPC.ChatAction {

	private String questname;
	private String state;
	private double karmaDiff;

	/**
	 * Creates a new SetQuestAction.
	 * 
	 * @param questname
	 *            name of quest-slot to change
	 * @param state
	 *            new value
	 * @param karmaDiff
	 *            amount of karma to add (negative numbers allowed)
	 */
	public SetQuestAndModifyKarmaAction(String questname, String state,
			double karmaDiff) {
		this.questname = questname;
		this.state = state;
		this.karmaDiff = karmaDiff;
	}

	@Override
	public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
		player.setQuest(questname, state);
		player.addKarma(karmaDiff);
	}

	@Override
	public String toString() {
		return "SetQuestAndModifyKarma<" + questname + ",\"" + state + "\","
				+ karmaDiff + ">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(karmaDiff);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		result = PRIME * result
				+ ((questname == null) ? 0 : questname.hashCode());
		result = PRIME * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SetQuestAndModifyKarmaAction other = (SetQuestAndModifyKarmaAction) obj;
		if (Double.doubleToLongBits(karmaDiff) != Double.doubleToLongBits(other.karmaDiff)) {
			return false;
		}
		if (questname == null) {
			if (other.questname != null) {
				return false;
			}
		} else if (!questname.equals(other.questname)) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
		return true;
	}

}