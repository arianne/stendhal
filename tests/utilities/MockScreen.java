/**
 *
 */
package utilities;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.EntityView;
import games.stendhal.client.gui.j2d.Text;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.common.NotificationType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;

public class MockScreen implements IGameScreen {

	public void addEntity(Entity entity) {
	}

	public void addText(double x, double y, String text,
			NotificationType type, boolean isTalking) {
	}

	public void addText(double x, double y, String text, Color color, boolean talking) {
	}

	public void addText(int sx, int sy, String text, NotificationType type, boolean talking) {
	}

	public void addText(int sx, int sy, String text, Color color, boolean isTalking) {
	}

	public void center() {
	}

	public void clear() {
	}

	public void clearTexts() {
	}

	public Point2D convertScreenToWorld(int x, int y) {
		return null;
	}

	public Point2D convertScreenViewToWorld(Point p) {
		return null;
	}

	public Point2D convertScreenViewToWorld(int x, int y) {
		return null;
	}

	public int convertWorldToScreen(double w) {
		return 0;
	}

	public Point convertWorldToScreenView(double wx, double wy) {
		return null;
	}

	public Rectangle convertWorldToScreenView(Rectangle2D wrect) {
		return null;
	}

	public Rectangle convertWorldToScreenView(double wx, double wy,
			double wwidth, double wheight) {

		return null;
	}

	public Point convertWorldToScreenView(Point2D pos) {
		return null;
	}

	public Sprite createString(String text, NotificationType type) {

		return null;
	}

	public Sprite createString(String text, Color textColor) {

		return null;
	}

	public Sprite createTextBox(String text, int width, Color textColor,
			Color fillColor, boolean isTalking) {

		return null;
	}

	public void draw() {
	}

	public void draw(Sprite sprite, double wx, double wy) {
	}

	public void drawInScreen(Sprite sprite, int sx, int sy) {
	}

	public void drawOutlineString(Graphics g, Color textColor, String text, int x, int y) {
	}

	public void drawOutlineString(Graphics g, Color textColor,
			Color outlineColor, String text, int x, int y) {
	}

	public Graphics2D expose() {
		return null;
	}

	public AttributedString formatLine(String line, Font fontNormal, Color colorNormal) {
		return null;
	}

	public Dimension convertWorldToScreen(Dimension2D d) {
	    return null;
    }

	public Dimension convertWorldXToScreenView(Dimension s) {
	    return null;
    }

	public Dimension getScreenSize() {
	    return null;
    }

	public Point getScreenViewPos() {
	    return null;
    }

	public Dimension getScreenViewSize() {
	    return null;
    }

	public Point2D getViewPos() {
	    return null;
    }

	public Dimension getViewSize() {
	    return null;
    }

	public Text getTextAt(double x, double y) {
		return null;
	}

	public boolean isInScreen(Rectangle srect) {
		return false;
	}

	public boolean isInScreen(int sx, int sy, int swidth, int sheight) {
		return false;
	}

	public void nextFrame() {
	}

	public void positionChanged(double x, double y) {
	}

	public void removeAll() {
	}

	public void removeEntity(Entity entity) {
	}

	public void removeText(Text entity) {
	}

	public void setMaxWorldSize(double width, double height) {
	}

	public void setOffline(boolean offline) {
	}

	public Entity2DView createView(Entity entity) {
		return null;
	}

	public EntityView getEntityViewAt(double x, double y) {
		return null;
	}

	public EntityView getMovableEntityViewAt(double x, double y) {
		return null;
	}

	public void addDialog(WtPanel panel) {
		// TODO Auto-generated method stub
		
	}

	public int convertWorldXToScreenView(double wx) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int convertWorldYToScreenView(double wy) {
		// TODO Auto-generated method stub
		return 0;
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

}
