
conditions {#lua_conditions}
==========

[TOC]

## Introduction

Object for creating {@link games.stendhal.server.entity.npc.ChatCondition} instances.

## Methods

---
### conditions:create
<span style="color:green; font-weight:bold;">conditions:create</span>(function)
- Creates a custom {@link games.stendhal.server.entity.npc.ChatCondition}.
- Parameters:
  - ***function:*** Lua function to be invoked when <code>ChatCondition.fire</code> is called.
- Returns: New <code>ChatCondition</code> instance.

---
### conditions:notCondition
<span style="color:green; font-weight:bold;">conditions:notCondition</span>(condition)
- Creates a {@link games.stendhal.server.entity.npc.condition.NotCondition}.
- Parameters:
  - ***condition:*** Can be a <code>ChatCondition</code>, <code>LuaValue</code> containing a <code>ChatCondition</code> instance, a Lua table of <code>ChatCondition</code> instances, or a function.
- Returns: New <code>NotCondition</code> instance.

---
### conditions:andCondition
<span style="color:green; font-weight:bold;">conditions:andCondition</span>(conditionList)
- Creates an {@link games.stendhal.server.entity.npc.condition.AndCondition}.
- Parameters:
  - ***conditionList:*** Lua table containing <code>ChatCondition</code> instances.
- Returns: New <code>AndCondition</code> instance.
