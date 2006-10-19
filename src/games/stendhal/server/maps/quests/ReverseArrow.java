package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * A quest where the player has to invert an arrow build out of stones
 * by moving only up to 3 tokens.
 *
 * @author hendrik
 */
public class ReverseArrow extends AbstractQuest {

	private static final String QUEST_SLOT = "reverse_arrow";
	private static final int MAX_MOVES = 3;
	private SpeakerNPC npc = null;

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	public void setNPC(SpeakerNPC npc) {
		this.npc = npc;
	}

	private void step_1() {
		// TODO: create NPC
		// TODO: create zone with tokens
		// TODO: create door
	}

	public void onTokenMoved(Player player) {
		String questState = player.getQuest(QUEST_SLOT);
		int moveCount = MathHelper.parseInt_default(questState, MAX_MOVES);
		moveCount++;
		if (moveCount >= 3) {
			// TODO: check token position
			// TODO: teleport player out
			if (true) {
				player.setQuest(QUEST_SLOT, "true");
			} else {
				player.setQuest(QUEST_SLOT, "failed");
			}
			return;
		}
		npc.say("This was your " + Grammar.ordered(moveCount) + " move.");
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
	}
}
