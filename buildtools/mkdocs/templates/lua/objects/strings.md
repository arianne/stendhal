
---
# Contents

[TOC]


---
# String Manipulation

Global table variable: `string`


---
## Description

The following methods have been added to the built-in Lua
[`string` table](https://www.lua.org/manual/5.3/manual.html#6.4).

---
# Methods


---
## string.builder
<div class="function">
    string.builder <span class="paramlist"></span>
</div>
<div class="function">
    string.builder <span class="paramlist">st</span>
</div>

- Creates a [StringBuilder][java.lang.StringBuilder].
- Parameters:
    - <span class="param">st</span>
      <span class="datatype">[string][LuaString]</span>
      String to append on instantiation.
- Returns:
  <span class="datatype">[StringBuilder][java.lang.StringBuilder]</span>
  New `StringBuilder` instance.


---
## string.endsWith
<div class="function">
    string.endsWith <span class="paramlist">st, suffix</span>
</div>

- Checks if a string ends with a set of characters.
- Parameters:
    - <span class="param">st</span>
      <span class="datatype">[string][LuaString]</span>
      The string to be checked.
    - <span class="param">suffix</span>
      <span class="datatype">[string][LuaString]</span>
      The suffix to be compared with.
- Returns:
  <span class="datatype">[bool][LuaBoolean]</span>
  `true` if ___suffix___ matches the end characters of ___st___.
- Aliases:
    - <span class="alias">string.endswith</span>


---
## string.isNumber
<div class="function">
    string.isNumber <span class="paramlist">st</span>
</div>

- Checks if a string contains numeric characters only.
- Parameters:
    - <span class="param">st</span>
      <span class="datatype">[string][LuaString]</span>
      The string to be checked.
- Returns:
  <span class="datatype">[bool][LuaBoolean]</span>
  `true` if all characters are numeric, `false` otherwise.
- Aliases:
    - <span class="alias">string.isnumber</span>
    - <span class="alias">string.isNumeric</span>
    - <span class="alias">string.isnumeric</span>


---
## string.ltrim
<div class="function">
    string.ltrim <span class="paramlist">st</span>
</div>

- Removes leading whitespace from a string.
- Parameters:
    - <span class="param">st</span>
      <span class="datatype">[string][LuaString]</span>
      The string to be trimmed.
- Returns:
  <span class="datatype">[string][LuaString]</span>
  Trimmed string.


---
## string.rtrim
<div class="function">
    string.rtrim <span class="paramlist">st</span>
</div>

- Removes trailing whitespace from a string.
- Parameters:
    - <span class="param">st</span>
      <span class="datatype">[string][LuaString]</span>
      The string to be trimmed.
- Returns:
  <span class="datatype">[string][LuaString]</span>
  Trimmed string.


---
## string.split
<div class="function">
    string.split <span class="paramlist">str, delim</span>
</div>

- Splits a string into a table.
- Parameters:
    - <span class="param">str</span>
      <span class="datatype">[string][LuaString]</span>
      The string to be split.
    - <span class="param">delim</span>
      <span class="datatype">[string][LuaString]</span>
      The delimiter character(s) used to split the string.
- Returns:
  <span class="datatype">[table][LuaTable]</span>
  List of strings.


---
## string.startsWith
<div class="function">
    string.startsWith <span class="paramlist">st, prefix</span>
</div>

- Checks if a string begins with a set of characters.
- Parameters:
    - <span class="param">st</span>
      <span class="datatype">[string][LuaString]</span>
      The string to be checked.
    - <span class="param">prefix</span>
      <span class="datatype">[string][LuaString]</span>
      The prefix to be compared with.
- Returns:
  <span class="datatype">[bool][LuaBoolean]</span>
  `true` if ___prefix___ matches the beginning characters of ___st___.
- Aliases:
    - <span class="alias">string.startswith</span>
    - <span class="alias">string.beginsWith</span>
    - <span class="alias">string.beginswith</span>


---
## string.trim
<div class="function">
    string.trim <span class="paramlist">st</span>
</div>

- Removes leading &amp; trailing whitespace from a string.
- Parameters:
    - <span class="param">st</span>
      <span class="datatype">[string][LuaString]</span>
      The string to be trimmed.
- Returns:
  <span class="datatype">[string][LuaString]</span>
  Trimmed string.


---
## string.valueOf
<div class="function">
    string.valueOf <span class="paramlist">obj</span>
</div>

- Retrieves string value of an object.
- Parameters:
    - <span class="param">obj</span>
      <span class="datatype">[Object][java.lang.Object]</span>
      Object instance to be converted.
- Returns:
  <span class="datatype">[string][LuaString]</span>
  String value of object.
- Aliases:
    - <span class="alias">string.valueof</span>


[java.lang.StringBuilder]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StringBuilder.html
[java.lang.Object]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html

[LuaBoolean]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaBoolean.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
