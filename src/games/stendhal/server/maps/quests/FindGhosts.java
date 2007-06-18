package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

/**
 * QUEST: Find Ghosts
 *
 * PARTICIPANTS:
 * - Carena
 * 
 * STEPS:
 * - Carena asks you to find the 4 other spirits on Fauimoni
 * - You go find them and remember their names
 * - You return and say the names
 * - Carena checks you have met them, then gives reward
 * - Note: you can meet the ghosts before you started the quest with her
 * 
 * REWARD:
 * - base HP bonus of 50
 * - 5000 XP
 * 
 * REPETITIONS:
 * - None.
 */
public class FindGhosts extends AbstractQuest {

   	private static final List<String> NEEDED_SPIRITS = Arrays.asList("Mary", "Ben", "Zak", "Goran");
   	
   	@Override
   	public boolean isCompleted(Player player) {
		if (!player.hasQuest("find_ghosts")) {
			return false;
		}
		
		return missingNames(player).size() == 0;
	}
	
	private List<String> missingNames(Player player) {
	    if (!player.hasQuest("find_ghosts")) {
			return NEEDED_SPIRITS;
		}
		/* the format of the list quest slot is
		   "looking;name;name;...:said;name;name;..." */
		String npcDoneText = player.getQuest("find_ghosts");
		String[] doneAndFound = npcDoneText.split(":");
		String[] done = doneAndFound[1].split(";");
		
		List<String> doneList = Arrays.asList(done);
		List<String> result = new LinkedList<String>();
		for (String name : NEEDED_SPIRITS) {
	        if (!doneList.contains(name)) 
		    	result.add(name);
	    }
	    
	    return result;
	}

	private void askingStep() {
		SpeakerNPC npc = npcs.get("Carena");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.hasQuest("find_ghosts")) {
							engine.say("I feel so lonely. I only ever see creatures and alive people. If I knew about #spirits like me, I would feel better.");
						} 
						else if (!player.isQuestCompleted("find_ghosts")){
							engine.say("I want help to find other spirits like me. Please find them, then come back and tell me their names.");
							engine.setCurrentState(ConversationStates.ATTENDING);
						}
						else {
							engine.say("Thank you! I feel better now that I know the names of other spirits on Fauimoni.");
							engine.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"That's lovely of you. Good luck searching for them.",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.addKarma(5.0);
						player.setQuest("find_ghosts", "looking:said");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Oh. Never mind. Perhaps since I'm only a ghost I couldn't offer you much reward anyway.",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.addKarma(-25.0);
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				Arrays.asList("spirits", "spirit"),
				null,
				ConversationStates.QUEST_OFFERED,
				"I sense that there are 4 other spirits, but if only I knew their names I could contact them. Will you find them, then come back and tell me their names?",
				null);
	}

	private void findingStep() {
// see the separate GhostNPC classes for what happens when a player finds a ghost (with or without quest slot defined)
	}

	private void tellingStep() {

		SpeakerNPC npc = npcs.get("Carena");

		// the player returns to Carena after having started the quest, or found some ghosts.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return player.hasQuest("find_ghosts")
								&& player.getQuest("find_ghosts").startsWith("looking");
					}
				},
			    ConversationStates.QUESTION_1,
				"If you found any #spirits, please tell me their name.",
				null);
				
				
	    for (String name : NEEDED_SPIRITS) {
	        npc.add(ConversationStates.QUESTION_1,
		        name,
		        null,
		        ConversationStates.QUESTION_1,
		        null,
		        new SpeakerNPC.ChatAction() {
		    	    @Override
		    	    public void fire(Player player, String text, SpeakerNPC npc) {
		    	        List<String> missing = missingNames(player);
                        
                        String npcQuestText = player.getQuest("find_ghosts");
			    		String[] npcDoneText = npcQuestText.split(":");
			    		List<String> looking = Arrays.asList(npcDoneText[0].split(";"));
			    		List<String> said = Arrays.asList(npcDoneText[1].split(";"));
			    		String reply ="";
			    		
			    		if (missing.contains(text) && looking.contains(text)
			    		    && !said.contains(text)) {
			    		    // we haven't said the name yet so we add it to the list
			    		    player.setQuest("find_ghosts", npcDoneText[0] + ":"
			    		                    +npcDoneText[1]+ ";" + text);
			    		    reply = "Thank you.";
			    		}
			    		else if (!looking.contains(text)) {
			    		    // we have said it was a valid name but haven't met them
			    		    reply = "I don't believe you've spoken with any spirit of that name.";
			    		}
			    		else if (!missing.contains(text) && said.contains(text)) {
			    		    // we have said the name so we are stupid!
			    		    reply = "You've told me that name already, thanks.";
			    		}
			    		
			    		// we may have changed the missing list
			    		missing = missingNames(player);
			    		
		    		    if (missing.size() > 0) {
		    		    	reply += " If you met any other spirits, please tell me their name.";
		    			    npc.say(reply);
		    		    } else {
		    			    player.setBaseHP(50+player.getBaseHP());
		    			    player.heal(50, true);
		    			    player.addXP(5000);
		    			    reply += " Now that I know those 4 names, perhaps I can even reach the spirits with my mind. I can't give you anything of material value, but I have given you a boost to your basic wellbeing, which will last forever. May you live long, and prosper.";
		    			    npc.say(reply);
		    			    player.setQuest("find_ghosts", "done");
		    			    player.notifyWorldAboutChanges();
		    			    npc.setCurrentState(ConversationStates.ATTENDING);
		                }
		            }
		        });
	    }
	    
	    npc.add(ConversationStates.QUESTION_1,
		    	"no",
				null,
				ConversationStates.IDLE,
				"No problem, come back later.",
				null);

	   //player says something which isn't in the needed spirits list.
	    npc.add(ConversationStates.QUESTION_1, "",
		    new SpeakerNPC.ChatCondition() {
		        @Override
		    	public boolean fire(Player player, String text, SpeakerNPC npc) {
		        	return !NEEDED_SPIRITS.contains(text);
		        }
		    },
		    ConversationStates.QUESTION_1, "Sorry, I don't understand you. What name are you trying to say?", null);

	    npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("spirits", "spirit"),
				null,
				ConversationStates.QUESTION_1,
				"I seek to know more about other spirits who are dead but stalk the earthly world as ghosts. Please tell me any names you know.",
				null);
				
		// the player goes to Carena and says hi, and has no quest or is completed.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return !player.hasQuest("find_ghosts")||player.isQuestCompleted("find_ghosts");
					}
				},
			    ConversationStates.ATTENDING,
				"Wooouhhhhhh!",
				null);				
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		askingStep();
		findingStep();
		tellingStep();
	}
}
