package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * Checks the records of kills.
 * Quest string should have in proper quest index string like "creature1,w,x,y,z,creature2,a,b,c,d,creature3,..."
 * Where creature1, creature2 - names of creatures to kill;
 *       w,x and a,b - number of creatures to kill, solo and shared;
 *       y,z and c,d - number of creatures killed by player before starting this quest, both solo and shared.
 * 
 * @author yoriy
 */
public class KilledForQuestCondition implements ChatCondition {
	private static Logger logger = Logger.getLogger(KilledForQuestCondition.class);
	private final String QUEST_SLOT;
	private final int questIndex;
	

	/**
	 * creates a new KilledForQuestCondition.
	 * 
	 * @param toKill
	 *            list of creatures which should be killed by the player
	 */
	public KilledForQuestCondition(String quest, int index) {
		this.QUEST_SLOT=quest;
		this.questIndex=index;
	}

	/**
	 * return true if player have killed proper creature numbers.
	 */
	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
		final String temp = player.getQuest(QUEST_SLOT, questIndex);
		final List<String> tokens = Arrays.asList(temp.split(","));
		// check for size - it should be able to divide by 5 without reminder.
		if((tokens.size() % 5)!=0) {
			logger.error("Wrong record in player's "+player.getName()+
					" quest slot ("+QUEST_SLOT+") : ["+player.getQuest(QUEST_SLOT)+"]");
			//npc.say("something wrong with you, i dont see how much monsters you killed.");
			return false;
		};
		
		for(int i=0; i<tokens.size()/5; i++) {
			final String creatureName=tokens.get(i);
			int toKillSolo;
			int toKillShared;
			int killedSolo;
			int killedShared;
			try {
				toKillSolo=Integer.parseInt(tokens.get(i+1));
				toKillShared=Integer.parseInt(tokens.get(i+2));
				killedSolo=Integer.parseInt(tokens.get(i+3));
				killedShared=Integer.parseInt(tokens.get(i+4));				
			} catch (NumberFormatException npe) {
				logger.error("NumberFormatException while parsing numbers in quest slot "+QUEST_SLOT+
						" of player "+player.getName()
						+" , creature " + i);
				return false;
			};
			final int diffSolo = player.getSoloKill(creatureName) - killedSolo - toKillSolo;
			final int diffShared = player.getSharedKill(creatureName) - killedShared - toKillShared;
			if((diffSolo<0)||(diffShared<0)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "KilledCondition";
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				KilledCondition.class);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
