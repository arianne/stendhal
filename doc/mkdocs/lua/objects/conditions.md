
[TOC]

## Introduction

Object for creating [ChatCondition][] instances.

## Methods

---
### conditions:create
<span style="color:green; font-weight:bold;">conditions:create</span>(function)

- Creates a custom [ChatCondition][].
- Parameters:
    - ***function:*** Lua function to be invoked when `ChatCondition.fire` is called.
- Returns: New `ChatCondition` instance.

---
### conditions:notC
<span style="color:green; font-weight:bold;">conditions:notC</span>(condition)

- Creates a [NotCondition][].
- Parameters:
    - ***condition:*** Can be a `ChatCondition`, `LuaValue` containing a `ChatCondition` instance, a Lua table of `ChatCondition` instances, or a function.
- Returns: New `NotCondition` instance.

---
### conditions:andC
<span style="color:green; font-weight:bold;">conditions:andC</span>(conditionList)

- Creates an [AndCondition][].
- Parameters:
    - ***conditionList:*** Lua table containing `ChatCondition` instances.
- Returns: New `AndCondition` instance.

---
### conditions:orC
<span style="color:green; font-weight:bold;">conditions:orC</span>(conditionList)

- Creates an [OrCondition][].
- Parameters:
    - ***conditionList:*** Lua table containing `ChatCondition` instances.
- Returns: New `OrCondition` instance.


[AndCondition]: ../../java/games/stendhal/server/entity/npc/condition/AndCondition.html
[ChatCondition]: ../../java/games/stendhal/server/entity/npc/ChatCondition.html
[NotCondition]: ../../java/games/stendhal/server/entity/npc/condition/NotCondition.html
[OrCondition]: ../../java/games/stendhal/server/entity/npc/condition/OrCondition.html
