package games.stendhal.client.actions;

import games.stendhal.client.StendhalUI;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * Display command usage. Eventually replace this with
 * ChatCommand.usage("script", true).
 */
class GMHelpScriptAction implements SlashAction {

	/**
	 * Execute a chat command.
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if was handled.
	 */
	public boolean execute(final String[] params, final String remainder) {
		final String[] lines = {
			    "All scripts are ran using: /script scriptname [params]. After running a script you can remove any traces of it with /script -unload scriptname, this would remove any summoned creatures, for example. It's good practise to do this every time you are finished using a script.",
				"#/script #AdminMaker.class : For test servers only, summons an adminmaker to aid testing.",
				"#/script #AdminSign.class #zone #x #y #text : Makes an AdminSign in zone at (x,y) with text. To put it next to you do /script AdminSign.class - - - text.",
				"#/script #DeepInspect.class #player : Deep inspects a player and all his/her items.",
				"#/script #DropPlayerItems.class #player #[amount] #item : Drop the specified amount of items from the player if they are equipped in the bag or body.",
				"#/script #EntitySearch.class #nonrespawn : Shows the locations of all creatures that don't respawn, for example creatures that were summoned by a GM, deathmatch creatures, etc.",
				"#/script #FixDM.class #player : sets a player's DeathMatch slot to victory status.",
				"#/script #ListNPCs.class : lists all npcs and their position.",
				"#/script #LogoutPlayer.class #player : kicks a player from the game.",
				"#/script #NPCShout.class #npc #text : NPC shouts text.",
                "#/script #NPCShoutZone.class #npc #zone #text : NPC shouts text to players in given zone. Use - in place of zone to make it your current zone.",
				"#/script #Plague.class #1 #creature : summon a plague of raid creatures around you.",
				"#/script #WhereWho.class : Lists where all the online players are",
				"#/script #Maria.class : Summons Maria, who sells food&drinks. Don't forget to -unload her after you're done.",
				"#/script #ServerReset.class : use only in a real emergency to shut down server. If possible please warn the players to logout and give them some time. It kills the server the hard way.",
				
		};

		for (final String line : lines) {
			StendhalUI.get().addEventLine(new HeaderLessEventLine(line, NotificationType.CLIENT));
		}

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 0;
	}
}
