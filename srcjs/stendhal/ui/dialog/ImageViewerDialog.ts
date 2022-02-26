/***************************************************************************
 *                (C) Copyright 2015-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";

declare var marauroa: any;
declare var stendhal: any;

/**
 * a dialog to display images
 */
export class ImageViewerDialog extends Component {

	constructor(caption: string, imageFilename: string) {
		super("imageviewer-template");
		this.componentElement.querySelector("h3")!.textContent = caption;
		this.componentElement.querySelector("img")!.src = imageFilename;
	}

}
