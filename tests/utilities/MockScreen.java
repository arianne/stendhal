/**
 *
 */
package utilities;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.NotificationType;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.j2d.Text;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.sprite.Sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;

public class MockScreen implements IGameScreen {

	public void addDialog(WtPanel panel) {
		// TODO Auto-generated method stub

	}

	public void addEntity(Entity entity) {
		// TODO Auto-generated method stub

	}

	public void addText(double x, double y, String text,
			NotificationType type, boolean isTalking) {
		// TODO Auto-generated method stub

	}

	public void addText(double x, double y, String text, Color color,
			boolean talking) {
		// TODO Auto-generated method stub

	}

	public void addText(int sx, int sy, String text, NotificationType type,
			boolean talking) {
		// TODO Auto-generated method stub

	}

	public void addText(int sx, int sy, String text, Color color,
			boolean isTalking) {
		// TODO Auto-generated method stub

	}

	public void center() {
		// TODO Auto-generated method stub

	}

	public void clear() {
		// TODO Auto-generated method stub

	}

	public void clearTexts() {
		// TODO Auto-generated method stub

	}

	public Point2D convertScreenToWorld(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public Point2D convertScreenViewToWorld(Point p) {
		// TODO Auto-generated method stub
		return null;
	}

	public Point2D convertScreenViewToWorld(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public int convertWorldToScreen(double w) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Point convertWorldToScreenView(double wx, double wy) {
		// TODO Auto-generated method stub
		return null;
	}

	public Rectangle convertWorldToScreenView(Rectangle2D wrect) {
		// TODO Auto-generated method stub
		return null;
	}

	public Rectangle convertWorldToScreenView(double wx, double wy,
			double wwidth, double wheight) {
		// TODO Auto-generated method stub
		return null;
	}

	public int convertWorldXToScreenView(double wx) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int convertWorldYToScreenView(double wy) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Sprite createString(String text, NotificationType type) {
		// TODO Auto-generated method stub
		return null;
	}

	public Sprite createString(String text, Color textColor) {
		// TODO Auto-generated method stub
		return null;
	}

	public Sprite createTextBox(String text, int width, Color textColor,
			Color fillColor, boolean isTalking) {
		// TODO Auto-generated method stub
		return null;
	}

	public void draw() {
		// TODO Auto-generated method stub

	}

	public void draw(Sprite sprite, double wx, double wy) {
		// TODO Auto-generated method stub

	}

	public void drawInScreen(Sprite sprite, int sx, int sy) {
		// TODO Auto-generated method stub

	}

	public void drawOutlineString(Graphics g, Color textColor, String text,
			int x, int y) {
		// TODO Auto-generated method stub

	}

	public void drawOutlineString(Graphics g, Color textColor,
			Color outlineColor, String text, int x, int y) {
		// TODO Auto-generated method stub

	}

	public Graphics2D expose() {
		// TODO Auto-generated method stub
		return null;
	}

	public AttributedString formatLine(String line, Font fontNormal,
			Color colorNormal) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getScreenHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getScreenViewHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getScreenViewWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getScreenViewX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getScreenViewY() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getScreenWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Text getTextAt(double x, double y) {
		// TODO Auto-generated method stub
		return null;
	}

	public double getViewHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getViewWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getViewX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getViewY() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isInScreen(Rectangle srect) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInScreen(int sx, int sy, int swidth, int sheight) {
		// TODO Auto-generated method stub
		return false;
	}

	public void nextFrame() {
		// TODO Auto-generated method stub

	}

	public void positionChanged(double x, double y) {
		// TODO Auto-generated method stub

	}

	public void removeAll() {
		// TODO Auto-generated method stub

	}

	public void removeEntity(Entity entity) {
		// TODO Auto-generated method stub

	}

	public void removeText(Text entity) {
		// TODO Auto-generated method stub

	}

	public void setMaxWorldSize(double width, double height) {
		// TODO Auto-generated method stub

	}

	public void setOffline(boolean offline) {
		// TODO Auto-generated method stub

	}

	public Entity2DView createView(Entity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	public Entity2DView getEntityViewAt(double x, double y) {
		// TODO Auto-generated method stub
		return null;
	}

	public Entity2DView getMovableEntityViewAt(double x, double y) {
		// TODO Auto-generated method stub
		return null;
	}

}