package games.stendhal.server.core.engine;


import java.util.concurrent.ConcurrentLinkedQueue;

class GameEventQueue {
	static final ConcurrentLinkedQueue<GameEvent> queue = new ConcurrentLinkedQueue<GameEvent>();
	
	static ConcurrentLinkedQueue<GameEvent> getGameEvents() {
		  return queue;	
	}

	static void add(final GameEvent gameEvent) {
		queue.add(gameEvent);
		
	}

}
