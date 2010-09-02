package games.stendhal.server.core.pathfinder;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.GuidedEntity;
import games.stendhal.server.entity.Registrator;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import marauroa.common.Pair;

public class MultiZonesFixedPath implements Observer {
		private final GuidedEntity ent;
		private final LinkedList<Pair<String, LinkedList<Node>>> route;
		private Integer count;
		private StendhalRPZone zone;
		private Registrator finishnotifier = new Registrator();
	
	/**
	 * constructor
	 * @param entity = pathnotifier owner
	 */
	public MultiZonesFixedPath(
			final GuidedEntity entity, 
			final LinkedList<Pair<String, LinkedList<Node>>> rt, 
			final Observer o) {
		ent=entity;
		count=0;
		route=rt;
		finishnotifier.setObserver(o);
	}
	
	private void removeFromZone() {
		ent.getZone().remove(ent);		
	}
	
	private void addToZone() {
		int x= route.get(count).second().get(0).getX();
		int y= route.get(count).second().get(0).getY();
		ent.setPosition(x, y);
		zone = SingletonRepository.getRPWorld().getZone(
				route.get(count).first());
		ent.setPath(new FixedPath(route.get(count).second(), false));	
		zone.add(ent);
	}
	
	public void update(Observable o, Object arg) {
		// will run at local path's end; have to change path to another
		if(count!=(route.size()-1)) {
			removeFromZone();
			count++;
			addToZone();
		} else {
			// last route finished
			finishnotifier.setChanges();
			finishnotifier.notifyObservers();
		}
	}	
}

