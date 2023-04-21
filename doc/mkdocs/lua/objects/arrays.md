
[TOC]

---
## Introduction

Object instance: `arrays`

---
### Description

Handles some conversion of Java [arrays][java.util.Arrays] &amp; [lists][java.util.List] to
[Lua tables][LuaTable].

---
## Methods

---
### arrays:toArray
<div class="function">
    arrays:toArray <span class="params">(table)</span>
</div>

- Converts a [`LuaTable`][LuaTable] to Java [array][java.util.Arrays].
- Parameters:
    - ___table:___ `LuaTable` with contents to be transferred to new array.
- Returns: New `Object[]` instance.

---
### arrays:toList
<div class="function">
    arrays:toList <span class="params">(table)</span>
</div>

- Converts a [`LuaTable`][LuaTable] to Java [`List`][java.util.List].
- Parameters:
    - ___table:___ `LuaTable` with contents to be transferred to new list.
- Returns: New `List<Object>` instance.

---
### arrays:toTable
<div class="function">
    arrays:toTable <span class="params">(list)</span>
</div>

- Converts a Java [array][java.util.Arrays] or [`List`][java.util.List] to a [`LuaTable`][LuaTable].
- Parameters:
    - ___list:___ Java array or `List`.
- Returns: New `LuaTable` with contents of ___list___ added.


[java.lang.Object]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html
[java.util.Arrays]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html
[java.util.List]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html

[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
