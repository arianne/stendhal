package games.stendhal.server.core.engine;

import java.util.List;

import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.game.dbcommand.LogGameEventCommand;


public class GameEvent {
	public final String source;
	public final String event;
	public final String[] params;

	public GameEvent(final String source, final String event, final String... params) {
		this.source = source;
		this.event = event;
		this.params = params;
	}
	
	public GameEvent(final String source, final String event, final List<String> params) {
		this.source = source;
		this.event = event;
		this.params = (String[]) params.toArray();
	}

	public void raise() {
		DBCommand command = new LogGameEventCommand(source, event, params);
		DBCommandQueue.get().enqueue(command);
	}
}
