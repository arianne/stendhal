"use strict";

function parseLogEntry(input) {
	var res = "";
	res += stendhal.ui.html.esc(input);
	return res;
}

function test(input, output) {
	var res = parseLogEntry(input);
	if (res == output) {
		console.log(input, "|", output);
	} else {
		console.warn(input, "|", output, "|", res);
	}
}

test("",            "");
test("a",           "a");
test("a <oops",     "a &lt;oops");
test("#a",          "<span class=\"h\">a</span>");
test("#a.",         "<span class=\"h\">a</span>.");
test("a #b c d",    "a <span class=\"h\">b</span> c d");
test("a #'b c' d",  "a <span class=\"h\">b c</span> d");
test("a §b c d",    "a <span class=\"i\">b</span> c d");   // not used
test("a §'b c' d",  "a <span class=\"i\">b c</span> d");
test("a #§'b c' d", "a <span class=\"h\"><span class=\"i\">b c</span></span> d");
