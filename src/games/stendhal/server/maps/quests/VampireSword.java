package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: The Vampire Sword
 * 
 * PARTICIPANTS:
 * - Hogart, a retired master dwarf smith, forgotten below the dwarf mines in Orril.
 * 
 * STEPS:
 * - Hogart tells you the story of the Vampire Lord.
 * - He offers to forge a Vampire Sword for you if you bring him what it needs.
 * - You get some items from the Catacombs and kill the Vampire Lord.
 * - You get the iron needed in the usual way by collecting iron ore and casting in Semos. 
 * - Hogart forges the Vampire Sword for you
 * 
 * REWARD:
 * - Vampire Sword
 * - 5000 XP
 * 
 *
 * REPETITIONS:
 * - None.
 */
public class VampireSword extends AbstractQuest {
	
	private static final String QUEST_SLOT = "vs_quest";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Hogart");

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (!player.isQuestCompleted(QUEST_SLOT)) {
							engine.say("I can forge a powerful life stealing sword for you. You will need to go to the Catacombs below Semos Graveyard and fight the Vampire Lord. Are you interested?");
						} else {
							engine.say("What are you bothering me for now? You've got your sword, go and use it!");
							engine.setCurrentState(1);
						}
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Then you need this #talisman. Bring it back full of blood of the enemies you meet in the #Catacombs.",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						player.setQuest(QUEST_SLOT, "start");
					}
				});
		// give a player bound empty goblet here
		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Oh, well forget it then. You must have a better sword than I can forge, huh?",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						player.setQuest(QUEST_SLOT, "rejected");
					}
				});

		npc.add(ConversationStates.ATTENDING,
				"Catacombs",
				null,
				ConversationStates.ATTENDING,
				"The Catacombs of North Semos of the ancient #stories.",
				null);

		npc.add(ConversationStates.ATTENDING,
				"talisman",
				null,
				ConversationStates.ATTENDING,
				"You'll need an empty goblet to get into the #Catacombs. It's a powerful talisman. Go fill it with blood.",
				null);


	}

	private void step_2() {
		// Go to the catacombs, kill 7 vampirettes to get to the 3rd level, kill 7 killer bats and the vampire lord to get the required items to fill the goblet. Fill the goblet and come back. Phew!
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Hogart");

		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals("start");
					}
				},
				ConversationStates.QUEST_ITEM_BROUGHT,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
					    if (player.isEquipped("goblet")) { //and not iron?
							engine.say("You have battled hard to bring that goblet. I will use it to #forge the #vampire_sword");
						} else {
							engine.say("Did you lose your way? The Catacombs are in North Semos. Don't come back without a full goblet!");
							engine.setCurrentState(1);
						}
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"forge",			
			//	make sure the player isn't cheating by putting the goblet
			//	away and then saying "yes"  -- is this needed?
			//	new SpeakerNPC.ChatCondition() {
			//		@Override
			//		public boolean fire(Player player, String text, SpeakerNPC engine) {
			//			return player.isEquipped("goblet");
			//		}
			//	}, 
			null,
			ConversationStates.QUEST_ITEM_BROUGHT,
			"Bring me 10 #iron bars to forge the sword with.",
			null);

	npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"iron",			
		null,
			ConversationStates.QUEST_ITEM_BROUGHT,
		"You know, collect the iron ore lying around and get it cast!",
			null);
		
	// check is he equipped with 10 iron and with the goblet. or take the goblet earlier and check only iron.
	// if he is, do this:

// 		Item vampireSword = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("vampire_sword");
// 										vampireSword.put("bound", player.getName());
// 										player.equip(vampireSword, true);
// 										player.addXP(5000);
// 										engine.say("At last, you deserve the Vampire Sword.");
// 										player.setQuest(QUEST_SLOT, "done");
// 										player.notifyWorldAboutChanges();	
			


	}
// somewhere there needs to be him giving you back an empty goblet so you can get to dungeons later if you want.
	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}
}
