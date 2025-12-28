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

import { Chat } from "./Chat";
import { DownloadObject } from "./DownloadObject";
import { SessionManager } from "./SessionManager";
import { StringUtil } from "./StringUtil";

import { ViewPort } from "../ui/ViewPort";


export class DownloadUtil {

	/**
	 * Creates a timestamp.
	 *
	 * @return {string}
	 *   Timestamp formatted string (yyyy-mm-dd_HH.MM.SS).
	 */
	static timestamp(): string {
		const d = new Date();
		const ts = {
			yyyy: "" + d.getFullYear(),
			mm: ("00" + (d.getMonth() + 1)).slice(-2),
			dd: ("00" + d.getDate()).slice(-2),
			HH: ("00" + d.getHours()).slice(-2),
			MM: ("00" + d.getMinutes()).slice(-2),
			SS: ("00" + d.getSeconds()).slice(-2),
			ms: "" + d.getMilliseconds()
		};
		while (ts.ms.length < 3) {
			// pad left with 0s
			ts.ms = "0" + ts.ms;
		}
		// date/time stamp is formatted as yyyy-mm-dd_HH.MM.SS
		return ts.yyyy + "-" + ts.mm + "-" + ts.dd + "_" + ts.HH + "." + ts.MM + "." + ts.SS;
	}

	/**
	 * Creates a downloadable object.
	 *
	 * @param filename {string}
	 *   Default filename.
	 * @param uri {string}
	 *   Data URI of download.
	 * @return {util.DownloadObject.DownloadObject}
	 *   Object to be handled by browser for download.
	 */
	static build(filename: string, uri: string): DownloadObject {
		return new DownloadObject(filename, uri);
	}

	/**
	 * Creates an image download from a canvas element.
	 *
	 * @param filename {string}
	 *   Default filename.
	 * @param canvas {Canvas}
	 *   Canvas containing data to convert to image.
	 * @return {util.DownloadObject.DownloadObject}
	 *   Object to be handled by browser for image download.
	 */
	static buildImage(filename: string, canvas: HTMLCanvasElement): DownloadObject {
		return DownloadUtil.build(filename, canvas.toDataURL("image/png"));
	}

	/**
	 * Creates a plain text file download.
	 *
	 * @param filename {string}
	 *   Default filename.
	 * @param text {string}
	 *   Text contents.
	 * @return {util.DownloadObject.DownloadObject}
	 *   Object to be handled by browser for text download.
	 */
	static buildText(filename: string, text: string): DownloadObject {
		return DownloadUtil.build(filename, StringUtil.toDataURL(text));
	}

	/**
	 * Creates an image download from a canvas element.
	 *
	 * @param canvas {Canvas}
	 *   Canvas containing data to convert to image.
	 * @return {util.DownloadObject.DownloadObject}
	 *   Object to be handled by browser for image download.
	 */
	static buildScreenshot(): DownloadObject {
		Chat.log("client", "Creating screenshot download ...");
		return DownloadUtil.buildImage("stendhal_" + DownloadUtil.timestamp() + ".png",
				ViewPort.get().getElement() as HTMLCanvasElement);
	}

	/**
	 * Creates a plain text download of chat log.
	 *
	 * @param text {string}
	 *   Chat log text contents.
	 * @return {util.DownloadObject.DownloadObject}
	 *   Object to be handled by browser for text download.
	 */
	static buildChatLog(text: string): DownloadObject {
		Chat.log("client", "Creating chat log download ...");
		const name = SessionManager.get().getCharName();
		return DownloadUtil.buildText("stendhal_chat_" + (!name ? "" : name + "_")
				+ DownloadUtil.timestamp() + ".txt", text);
	}
}
