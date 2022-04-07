/***************************************************************************
 *                     Copyright © 2022 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package org.arianne.stendhal.client.js;

import android.webkit.JavascriptInterface;


public abstract class JSInterface {

	private String html;


	@JavascriptInterface
	public void fire(final String html) {
		this.html = html;

		onFire();
	}

	protected abstract void onFire();

	public String getHTML() {
		return html;
	}
}
