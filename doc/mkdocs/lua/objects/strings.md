
[TOC]

---
## String Manipulation

Object instance: `string`

---
### Description

The following methods have been added to the built-in Lua
[string library](https://www.lua.org/manual/5.3/manual.html#6.4).

## Methods

---
### string.builder
<div class="function">
    string.builder <span class="params">()</span>
</div>
<div class="function">
    string.builder <span class="params">(st)</span>
</div>

- Creates a new instance of [java.lang.StringBuilder][].
- Parameters:
    - ___st:__ ([string][LuaString])_ String to append on instantiation.
- Returns: _([StringBuilder][java.lang.StringBuilder])_
    - New StringBuilder instance.

---
### string.endsWith
<div class="function">
    string.endsWith <span class="params">(st, suffix)</span>
</div>

- Checks if a string ends with a set of characters.
- Parameters:
    - ___st:__ ([string][LuaString])_ The string to be checked.
    - ___suffix:__ ([string][LuaString])_ The suffix to be compared with.
- Returns: _([bool][LuaBoolean])_
    - `true` if ___suffix___ matches then end characters of ___st___.
- Aliases:
    - <span style="color:green; font-style:italic;">string.endswith</span>

---
### string.isNumber
<div class="function">
    string.isNumber <span class="params">(st)</span>
</div>

- Checks if a string contains numeric characters only.
- Parameters:
    - ___st:__ ([string][LuaString])_ The string to be checked.
- Returns: _([bool][LuaBoolean])_
    - `true` if all characters are numeric, `false` otherwise.
- Aliases:
    - <span style="color:green; font-style:italic;">string.isnumber</span>
    - <span style="color:green; font-style:italic;">string.isNumeric</span>
    - <span style="color:green; font-style:italic;">string.isnumeric</span>

---
### string.ltrim
<div class="function">
    string.ltrim <span class="params">(st)</span>
</div>

- Removes leading whitespace from a string.
- Parameters:
    - ___st:__ ([string][LuaString])_ The string to be trimmed.
- Returns: _([string][LuaString])_
    - Trimmed string.

---
### string.rtrim
<div class="function">
    string.rtrim <span class="params">(st)</span>
</div>

- Removes trailing whitespace from a string.
- Parameters:
    - ___st:__ ([string][LuaString])_ The string to be trimmed.
- Returns: _([string][LuaString])_
    - Trimmed string.

---
### string.split
<div class="function">
    string.split <span class="params">(str, delim)</span>
</div>

- Splits a string into a table.
- Parameters:
    - ___str:__ ([string][LuaString])_ String to be split.
    - ___delim:__ ([string][LuaString])_ The delimiter character(s) used to split the string.
- Returns: _([table][LuaTable])_
    - List of strings.

---
### string.startsWith
<div class="function">
    string.startsWith <span class="params">(st, prefix)</span>
</div>

- Checks if a string begins with a set of characters.
- Parameters:
    - ___st:__ ([string][LuaString])_ The string to be checked.
    - ___prefix:__ ([string][LuaString])_ The prefix to be compared with.
- Returns: _([bool][LuaBoolean])_
    - `true` if ___prefix___ matches the beginning characters of ___st___.
- Aliases:
    - <span style="color:green; font-style:italic;">string.startswith</span>
    - <span style="color:green; font-style:italic;">string.beginsWith</span>
    - <span style="color:green; font-style:italic;">string.beginswith</span>

---
### string.trim
<div class="function">
    string.trim <span class="params">(st)</span>
</div>

- Removes leading &amp; trailing whitespace from a string.
- Parameters:
    - ___st:__ ([string][LuaString])_ The string to be trimmed.
- Returns: _([string][LuaString])_
    - Trimmed string.

---
### string.valueOf
<div class="function">
    string.valueOf <span class="params">(obj)</span>
</div>

- Retrieves string value of an object.
- Parameters:
    - ___obj:__ ([Object][java.lang.Object])_ Object instance to be converted.
- Returns: _([string][LuaString])_
    - String value of object.
- Aliases:
    - <span style="color:green; font-style:italic;">string.valueof</span>


[java.lang.StringBuilder]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StringBuilder.html
[java.lang.Object]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html

[LuaBoolean]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaBoolean.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
