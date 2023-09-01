/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * Graphics wrapper for drawing with transparency.
 */
public class TransparencyGraphicsWrapper extends Graphics2D {

	private final Graphics2D g;
	private Color origColor;
	private final int transparencyRatio;
	private AlphaComposite alcom;

	public TransparencyGraphicsWrapper(Graphics2D g, int transparencyRatio) {
		this.g = g;
		this.origColor = g.getColor();
		setModColor(origColor);
		this.transparencyRatio = transparencyRatio;
		alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ((float) transparencyRatio) / 256);
	}

	@Override
	public void draw(Shape s) {
		g.draw(s);
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(alcom);
		boolean done = g2d.drawImage(img, xform, obs);
		g2d.dispose();
		return done;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(alcom);
		g2d.drawImage(img, op, x, y);
		g2d.dispose();
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(alcom);
		g2d.drawRenderedImage(img, xform);
		g2d.dispose();
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(alcom);
		g2d.drawRenderableImage(img, xform);
		g2d.dispose();
	}

	@Override
	public void drawString(String str, int x, int y) {
		g.setColor(origColor);
		g.drawString(str, x, y);
		setModColor(origColor);
	}

	@Override
	public void drawString(String str, float x, float y) {
		g.setColor(origColor);
		g.drawString(str, x, y);
		setModColor(origColor);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		g.setColor(origColor);
		g.drawString(iterator, x, y);
		setModColor(origColor);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		g.setColor(origColor);
		g.drawString(iterator, x, y);
		setModColor(origColor);
	}

	@Override
	public void drawGlyphVector(GlyphVector vec, float x, float y) {
		g.drawGlyphVector(vec, x, y);
	}

	@Override
	public void fill(Shape s) {
		g.fill(s);
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return g.hit(rect, s, onStroke);
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return g.getDeviceConfiguration();
	}

	@Override
	public void setComposite(Composite comp) {
		g.setComposite(comp);
	}

	@Override
	public void setPaint(Paint paint) {
		g.setPaint(paint);
	}

	@Override
	public void setStroke(Stroke s) {
		g.setStroke(s);
	}

	@Override
	public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
		g.setRenderingHint(hintKey, hintValue);
	}

	@Override
	public Object getRenderingHint(RenderingHints.Key hintKey) {
		return g.getRenderingHint(hintKey);
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		g.setRenderingHints(hints);
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		g.addRenderingHints(hints);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return g.getRenderingHints();
	}

	@Override
	public void translate(int x, int y) {
		g.translate(x, y);
	}

	@Override
	public void translate(double tx, double ty) {
		g.translate(tx, ty);
	}

	@Override
	public void rotate(double theta) {
		g.rotate(theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		g.rotate(theta, x, y);
	}

	@Override
	public void scale(double sx, double sy) {
		g.scale(sx, sy);
	}

	@Override
	public void shear(double shx, double shy) {
		g.shear(shx, shy);
	}

	@Override
	public void transform(AffineTransform Tx) {
		g.transform(Tx);
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		g.setTransform(Tx);
	}

	@Override
	public AffineTransform getTransform() {
		return g.getTransform();
	}

	@Override
	public Paint getPaint() {
		return g.getPaint();
	}

	@Override
	public Composite getComposite() {
		return g.getComposite();
	}

	@Override
	public void setBackground(Color color) {
		Color modColor = new Color(color.getRed(), color.getGreen(), color.getBlue(),
				color.getAlpha() * transparencyRatio / 255);
		g.setBackground(modColor);
	}

	@Override
	public Color getBackground() {
		return g.getBackground();
	}

	@Override
	public Stroke getStroke() {
		return g.getStroke();
	}

	@Override
	public void clip(Shape s) {
		g.clip(s);
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return g.getFontRenderContext();
	}

	@Override
	public Graphics create() {
		return new TransparencyGraphicsWrapper((Graphics2D) g.create(), transparencyRatio);
	}

	@Override
	public Color getColor() {
		return g.getColor();
	}

	@Override
	public void setColor(Color color) {
		this.origColor = color;
		setModColor(color);
	}

	@Override
	public void setPaintMode() {
		g.setPaintMode();
	}

	@Override
	public void setXORMode(Color c1) {
		g.setXORMode(c1);
	}

	@Override
	public Font getFont() {
		return g.getFont();
	}

	@Override
	public void setFont(Font font) {
		g.setFont(font);
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return g.getFontMetrics(f);
	}

	@Override
	public Rectangle getClipBounds() {
		return g.getClipBounds();
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		g.clipRect(x, y, width, height);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		g.setClip(x, y, width, height);
	}

	@Override
	public Shape getClip() {
		return g.getClip();
	}

	@Override
	public void setClip(Shape clip) {
		g.setClip(clip);
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		g.copyArea(x, y, width, height, dx, dy);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		g.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		g.fillRect(x, y, width, height);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		g.clearRect(x, y, width, height);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		g.drawOval(x, y, width, height);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		g.fillOval(x, y, width, height);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		g.drawPolyline(xPoints, yPoints, nPoints);
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		g.drawPolygon(xPoints, yPoints, nPoints);
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		g.fillPolygon(xPoints, yPoints, nPoints);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(alcom);
		boolean done = g2d.drawImage(img, x, y, observer);
		g2d.dispose();
		return done;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(alcom);
		boolean done = g2d.drawImage(img, x, y, width, height, observer);
		g2d.dispose();
		return done;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(alcom);
		boolean done = g2d.drawImage(img, x, y, bgcolor, observer);
		g2d.dispose();
		return done;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(alcom);
		boolean done = g2d.drawImage(img, x, y, width, height, bgcolor, observer);
		g2d.dispose();
		return done;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(alcom);
		boolean done = g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
		g2d.dispose();
		return done;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			Color bgcolor, ImageObserver observer) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(alcom);
		boolean done = g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
		g2d.dispose();
		return done;
	}

	@Override
	public void dispose() {
		g.dispose();
	}

	private void setModColor(Color color) {
		Color modColor = new Color(color.getRed(), color.getGreen(), color.getBlue(),
				color.getAlpha() * transparencyRatio / 255);
		g.setColor(modColor);
	}
}
