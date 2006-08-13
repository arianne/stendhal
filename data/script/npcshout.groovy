import games.stendhal.server.*
import games.stendhal.server.entity.*

if (player != null) {
	if (args.length < 2) {
		player.sendPrivateText("Usage: /script npcshout.groovy npc text");
	} else {
		StringBuilder sb = new StringBuilder();
		sb.append(args[0]);
		sb.append(" shouts: ");
		boolean first = true;
		for (String word in args) {
			if (first) {
				first = false;
			} else {
				sb.append(word);
				sb.append(" ");
			}
		}
		String text = sb.toString();
	
		List players = rules.getPlayers();
		for (player2 in players) {
			player2.sendPrivateText(text);
		}
	}
}
