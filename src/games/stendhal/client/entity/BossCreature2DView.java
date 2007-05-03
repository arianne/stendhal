package games.stendhal.client.entity;

import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.Sprite;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import marauroa.common.game.RPObject;


public class BossCreature2DView extends Creature2DView {
	private AnimatedSprite animatedSprite;

	public BossCreature2DView(BossCreature creature) {
		super(creature,creature.getWidth(),creature.getHeight());
    }

	@Override
	protected void buildSprites(Map<String, AnimatedSprite> map, RPObject object) {
		animatedSprite=new AnimatedSprite(new Sprite[] { getAnimationSprite(object)}, 0L, false);		
	}

	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), getWidth(), getHeight());
	}

	protected AnimatedSprite getSprite(final String state) {
		return animatedSprite;
	}
}
