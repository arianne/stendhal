
---
# Contents

[TOC]


---
# Table Manipulation

Global table variable: `table`


---
## Description

The following methods have been added to the built-in Lua
[`table` table](https://www.lua.org/manual/5.3/manual.html#6.6).

---
# Methods


---
## table.clean
<div class="function">
    table.clean <span class="paramlist">tbl</span>
</div>

- Removes [`nil`][LuaNil] values from a table.
- Parameters:
    - <span class="param">tbl</span>
      <span class="datatype">[table][LuaTable]</span>
      The table to be cleaned.
- Returns:
  <span class="datatype">[table][LuaTable]</span>
  Copy of ___tbl___ with `nil` values removed.


---
## table.concat
<div class="function">
    table.concat <span class="paramlist">tbl1, tbl2</span>
</div>

- Merges the contents of one table into another.
- Parameters:
    - <span class="param">tbl1</span>
      <span class="datatype">[table][LuaTable]</span>
      The table receiving the new content.
    - <span class="param">tbl2</span>
      <span class="datatype">[table][LuaTable]</span>
      The table containing the content to be copied.


---
## table.contains
<div class="function">
    table.contains <span class="paramlist">tbl, o</span>
</div>

- Checks if a table contains a value.
- Parameters:
    - <span class="param">tbl</span>
      <span class="datatype">[table][LuaTable]</span>
      Table to be checked.
    - <span class="param">obj</span>
      <span class="datatype">[Object][java.lang.Object]</span>
      The object to check for.
- Returns:
  <span class="datatype">[bool][LuaBoolean]</span>
  `true` if ___obj___ is in ___table___.


---
## table.join
<div class="function">
    table.join <span class="paramlist">tbl</span>
</div>
<div class="function">
    table.join <span class="paramlist">tbl, delim</span>
</div>

- Converts a list of strings into a string.
- Parameters:
    - <span class="param">tbl</span>
      <span class="datatype">[table][LuaTable]</span>
      Table to be joined.
    - <span class="param">delim</span>
      <span class="datatype">[string][LuaString]</span>
      Character(s) to be used as separator.
- Returns:
  <span class="datatype">[string][LuaString]</span>
  The resulting string.


---
## table.toList
<div class="function">
    table.toList <span class="paramlist">tbl</span>
</div>

- Converts an indexed [table][LuaTable] to Java [`List`][java.util.List].
- Parameters:
    - <span class="param">tbl</span>
      <span class="datatype">[table][LuaTable]</span>
      Table with contents to be transferred to new list.
- Returns: New `List` instance.
- Aliases:
    - <span class="alias">table.tolist</span>


[java.lang.Object]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html
[java.util.List]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html

[LuaBoolean]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaBoolean.html
[LuaNil]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaNil.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
