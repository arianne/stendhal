package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;


import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

/**
 * QUEST: The Ring Maker
 * 
 * PARTICIPANTS:
 * <ul>
 *  <li>Ognir, who works in the weaponshop in Fado
 * </ul>
 * 
 * STEPS:
 * <ul>
 *  <li>If you go to Ognir with a broken emerald ring he offers to fix it </li>
 *  <li>Bring him the money he wants (a lot) and gold to fix the ring.</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 *  <li>Fixed Ring</li>
 *  <li>500 XP</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 *  <li>Anytime you need it</li>
 * </ul>
 */
public class RingMaker extends AbstractQuest {

	private static final int REQUIRED_GOLD = 2;

	private static final int REQUIRED_MONEY = 100000;
	
	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "fix_emerald_ring";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void FixRingStep() {
		SpeakerNPC npc = npcs.get("Ognir");

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
		        new SpeakerNPC.ChatCondition() {
			        @Override
			        public boolean fire(Player player, String text, SpeakerNPC npc) {
				        return player.isEquipped("emerald_ring"); 
			        }
		        },
		        ConversationStates.QUEST_ITEM_BROUGHT,
		        null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
			        	Item emeraldRing=player.getFirstEquipped("emerald_ring");
			        	if(emeraldRing!=null && emeraldRing.getInt("amount")>0){
			        		// ring is not broken so he just lets player know where it can be fixed
					        npc.say("Hello. That's a rare emerald on your ring. If it gets broken, come to me to fix it.");
					        npc.setCurrentState(ConversationStates.ATTENDING);
				        } else {
				        	// notices ring is broken
					        npc.say("What a pity, your emerald ring is broken. I can fix it, for a #price.");
					        player.setQuest(QUEST_SLOT, "start");
				        }
			        }
		        });
     // player not wearing emerald ring
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
		        new SpeakerNPC.ChatCondition() {
			        @Override
			        public boolean fire(Player player, String text, SpeakerNPC npc) {
				        return !player.isEquipped("emerald_ring")&& (!player.hasQuest(QUEST_SLOT)||player.isQuestCompleted(QUEST_SLOT));
			        }
		        },
		        ConversationStates.ATTENDING,
		        null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
				        npc.say("Hi, can I help you?");
			        }
		        });

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
		        new SpeakerNPC.ChatCondition() {
			        @Override
			        public boolean fire(Player player, String text, SpeakerNPC npc) {
				        return player.hasQuest(QUEST_SLOT)
				                && player.getQuest(QUEST_SLOT).startsWith("forging;");
			        }
		        },
		        ConversationStates.IDLE,
		        null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
				        String[] tokens = player.getQuest(QUEST_SLOT).split(";");
				        long delay = REQUIRED_MINUTES * 60 * 1000; // minutes -> milliseconds
				        long timeRemaining = (Long.parseLong(tokens[1]) + delay)
				                - System.currentTimeMillis();
				        if (timeRemaining > 0L) {
					        npc.say("I haven't finished fixing your ring. Please check back in "
			                        + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
			                        + ".");
					        return;
				        }
				        npc.say("I'm pleased to say, your ring is fixed! It's good as new now.");
				        player.addXP(500);
				        Item emeraldRing = StendhalRPWorld.get().getRuleManager()
				                .getEntityManager().getItem("emerald_ring");
				        emeraldRing.put("bound", player.getName());
				        player.equip(emeraldRing, true);
				        player.setQuest(QUEST_SLOT, "done");
				        player.notifyWorldAboutChanges();
			        }
		        });

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
                "price",
                null,
                ConversationStates.QUEST_ITEM_BROUGHT,
                "The charge for a new emerald is " + REQUIRED_MONEY + " money, and I need " + REQUIRED_GOLD + " gold bars to fix the ring. Do you want to pay now?",
                null);
		
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES,
                null,
                ConversationStates.QUEST_ITEM_BROUGHT, null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
                if ((player.isEquipped("gold_bar", REQUIRED_GOLD)) && (player.isEquipped("money", REQUIRED_MONEY))){
                	player.drop("gold_bar", REQUIRED_GOLD);
                	player.drop("money", REQUIRED_MONEY);
                	player.drop("emerald_ring");
                	npc.say("Okay, that's all I need to fix the ring. Come back in "
			        		+ REQUIRED_MINUTES + " minutes and it will be ready");
		        	player.setQuest(QUEST_SLOT, "forging;" + System.currentTimeMillis());
		        	npc.setCurrentState(ConversationStates.IDLE);
		        } else {
		        	npc.say("Come back when you have both the money and the gold.");
				/* set quest slot to done until he decides he wants to pay
				this is incase player comes back without the ring and
				wants to talk to him about something else */
				player.setQuest(QUEST_SLOT, "done");
		        	npc.setCurrentState(ConversationStates.ATTENDING);
		        }
			        }
		        });
		
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.NO_MESSAGES,
                null,
                ConversationStates.ATTENDING,
                "No problem, just come back when you have both the money and the gold.",
				/* set quest slot to done until he decides he wants to pay
				this is incase player comes back without the ring and
				wants to talk to him about something else */
		player.setQuest(QUEST_SLOT, "done");
                null);
		
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		FixRingStep();


	}

	@Override
	public List<String> getHistory(Player player) {
		List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		String questState = player.getQuest(QUEST_SLOT);
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("QUEST_ACCEPTED");
		}
		if (player.getQuest(QUEST_SLOT).startsWith("forging;")) {
			res.add("FORGING");
		}
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}
}
