/***************************************************************************
 *                   (C) Copyright 2003-2026 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "stendhal";
import { ImageRef } from "./ImageRef";

export class ImageRefImpl extends ImageRef {
	private refCount = 0;
	private lastFreed?: Date;
	private closed = false;
	private loaded: Promise<void>;
	protected promiseResolve!: Function;

	constructor(protected filename: string) {
		super();
		this.loaded = new Promise((resolve) => {
			this.promiseResolve = resolve;
		})
	}

	async load() {
		let url = this.filename + "?v=" + stendhal.data.build.version;
		let response = await fetch(url);
		if (!response.ok || this.closed) {
			this.promiseResolve(undefined)
			return;
		}
		let blob = await response.blob();
		if (this.closed) {
			this.promiseResolve(undefined)
			return;
		}
		let bitmap = await createImageBitmap(blob);
		if (this.closed) {
			bitmap.close();
			this.promiseResolve(undefined)
			return;
		}
		this.image = bitmap;
		this.promiseResolve(bitmap);
	}

	/**
	 * called internally by ImageManager
	 */
	use() {
		this.refCount++;
		this.lastFreed = undefined;
	}

	/**
	 * called internally by ImageManager
	 */
	override free() {
		this.refCount--;
		if (this.refCount < 0) {
			console.error("Negative reference count", this);
		}
		if (this.refCount <= 0) {
			this.lastFreed = new Date();
		}
	}

	shouldCleanup(olderThan: Date) {
		if (this.refCount > 0 || !this.lastFreed) {
			return false;
		}
		return (this.lastFreed < olderThan);
	}

	/**
	 * called internally by ImageManager
	 */
	close() {
		this.closed = true;
		this.image?.close();
		this.image = undefined;
	}

	override async waitFor(): Promise<void> {
		return this.loaded
	}
}
