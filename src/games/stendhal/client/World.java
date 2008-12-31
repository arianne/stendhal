package games.stendhal.client;

public final class World {

	private static final World instance = new World();
	
	
	
	public final static World get(){
		return instance;
	}
	
	private PlayerList playerList = new PlayerList();

	public static PlayerList getPlayerList() {
		return get().playerList;
	}
	
}
