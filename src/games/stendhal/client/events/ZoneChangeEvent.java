package games.stendhal.client.events;

public interface ZoneChangeEvent {

	// Called when entity enters a new zone
	void onEnterZone(String zone);

	// Called when entity leaves a zone
	void onLeaveZone(String zone);
}
