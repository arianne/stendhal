package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.PlantGrower;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;
import marauroa.common.game.IRPZone;

/**
 * QUEST: Plink's Toy
 *
 * PARTICIPANTS:
 * - Plink
 * - some wolfs
 *
 * STEPS:
 * - Plink tells you that he got scared by some wolfs and
	 ran away dropping his teddy.
 * - Find the teddy in the Park Of Wolfs
 * - Bring it back to Plink
 *
 * REWARD:
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

	private void step_1() {
		SpeakerNPC npc = npcs.get("Plink");

		npc.add(ConversationStates.IDLE,
			SpeakerNPC.GREETING_MESSAGES,
			new StandardInteraction.QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"**cry** Theres was a rudle of #wolfs! *snief* I ran away dropping my #Teddy! *snief* Please! Will you bring it back? Please!",
			null);

		npc.add(ConversationStates.QUEST_OFFERED,
				"yes",
				null,
				ConversationStates.IDLE,
				null,
				new SpeakerNPC.ChatAction() {
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

		npc.add(ConversationStates.QUEST_OFFERED,
				"wolf",
				null,
				ConversationStates.QUEST_OFFERED,
				"They live in #park east of here. Wolfs are dangerous.",
				null);
		
		npc.add(ConversationStates.QUEST_OFFERED,
				"park",
				null,
				ConversationStates.QUEST_OFFERED,
				"Mom told me not to go into the Park Of Wolfs. But i got lost during play. Please don't tell her.",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				"teddy",
				null,
				ConversationStates.QUEST_OFFERED,
				"He is my favorite toy. Will you bring him back?",
				null);
	}
	
	private void step_2() {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID("0_semos_plains_n"));
		PlantGrower plantGrower = new PlantGrower("teddy", 1500);
		zone.assignRPObjectID(plantGrower);
		plantGrower.setx(107);
		plantGrower.sety(84);
		plantGrower.setDescription("Plink lost his teddy here.");
		zone.add(plantGrower);

		rules.getPlantGrowers().add(plantGrower);
	}
	
	private void step_3() {
		SpeakerNPC npc = npcs.get("Plink");

		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return !player.isQuestCompleted(QUEST_SLOT) && player.isEquipped("teddy");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.drop("teddy");
						engine.say("Thanks a lot. *smile*");
						player.setQuest(QUEST_SLOT, "done");
					}
				});

		npc.add(ConversationStates.ATTENDING,
				"teddy",
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return !player.isQuestCompleted(QUEST_SLOT) && !player.isEquipped("teddy");
					}
				},
				ConversationStates.ATTENDING,
				"I lost my teddy in the Park of Wolfs east of here.",
				null);

		npc.add(ConversationStates.ATTENDING,
				"teddy",
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.isQuestCompleted(QUEST_SLOT) && player.isEquipped("teddy");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("This is not my teddy. You brought my teddy back some time ago.");
					}
				});
	}

	@Override
	public void addToWorld(StendhalRPWorld world, StendhalRPRuleProcessor rules) {
		super.addToWorld(world, rules);

		step_1();
		step_2();
		step_3();
	}

}
