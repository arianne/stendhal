
Strings {#lua_strings}
=======

[TOC]

## String Manipulation

The following methods have been added to the built-in Lua [string library](https://www.lua.org/manual/5.3/manual.html#6.4).

---
### string.startsWith
<span style="color:green; font-weight:bold;">string.startsWith</span>(st, prefix)
- Checks if a string begins with a set of characters.
- Parameters:
  - ***st:*** The string to be checked.
  - ***prefix:*** The prefix to be compared with.
- Returns: <code>true</code> if <code>prefix</code> matches the beginning characters of <code>st</code>.
- Aliases:
  - <span style="color:green; font-style:italic;">string.beginsWith</span>

---
### string.endsWith
<span style="color:green; font-weight:bold;">string.endsWith</span>(st, suffix)
- Checks if a string ends with a set of characters.
- Parameters:
  - ***st:*** The string to be checked.
  - ***suffix:*** The suffix to be compared with.
- Returns: <code>true</code> if <code>suffix</code> matches then end characters of <code>st</code>.

---
### string.isNumber
<span style="color:green; font-weight:bold;">string.isNumber</span>(st)
- Checks if a string contains numeric characters only.
- Parameters:
  - ***st:*** The string to be checked.
- Returns: <code>true</code> if all characters are numeric, <code>false</code> otherwise.
- Aliases:
  - <span style="color:green; font-style:italic;">string.isNumeric</span>

---
### string.trim
<span style="color:green; font-weight:bold;">string.trim</span>(st)
- Removes leading & trailing whitespace from a string.
- Parameters:
  - ***st:*** The string to be trimmed.
- Returns: Trimmed string.

---
### string.ltrim
<span style="color:green; font-weight:bold;">string.ltrim</span>(st)
- Removes leading whitespace from a string.
- Parameters:
  - ***st:*** The string to be trimmed.
- Returns: Trimmed string.

---
### string.rtrim
<span style="color:green; font-weight:bold;">string.rtrim</span>(st)
- Removes trailing whitespace from a string.
- Parameters:
  - ***st:*** The string to be trimmed.
- Returns: Trimmed string.

---
### string.builder
<span style="color:green; font-weight:bold;">string.builder</span>(st)
- Creates a new instance of {@link java.lang.StringBuilder}.
- Parameters:
  - ***st:*** (optional) String to append on instantiation.
- Returns: new StringBuilder instance.
