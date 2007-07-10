package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.StendhalRPWorld;

import java.util.Arrays;

/**
 * QUEST: Club of Thorns
 *
 * PARTICIPANTS:
 * - Orc Saman
 * 
 * STEPS:
 * - Orc Saman asks you to kill mountain orc chief in prison for revenge
 * - Go kill mountain orc chief in prison using key given by Saman to get in
 * - Return and you get Club of Thorns as reward
 * 
 * REWARD:
 * - 1000 XP
 * 
 * REPETITIONS:
 * - None.
 */
public class ClubOfThorns extends AbstractQuest {

	private void step_1() {
		SpeakerNPC npc = npcs.get("Orc Saman");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("club_thorns")) {
							engine.say("Make revenge! Kill de Mountain Orc Chief! unnerstand?");
						} else {
							engine.say("Saman has revenged! dis Good!");
							engine.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Take dat key. he in jail. Kill! Denn, say me #kill! Say me #kill!",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.addKarma(6.0);
						player.setQuest("club_thorns", "start");
						player.removeKill("mountain_orc_chief");
						Item key = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("kotoch_prison_key");				
						player.equip(key, true);	    	

					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Ugg! i want hooman make #task, kill!",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.addKarma(-6.0);
						player.setQuest("club_thorns", "rejected");
					}
				});

	}

	private void step_2() {
		// Go kill the mountain orc chief using key to get into prison.
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Orc Saman");

		// the player returns after having started the quest.
		// Saman checks if kill was made
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("kill", "Kill"),
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return player.hasQuest("club_thorns")
								&& player.getQuest("club_thorns").equals("start");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (player.hasKilled("mountain_orc_chief")) {
							engine.say("Revenge! Good! Take club of hooman blud.");
							player.addKarma(3.0);
							player.addXP(1000);
							Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("club_of_thorns");
							item.put("bound", player.getName());
							player.equip(item, true);	    	
							player.setQuest("club_thorns", "done");
							engine.setCurrentState(ConversationStates.ATTENDING);
						} else {
							engine.say("kill Mountain Orc Chief! Kotoch orcs nid revenge!");
						}
					}
				});
		

	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}
}
