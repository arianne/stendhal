package games.stendhal.client.gui.spellcasting;

import java.awt.Point;
import java.awt.geom.Point2D;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.actions.CastSpellAction;
import games.stendhal.client.gui.GroundContainer;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import marauroa.common.game.RPObject;
/**
 * State of the GroundContainer that is switched to, when a spell is selected.
 *
 * @author madmetzger
 */
public class SpellCastingGroundContainerMouseState extends
		GroundContainerMouseState {

	private RPObject spell;

	/**
	 * Create a new SpellCastingGroundContainerMouseState acting on a given GroundContainer
	 *
	 * @param ground the GroundContainer
	 */
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
		// determine target
		EntityView<?> target = screen.getEntityViewAt(x, y);
		if(target == null) {
			target = screen.getMovableEntityViewAt(x, y);
		}
		// when target found construct and execute spellcasting action
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

	/**
	 * Sets the previously selected spell, which will be casted on click on an entity
	 *
	 * @param spell the selected spell
	 */
	public void setSpell(RPObject spell) {
		this.spell = spell;
	}

	@Override
	public StendhalCursor getCursor(Point point) {
		Point2D point2 = ground.getScreen().convertScreenViewToWorld(point);
		final EntityView<?> view = ground.getScreen().getEntityViewAt(point2.getX(), point2.getY());
		if(view != null) {
			// use magic symbol when cursor aims at an entity
			return StendhalCursor.SPELLCASTING;
		}
		// default to normal cursor which signals no behaviour in this state
		return StendhalCursor.NORMAL;
	}

}
