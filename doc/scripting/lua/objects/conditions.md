
conditions {#lua_conditions}
==========

[TOC]

## Introduction

Object for creating {{StendhalFile|master|src/games/stendhal/server/entity/npc/ChatCondition.java|games.stendhal.server.entity.npc.ChatCondition}} instances.

## Methods

; ''<span style="color:green;">conditions:create</span>(function)''
: Creates a custom {{StendhalFile|master|src/games/stendhal/server/entity/npc/ChatCondition.java|ChatCondition}}.
: '''''function:''''' Lua function to be invoked when <code>ChatCondition.fire</code> is called.
: ''returns:'' New <code>ChatCondition</code> instance.

; ''<span style="color:green;">conditions:notCondition</span>(condition)''
: Creates a {{StendhalFile|master|src/games/stendhal/server/entity/npc/condition/NotCondition.java|NotCondition}}.
: '''''condition:''''' Can be a <code>ChatCondition</code>, <code>LuaValue</code> containing a <code>ChatCondition</code> instance, a Lua table of <code>ChatCondition</code> instances, or a function.
: ''returns:'' New <code>NotCondition</code> instance.

; ''<span style="color:green;">conditions:andCondition</span>(conditionList)''
: Creates an {{StendhalFile|master|src/games/stendhal/server/entity/npc/condition/AndCondition.java|AndCondition}}.
: '''''conditionList:''''' Lua table containing <code>ChatCondition</code> instances.
: ''returns:'' New <code>AndCondition</code> instance.
