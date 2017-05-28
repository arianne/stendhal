/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package utilities;

import java.awt.Point;
import java.awt.geom.Point2D;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.gui.j2d.RemovableSprite;
import games.stendhal.client.gui.j2d.entity.EntityView;

public class MockScreen implements IGameScreen {
	@Override
	public void center() {
	}

	@Override
	public void clearTexts() {
	}

	@Override
	public Point2D convertScreenViewToWorld(final Point p) {
		return null;
	}

	@Override
	public Point2D convertScreenViewToWorld(final int x, final int y) {
		return null;
	}

	@Override
	public RemovableSprite getTextAt(final int x, final int y) {
		return null;
	}

	@Override
	public void nextFrame() {
	}

	@Override
	public void positionChanged(final double x, final double y) {
	}

	@Override
	public void removeText(final RemovableSprite entity) {
	}

	@Override
	public void setOffline(final boolean offline) {
	}

	@Override
	public EntityView<?> getEntityViewAt(final double x, final double y) {
		return null;
	}

	@Override
	public EntityView<?> getMovableEntityViewAt(final double x, final double y) {
		return null;
	}
}
