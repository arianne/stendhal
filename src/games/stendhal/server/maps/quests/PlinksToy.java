package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * QUEST: Plink's Toy
 * <p>
 * PARTICIPANTS: <ul><li> Plink <li> some wolves </ul>
 * 
 * STEPS: <ul><li> Plink tells you that he got scared by some wolves and ran away
 * dropping his teddy. <li> Find the teddy in the Park Of Wolves <li> Bring it back to
 * Plink </ul>
 * 
 * REWARD: <ul><li> a smile <li> 20 XP <li> 10 Karma </ul>
 * 
 * REPETITIONS: <ul><li> None. </ul>
 */
public class PlinksToy extends AbstractQuest {

	private static final String QUEST_SLOT = "plinks_toy";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			if (player.isEquipped("teddy")) {
				res.add("Plink commented about the bear I have with me");
			}
			return res;
		}
		res.add("I have met Plink");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I do not want to find Plink's toy bear");
			return res;
		}
		res.add("I do want to help Plink find his bear");
		if ((player.isEquipped("teddy")) || isCompleted(player)) {
			res.add("I have found Plink's toy bear");
		}
		if (isCompleted(player)) {
			res.add("I gave Plink his bear.");
		}
		return res;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Plink");

		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestNotCompletedCondition(QUEST_SLOT), new NotCondition(new PlayerHasItemWithHimCondition("teddy"))),
			ConversationStates.QUEST_OFFERED,
			"*cries* There were wolves in the #park! *sniff* I ran away, but I dropped my #teddy! Please will you get it for me? *sniff* Please?",
			null);

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.IDLE, "*sniff* Thanks a lot! *smile*",
			new SetQuestAction(QUEST_SLOT, "start"));

		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.QUEST_OFFERED,
			"*sniff* But... but... PLEASE! *cries*", null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("wolf", "wolves"),
			null,
			ConversationStates.QUEST_OFFERED,
			"They came in from the plains, and now they're hanging around the #park over to the east a little ways. I'm not allowed to go near them, they're dangerous.",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			"park",
			null,
			ConversationStates.QUEST_OFFERED,
			"My parents told me not to go to the park by myself, but I got lost when I was playing... Please don't tell them! Can you bring my #teddy back?",
			null);

		npc.add(ConversationStates.QUEST_OFFERED, "teddy", null,
			ConversationStates.QUEST_OFFERED,
			"Teddy is my favourite toy! Please will you bring him back?",
			null);
	}

	private void step_2() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_plains_n");
		final PassiveEntityRespawnPoint teddyRespawner = new PassiveEntityRespawnPoint("teddy", 1500);
		teddyRespawner.setPosition(107, 84);
		teddyRespawner.setDescription("There's a teddy-bear-shaped depression in the sand here.");
		zone.add(teddyRespawner);

		teddyRespawner.setToFullGrowth();
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Plink");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new OrCondition(new QuestNotStartedCondition(QUEST_SLOT), new QuestNotCompletedCondition(QUEST_SLOT)), new PlayerHasItemWithHimCondition("teddy")),
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					player.drop("teddy");
					npc.say("You found him! *hugs teddy* Thank you, thank you! *smile*");
					player.addXP(20);
					player.addKarma(10.0);
					player.setQuest(QUEST_SLOT, "done");
				}
			});

		npc.add(
			ConversationStates.ATTENDING,
			"teddy",
			new AndCondition(new QuestNotCompletedCondition(QUEST_SLOT), new NotCondition(new PlayerHasItemWithHimCondition("teddy"))),
			ConversationStates.ATTENDING,
			"I lost my teddy in the #park over east, where all those #wolves are hanging about.",
			null);

		npc.add(ConversationStates.ATTENDING, "teddy",
			new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new PlayerHasItemWithHimCondition("teddy")),
			ConversationStates.ATTENDING,
			"That's not my teddy, I've got him right here! Remember, you found him for me!",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Plinks toy",
				"Plink wants me to find his teddy.",
				false);
		step_1();
		step_2();
		step_3();
	}

	@Override
	public String getName() {
		return "PlinksToy";
	}

}
