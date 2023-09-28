
---
# Contents

[TOC]


---
# Introduction

Object instance: `luajava`


---
## Description

`luajava` is an object of the [LuajavaLib library][home.luaj]. It can be used to coerce Java static
objects to Lua or create new Java object instances.


---
# Examples

Example of exposing a static object or enum to Lua:

```lua
-- store a Java enum in a Lua global variable
ConversationStates = luajava.bindClass("games.stendhal.server.entity.npc.ConversationStates")

-- access the enum values like so
ConversationStates.IDLE
```

Example of creating an object instance:

```lua
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


---
# Synopsis

To make scripting easier, Stendhal employs a [master script][init.lua] &amp; some
[helper objects &amp; methods][objects] to handle the functionality mentioned above.


[home.luaj]: http://luaj.org/luaj.html

[init.lua]: https://github.com/arianne/stendhal/blob/master/src/games/stendhal/server/core/scripting/lua/init.lua

[objects]: /reference/lua/objects
