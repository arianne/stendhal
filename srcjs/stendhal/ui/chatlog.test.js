"use strict";


function test(input, output) {
	var res = stendhal.ui.chatLog.formatLogEntry(input);
	if (res == output) {
		console.log(input, "|", output);
	} else {
		console.warn(input, "|", output, "|", res);
		window.failedtests.push("chatlog: " + input + " | " + output + " | " + res)
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
test("#http://stendhalgame.org", "<span class=\"logh\">http://stendhalgame.org</span>");
