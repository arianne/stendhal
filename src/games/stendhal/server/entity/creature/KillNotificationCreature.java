package games.stendhal.server.entity.creature;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
    * 				- observer, which will get info about creature death.
    */
   public void registerObjectsForNotification(final Observer observer) {
	   if(observer!=null) {
		      registrator.setObserver(observer);		   
	   } 	   
   }
   
   /**
    * sets new observer 
    * @param observers
    * 				- observers, which will get info about creature death.
    */
   public void registerObjectsForNotification(final List<Observer> observers) {
	   for(Observer observer : observers) {
		   if(observer!=null) {
			      registrator.setObserver(observer);		   
		   }		   
	   } 	   
   }
 
   /**
    * unset observer 
    * @param observer
    * 				- observer to remove.
    */
   public void unregisterObjectsForNotification(final Observer observer) {
	   if(observer!=null) {
		      registrator.removeObserver(observer);		   
	   } 	   
   }
   
   /**
    * unset observer 
    * @param observers
    * 				- observers to remove.
    */
   public void unregisterObjectsForNotification(final List<Observer> observers) {
	   for(Observer observer : observers) {
		   if(observer!=null) {
			      registrator.removeObserver(observer);		   
		   }		   
	   } 	   
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
   public KillNotificationCreature(final KillNotificationCreature copy) {
		super(copy);
	}
   
	public KillNotificationCreature(final Creature copy) {
		super(copy);
}

	public KillNotificationCreature getNewInstance() {
		return new KillNotificationCreature(this);
	}
}