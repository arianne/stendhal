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
	static final Sprite closedGate;
	static final Sprite openGate;
	static {
		 final Sprite sprite = SpriteStore.get().getSprite("data/sprites/doors/fence_gate_h.png");
		 closedGate = sprite.createRegion(0, 32 + 96, 96, 64, null);
		 openGate = sprite.createRegion(0, 32, 96, 64, null);
	
	}
	
	public Gate2DView(final Gate entity) {
		this ((IEntity) entity);
	}


	public Gate2DView(final IEntity entity) {
		super(entity);
		this.entity = entity;
	}

	@Override
	protected String translate(final String name) {
		return "data/sprites/doors/fence_gate_h.png";
	}
	
	@Override
	protected void buildActions(final List<String> list) {
		list.add(ActionType.USE.getRepresentation());
	}
	
	@Override
	protected void buildRepresentation(final IGameScreen gameScreen) {
		final SpriteStore store = SpriteStore.get();
		final Sprite tiles = store.getSprite(translate(""));

		final int width = tiles.getWidth();
		final int height = tiles.getHeight() / 2;

		setSprite(store.getTile(tiles, 0, 0, width, height));
		super.buildRepresentation(gameScreen);
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
	
		
		if (entity.getResistance() == 100) {
			closedGate.draw(g2d, x - 32, y);
		} else {
			openGate.draw(g2d, x - 32, y);
		}
	}


	@Override
	public int getZIndex() {
		
		return 0;
	}
}

