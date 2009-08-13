/**
 *
 */
package utilities;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.j2d.Text;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.common.NotificationType;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MockScreen implements IGameScreen {

	public void addEntity(final IEntity entity) {
	}

	public void addText(final double x, final double y, final String text,
			final NotificationType type, final boolean isTalking) {
	}

	public void addText(final double x, final double y, final String text, final Color color, final boolean talking) {
	}

	public void addText(final int sx, final int sy, final String text, final NotificationType type, final boolean talking) {
	}

	public void addText(final int sx, final int sy, final String text, final Color color, final boolean isTalking) {
	}

	public void center() {
	}

	public void clear() {
	}

	public void clearTexts() {
	}

	public Point2D convertScreenToWorld(final int x, final int y) {
		return null;
	}

	public Point2D convertScreenViewToWorld(final Point p) {
		return null;
	}

	public Point2D convertScreenViewToWorld(final int x, final int y) {
		return null;
	}

	public int convertWorldToScreen(final double w) {
		return 0;
	}

	public Point convertWorldToScreenView(final double wx, final double wy) {
		return null;
	}

	public Rectangle convertWorldToScreenView(final Rectangle2D wrect) {
		return null;
	}

	public Rectangle convertWorldToScreenView(final double wx, final double wy,
			final double wwidth, final double wheight) {

		return null;
	}

	public Sprite createString(final String text, final NotificationType type) {

		return null;
	}

	public Sprite createString(final String text, final Color textColor) {

		return null;
	}

	public void draw() {
	}

	public void draw(final Sprite sprite, final double wx, final double wy) {
	}

	public void drawInScreen(final Sprite sprite, final int sx, final int sy) {
	}

	public void drawOutlineString(final Graphics g, final Color textColor, final String text, final int x, final int y) {
	}

	public void drawOutlineString(final Graphics g, final Color textColor,
			final Color outlineColor, final String text, final int x, final int y) {
	}

	public Graphics2D getGraphics() {
		return null;
	}

	public Text getTextAt(final double x, final double y) {
		return null;
	}

	public boolean isInScreen(final Rectangle srect) {
		return false;
	}

	public boolean isInScreen(final int sx, final int sy, final int swidth, final int sheight) {
		return false;
	}

	public void nextFrame() {
	}

	public void positionChanged(final double x, final double y) {
	}

	public void removeAll() {
	}

	public void removeEntity(final IEntity entity) {
	}

	public void removeText(final Text entity) {
	}

	public void setMaxWorldSize(final double width, final double height) {
	}

	public void setOffline(final boolean offline) {
	}

	public EntityView createView(final IEntity entity) {
		return null;
	}

	public EntityView getEntityViewAt(final double x, final double y) {
		return null;
	}

	public EntityView getMovableEntityViewAt(final double x, final double y) {
		return null;
	}

	public void addDialog(final WtPanel panel) {
		
	}

	public int convertWorldXToScreenView(final double wx) {
		return 0;
	}

	public int convertWorldYToScreenView(final double wy) {
		return 0;
	}

	public int getScreenHeight() {
		
		return 0;
	}

	public int getScreenViewHeight() {
		
		return 0;
	}

	public int getScreenViewWidth() {
		
		return 0;
	}

	public int getScreenViewX() {
		
		return 0;
	}

	public int getScreenViewY() {
		
		return 0;
	}

	public int getScreenWidth() {
		
		return 0;
	}

	public double getViewHeight() {
		
		return 0;
	}

	public double getViewWidth() {
		
		return 0;
	}

	public double getViewX() {
		
		return 0;
	}

	public double getViewY() {
		
		return 0;
	}

}
