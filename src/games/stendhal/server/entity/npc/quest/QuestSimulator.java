package games.stendhal.server.entity.npc.quest;

class QuestSimulator {

	public void playerSays(String text) {
		System.out.println("Player: " + text);
	}

	public void npcSays(String name, String text) {
		System.out.println(name + ": " + text);
	}

	public void history(String text) {
		System.out.println("History: " + text);
	}

	public void info(String text) {
		System.out.println(text);
	}
}
