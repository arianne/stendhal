package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Has 'delay' time passed since the quest was last done?
 * If the quest slot isn't in the expected format, returns true
 */
public class TimePassedCondition implements ChatCondition {

	private final String questname;
	private final int delay;
	private final int arg;
	
	/**
	 * Creates a new TimePassedCondition .
	 * 
	 * @param questname
	 *            name of quest-slot
	 * @param delay
	 *            delay in minutes
	 * @param arg
	 *            position of the timestamp within the quest slot 'array'
	 */
	public TimePassedCondition(final String questname, final int delay, final int arg) {
		this.questname = questname;
		this.delay = delay;
		this.arg = arg;
	}
	/**
	 * Creates a new TimePassedCondition, where the timestamp alone is stored in the quest state.
	 * 
	 * @param questname
	 *            name of quest-slot
	 * @param delayInMinutes
	 *            delay in minutes
	 */
	public TimePassedCondition(final String questname, final int delayInMinutes) {
		this.questname = questname;
		this.delay = delayInMinutes;
		this.arg = 0;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		if (!player.hasQuest(questname)) {
			// never done quest so enough 'time' has passed
			return true;
		} else {
			final String[] tokens = player.getQuest(questname).split(";"); 
			final long delayInMilliseconds = delay * MathHelper.MILLISECONDS_IN_ONE_MINUTE; 
		    if (tokens.length - 1 < arg) {
                // old quest status, the split did not work, so we assume enough time is passed.
                return true;		
            }
            // timeRemaining is ''time when quest was done +
			// delay - time now''
			// if this is > 0, the time has not yet passed
            long questtime;            
            try {
			    questtime = Long.parseLong(tokens[arg]);
		    } catch (final NumberFormatException e) {
                // set to 0 if it was no Long, as if this quest was done at the beginning of time.
			    questtime = 0;
		    }
			final long timeRemaining = (questtime + delayInMilliseconds)
				- System.currentTimeMillis();
		return (timeRemaining <= 0L);
		}
	}

	@Override
	public String toString() {
		return delay + " minutes passed since last doing quest " + questname + "?";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				TimePassedCondition.class);
	}
}
