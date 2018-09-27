/*
	Launch4j (http://launch4j.sourceforge.net/)
	Cross-platform Java application wrapper for creating Windows native executables.

	Copyright (c) 2015 Sebastian Bögl
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

package net.sf.launch4j.config;

import java.util.Arrays;
/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381058%28v=vs.85%29.aspx">VERSIONINFO resource</a>
 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381057%28v=vs.85%29.aspx">VarFileInfo BLOCK</a>
 */
public enum CharsetID implements Describable {
    /** 0x0000 */
    ASCII(0, Messages.getString("Charset.ascii")),
    /** 0x04E8 */
    ARABIC(1256, Messages.getString("Language.arabic")),
    /** 0x04E3 */
    CYRILLIC(1251, Messages.getString("Charset.cyrillic")),
    /** 0x04E5 */
    GREEK(1253, Messages.getString("Language.greek")),
    /** 0x04E7 */
    HEBREW(1255, Messages.getString("Language.hebrew")),
    /** 0x03A4 */
    SHIFT_JIS(932, Messages.getString("Charset.shift.jis")),
    /** 0x03B5 */
    SHIFT_KSC(949, Messages.getString("Charset.shift.ksc")),
    /** 0x04E2 */
    LATIN2(1250, Messages.getString("Charset.latin2")),
    /** 0x04E4 */
    MULTILINGUAL(1252, Messages.getString("Charset.multilingual")),
    /** 0x0B63 */
    BIG5(950, Messages.getString("Charset.big5")),
    /** 0x04E6 */
    TURKISH(1254, Messages.getString("Language.turkish")),
    /** 0x04B0 */
    UNICODE(1200, Messages.getString("Charset.unicode")),
    ;

    private static final CharsetID[] VALUES = CharsetID.values();

    static {
        Arrays.sort(VALUES, new DescribableComparator());
    }

    private final int id;
    private final String description;

    CharsetID(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getIndex() {
        for (int i = 0; i < VALUES.length; i++) {
            if (VALUES[i] == this) {
                return i;
            }
        }
        // should never happen
        return -1;
    }

    public static CharsetID[] sortedValues() {
        return VALUES;
    }
}
