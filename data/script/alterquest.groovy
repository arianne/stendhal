import games.stendhal.server.entity.Player;

if (player != null) {
	Player target = rules.getPlayer(args[0]);
	if (target != null) {
		String questName = args[1];
		String oldQuestState = target.getQuest(questName);
		String newQuestState = null;
		try {
			newQuestState = args[2];
		} catch (ArrayIndexOutOfBoundsException e) {
			// just leave it as null
		}
		target.setQuest(questName, newQuestState);
		target.sendPrivateText("Admin " + player.getName() + " changed your state of the quest '" 
				+ questName + "' from '" + oldQuestState + "' to '" + newQuestState + "'");
		player.sendPrivateText("Changed the state of quest '" 
				+ questName + "' from '" + oldQuestState + "' to '" + newQuestState + "'");
	} else {
		player.sendPrivateText(args[0] + " is not logged in");
	}
}