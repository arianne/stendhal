
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
<div class="function">
    conditions:andC <span class="paramlist">(conditionList)</span>
</div>

- Creates an [`AndCondition`][AndCondition].
- Parameters:
    - <span class="param">conditionList:</span> Lua table containing
      [`ChatCondition`][ChatCondition] instances.
- Returns: New `AndCondition` instance.


---
## conditions:create
<div class="function">
    conditions:create <span class="paramlist">(function)</span>
</div>
<div class="function">
    conditions:create <span class="paramlist">(className, args)</span>
</div>

- Creates a custom [`ChatCondition`][ChatCondition].
- Parameters:
    - <span class="param">function:</span> [`LuaFunction`][LuaFunction] to be invoked when
      `ChatCondition.fire` is called.
    - <span class="param">className:</span> `ChatCondition` class basename.
    - <span class="param">args:</span> `LuaTabl` of objects passed to the constructor.
- Returns: New `ChatCondition` instance.


---
## conditions:notCondition
<div class="function">
    conditions:notCondition <span class="paramlist">(condition)</span>
</div>
<div class="function">
    conditions:notC <span class="paramlist">(condition)</span>
</div>

- Creates a [`NotCondition`][NotCondition].
- Parameters:
    - <span class="param">condition:</span> Can be a [`ChatCondition`][ChatCondition],
      [`LuaValue`][LuaValue] containing a `ChatCondition` instance, a [table][LuaTable] of
      `ChatCondition` instances, or a [`LuaFunction`][LuaFunction].
- Returns: New `NotCondition` instance.


---
## conditions:orCondition
<div class="function">
    conditions:orCondition <span class="paramlist">(conditionList)</span>
</div>
<div class="function">
    conditions:orC <span class="paramlist">(conditionList)</span>
</div>

- Creates an [`OrCondition`][OrCondition].
- Parameters:
    - <span class="param">conditionList:</span> [`LuaTable`][LuaTable] containing
      [`ChatCondition`][ChatCondition] instances.
- Returns: New `OrCondition` instance or [`nil`][LuaNil] if failed.


[AndCondition]: /reference/java/games/stendhal/server/entity/npc/condition/AndCondition.html
[ChatCondition]: /reference/java/games/stendhal/server/entity/npc/ChatCondition.html
[NotCondition]: /reference/java/games/stendhal/server/entity/npc/condition/NotCondition.html
[OrCondition]: /reference/java/games/stendhal/server/entity/npc/condition/OrCondition.html

[LuaFunction]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaFunction.html
[LuaNil]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaNil.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
[LuaValue]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaValue.html
