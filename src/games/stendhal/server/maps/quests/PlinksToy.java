package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.PlantGrower;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import marauroa.common.game.IRPZone;

/**
 * QUEST: Plink's Toy
 *
 * PARTICIPANTS:
 * - Plink
 * - some wolves
 *
 * STEPS:
 * - Plink tells you that he got scared by some wolves and
	 ran away dropping his teddy.
 * - Find the teddy in the Park Of Wolves
 * - Bring it back to Plink
 *
 * REWARD:
 * - a smile
 * - 20 XP
 *
 * REPETITIONS:
 * - None.
 */
public class PlinksToy extends AbstractQuest {

	private static final String QUEST_SLOT = "plinks_toy";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	@Override
	public List<String> getHistory(Player player) {
		List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			if (player.isEquipped("teddy")) {
				res.add("FOUND_ITEM_WITHOUT_QUEST");
			}
			return res;
		}
		res.add("FIRST_CHAT");
		String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("QUEST_REJECTED");
			return res;
		}
		res.add("QUEST_ACCEPTED");
		if ((player.isEquipped("teddy")) || isCompleted(player)) {
			res.add("FOUND_ITEM");
		}
		if (isCompleted(player)) {
			res.add("DONE");
		}
		return res;
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Plink");

		npc.add(ConversationStates.IDLE,
			SpeakerNPC.GREETING_MESSAGES,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, SpeakerNPC engine) {
					return !player.isEquipped("teddy") && !player.isQuestCompleted(QUEST_SLOT);
				}
			},
			ConversationStates.QUEST_OFFERED,
			"**cry** Theres was a rudle of #wolves! *snief* I ran away dropping my #teddy! *snief* Please! Will you bring it back? Please!",
			null);

		npc.add(ConversationStates.QUEST_OFFERED,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("*snief* Thanks a lot. *smile*");
						player.setQuest(QUEST_SLOT, "start");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.QUEST_OFFERED,
				"*cry* Please! *snief*",
				null);

		List<String> wolf = Arrays.asList("wolf", "wolves");
		npc.add(ConversationStates.QUEST_OFFERED,
				wolf,
				null,
				ConversationStates.QUEST_OFFERED,
				"They live in the #park east of here. Wolves are dangerous.",
				null);
		
		npc.add(ConversationStates.QUEST_OFFERED,
				"park",
				null,
				ConversationStates.QUEST_OFFERED,
				"Mom told me not to go into the Park of Wolves. But i got lost during play. Please don't tell her.",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				"teddy",
				null,
				ConversationStates.QUEST_OFFERED,
				"He is my favorite toy. Will you bring him back?",
				null);
	}
	
	private void step_2() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("0_semos_plains_n"));
		PlantGrower plantGrower = new PlantGrower("teddy", 1500);
		zone.assignRPObjectID(plantGrower);
		plantGrower.setX(107);
		plantGrower.setY(84);
		plantGrower.setDescription("Plink lost his teddy here.");
		plantGrower.setToFullGrowth();
		zone.add(plantGrower);

		StendhalRPRuleProcessor.get().getPlantGrowers().add(plantGrower);
	}
	
	private void step_3() {
		SpeakerNPC npc = npcs.get("Plink");

		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return !player.hasQuest(QUEST_SLOT) && player.isEquipped("teddy");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.drop("teddy");
						engine.say("Oh, my teddy. *smile* Thanks a lot for bringing it back from that dangerous #Park of #Wolves where I dropped it. *smile*");
						player.addXP(10);
						player.setQuest(QUEST_SLOT, "done");
					}
				});

		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT) && !player.isQuestCompleted(QUEST_SLOT) && player.isEquipped("teddy");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.drop("teddy");
						engine.say("Oh, my teddy. *smile* Thanks a lot. *smile*");
						player.addXP(10);
						player.setQuest(QUEST_SLOT, "done");
					}
				});

		npc.add(ConversationStates.ATTENDING,
				"teddy",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return !player.isQuestCompleted(QUEST_SLOT) && !player.isEquipped("teddy");
					}
				},
				ConversationStates.ATTENDING,
				"I lost my teddy in the Park of Wolves east of here.",
				null);

		npc.add(ConversationStates.ATTENDING,
				"teddy",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.isQuestCompleted(QUEST_SLOT) && player.isEquipped("teddy");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("This is not my teddy. You brought my teddy back some time ago.");
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
