package games.stendhal.client.entity;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.RPObject;


public class Fire extends AnimatedEntity {

	@Override
	protected void buildAnimations(RPObject object) {
		SpriteStore store = SpriteStore.get();
		for (int i = 0; i < 2; i++) {
			
			sprites.put(Integer.toString(i), store.getAnimatedSprite(translate("fire"), i, 1, 1, 1));
		}


	}

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
    protected void buildOfferedActions(List<String> list) {
	   list.clear();
    }

	@Override
    public ActionType defaultAction() {
	   return null;
    }

	@Override
    protected Sprite defaultAnimation() {
	   return sprites.get("0")[0];
    }

}
