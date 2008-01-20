package games.stendhal.server.entity.player;

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
	
	private Player player;
	
	public KillRecording(Player player) {
		this.player = player;
	}

	/**
	 * Checks if the player has ever killed a creature with the given name
	 * without the help of any other player.
	 * 
	 * @param name of the creature to check.
	 * @return true iff this player has ever killed this creature on his own.
	 */
	public boolean hasKilledSolo(String name) {
		String count = player.getKeyedSlot(KILL_SLOT_NAME, PREFIX_SOLO + name);
		return MathHelper.parseIntDefault(count, 0) > 0;
	}

	/**
	 * Checks if the player has ever killed a creature, with or without the help
	 * of any other player.
	 * 
	 * @param  name of the creature to check.
	 * @return true iff this player has ever killed this creature on his own.
	 */
	public boolean hasKilled(String name) {
		String count = player.getKeyedSlot(KILL_SLOT_NAME, PREFIX_SHARED + name);
		boolean shared = MathHelper.parseIntDefault(count, 0) > 0;
		return shared || hasKilledSolo(name);
	}

	/**
	 * Stores in which way the player has killed a creature with the given name.
	 * 
	 * @param name of the killed creature.
	 * @param mode
	 *            either "solo", "shared", or null.
	 */
	private void setKill(String name, String mode) {
		String key = mode + "." + name;
		String count = player.getKeyedSlot(KILL_SLOT_NAME, key);
		int oldValue = MathHelper.parseIntDefault(count, 0);
		player.setKeyedSlot(KILL_SLOT_NAME, key, Integer.toString(oldValue + 1));
	}

	/**
	 * Stores that the player has killed 'name' solo. Overwrites shared kills of
	 * 'name'
	 * @param name of the killed entity
	 * 
	 */
	public void setSoloKill(String name) {
		setKill(name, "solo");
	}

	/**
	 * Stores that the player has killed 'name' with help of others. Does not
	 * overwrite solo kills of 'name'
	 * @param name of the killed entity 
	 */
	public void setSharedKill(String name) {
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
	public void removeKill(String name) {
		player.setKeyedSlot(KILL_SLOT_NAME, PREFIX_SHARED + name, null);
		player.setKeyedSlot(KILL_SLOT_NAME, PREFIX_SOLO + name, null);
	}
}
