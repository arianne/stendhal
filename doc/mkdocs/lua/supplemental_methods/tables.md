
Tables {#lua_tables}
======

[TOC]

## Table Manipulation

The following methods have been added to the built-in Lua [table library](https://www.lua.org/manual/5.3/manual.html#6.6).

---
### table.concat
<span style="color:green; font-weight:bold;">table.concat</span>(tbl1, tbl2)
- Merges the contents of one table into another.
- Parameters:
  - ***tbl1:*** The table receiving the new content.
  - ***tbl2:*** The table containing the content to be copied.

---
### table.contains
<span style="color:green; font-wight:bold;">table.contains</span>(table, o)
- Checks if a table contains a value.
- Parameters:
  - ***table:*** Table to be checked.
  - ***o:*** The object to check for.
- Returns: <code>true</code> if <code>o</code> is in <code>table</code>.

---
### table.clean
<span style="color:green; font-weight:bold;">table.clean</span>(tbl)
- Removes <code>nil</code> values from a table.
- Parameters:
  - ***tbl:*** The table to be cleaned.
- Returns: Copy of <code>tbl</code> with <code>nil</code> values removed.

---
### table.join
<span style="color:green; font-wight:bold;">table.join</span>(table, delim)
- Joins a table of strings into a string.
- Parameters:
  - ***table:*** Table to be joined.
  - ***delim:*** (`string`) Character(s) to be used as separator.
- Returns: (`string`) The resulting string.
