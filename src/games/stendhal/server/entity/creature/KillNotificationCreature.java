package games.stendhal.server.entity.creature;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Observer;

/**
 * This class have ability to notify registered observers
 * about creature's death. 
 */
public class KillNotificationCreature extends Creature {
	 
   private CircumstancesOfDeath circumstances;
   private Registrator registrator = new Registrator();
   
   /**
    * sets new observer 
    * @param observer
    * 				- observer, which will give info about creature death.
    */
   public void registerObjectsForNotification(final Observer observer) {
      registrator.setObserver(observer);    	   
   }
   
   /**
    * Will notify observers when event will occurred (death). 
    */
   public void notifyRegisteredObjects() {
      registrator.setChanges();
      registrator.notifyObservers(circumstances);
   }

   @Override
   public void onDead(final Entity killer, final boolean remove) {
	  circumstances=new CircumstancesOfDeath((RPEntity)killer, this, this.getZone());
      notifyRegisteredObjects();
      super.onDead(killer, remove);  

   }
   
   /**
    * override noises for changes.
    * 
    */
   public void setNoises(final LinkedHashMap<String, LinkedList<String>> creatureNoises){
   	noises.clear();
   	noises.putAll(creatureNoises);
   }
   
   /**
    * wrapper for constructor 
    * @param copy
    * 			- creature to create :-)
    */
   public KillNotificationCreature(final Creature copy) {
		super(copy);
	}
}