
[TOC]

## Introduction

The main object that handles setting zone & adding entities to game.

## Methods

---
### game:add
<span style="color:green; font-weight:bold;">game:add</span>(object)

- Adds an {@link marauroa.common.game.RPObject} instance to the current zone.
- Parameters:
    - ***object:*** Object to add.

<span style="color:green; font-weight:bold;">game:add</span>(npc)

- Adds an {@link games.stendhal.server.entity.npc.NPC} instance to the current zone.
- Parameters:
    - ***npc:*** NPC to add.

<span style="color:green; font-weight:bold;">game:add</span>(creature, x, y)

- Adds a {@link games.stendhal.server.entity.creature.Creature} instance to the current zone.
- Parameters:
    - ***creature:*** Creature to add.
    - ***x:*** Horizontal position of where to add creature.
    - ***y:*** Vertical position of where to add creature.

---
### game:remove
<span style="color:green; font-weight:bold;">game:remove</span>(object)

- Parameters:
    - ***object:***

<span style="color:green; font-weight:bold;">game:remove</span>(npc)

- Parameters:
    - ***npc:***

---
### game:addGameEvent
<span style="color:green; font-weight:bold;">game:addGameEvent</span>(source, event, params)

- Adds a new {@link games.stendhal.server.core.engine.GameEvent}.
- Parameters:
    - ***source:***
    - ***event:***
    - ***params:***

---
### game:setZone
<span style="color:green; font-weight:bold;">game:setZone</span>(name)

- Sets the current zone.
- Parameters:
    - ***name:*** String identifier for zone to be set as current zone.
- Returns: <code>true</code> if zone was successfully set.

<span style="color:green; font-weight:bold;">game:setZone</span>(zone)

- Sets the current zone.
- Parameters:
    - ***zone:*** {@link games.stendhal.server.core.engine.StendhalRPZone} instance to set as current zone.
- Returns: <code>true</code> if zone was successfully set.

---
### game:getZone
<span style="color:green; font-weight:bold;">game:getZone</span>(object)

- Retrieves the zone where <code>object</code> is located.
- Parameters:
    - ***object:*** The {@link marauroa.common.game.RPObject} from which the zone should be retrieved.
- Returns: {@link games.stendhal.server.core.engine.StendhalRPZone} or <code>null</code> if it doesn't exists

<span style="color:green; font-weight:bold;">game:getZone</span>(name)

- Retrieves a zone by string ID.
    - ***zoneName:*** Name of zone to retrieve.
- Returns: {@link games.stendhal.server.core.engine.StendhalRPZone} or <code>null</code> if it doesn't exist.

---
### game:setMusic
<span style="color:green; font-weight:bold;">game:setMusic</span>(filename, args)

- Sets the music for the currently selected zone.
- Parameters:
    - ***filename:*** File basename excluding .ogg extension.
    - ***args:*** Lua table of key=value integer values.
        - Valid keys:
            - <span style="color:darkblue; font-style:italic;">volume:</span> Volume level (default: 100).
            - <span style="color:darkblue; font-style:italic;">x:</span> The X coordinate of the sound source (default: 1).
            - <span style="color:darkblue; font-style:italic;">y:</span> The Y coordinate of the sound source (default: 1).
            - <span style="color:darkblue; font-style:italic;">radius:</span> The radius from which the music can be heard (default: 10000).

---
### game:playerIsInZone
<span style="color:green; font-weight:bold;">game:playerIsInZone</span>(player, zoneName)

- Parameters:
    - ***player:***
    - ***zoneName:***
- Returns: <code>boolean</code>

---
### game:getCreatures
<span style="color:green; font-weight:bold;">game:getCreatures</span>()

- Returns: An array of all available creatures.

---
### game:getCreature
<span style="color:green; font-weight:bold;">game:getCreature</span>(clazz)

- Retrieves a {@link games.stendhal.server.entity.creature.Creature} instance.
    - ***clazz:*** String name of the creature.
- Returns: Creature or <code>null</code> if doesn't exist.

---
### game:getItems
<span style="color:green; font-weight:bold;">game:getItems</span>()

- Returns: Array list of available items.

---
### game:getItem
<span style="color:green; font-weight:bold;">game:getItem</span>(name)

- Parameters:
    - ***name:***
- Returns: Item instance or <code>null</code> if doesn't exist.

---
### game:modify
<span style="color:green; font-weight:bold;">game:modify</span>(entity)

- Parameters:
    - ***entity:***

---
### game:privateText
<span style="color:green; font-weight:bold;">game:privateText</span>(player, text)

- Sends a private text to a player.
- Parameters:
    - ***player:*** Player to receive the message.
    - ***text:*** Message text to send to player.

---
### game:getMessage
<span style="color:green; font-weight:bold;">game:getMessage</span>()

- Returns: <code>String</code>
