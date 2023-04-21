
[TOC]

---
## Introduction

Object instance: `conditions`

---
### Description

For creating [`ChatCondition`][ChatCondition] instances.

---
## Methods

---
### conditions:andCondition
<div class="function">
    conditions:andCondition <span class="params">(conditionList)</span>
</div>
<div class="function">
    conditions:andC <span class="params">(conditionList)</span>
</div>

- Creates an [`AndCondition`][AndCondition].
- Parameters:
    - ___conditionList:___ Lua table containing [`ChatCondition`][ChatCondition] instances.
- Returns: New `AndCondition` instance.

---
### conditions:create
<div class="function">
    conditions:create <span class="params">(function)</span>
</div>
<div class="function">
    conditions:create <span class="params">(className, args)</span>
</div>

- Creates a custom [`ChatCondition`][ChatCondition].
- Parameters:
    - ___function:___ [`LuaFunction`][LuaFunction] to be invoked when `ChatCondition.fire` is
      called.
    - ___className:___ `ChatCondition` class basename.
    - ___args:___ `LuaTabl` of objects passed to the constructor.
- Returns: New `ChatCondition` instance.

---
### conditions:notCondition
<div class="function">
    conditions:notCondition <span class="params">(condition)</span>
</div>
<div class="function">
    conditions:notC <span class="params">(condition)</span>
</div>

- Creates a [`NotCondition`][NotCondition].
- Parameters:
    - ___condition:___ Can be a [`ChatCondition`][ChatCondition], [`LuaValue`][LuaValue] containing
      a `ChatCondition` instance, a [table][LuaTable] of `ChatCondition` instances, or a
      [`LuaFunction`][LuaFunction].
- Returns: New `NotCondition` instance.

---
### conditions:orCondition
<div class="function">
    conditions:orCondition <span class="params">(conditionList)</span>
</div>
<div class="function">
    conditions:orC <span class="params">(conditionList)</span>
</div>

- Creates an [`OrCondition`][OrCondition].
- Parameters:
    - ___conditionList:___ [`LuaTable`][LuaTable] containing [`ChatCondition`][ChatCondition]
      instances.
- Returns: New `OrCondition` instance or [`nil`][LuaNil] if failed.


[AndCondition]: /reference/java/games/stendhal/server/entity/npc/condition/AndCondition.html
[ChatCondition]: /reference/java/games/stendhal/server/entity/npc/ChatCondition.html
[NotCondition]: /reference/java/games/stendhal/server/entity/npc/condition/NotCondition.html
[OrCondition]: /reference/java/games/stendhal/server/entity/npc/condition/OrCondition.html

[LuaFunction]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaFunction.html
[LuaNil]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaNil.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
[LuaValue]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaValue.html
