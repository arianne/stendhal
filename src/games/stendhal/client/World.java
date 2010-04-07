package games.stendhal.client;

public final class World {

	private static World instance;
	
	public static World get() {
		if(instance == null) {
			instance = new World();
		}
		return instance;
	}
	
	private PlayerList playerList = new PlayerList();

	public PlayerList getPlayerList() {
		return playerList;
	}
	
	public void removePlayerLoggingOut(String player) {
		playerList.removePlayer(player);
	}
	
	public void addPlayerLoggingOn(String player) {
		playerList.addPlayer(player);
	}
}
