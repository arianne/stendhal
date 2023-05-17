
---
# Contents

[TOC]


---
# Introduction

Object instance: `conditions`


---
## Description

For creating [`ChatCondition`][ChatCondition] instances.


---
# Methods


---
## conditions:andCondition
<div class="function">
    conditions:andCondition <span class="paramlist">(conditionList)</span>
</div>

- Creates an [`AndCondition`][AndCondition].
- Parameters:
    - <span class="param">conditionList:</span> Lua table containing
      [`ChatCondition`][ChatCondition] instances.
- Returns: New `AndCondition` instance.
- Aliases:
    - _<span style="color:green;">conditions:andC</span>_


---
## conditions:create
<div class="function">
    conditions:create <span class="paramlist">(func)</span>
</div>
<div class="function">
    conditions:create <span class="paramlist">(className, args)</span>
</div>

- Creates a custom [`ChatCondition`][ChatCondition].
- Parameters:
    - <span class="param">func:</span> _([function][LuaFunction])_ Function to be invoked when
      `ChatCondition.fire` is called.
    - <span class="param">className:</span> _([string][LuaString])_ `ChatCondition` class basename.
    - <span class="param">args:</span> _([table][LuaTable])_ List of objects passed to the
      constructor.
- Returns: New `ChatCondition` instance or [nil][LuaNil].


---
## conditions:notCondition
<div class="function">
    conditions:notCondition <span class="paramlist">(condition)</span>
</div>

- Creates a [`NotCondition`][NotCondition].
- Parameters:
    - <span class="param">condition:</span> Can be a [`ChatCondition`][ChatCondition],
      [`LuaValue`][LuaValue] containing a `ChatCondition` instance, a [table][LuaTable] of
      `ChatCondition` instances, or a [`LuaFunction`][LuaFunction].
- Returns: New `NotCondition` instance.
- Aliases:
    - _<span style="color:green;">conditions:notC</span>_


---
## conditions:orCondition
<div class="function">
    conditions:orCondition <span class="paramlist">(conditionList)</span>
</div>

- Creates an [`OrCondition`][OrCondition].
- Parameters:
    - <span class="param">conditionList:</span> [`LuaTable`][LuaTable] containing
      [`ChatCondition`][ChatCondition] instances.
- Returns: New `OrCondition` instance or [`nil`][LuaNil] if failed.
- Aliases:
    - _<span style="color:green;">conditions:orC</span>_


[AndCondition]: /reference/java/games/stendhal/server/entity/npc/condition/AndCondition.html
[ChatCondition]: /reference/java/games/stendhal/server/entity/npc/ChatCondition.html
[NotCondition]: /reference/java/games/stendhal/server/entity/npc/condition/NotCondition.html
[OrCondition]: /reference/java/games/stendhal/server/entity/npc/condition/OrCondition.html

[LuaFunction]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaFunction.html
[LuaNil]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaNil.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
[LuaValue]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaValue.html
