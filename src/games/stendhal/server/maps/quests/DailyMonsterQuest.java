package games.stendhal.server.maps.quests;

import games.stendhal.common.Level;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * QUEST: Daily Monster Kill Quest
 *
 * PARTICIPANTS:
 * - Major
 * - some creatures
 *
 * STEPS:
 * - talk to Major to get a quest to kill one of a named creature class
 * - kill one creature of that class
 * - tell Major that you are done
 *
 * REWARD:
 * - xp
 *
 * REPETITIONS:
 * - once a day
 */
public class DailyMonsterQuest extends AbstractQuest {

	private static final String QUEST_SLOT = "daily";
	
	class DailyQuestAction extends SpeakerNPC.ChatAction {

		/** All creatures, sorted by level */
		List<Creature> sortedcreatures;

		public DailyQuestAction() {
			Collection<Creature> creatures = StendhalRPWorld.get().getRuleManager().getEntityManager().getCreatures();
			sortedcreatures = new LinkedList<Creature>();
			sortedcreatures.addAll(creatures);
			Collections.sort(sortedcreatures, new Comparator<Creature>() {
				public int compare(Creature o1, Creature o2) {
					return o1.getLevel() - o2.getLevel();
				}
				
			});
		}

		public void fire(Player player, String text, SpeakerNPC engine)	{
			String questInfo = player.getQuest("daily");
			String questKill = null;
			String questCount = null;
			String questLast = null;
			long delay = 60 * 60 * 24 * 1000; // Miliseconds in a day

			if(questInfo != null) {
				String[] tokens = (questInfo+";0;0;0").split(";");
				questKill = tokens[0];
				questLast = tokens[1];
				questCount = tokens[2];
			}
			if(questKill != null && !"done".equals(questKill)) {
				engine.say("You're already on a quest to slay a " + questKill + ". Say #complete if you're done with it!");
				return;
			}
			if(questLast != null && (new Date()).getTime() - new Long( questLast) < delay ) {
				engine.say("I can only give you a new quest once a day. Please check later.");
				return;
			}
			
			int current = 0;
			int start = 0;
			int level = player.getLevel();
			for (Creature creature : sortedcreatures) {
				if((start == 0) && creature.getLevel() > 0 && creature.getLevel() >= level - 5) {
					start=current;					
				}
				if(creature.getLevel() > level + 5) {
					current--;
					break;
				}
				current++;
			}
			if(start >= sortedcreatures.size() - 1) {
				start = sortedcreatures.size() - 2;				
			}
			if(start < 0) {
				start = 0;				
			}
			if(current == sortedcreatures.size()) {
				current--;
			}
			if(current>=start) {
				int result = start + new Random().nextInt(current - start + 1);
				String creatureName = sortedcreatures.get(result).getName();

				// don't ask level 0 players to kill a bat as this cannot be found
				// anywhere they have a chance to survive.
				if ("bat".equals(creatureName)) {
						creatureName = "rat";
				}
				engine.say("Semos is in need of help. Go kill a " + creatureName + " and say #complete, once you're done.");
				player.removeKill(creatureName);
				questLast = "" + (new Date()).getTime();
				player.setQuest("daily", creatureName + ";" + questLast + ";" + questCount);
				}
			else { // shouldn't happen
				engine.say("Thanks for asking, but there's nothing you can do for me now.");			
			}
		}
	}

	class DailyQuestCompleteAction extends SpeakerNPC.ChatAction {
		
		public void fire(Player player, String text, SpeakerNPC engine)	{
			String questInfo = player.getQuest("daily");
			String questKill = null;
			String questCount = null;
			String questLast = null;
			
			if(questInfo == null) {
				engine.say("I'm afraid I didn't send you on a #quest yet.");
				return;
			}
			String[] tokens = (questInfo + ";0;0").split(";");
			questKill = tokens[0];
			questLast = tokens[1];
			questCount = tokens[2];
			if(questCount.equals("null")) {
				questCount = "0";
			}
			if("done".equals(questKill)) {
				engine.say("You already completed the last quest I had given to you.");
				return;
			}
			if(player.hasKilled(questKill)) {
				int start = Level.getXP(player.getLevel());
				int next = Level.getXP(player.getLevel()+1);
				int reward = (next - start) / 5;
				if(player.getLevel() >= Level.maxLevel()) {
					reward = 0;
				}
				engine.say("Good work! Let me thank you in the name of the people of Semos!");
				player.addXP(reward);
				questCount = "" + (new Integer(questCount) + 1 );
				questLast = "" + (new Date()).getTime();
				player.setQuest("daily","done" + ";" + questLast + ";" + questCount);
			}
			else {
				engine.say("You didn't kill a " + questKill + " yet. Go and do it and say #complete only after you're done.");
			}
		}
	}
	

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Mayor");
		npc.add(ConversationStates.ATTENDING, Arrays.asList("quest", "task"), null, 
				ConversationStates.ATTENDING, null, new DailyQuestAction());
	}

	private void step_2() {
		// kill the monster
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Mayor");
		npc.add(ConversationStates.ATTENDING, Arrays.asList("complete", "done"), null, 
						ConversationStates.ATTENDING, null, new DailyQuestCompleteAction());
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}

}
