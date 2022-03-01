
[TOC]

## Introduction

`luajava` is an object of the LuajavaLib library. It can be used to coerce Java static objects to Lua or create new Java object instances.

Example of exposing a static object & enums to Lua:
```
-- store a Java enum in a Lua global variable
ConversationStates = luajava.bindClass("games.stendhal.server.entity.npc.ConversationStates")

-- access the enum values like so
ConversationStates.IDLE
```

Example of creating an object instance:
```
-- store instance in local variable
local dog = luajava.newInstance("games.stendhal.server.entity.npc.SilentNPC")
-- access object methods like so
dog:setEntityClass("animal/puppy")
dog:setPosition(2, 5)

-- class with constructor using parameters
local speaker = luajava.newInstance("games.stendhal.server.entity.npc.SpeakerNPC", "Frank")
speaker:setOutfit("body=0,head=0,eyes=0,hair=5,dress=5")
speaker:setPosition(2, 6)
```

To make scripting easier, Stendhal employs a [master script](https://github.com/arianne/stendhal/blob/master/src/games/stendhal/server/core/scripting/lua/init.lua) & some helper objects & methods to handle the functionality mentioned above.
