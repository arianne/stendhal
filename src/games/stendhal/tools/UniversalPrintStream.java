/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.tools;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;


/**
 * Stream output that enforces LF line endings & UTF-8 encoding.
 */
public class UniversalPrintStream extends PrintStream {

	public UniversalPrintStream() throws UnsupportedEncodingException {
		super(System.out, true, "UTF-8");
	}

	@Override
	public void println() {
		write("\n".getBytes(), 0, 1);
	}

	@Override
	public void println(final String x) {
		write((x + "\n").getBytes(), 0, x.length() + 1);
	}

	@Override
	public void println(final Object x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(final boolean x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(final char x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(final char[] x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(final double x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(final float x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(final int x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(final long x) {
		println(String.valueOf(x));
	}
}
