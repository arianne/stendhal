/***************************************************************************
 *                (C) Copyright 2015-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";

import { marauroa } from "marauroa"
import { stendhal } from "../../stendhal";

/**
 * a dialog to display images
 */
export class ImageViewerDialog extends Component {

	constructor(caption: string, imageFilename: string) {
		super("imageviewer-template");
		this.child("h3")!.textContent = caption;
		this.componentElement.querySelector("img")!.src = imageFilename;
	}

}
