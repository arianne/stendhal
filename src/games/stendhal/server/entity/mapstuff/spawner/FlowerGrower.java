package games.stendhal.server.entity.mapstuff.spawner;

import java.util.List;

import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.area.FertileGround;
import marauroa.common.game.RPObject;

public class FlowerGrower extends VegetableGrower {

	private static final String ITEM_NAME = "lilia";
	private String[] description = { "0", "1", "2", "3", "4" };

	public FlowerGrower(RPObject object, String itemname) {
		super(object, itemname);
		setMaxRipeness(4);
		store();
	}

	public FlowerGrower() {
		this(ITEM_NAME);
		
		store();
	}

	public FlowerGrower(String infoString) {
		super(infoString);
		setMaxRipeness(4);
		store();
	}

	@Override
	public void onFruitPicked(Item picked) {
		getZone().remove(this);
		notifyWorldAboutChanges();
	}

	@Override
	protected int getRandomTurnsForRegrow() {
		return 3;
	}

	@Override
	public String describe() {
		if (getRipeness() < 0 || getRipeness() > getMaxRipeness()) {
			return super.describe();
		} else {
			return description[getRipeness()];
		}
	}

	public boolean isOnFertileGround() {
		if (this.getZone()== null){
			return false;
		} else {
			StendhalRPZone zone = this.getZone();
			List<Entity> ferts=zone.getFilteredEntities(new FilterCriteria<Entity>(){

				public boolean passes(Entity o) {
					if(o instanceof FertileGround){
						return o.getArea().contains(getX(),getY());
					}
					return false;
				}});
				
			return !ferts.isEmpty();
			
		}
	}
	
	@Override
	protected void growNewFruit() {
		if (isOnFertileGround()) {
			super.growNewFruit();
		} else {
			if (getZone()!= null){
				getZone().remove(this);
				notifyWorldAboutChanges();
			}
			
		}
	}

}
