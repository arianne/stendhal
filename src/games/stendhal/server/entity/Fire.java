package games.stendhal.server.entity;

import games.stendhal.server.events.UseListener;

import java.awt.geom.Rectangle2D;


public class Fire extends Entity implements UseListener{

	public Fire() {
		super();
		setDescription("You see a flickering light. You are tented to touch it");
		put("type", "fire");
	}
	
	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);

	}

	@Override
    public boolean isObstacle(Entity entity) {
		return true;
    }

	public void onUsed(RPEntity user) {
	   user.sendPrivateText("No good idea!");
	   
	    
    }

}
