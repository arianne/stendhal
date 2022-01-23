
game {#lua_game}
====

[TOC]

## Introduction

The main object that handles setting zone & adding entities to game.

## Methods

---
### game:add
; ''<span style="color:green">game:add</span>(object)''
: Adds an {{MarauroaFile|master|src/marauroa/common/game/RPObject.java|RPObject}} instance to the current zone.
: '''''object:''''' Object to add.

; ''<span style="color:green">game:add</span>(npc)''
: Adds an {{StendhalFile|master|src/games/stendhal/server/entity/npc/NPC.java|NPC}} instance to the current zone.
: '''''npc:''''' NPC to add.

; ''<span style="color:green">game:add</span>(creature, x, y)''
: Adds a {{StendhalFile|master|src/games/stendhal/server/entity/creature/Creature.java|Creature}} instance to the current zone.
: '''''creature:''''' Creature to add.
: '''''x:''''' Horizontal position of where to add creature.
: '''''y:''''' Vertical position of where to add creature.

---
### game:remove
; ''<span style="color:green">game:remove</span>(object)''
:
: '''''object:'''''

; ''<span style="color:green">game:remove</span>(npc)''
:
: '''''npc:'''''

---
### game:addGameEvent
; ''<span style="color:green">game:addGameEvent</span>(source, event, params)''
: Adds a new {{StendhalFile|master|src/games/stendhal/server/core/engine/GameEvent.java|GameEvent}}.
: '''''source:'''''
: '''''event:'''''
: '''''params:'''''

---
### game:setZone
; ''<span style="color:green">game:setZone</span>(name)''
: Sets the current zone.
: '''''name:''''' String identifier for zone to be set as current zone.
: ''returns:'' <code>true</code> if zone was successfully set.

; ''<span style="color:green">game:setZone</span>(zone)''
: Sets the current zone.
: '''''zone:''''' {{StendhalFile|master|src/games/stendhal/server/core/engine/StendhalRPZone.java|StendhalRPZone}} instance to set as current zone.
: ''returns:'' <code>true</code> if zone was successfully set.

---
### game:getZone
; ''<span style="color:green">game:getZone</span>(object)''
: Retrieves the zone where <code>object</code> is located.
: '''''object:''''' The {{MarauroaFile|master|src/marauroa/common/game/RPObject.java|RPObject}} from which the zone should be retrieved.
: ''returns:'' {{StendhalFile|master|src/games/stendhal/server/core/engine/StendhalRPZone.java|StendhalRPZone}} or <code>null</code> if it doesn't exists

; ''<span style="color:green">game:getZone</span>(name)''
: Retrieves a zone by string ID.
: '''''zoneName:''''' Name of zone to retrieve.
: ''returns:'' {{StendhalFile|master|src/games/stendhal/server/core/engine/StendhalRPZone.java|StendhalRPZone}} or <code>null</code> if it doesn't exist.

---
### game:setMusic
; ''<span style="color:green">game:setMusic</span>(filename, args)''
: Sets the music for the currently selected zone.
:; <span style="color:darkgreen; font-style:italic;">filename:</span>
:: File basename excluding .ogg extension.
:; <span style="color:darkgreen; font-style:italic;">args:</span>
:: Lua table of key=value integer values.
::; Valid keys&#58;
::: <span style="color:darkblue; font-style:italic;">volume:</span> Volume level (default: 100).
::: <span style="color:darkblue; font-style:italic;">x:</span> The X coordinate of the sound source (default: 1).
::: <span style="color:darkblue; font-style:italic;">y:</span> The Y coordinate of the sound source (default: 1).
::: <span style="color:darkblue; font-style:italic;">radius:</span> The radius from which the music can be heard (default: 10000).

---
### game:playerIsInZone
; ''<span style="color:green">game:playerIsInZone</span>(player, zoneName)''
:
: '''''player:'''''
: '''''zoneName:'''''
: ''returns:'' <code>boolean</code>

---
### game:getCreatures
; ''<span style="color:green">game:getCreatures</span>()''
:
: ''returns:'' An array of all available creatures.

---
### game:getCreature
; ''<span style="color:green">game:getCreature</span>(clazz)''
: Retrieves a {{StendhalFile|master|src/games/stendhal/server/entity/creature/Creature.java|Creature}} instance.
: '''''clazz:''''' String name of the creature.
: ''returns:'' Creature or <code>null</code> if doesn't exist.

---
### game:getItems
; ''<span style="color:green">game:getItems</span>()''
:
: ''returns:'' Array list of available items.

---
### game:getItem
; ''<span style="color:green">game:getItem</span>(name)''
:
: '''name:'''
: ''returns:'' Item instance or <code>null</code> if doesn't exist.

---
### game:modify
; ''<span style="color:green">game:modify</span>(entity)''
:
: '''''entity:'''''

---
### game:privateText
; ''<span style="color:green">game:privateText</span>(player, text)''
: Sends a private text to a player.
: '''''player:''''' Player to receive the message.
: '''''text:''''' Message text to send to player.

---
### game:getMessage
; ''<span style="color:green">game:getMessage</span>()''
:
: ''returns:'' <code>String</code>
