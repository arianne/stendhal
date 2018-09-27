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
public enum LanguageID implements Describable {
    ALBANIAN(0x041C, Messages.getString("Language.albanian")),
    ARABIC(0x0401, Messages.getString("Language.arabic")),
    BAHASA(0x0421, Messages.getString("Language.bahasa")),
    DUTCH_BELGIAN(0x0813, Messages.getString("Language.belgian.dutch")),
    FRENCH_BELGIAN(0x080C, Messages.getString("Language.belgian.french")),
    BULGARIAN(0x0402, Messages.getString("Language.bulgarian")),
    FRENCH_CANADIAN(0x0C0C, Messages.getString("Language.canadian.french")),
    CASTILIAN_SPANISH(0x040A, Messages.getString("Language.spanish.castilian")),
    CATALAN(0x0403, Messages.getString("Language.catalan")),
    CROATO_SERBIAN_LATIN(0x041A, Messages.getString("Language.croato.serbian.latin")),
    CZECH(0x0405, Messages.getString("Language.czech")),
    DANISH(0x0406, Messages.getString("Language.danish")),
    DUTCH(0x0413, Messages.getString("Language.dutch")),
    ENGLISH_UK(0x0809, Messages.getString("Language.english.uk")),
    ENGLISH_US(0x0409, Messages.getString("Language.english.us")),
    FINNISH(0x040B, Messages.getString("Language.finnish")),
    FRENCH(0x040C, Messages.getString("Language.french")),
    GERMAN(0x0407, Messages.getString("Language.german")),
    GREEK(0x0408, Messages.getString("Language.greek")),
    HEBREW(0x040D, Messages.getString("Language.hebrew")),
    HUNGARIAN(0x040E, Messages.getString("Language.hungarian")),
    ICELANDIC(0x040F, Messages.getString("Language.icelandic")),
    ITALIAN(0x0410, Messages.getString("Language.italian")),
    JAPANESE(0x0411, Messages.getString("Language.japanese")),
    KOREAN(0x0412, Messages.getString("Language.korean")),
    NORWEGIAN_BOKMAL(0x0414, Messages.getString("Language.norwegian.bokmal")),
    NORWEGIAN_NYNORSK(0x0814, Messages.getString("Language.norwegian.nynorsk")),
    POLISH(0x0415, Messages.getString("Language.polish")),
    PORTUGUESE_BRAZIL(0x0416, Messages.getString("Language.portuguese.brazil")),
    PORTUGUESE_PORTUGAL(0x0816, Messages.getString("Language.portuguese.portugal")),
    RHAETO_ROMANIC(0x0417, Messages.getString("Language.rhaeto.romanic")),
    ROMANIAN(0x0418, Messages.getString("Language.romanian")),
    RUSSIAN(0x0419, Messages.getString("Language.russian")),
    SERBO_CROATIAN_CYRILLIC(0x081A, Messages.getString("Language.serbo.croatian.cyrillic")),
    SIMPLIFIED_CHINESE(0x0804, Messages.getString("Language.chinese.simplified")),
    SLOVAK(0x041B, Messages.getString("Language.slovak")),
    SPANISH_MEXICO(0x080A, Messages.getString("Language.spanish.mexico")),
    SWEDISH(0x041D, Messages.getString("Language.swedish")),
    FRENCH_SWISS(0x100C, Messages.getString("Language.swiss.french")),
    GERMAN_SWISS(0x0807, Messages.getString("Language.swiss.german")),
    ITALIAN_SWISS(0x0810, Messages.getString("Language.swiss.italian")),
    THAI(0x041E, Messages.getString("Language.thai")),
    TRADITIONAL_CHINESE(0x0404, Messages.getString("Language.chinese.traditional")),
    TURKISH(0x041F, Messages.getString("Language.turkish")),
    URDU(0x0420, Messages.getString("Language.urdu")),
    ;

    private static final LanguageID[] VALUES = LanguageID.values();

    static {
        Arrays.sort(VALUES, new DescribableComparator());
    }

    private final int id;
    private final String description;

    LanguageID(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
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

    public static LanguageID[] sortedValues() {
        return VALUES;
    }
}
