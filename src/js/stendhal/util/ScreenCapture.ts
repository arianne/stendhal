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
import { Debug } from "./Debug";
import { DownloadUtil } from "./DownloadUtil";


/**
 * Interface defining video attributes such as MIME type, framerate, & encoders.
 */
interface VideoDefinition {
	mime: string, // MIME type
	container: string, // media container
	framerate: number, // video framerate (frames per second)
	codec?: string, // video encoder
	bitrate?: number, // video bitrate (bits per second)
	acodec?: string, // audio encoder
	abitrate?: number // audio bitrate (bits per second)
}


/**
 * Manages capturing video of the client.
 *
 * TODO:
 * - handle audio capture
 * - allow attributes such as framerate, bitrate, etc. to be customized
 */
export class ScreenCapture {

	/** Property denoting state of activity. */
	private static active = false;
	/** Video recorder. */
	private capture!: MediaRecorder;
	/** Recorded data. */
	private readonly chunks: any[];
	/** Video capture definition. */
	private readonly def?: VideoDefinition;


	constructor() {
		this.chunks = [];
		this.def = this.getDefinition();
	}

	/**
	 * Begins recording.
	 *
	 * @param {Canvas} canvas
	 *   Canvas element to record.
	 * @param {AudioContext=} audio
	 *   Sound manager from which to record audio.
	 */
	start(canvas: HTMLCanvasElement, audio?: AudioContext) {
		if (!Debug.isActive("screencap")) {
			Chat.log("client", "Screen capture debugging is disabled");
			return;
		}

		if (!this.def) {
			Chat.log("error", "No suitable video codec available");
			return;
		}
		if (!this.def.acodec) {
			Chat.log("warning", "Audio capture not supported");
		}
		Chat.log("client", "Starting video capture ...");
		Chat.log("client", "&nbsp;&nbsp;MIME type: " + this.def.mime);
		Chat.log("client", "&nbsp;&nbsp;framerate: " + this.def.framerate.toFixed(2) + " frames/sec");
		if (this.def.codec) {
			Chat.log("client", "&nbsp;&nbsp;video encoder: " + this.def.codec + " (" + this.def.bitrate
					+ " bits/sec)");
			if (this.def.acodec) {
				Chat.log("client", "&nbsp;&nbsp;audio encoder: " + this.def.acodec + " ("
						+ this.def.abitrate + " bits/sec)");
			}
		}

		// FIXME: framerate not working, may need to use library for encoding
		this.capture = new MediaRecorder(canvas.captureStream(this.def.framerate), {
			mimeType: this.getCodec(this.def),
			videoBitsPerSecond: this.def.bitrate,
			audioBitsPerSecond: this.def.abitrate
		});
		// listener to store captured data
		this.capture.ondataavailable = (e: any) => {
			this.chunks.push(e.data);
		};
		// listener to create download after capture ends
		this.capture.onstop = () => {
			this.createDownload();
		};
		ScreenCapture.active = true;
		this.capture.start();
	}

	/**
	 * Stops recording & creates data download.
	 */
	stop() {
		ScreenCapture.active = false;
		if (!this.capture) {
			return;
		}
		this.capture.stop();
	}

	/**
	 * Creates a video definition based on available media containers & codecs.
	 *
	 * @returns {VideoDefinition|undefined}
	 *   Supported video definiton or `undefined`.
	 */
	private getDefinition(): VideoDefinition|undefined {
		const containers = ["mp4", "webm", "ogg"];
		const codecs = ["avc1", "h264", "av1", "vp9", "vp8"];
		const acodecs = ["aac", "vorbis", "mpeg"];

		for (const container of containers) {
			for (const codec of codecs) {
				for (const acodec of acodecs) {
					const def = this.buildDefinition(container, codec, acodec);
					if (this.checkDefinition(def)) {
						return def;
					}
				}
			}
		}
		// fallback to video only
		for (const container of containers) {
			for (const codec of codecs) {
				const def = this.buildDefinition(container, codec);
				if (this.checkDefinition(def)) {
					return def;
				}
			}
		}
		// fallback to default encoder
		for (const container of containers) {
			const def = this.buildDefinition(container);
			if (this.checkDefinition(def)) {
				return def;
			}
		}
		// no suitable codecs available
		return undefined;
	}

	/**
	 * Builds a video definition based on parameters.
	 *
	 * @param {string} container
	 *   Container type (mp4, webm, or ogg).
	 * @param {string=} codec
	 *   Video codec (avc1, h264, vp9, etc.).
	 * @param {string=} acodec
	 *   Audio codec (aac, vorbis, mpeg, etc.).
	 */
	private buildDefinition(container: string, codec?: string, acodec?: string): VideoDefinition {
		const def: any = {
			mime: "video/" + container,
			container: container,
			framerate: 30000 / 1001, // NTSC standard (29.97)
			bitrate: 1500000 // 1.5 Mbit/sec
		};
		if (codec) {
			def.codec = codec;
			if (acodec) {
				def.acodec = acodec;
				def.abitrate = 96000; // 96 Kbit/sec
			}
		}
		return def;
	}

	/**
	 * Checks if a video definition is supported by Web API & browser.
	 *
	 * @param {VideoDefinition} def
	 *   Video definition with MIME, container, codec, etc. information.
	 * @returns {boolean}
	 *   `true` if the browser supports encoding with definition's codecs.
	 */
	private checkDefinition(def: VideoDefinition): boolean {
		return MediaRecorder.isTypeSupported(this.getCodec(def));
	}

	/**
	 * Converts video definition to codec string formatted for use with Web API.
	 *
	 * @param {VideoDefinition} def
	 *   Video definition with MIME, container, codec, etc. information.
	 * @returns {string}
	 *   String formatted as "<mimetype>[;codecs=<video-encoder>[,<audio-encoder]]".
	 */
	private getCodec(def: VideoDefinition): string {
		if (def.codec) {
			if (def.acodec) {
				return def.mime + ";codecs=\"" + def.codec + "," + def.acodec + "\"";
			}
			return def.mime + ";codecs=" + def.codec;
		}
		return def.mime;
	}

	/**
	 * Gets a URL from recorded data.
	 *
	 * @returns {string}
	 *   Video data URL.
	 */
	private toDataURL(): string {
		let blob: Blob;
		if (this.def) {
			blob = new Blob(this.chunks, {type: this.def.mime});
		} else {
			blob = new Blob(this.chunks);
		}
		return URL.createObjectURL(blob);
	}

	/**
	 * Prepares video data for download.
	 */
	private createDownload() {
		Chat.log("client", "Creating video download ...");
		// NOTE: some browsers trim seconds from end of filename if extension is excluded
		const filename = "stendhal_" + DownloadUtil.timestamp() + "." + this.def!.container;
		DownloadUtil.build(filename, this.toDataURL()).execute();
	}

	/**
	 * Retrieves current activity state.
	 *
	 * @returns {boolean}
	 *   `true` if currently recording.
	 */
	public static isActive(): boolean {
		return ScreenCapture.active;
	}
}
