package games.stendhal.client.gui.map;

import java.awt.Color;
import java.awt.Graphics;

import games.stendhal.client.entity.DomesticAnimal;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;

public class DomesticAnimalMapObject extends MovingMapObject {
	private static final Color COLOR_DOMESTIC_ANIMAL = new Color(255, 150, 0);
	
	private DomesticAnimal domesticanimal;
	protected Color drawColor;
	
	public DomesticAnimalMapObject(final IEntity entity) {
		super(entity);
		this.domesticanimal = (DomesticAnimal) entity;
		drawColor = COLOR_DOMESTIC_ANIMAL;
	}
	
	@Override
	public void draw(final Graphics g, final int scale) {
		// we check this here rather than in the MapPanel so that any changes to the user are refreshed (e.g. disowning pet)
		User user = User.get();
		if ((user.hasPet() && user.getPetID() == domesticanimal.getObjectID()) || (user.hasSheep() && user.getSheepID() == domesticanimal.getObjectID())) {
			draw(g, scale, drawColor);
		}
	}
}
