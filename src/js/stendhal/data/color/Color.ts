/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { MathUtil } from "../../util/MathUtil";


/**
 * Color representation class.
 */
abstract class ColorType {
	[index: string]: number|Function|undefined;

	/** Alpha level. */
	readonly A?: number;


	/**
	 * Creates a new color representation with alpha level.
	 *
	 * @param A {number}
	 *   Alpha level. Float value where 0 represents fully transparent and 1 fully opaque (min: 0,
	 *   max: 1, default: `undefined`).
	 */
	constructor(A?: number) {
		if (typeof(A) === "number") {
			this.A = Math.max(Math.min(A, 1), 0);
		}
	}

	/**
	 * Converts color to string representation.
	 *
	 * @return {string}
	 *   String representation.
	 */
	abstract toString(): string;
}

/**
 * RGB color representation class.
 */
export class RGBColor extends ColorType {

	/** Red level. */
	readonly R: number;
	/** Green level. */
	readonly G: number;
	/** Blue level. */
	readonly B: number;
	/** Alpha level. */
	//~ readonly A?: number;


	/**
	 * Creates a new RGB color representation.
	 *
	 * @param R {number}
	 *   Red color level (min: 0, max: 255).
	 * @param G {number}
	 *   Green color level (min: 0, max: 255).
	 * @param B {number}
	 *   Blue color level (min: 0, max: 255).
	 * @param A {number}
	 *   Alpha level. Float value where 0 represents fully transparent and 1 fully opaque (min: 0,
	 *   max: 1, default: 1).
	 */
	constructor(R: number, G: number, B: number, A?: number) {
		super(A);
		this.R = Math.max(Math.min(R, 255), 0);
		this.G = Math.max(Math.min(G, 255), 0);
		this.B = Math.max(Math.min(B, 255), 0);
	}

	/**
	 * Converts RGB color to string representation.
	 *
	 * @return {string}
	 *   RGB or RGBA formatted string. Example: "rgb(255, 255, 255)"
	 */
	override toString(): string {
		const rgb = this.R + ", " + this.G + ", " + this.B;
		if (typeof(this.A) === "number") {
			return "rgba(" + rgb + ", " + this.A + ")";
		}
		return "rgb(" + rgb + ")";
	}
}

/**
 * HSL color representation class.
 */
export class HSLColor extends ColorType {

	/** Hue degree. */
	readonly H: number;
	/** Saturation level. */
	readonly S: number;
	/** Lightness level. */
	readonly L: number;
	/** Alpha level. */
	//readonly A?: number;


	/**
	 * Creates a new HSL color representation.
	 *
	 * @param H {number}
	 *   Hue degree.
	 * @param S {number}
	 *   Saturation level. Float value representing between 0%-100% (min: 0, max: 1).
	 * @param L {number}
	 *   Lightness level. Float value representing between 0%-100% (min: 0, max: 1).
	 * @param A {number}
	 *   Alpha level. Float value where 0 represents fully transparent and 1 fully opaque (min: 0,
	 *   max: 1, default: 1).
	 */
	constructor(H: number, S: number, L: number, A?: number) {
		super(A);
		this.H = MathUtil.normDeg(H);
		this.S = Math.max(Math.min(S, 1), 0);
		this.L = Math.max(Math.min(L, 1), 0);
	}

	/**
	 * Converts HSL color to string representation.
	 *
	 * @return {string}
	 *   HSL or HSLA formatted string. Example: "hsl(300, 100%, 50%)"
	 */
	override toString(): string {
		const hsl = this.H + ", " + (this.S * 100) + "%, " + (this.L * 100) + "%";
		if (typeof(this.A) === "number") {
			return "hsla(" + hsl + ", " + this.A + ")";
		}
		return "hsl(" + hsl + ")";
	}
}

/**
 * Static class for color string representations and conversion.
 */
export class Color {
	public static readonly AQUA = "rgb(0, 255, 255)"; // #00FFFF
	public static readonly BLACK = "rgb(0, 0, 0)"; // #000000
	public static readonly BLUE = "rgb(0, 0, 255)"; // #0000FF
	public static readonly CYAN = Color.AQUA;
	public static readonly DARK_GRAY = "rgb(96, 96, 96)"; // #606060
	public static readonly GRAY = "rgb(128, 128, 128)"; // #808080
	public static readonly GREEN = "rgb(0, 128, 0)"; // #008000
	public static readonly LIGHT_GRAY = "rgb(192, 192, 192)"; // #C0C0C0
	public static readonly MAGENTA = "rgb(255, 0, 255)" // #FF00FF
	public static readonly ORANGE = "rgb(255, 165, 0)"; // #FFA500
	public static readonly PINK = "rgb(255, 192, 203)"; // #FFC0CB
	public static readonly RED = "rgb(255, 0, 0)"; // #FF0000
	public static readonly VIOLET = "rgb(238, 130, 238)"; // #EE82EE
	public static readonly WHITE = "rgb(255, 255, 255)"; // #FFFFFF
	public static readonly YELLOW = "rgb(255, 255, 0)"; // #FFFF00

	public static readonly CREATURE = Color.YELLOW;
	public static readonly DOMESTICANIMAL = "rgb(255, 150, 0)"; // #FF9600 (orange)
	public static readonly GHOST = Color.GRAY;
	public static readonly GROUP = "rgb(99, 61, 139)"; // #633D8B (dark violet)
	public static readonly NPC = "rgb(0, 150, 0)"; // #009600 (green)
	public static readonly PLAYER = Color.WHITE;
	public static readonly USER = Color.BLUE;

	public static readonly BACKGROUND = "rgb(204, 204, 204)"; // #CCCCCC (light gray)
	public static readonly BLOCKED = Color.RED;
	public static readonly COLLISION = Color.RED;
	public static readonly FLYOVER = "rgb(212, 158, 72)"; // #D49E48 (brown-orange)
	public static readonly PROTECTION = "rgb(202, 230, 202)"; // #CAE6CA (light green)
	public static readonly WALKBLOCK = "rgb(209, 144, 224)"; // #D190E0 (violet)

	public static readonly MOVING = "rgb(200, 255, 200)"; // #C8FFC8 (light green)
	public static readonly PORTAL = Color.WHITE;
	public static readonly PORTALOUTLINE = Color.BLACK;
	public static readonly CHEST = Color.VIOLET;

	public static readonly CHAT_CLIENT = Color.GRAY;
	public static readonly CHAT_DETAILED = Color.BLACK;
	public static readonly CHAT_EMOTE = Color.GROUP;
	public static readonly CHAT_ERROR = Color.RED;
	public static readonly CHAT_GROUP = "rgb(00, 00, 160)"; // #0000A0 (dark blue)
	public static readonly CHAT_HEAL = Color.GREEN;
	public static readonly CHAT_HIGHLIGHT = Color.BLUE;
	public static readonly CHAT_INFO = Color.ORANGE;
	public static readonly CHAT_NEGATIVE = Color.RED;
	public static readonly CHAT_NORMAL = Color.BLACK;
	public static readonly CHAT_POISON = Color.RED;
	public static readonly CHAT_POSITIVE = Color.GREEN;
	public static readonly CHAT_PRIVATE = Color.DARK_GRAY;
	public static readonly CHAT_RESPONSE = "rgb(0, 100, 0)"; // #006400 (dark green)
	public static readonly CHAT_SCENE = "rgb(87, 32, 2)"; // #572002 (brown)
	public static readonly CHAT_SERVER = Color.GRAY;
	public static readonly CHAT_SIG_NEGATIVE = Color.PINK;
	public static readonly CHAT_SIG_POSITIVE = "rgb(65, 105, 225)"; // #4169FF (light blue)
	public static readonly CHAT_SUPPORT = "rgb(255, 114, 0)"; // #FF7200 (orange)
	public static readonly CHAT_TUTORIAL = "rgb(172, 0, 172)"; // #AC00AC (violet)
	public static readonly CHAT_WARN = "rgb(160, 0, 0)"; // #A00000 // (red)


	/**
	 * Static members & methods only.
	 */
	private constructor() {
		// do nothing
	}

	static getStatBarColor(ratio: number): string {
		const red = Math.floor(Math.min((1 - ratio) * 2, 1) * 255);
		const green = Math.floor(Math.min(ratio * 2, 1) * 255);
		return "rgb(" + red + ", " + green + ", 0)";
	}

	/**
	 * Converts a number value to RGB color.
	 *
	 * @param n {number}
	 *   Value to be converted.
	 * @return {data.color.Color.RGBColor}
	 *   RGB color representation.
	 */
	static numToRGB(n: number): RGBColor {
		const r = (n >> 16) & 255;
		const g = (n >> 8) & 255;
		const b = n & 255;
		return new RGBColor(r, g, b);
	}

	/**
	 * Converts a number value to HSL color.
	 *
	 * @param n {number}
	 *   Value to be converted.
	 * @return {data.color.Color.HSLColor}
	 *   HSL color representation.
	 */
	static numToHSL(n: number): HSLColor {
		return Color.RGBToHSL(Color.numToRGB(n));
	}

	/**
	 * Converts hex string to RGB color.
	 *
	 * FIXME: this should do error checking
	 *
	 * @param h {string}
	 *   Hex value to be converted.
	 * @return {data.color.Color.RGBColor}
	 *   RGB color representation.
	 */
	static hexToRGB(h: string): RGBColor {
		h = h.replace(/^#/, "");
		/*
		const r = parseInt(h.substring(0, 2), 16);
		const g = parseInt(h.substring(2, 4), 16);
		const b = parseInt(h.substring(4, 6), 16);
		return new RGBColor(r, g, b);
		*/
		return Color.numToRGB(parseInt(h, 16));
	}

	/**
	 * Converts hex string to HSL color.
	 *
	 * @param h {string}
	 *   Hex value to be converted.
	 * @return {data.color.Color.HSLColor}
	 *   HSL color representation.
	 */
	static hexToHSL(h: string): HSLColor {
		return Color.RGBToHSL(Color.hexToRGB(h));
	}

	/**
	 * Converts RGB string to HSL.
	 *
	 * https://css-tricks.com/converting-color-spaces-in-javascript/
	 *
	 * @param rgb {data.color.Color.RGBColor}
	 *   RGB color representation.
	 * @return {data.color.Color.HSLColor}
	 *   HSL color representation.
	 */
	static RGBToHSL(rgb: RGBColor): HSLColor {
		const r = rgb.R / 255;
		const g = rgb.G / 255;
		const b = rgb.B / 255;
		const cmin = Math.min(r, g, b);
		const cmax = Math.max(r, g, b);
		const delta = cmax - cmin;

		// hue
		let h = 0;
		if (delta == 0) {
			h = 0;
		} else if (cmax == r) {
			h = ((g - b) / delta) % 6;
		} else if (cmax == g) {
			h = (b - r) / delta + 2;
		} else {
			h = (r - g) / delta + 4;
		}
		h = Math.round(h * 60);
		/*
		if (h < 0) {
			h += 360;
		}
		h /= 360;
		*/

		// lightness
		let l = (cmax + cmin) / 2;
		// saturation
		let s = delta == 0 ? 0 : delta / (1 - Math.abs(2 * l - 1));

		/*
		l = +(l * 100).toFixed(1);
		s = +(s * 100).toFixed(1);
		*/

		return new HSLColor(h, Number(s.toFixed(3)), Number(l.toFixed(3)));
	}

	/**
	 * Parses RBG color values.
	 *
	 * @param rbg {string}
	 *   RGB formatted string.
	 * @return {data.color.Color.RGBColor}
	 *   Object with R/G/B numerical values.
	 */
	static parseRGB(rgb: string): RGBColor {
		const tmp = rgb.replace(/^rgb\(/, "").replace(/\)$/, "").split(",");
		return new RGBColor(Number(tmp[0]), Number(tmp[1]), Number(tmp[2]));
	}

	/**
	 * Parses HSL color values.
	 *
	 * @parm hsl (strong}
	 *   HSL formatted string.
	 * @return {data.color.Color.HSLColor}
	 *   Object with H/S/L numerical values.
	 */
	static parseHSL(hsl: string): HSLColor {
		const tmp = hsl.replace(/^hsl\(/, "").replace(/\)$/, "").split(",");
		const h = Number(tmp[0]);
		// NOTE: `parseFloat` automatically trims trailing "%"
		const s = parseFloat(tmp[1]) / 100;
		const l = parseFloat(tmp[2]) / 100;
		return new HSLColor(h, s, l);
	}
}
