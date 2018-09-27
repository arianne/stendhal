/*
	Launch4j (http://launch4j.sourceforge.net/)
	Cross-platform Java application wrapper for creating Windows native executables.

	Copyright (c) 2013 toshimm
	All rights reserved.

	Redistribution and use in source and binary forms, with or without modification,
	are permitted provided that the following conditions are met:
	
	1. Redistributions of source code must retain the above copyright notice,
	   this list of conditions and the following disclaimer.
	
	2. Redistributions in binary form must reproduce the above copyright notice,
	   this list of conditions and the following disclaimer in the documentation
	   and/or other materials provided with the distribution.
	
	3. Neither the name of the copyright holder nor the names of its contributors
	   may be used to endorse or promote products derived from this software without
	   specific prior written permission.
	
	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
	THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
	FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
	AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
	OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
	OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.sf.launch4j;

import java.io.OutputStream;
import java.io.IOException;

/**
 * @author toshimm (2013)
 * 
 *         This class makes Japanese Kanji characters in MS932 charcode escaped
 *         in octal form.
 */
public class KanjiEscapeOutputStream extends OutputStream {

	protected OutputStream parent;

	public KanjiEscapeOutputStream(OutputStream out) {
		this.parent = out;
	}

	private final int MASK = 0x000000FF;
	private boolean state = true;

	public void write(int b) throws IOException {
		b = b & MASK;

		if (state) {
			if (0x00 <= b && b <= 0x7f) {
				this.parent.write(b);
			} else {
				this.octprint(b);
				if ((0x81 <= b && b <= 0x9f) || (0xe0 <= b && b <= 0xfc)) {
					this.state = false;
				}
			}
		} else {
			if ((0x40 <= b && b <= 0x7e) || (0x80 <= b && b <= 0xfc)) {
				this.octprint(b);
			} else if (0x00 <= b && b <= 0x7f) {
				this.parent.write(b);
			} else {
				this.octprint(b);
			}
			this.state = true;
		}
	}

	private void octprint(int b) throws IOException {
		String oct = "\\" + String.format("%o", b & MASK);
		for (int i = 0; i < oct.length(); ++i) {
			int bb = oct.charAt(i);
			this.parent.write(bb);
		}
	}
}
