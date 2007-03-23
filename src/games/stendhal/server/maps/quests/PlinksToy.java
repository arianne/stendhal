package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spawner.PassiveEntityRespawnPoint;
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

		npc
		        .add(
		                ConversationStates.IDLE,
		                ConversationPhrases.GREETING_MESSAGES,
		                new SpeakerNPC.ChatCondition() {

			                @Override
			                public boolean fire(Player player, String text, SpeakerNPC engine) {
				                return !player.isEquipped("teddy") && !player.isQuestCompleted(QUEST_SLOT);
			                }
		                },
		                ConversationStates.QUEST_OFFERED,
		                "*cries* There were wolves in the #park! *sniff* I ran away, but I dropped my #teddy! Please will you get it for me? *sniff* Please?",
		                null);

		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.YES_MESSAGES, null, ConversationStates.IDLE,
		        null, new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        engine.say("*sniff* Thanks a lot! *smile*");
				        player.setQuest(QUEST_SLOT, "start");
			        }
		        });

		npc.add(ConversationStates.QUEST_OFFERED, "no", null, ConversationStates.QUEST_OFFERED,
		        "*sniff* But... but... PLEASE! *cries*", null);

		List<String> wolf = Arrays.asList("wolf", "wolves");
		npc
		        .add(
		                ConversationStates.QUEST_OFFERED,
		                wolf,
		                null,
		                ConversationStates.QUEST_OFFERED,
		                "They came in from the plains, and now they're hanging around the #park over to the east a little ways. I'm not allowed to go near them, they're dangerous.",
		                null);

		npc
		        .add(
		                ConversationStates.QUEST_OFFERED,
		                "park",
		                null,
		                ConversationStates.QUEST_OFFERED,
		                "My parents told me not to go to the park by myself, but I got lost when I was playing... Please don't tell them!",
		                null);

		npc.add(ConversationStates.QUEST_OFFERED, "teddy", null, ConversationStates.QUEST_OFFERED,
		        "Teddy is my favourite toy! Please will you bring him back?", null);
	}

	private void step_2() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("0_semos_plains_n"));
		PassiveEntityRespawnPoint plantGrower = new PassiveEntityRespawnPoint("teddy", 1500);
		zone.assignRPObjectID(plantGrower);
		plantGrower.setX(107);
		plantGrower.setY(84);
		plantGrower.setDescription("There's a teddy-bear-shaped depression in the sand here.");
		plantGrower.setToFullGrowth();
		zone.add(plantGrower);

		StendhalRPRuleProcessor.get().getPlantGrowers().add(plantGrower);
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Plink");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return !player.hasQuest(QUEST_SLOT) && player.isEquipped("teddy");
			}
		}, ConversationStates.ATTENDING, null, new SpeakerNPC.ChatAction() {

			@Override
			public void fire(Player player, String text, SpeakerNPC engine) {
				player.drop("teddy");
				engine
				        .say("You found him! *hugs teddy* Thank you, thank you for bringing it back from that dangerous #Park of #Wolves where I dropped it. *smile*");
				player.addXP(10);
				player.setQuest(QUEST_SLOT, "done");
			}
		});

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return player.hasQuest(QUEST_SLOT) && !player.isQuestCompleted(QUEST_SLOT)
				        && player.isEquipped("teddy");
			}
		}, ConversationStates.ATTENDING, null, new SpeakerNPC.ChatAction() {

			@Override
			public void fire(Player player, String text, SpeakerNPC engine) {
				player.drop("teddy");
				engine.say("You found him! *hugs teddy* Thank you, thank you! *smile*");
				player.addXP(10);
				player.setQuest(QUEST_SLOT, "done");
			}
		});

		npc.add(ConversationStates.ATTENDING, "teddy", new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return !player.isQuestCompleted(QUEST_SLOT) && !player.isEquipped("teddy");
			}
		}, ConversationStates.ATTENDING,
		        "I lost my teddy in the #park over east, where all those #wolves are hanging about.", null);

		npc.add(ConversationStates.ATTENDING, "teddy", new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return player.isQuestCompleted(QUEST_SLOT) && player.isEquipped("teddy");
			}
		}, ConversationStates.ATTENDING, null, new SpeakerNPC.ChatAction() {

			@Override
			public void fire(Player player, String text, SpeakerNPC engine) {
				engine.say("That's not my teddy, I've got him right here! Remember, you found him for me?");
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
