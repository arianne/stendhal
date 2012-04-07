package games.stendhal.client.gui.spellcasting;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.actions.CastSpellAction;
import games.stendhal.client.gui.GroundContainer;
import games.stendhal.client.gui.j2d.entity.EntityView;

import java.awt.Point;
import java.awt.geom.Point2D;

import marauroa.common.game.RPObject;

public class SpellCastingGroundContainerMouseState extends
		GroundContainerMouseState {
	
//	private static final Logger _LOGGER = Logger.getLogger(SpellCastingGroundContainerMouseState.class);
	
	private RPObject spell;

	public SpellCastingGroundContainerMouseState(GroundContainer ground) {
		super(ground);
	}

	@Override
	public void switchState() {
		this.ground.setNewMouseHandlerState(new DefaultGroundContainerMouseState(this.ground));
	}

	@Override
	protected boolean onMouseClick(Point point) {
		IGameScreen screen = this.ground.getScreen();
		Point2D point2d = screen.convertScreenViewToWorld(point);
		double x = point2d.getX();
		double y = point2d.getY();
		EntityView<?> target = screen.getEntityViewAt(x, y);
		if(target == null) {
			target = screen.getMovableEntityViewAt(x, y);
		}
		if(target != null) {
			String[] params = {String.valueOf(spell.getID().getObjectID()), String.valueOf(target.getEntity().getID().getObjectID())};
			new CastSpellAction().execute(params, null);
			this.switchState();
		}
		return true;
	}

	@Override
	protected boolean onMouseDoubleClick(Point point) {
		this.switchState();
		return false;
	}

	@Override
	protected void onMouseRightClick(Point point) {
		this.switchState();
	}

	@Override
	protected void onDragStart(Point point) {
		this.switchState();
	}

	public void setSpell(RPObject spell) {
		this.spell = spell;
	}

}
