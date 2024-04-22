/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


/**
 * Represents a downloadable file.
 */
export class DownloadObject {

	/** DOM anchor element download link. */
	private readonly anchor: HTMLAnchorElement;


	/**
	 * Creates a new downloadable object.
	 *
	 * @param filename {string}
	 *   Default filename.
	 * @param data {string}
	 *   Data contents of download.
	 */
	constructor(filename: string, data: string) {
		this.anchor = document.createElement("a") as HTMLAnchorElement;
		this.anchor.download = filename;
		this.anchor.target = "_blank";
		this.anchor.href = data;
	}

	/**
	 * Simulates click on anchor element to start download.
	 *
	 * @return {boolean}
	 *   `true` if the download was created.
	 */
	execute(): boolean {
		this.anchor.click();
		// TODO: is there a way to check that download execution succeeded?
		return true;
	}
}
