package games.stendhal.client.entity;

import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.Sprite;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.RPObject;


public class BossCreature2DView extends Creature2DView {
	public BossCreature2DView(BossCreature creature) {
		super(creature,creature.getWidth(),creature.getHeight());
    }

	@Override
	protected void buildSprites(Map<String, AnimatedSprite> map, RPObject object) {
		Sprite tiles=getAnimationSprite(object);
		
		map.put(ActiveEntity.STATE_RIGHT, getAnimatedWalk(tiles, 0, getWidth(), getHeight()));
		map.put(ActiveEntity.STATE_LEFT, getAnimatedWalk(tiles, 1, getWidth(), getHeight()));
	}

	@Override
	protected String getDefaultState() {
		return ActiveEntity.STATE_RIGHT;
	}
	
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), getWidth(), getHeight());
	}

	protected AnimatedSprite getSprite(final String state) {
		if(state.equals(ActiveEntity.STATE_UP)) {
			return sprites.get(ActiveEntity.STATE_RIGHT);
		}
		if(state.equals(ActiveEntity.STATE_DOWN)) {
			return sprites.get(ActiveEntity.STATE_LEFT);
		}
		
		return sprites.get(state);
	}
}
