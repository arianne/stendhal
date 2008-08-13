package games.stendhal.client.gui.j2d.entity;

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
	
	public Gate2DView(Gate entity) {
		this ((IEntity) entity);
	}


	public Gate2DView(IEntity entity) {
		super(entity);
		this.entity = entity;
	}

	@Override
	protected String translate(String name) {
		return "data/sprites/doors/fence_gate_h.png";
	}
	
	@Override
	protected void buildActions(List<String> list) {
		list.add(ActionType.USE.getRepresentation());
	}
	
	@Override
	protected void buildRepresentation(IGameScreen gameScreen) {
		SpriteStore store = SpriteStore.get();
		final Sprite tiles = store.getSprite(translate(""));

		int width = tiles.getWidth();
		int height = tiles.getHeight() / 2;

		setSprite(store.getTile(tiles, 0, 0, width, height));
//		map.put("closed", store
//				.getTile(tiles, 0, height, width, height));	// TODO Auto-generated method stub
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
}

