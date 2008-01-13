package games.stendhal.server.entity.player;

/**
 * recording of killings.
 *
 * @author hendrik
 */
class KillRecording {
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
		String info = player.getKeyedSlot("!kills", name);

		if (info == null) {
			return false;
		}
		return "solo".equals(info);
	}

	/**
	 * Checks if the player has ever killed a creature, with or without the help
	 * of any other player.
	 * 
	 * @param  name of the creature to check.
	 * @return true iff this player has ever killed this creature on his own.
	 */
	public boolean hasKilled(String name) {
		return (player.getKeyedSlot("!kills", name) != null);
	}

	/**
	 * Checks in which way this player has killed the creature with the given
	 * name.
	 * 
	 * @param name of the creature to check.
	 * @return either "solo", "shared", or null.
	 */
	public String getKill(String name) {
		return player.getKeyedSlot("!kills", name);
	}

	/**
	 * Stores in which way the player has killed a creature with the given name.
	 * 
	 * @param name of the killed creature.
	 * @param mode
	 *            either "solo", "shared", or null.
	 */
	private void setKill(String name, String mode) {
		player.setKeyedSlot("!kills", name, mode);
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
		if (!hasKilledSolo(name)) {
			setKill(name, "shared");
		}
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
		player.setKeyedSlot("!kills", name, null);
	}
}
