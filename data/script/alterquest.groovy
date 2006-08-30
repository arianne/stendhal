if (player != null) {
	List players = rules.getPlayers();
	boolean found = false;
	for (aPlayer in players) {
		if (aPlayer.getName().equals(args[0])) {
			aPlayer.sendPrivateText("Admin " + player.getName() + " changed your state of the quest '" 
					+ args[1] + "' from '" + oldQuestState + "' to " + args[3]);
			aPlayer.setQuest(args[1], args[2]);
		}
	}
	
	if (found) {
		player.sendPrivateText("Changed the state of quest '" 
				+ args[1] + "' from '" + oldQuestState + "' to " + args[3]);
	} else {
		player.sendPrivateText(args[0] " is not logged in");
	}
}