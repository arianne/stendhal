package games.stendhal.server.entity.player;

import java.util.Arrays;
import java.util.List;

import games.stendhal.common.MathHelper;

/**
 * recording of killings.
 *
 * @author hendrik
 */
class KillRecording {
	private static final String KILL_SLOT_NAME = "!kills";
	private static final String PREFIX_SHARED = "shared.";
	private static final String PREFIX_SOLO = "solo.";
	
	private final Player player;
	
	public KillRecording(final Player player) {
		this.player = player;
	}

	/**
	 * Checks if the player has ever killed a creature with the given name
	 * without the help of any other player.
	 * 
	 * @param name of the creature to check.
	 * @return true iff this player has ever killed this creature on his own.
	 */
	public boolean hasKilledSolo(final String name) {
		final String count = player.getKeyedSlot(KILL_SLOT_NAME, PREFIX_SOLO + name);
		return MathHelper.parseIntDefault(count, 0) > 0;
	}

	/**
	 * Checks if the player has ever killed a creature, with or without the help
	 * of any other player.
	 * 
	 * @param  name of the creature to check.
	 * @return true iff this player has ever killed this creature on his own.
	 */
	public boolean hasKilled(final String name) {
		final String count = player.getKeyedSlot(KILL_SLOT_NAME, PREFIX_SHARED + name);
		final boolean shared = MathHelper.parseIntDefault(count, 0) > 0;
		return shared || hasKilledSolo(name);
	}

	/**
	 * Stores in which way the player has killed a creature with the given name.
	 * 
	 * @param name of the killed creature.
	 * @param mode
	 *            either "solo", "shared", or null.
	 */
	private void setKill(final String name, final String mode) {
		final String key = mode + "." + name;
		final String count = player.getKeyedSlot(KILL_SLOT_NAME, key);
		final int oldValue = MathHelper.parseIntDefault(count, 0);
		player.setKeyedSlot(KILL_SLOT_NAME, key, Integer.toString(oldValue + 1));
	}

	/**
	 * Stores that the player has killed 'name' solo. Overwrites shared kills of
	 * 'name'
	 * @param name of the killed entity
	 * 
	 */
	public void setSoloKill(final String name) {
		setKill(name, "solo");
	}

	/**
	 * Stores that the player has killed 'name' with help of others. Does not
	 * overwrite solo kills of 'name'
	 * @param name of the killed entity 
	 */
	public void setSharedKill(final String name) {
		setKill(name, "shared");
	}

	/**
	 * Makes the game think that this player has never killed a creature with
	 * the given name. Use this for quests where the player should kill a
	 * creature of a specific type.
	 * 
	 * @param name
	 *            The name of the creature.
	 */
	public void removeKill(final String name) {
		player.setKeyedSlot(KILL_SLOT_NAME, PREFIX_SHARED + name, null);
		player.setKeyedSlot(KILL_SLOT_NAME, PREFIX_SOLO + name, null);
	}
	
	/**
	 * Return information about how much creatures with the given name player killed.
	 * 
	 * @param name of the killed creature.
	 * @param mode
	 *            either "solo", "shared", or null.
	 * @return number of killed creatures
	 */
	public int getKill(final String name, final String mode) {
		final String key = mode + "." + name;		
		final int kills = MathHelper.parseIntDefault(player.getKeyedSlot(KILL_SLOT_NAME, key),0);
		return(kills);
	}
	
	/**
	 * Return how much the player has killed 'name' solo. Overwrites shared kills of 'name'
	 * @param name of the killed entity
	 * @return number of killed creatures
	 */	
	public int getSoloKill(final String name) {
		return(getKill(name, "solo"));
	}
	
	/**
	 * Return how much the player has killed 'name' shared. 
	 * @param name of the killed entity
	 * @return number of killed creatures
	 */	
	public int getSharedKill(final String name) {
		return(getKill(name, "shared"));
	}
	
	/**
	 * return differences between stored in quest slot info about killed creatures
	 *    and number of killed creatures. 
	 * @param questSlot  - name of quest
	 * @param questIndex - index of quest's record 
	 * @param creature   - name of creature
	 * @return - difference in killed creatures
	 */
	public int getQuestKills(final String questSlot, final int questIndex, final String creature) {
		final List<String> content = Arrays.asList(player.getQuest(questSlot, questIndex).split(","));
		final int index = content.indexOf(creature);
		final int solo = new Integer(content.get(index+1));
		final int shared = new Integer(content.get(index+2));		
		return(solo+shared);
	}
}
