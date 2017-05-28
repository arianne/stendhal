/***************************************************************************
 *                   (C) Copyright 2016-2017 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    * 
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var stendhal = window.stendhal = window.stendhal || {};

function test(input, output) {
	var res = stendhal.ui.chatLog.formatLogEntry(input);
	if (res === output) {
		console.log(input, "|", output);
	} else {
		console.warn(input, "|", output, "|", res);
		window.failedtests.push("chatlog: " + input + " | " + output + " | " + res);
	}
}

test("",            "");
test("a",           "a");
test("a <oops",     "a &lt;oops");
test("#a",          "<span class=\"logh\">a</span>");
test("##a",         "#a");
test("#a#b",        "<span class=\"logh\">a#b</span>");
test("#a.",         "<span class=\"logh\">a</span>.");
test("a #b c d",    "a <span class=\"logh\">b</span> c d");
test("a #'b c' d",  "a <span class=\"logh\">b c</span> d");
test("a §b c d",    "a <span class=\"logi\">b</span> c d");   // not used
test("a §'b c' d",  "a <span class=\"logi\">b c</span> d");
test("a #§'b c' d", "a <span class=\"logh\"><span class=\"logi\">b c</span></span> d");
test("\\\\a",       "\\a");
test("\\§a",        "§a");
test("#https://stendhalgame.org", "<span class=\"logh\">https://stendhalgame.org</span>");
