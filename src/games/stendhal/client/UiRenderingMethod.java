/* $Id$ */
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
package games.stendhal.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration of rendering methods.
 * 
 * @see <a href=
 *      "https://docs.oracle.com/javase/8/docs/technotes/guides/2d/flags.html">JavaSE
 *      8 2D Technology</a>
 * @see Wayland + Vulkan:
 *      <a href="https://openjdk.org/projects/wakefield/">Project WakeField</a>
 * @see MacOS Metal framework:
 *      <a href="https://openjdk.org/projects/lanai/">Project Lanai</a>
 * 
 */
public enum UiRenderingMethod {

	DEFAULT("", "Default (system)"),

	DIRECT_DRAW("", "Default (DirectDraw)"),

	DIRECT_DRAW_SCALE("ddraw_scale", "DirectDraw HW scaling"),

	SOFTWARE("software", "Software rendering"),

	WINDOWS_API("software", "Windows API"),

	OPEN_GL("opengl", "Open GL"),

	XRENDER("xrender", "XRender"),

	METAL("metal", "Metal Framework");

	private final String propertyValue;
	private final String displayName;

	private UiRenderingMethod(String propertyValue, String displayName) {
		this.propertyValue = propertyValue;
		this.displayName = displayName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static UiRenderingMethod fromPropertyValue(String propertyValue) {
		for (UiRenderingMethod method : values()) {
			if (propertyValue.equals(method.getPropertyValue())) {
				return method;
			}
		}

		return null;
	}

	public static List<UiRenderingMethod> getAvailableMethods() {
		List<UiRenderingMethod> methods = new ArrayList<>();

		final String platformOs = System.getProperty("os.name").toLowerCase();
		if (platformOs.startsWith("windows")) {
			methods.add(DIRECT_DRAW);
			methods.add(DIRECT_DRAW_SCALE);
			methods.add(WINDOWS_API);
			methods.add(OPEN_GL);
		} else if (platformOs.startsWith("mac os")) {
			methods.add(DEFAULT);
			methods.add(SOFTWARE);
			methods.add(OPEN_GL);
			methods.add(METAL);
		} else {
			methods.add(DEFAULT);
			methods.add(SOFTWARE);
			methods.add(OPEN_GL);
			methods.add(XRENDER);
		}

		return methods;
	}
}
