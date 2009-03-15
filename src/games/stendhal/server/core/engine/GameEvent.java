package games.stendhal.server.core.engine;


public class GameEvent {
	public final String source;
	public final String event;
	public final String[] params;

	public GameEvent(final String source, final String event, final String... params) {
		this.source = source;
		this.event = event;
		this.params = params;
	}

	public void raise() {
		GameEventQueue.add(this);

	}
}
