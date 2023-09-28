
---
# Contents

[TOC]


---
# Introduction

Object instance: `arrays`


---
## Description

<span class="deprecated">Methods in this object will be replaced with
[table object][tables] in future releases.</span>

Handles some conversion between Java [arrays][java.util.Arrays] or [lists][java.util.List] &amp; Lua
[tables][LuaTable].


---
# Methods


---
## arrays:fromTable
<div class="function">
    arrays:fromTable <span class="paramlist">table</span>
</div>

- Converts an indexed [table][LuaTable] to Java [array][java.util.Arrays].
- Parameters:
    - <span class="param">table</span>
      <span class="datatype">[table][LuaTable]</span>
      Table with contents to be transferred to new array.
- Returns: New `Object[]` instance.
- <span class="todo"></span>
    - _Replace with `table.toArray`._


---
## arrays:toArray
<div class="function">
    arrays:toArray <span class="paramlist">table</span>
</div>

- <span class="deprecated">Use [arrays:fromTable](#arraysfromtable).</span>
- Converts an indexed [table][LuaTable] to Java [array][java.util.Arrays].
- Parameters:
    - <span class="param">table</span>
      <span class="datatype">[table][LuaTable]</span>
      Table with contents to be transferred to new array.
- Returns: New `Object[]` instance.


---
## arrays:toList
<div class="function">
    arrays:toList <span class="paramlist">table</span>
</div>

- <span class="deprecated">Use [table.toList].</span>
- Converts an indexed [table][LuaTable] to Java [`List`][java.util.List].
- Parameters:
    - <span class="param">table</span>
      <span class="datatype">[table][LuaTable]</span>
      Table with contents to be transferred to new list.
- Returns: New `List` instance.


---
## arrays:toTable
<div class="function">
    arrays:toTable <span class="paramlist">list</span>
</div>

- Converts a Java [array][java.util.Arrays] or [list][java.util.List] to Lua [table][LuaTable].
- Parameters:
    - <span class="param">list</span>
      Java array or `List`.
- Returns: New table with contents of ___list___ added.
- <span class="todo"></span>
    - _Replace with `table.fromArray` &amp; `table.fromList`._


[table.toList]: /reference/lua/objects/tables/#tabletolist
[tables]: /reference/lua/objects/tables/

[java.lang.Object]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html
[java.util.Arrays]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html
[java.util.List]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html

[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
