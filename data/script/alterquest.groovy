import games.stendhal.server.entity.Player;

if (player != null) {
	Player target = rules.getPlayer(args[0]);
	if (target != null) {
		String oldQuestState = target.getQuest(args[1]);
		target.setQuest(args[1], args[2]);
		target.sendPrivateText("Admin " + player.getName() + " changed your state of the quest '" 
				+ args[1] + "' from '" + oldQuestState + "' to '" + args[2] + "'");
		player.sendPrivateText("Changed the state of quest '" 
				+ args[1] + "' from '" + oldQuestState + "' to '" + args[2] + "'");
	} else {
		player.sendPrivateText(args[0] + " is not logged in");
	}
}