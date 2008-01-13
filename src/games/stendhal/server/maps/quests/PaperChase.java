package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.StringTokenizer;

/**
 * A kind of paper chase.
 *
 * @author hendrik
 */
public class PaperChase extends AbstractQuest {
	private static final String QUEST_SLOT = "paper_chase";

	private String[] points = new String[] { "Carmen", "Monogenes",
			"Hayunn Naratha",
			// TODO: load groovy before quest are inited (or convert
			// kanmararn.groovy to java) "Henry", // TODO: ignore if groovy is
			// inactive
			"Margaret", "Balduin",
			// TODO: load groovy before quest are inited (or convert
			// deathmatch_entry.groovy to java) "Deathmatch Recruiter",
			// TODO: ignore if groovy is inactive
			"Katinka", "Haizen", "Bario", "Ceryl", "Nishiya", "Marcus",
			"Jynath", "Loretta", "Fidorea" };

	/**
	 * Handles all normal points in this paper chase (without the first and last.
	 * one)
	 */
	private class PaperChasePoint extends SpeakerNPC.ChatAction {
		private int idx;

		PaperChasePoint(int idx) {
			this.idx = idx;
		}

		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
			String state = points[idx];
			String next = points[idx + 1];
			String questState = player.getQuest(QUEST_SLOT);

			// player does not have this quest or finished it
			if ((questState == null) || (questState.indexOf(";") < 0)) {
				engine.say("Talk to Fidorea to start the paper chase.");
				return;
			}

			// analyze quest state
			StringTokenizer st = new StringTokenizer(questState, ";");
			String nextNPC = st.nextToken();
			String startTime = st.nextToken();

			// is the player supposed to speak to another NPC?
			if (!nextNPC.equals(state)) {
				engine.say("Sorry, you are suposed to talk to " + nextNPC + ".");
				return;
			}

			// send player to the next NPC and record it in quest state
			engine.say("OK, please talk to " + next + " now.");
			String newState = next + ";" + startTime;
			player.setQuest(QUEST_SLOT, newState);
		}

	}

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	/**
	 * Adds the task to the specified NPC. Note that the start and end of this
	 * quest have to be coded specially.
	 *
	 * @param idx
	 *            index of way point
	 */
	private void addTaskToNPC(int idx) {
		String state = points[idx];
		SpeakerNPC npc = npcs.get(state);
		npc.add(ConversationStates.ATTENDING, "paper", null,
				ConversationStates.ATTENDING, null, new PaperChasePoint(idx));
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		// TODO: add Fidorea to world introducing the quest
		// TODO: detect using of scrolls

		// add normal way points (without first and last)
		for (int i = 0; i < points.length - 1; i++) {
			addTaskToNPC(i);
		}

		// TODO: Fidorea doing the post processing of this quest (calc points
		// based on time and level)
		// TODO: store and read result (with server restart in mind)
		// TODO: create sign as Hall of Fame
	}
}
