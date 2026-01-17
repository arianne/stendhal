/***************************************************************************
 *                   (C) Copyright 2005-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ImageViewerDialog } from "../ui/dialog/ImageViewerDialog";
import { ui } from "../ui/UI";
import { RPEvent } from "marauroa"

import { marauroa } from "marauroa"

/**
 * shows an image for a detail view
 */
export class ExamineEvent extends RPEvent {

	public title!: string;
	public caption!: string;
	public path!: string;

	public execute(entity: any): void {
		if (entity !== marauroa.me) {
			return;
		}
		ui.createSingletonFloatingWindow(
			this["title"],
			new ImageViewerDialog(this["caption"], this["path"]),
			100, 50)
	}

};
