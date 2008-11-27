package games.stendhal.client.gui.j2d.entity;

import java.awt.Graphics2D;
import java.util.List;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Gate;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

public class Gate2DView extends Entity2DView {
	static final Sprite horizontalClosedGate;
	static final Sprite horizontalOpenGate;
	private static final Sprite verticalClosedGate;
	private static final Sprite verticalOpenGate;
	static {
		 final Sprite sprite = SpriteStore.get().getSprite("data/sprites/doors/fence_gate_h.png");
		 horizontalClosedGate = sprite.createRegion(0, 32 + 96, 96, 64, null);
		 horizontalOpenGate = sprite.createRegion(0, 32, 96, 64, null);
		 final Sprite sprite_v = SpriteStore.get().getSprite("data/sprites/doors/fence_gate_v.png");
		 verticalClosedGate = sprite_v.createRegion(0, 96, 96, 96, null);
		 verticalOpenGate = sprite_v.createRegion(0, 0, 96, 96, null);
	
	}


	private String orientation = "orientation";
	
	public Gate2DView(final Gate entity) {
		this ((IEntity) entity);
		
	}


	public Gate2DView(final IEntity entity) {
		super(entity);
		this.entity = entity;
	}

	@Override
	protected String translate(final String name) {
		return "data/sprites/doors/fence_gate_" + entity.getRPObject().get("orientation") + ".png";
	}
	
	@Override
	protected void buildActions(final List<String> list) {
		list.add(ActionType.USE.getRepresentation());
	}
	
	@Override
	protected void buildRepresentation(final IGameScreen gameScreen) {

	}
	
	@Override
	public void onAction(final ActionType at) {
		if (entity == null) {
			Logger.getLogger(Entity2DView.class).error(
					"View already released - action not processed: " + at);
			return;
		}

		RPAction rpaction;

		switch (at) {
		case USE:
			rpaction = new RPAction();
			rpaction.put("type", at.toString());
			entity.fillTargetInfo(rpaction);

			at.send(rpaction);
			break;
		default:
			super.onAction(at);
			break;
		}
	}
	@Override
	protected void drawEntity(final Graphics2D g2d, final int x, final int y, final int width,
			final int height, final IGameScreen gameScreen) {
	
		if (entity.getRPObject().get(orientation).equals("h")) {
			if (entity.getResistance() == 100) {
				horizontalClosedGate.draw(g2d, x - 32, y);
			} else {
				horizontalOpenGate.draw(g2d, x - 32, y);
			}
		} else	if (entity.getResistance() == 100) {
			verticalClosedGate.draw(g2d, x - 32, y - 32);
		} else {
			verticalOpenGate.draw(g2d, x - 32, y - 32);
		}
	}


	@Override
	public int getZIndex() {
		
		return 0;
	}
}

