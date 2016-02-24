package games.stendhal.server.maps.quests.logic;

public interface ItemCollectorSetters {

	ItemCollectorSetters item(String itemName);

	ItemCollectorSetters pieces(int count);

	ItemCollectorSetters bySaying(String message);
}
