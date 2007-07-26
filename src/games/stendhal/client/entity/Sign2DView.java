/*
 * @(#) games/stendhal/client/entity/Sign2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.NotificationType;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * The 2D view of a sign.
 */
public class Sign2DView extends Entity2DView {
	/**
	 * The sign entity.
	 */
	private Sign		sign;


	/**
	 * Create a 2D view of a sign.
	 *
	 * @param	entity		The entity to render.
	 */
	public Sign2DView(final Sign sign) {
		super(sign);

		this.sign = sign;
	}


	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions.
	 * <strong>NOTE: The first entry should be the default.</strong>
	 *
	 * @param	list		The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		list.add(ActionType.READ.getRepresentation());

		super.buildActions(list);
		// TODO: Allow a sign to be looked at in general, maybe like:
		// "You see a sign, maybe it's worth reading."
		list.remove(ActionType.LOOK.getRepresentation());
	}


	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation() {
		String name = getClassResourcePath();

		if (name == null) {
			name = "default";
		}

		setSprite(SpriteStore.get().getSprite(translate(name)));
	}


	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), 1.0, 1.0);
	}


	/**
	 * Determines on top of which other entities this entity should be
	 * drawn. Entities with a high Z index will be drawn on top of ones
	 * with a lower Z index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return	The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 5000;
	}


	/**
	 * Translate a resource name into it's sprite image path.
	 *
	 * @param	name		The resource name.
	 *
	 * @return	The full resource name.
	 */
	@Override
	protected String translate(final String name) {
		return "data/sprites/signs/" + name + ".png";
	}


	//
	// EntityChangeListener
	//

	/**
	 * An entity was changed.
	 *
	 * @param	entity		The entity that was changed.
	 * @param	property	The property identifier.
	 */
	@Override
	public void entityChanged(final Entity entity, final Object property)
	{
		super.entityChanged(entity, property);

		if(property == Entity.PROP_CLASS) {
			representationChanged = true;
		}
	}


	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.READ);
	}


	/**
	 * Perform an action.
	 *
	 * @param	at		The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
			case READ:
				String text = sign.getText();

				GameScreen.get().addText(
					sign.getX(), sign.getY(), text, NotificationType.RESPONSE, false);

				if (text.contains("\n")) {
					// The sign's text has multiple lines. Add a linebreak after
					// "you read" so that it is easier readable.
					StendhalUI.get().addEventLine("You read:\n\"" + text + "\"", NotificationType.RESPONSE);
				} else {
					StendhalUI.get().addEventLine("You read: \"" + text + "\"", NotificationType.RESPONSE);
				}
				break;

			default:
				super.onAction(at);
				break;
		}
	}
}
