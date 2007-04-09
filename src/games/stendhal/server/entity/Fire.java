package games.stendhal.server.entity;

import games.stendhal.server.events.UseListener;

import java.awt.geom.Rectangle2D;


public class Fire extends Entity implements UseListener {
	private int width;
	private int height;
	
	public Fire(int width, int height) {
		super();
		setDescription("You see a flickering light. You are tented to touch it");
		put("type", "fire");
		put("width", width);
		put("height", height);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, width, height);

	}

	@Override
    public boolean isObstacle(Entity entity) {
		return true;
    }

	public void onUsed(RPEntity user) {
	   user.sendPrivateText("No good idea!");
	   
	    
    }

}
