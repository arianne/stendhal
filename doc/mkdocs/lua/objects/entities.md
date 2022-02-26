
entities {#lua_entities}
========

[TOC]

## Introduction

See also: [StendhalAPI#Entities](https://stendhalgame.org/wiki/StendhalAPI#Entities) for public methods that can be performed on `Entity` objects.

## Methods


---
### entities:create
<span style="color:green; font-weight:bold;">entities:create</span>(name)
- Creates a new entity.
- Parameters:
  - ***def:*** Entity definition table.
- Returns: games.stendhal.server.entity.Entity New entity instance.
- See also: @ref games.stendhal.server.core.scripting.lua.LuaEntityHelper#create

Options for `def` table:

General:

| Key         | Type   | Description                                                 |
| ----------- | ------ | ----------------------------------------------------------- |
| type        | string | "SpeakerNPC", "SilentNPC", "Sign", "ShopSign", or "Reader". |
| pos         | table  | Entity position relative to zone ({x, y}).                  |
| description | string | Information for "look" command.                             |
| class       | string | |
| subclass    | string | |
| resistance  | int    | |
| size        | table  | {w, h}                                                      |
| cursor      | string | |
| visibility  | int    | |
| menu        | string | |


SpeakerNPC & SilentNPC types:

| Key | Type | Description |
| --- | ---- | ----------- |
| dir | Direction | Entity's initial facing direction. |
| ignoresCollision | boolean | |
| path             | table   | |
| path.nodes       | table   | |
| path.loop        | boolean | |
| path.retrace     | boolean | |
| path.collisionAction | CollisionAction | |
| speed                | double          | |
| basehp               | int             | |
| hp                   | int             | |
| outfit               | table           | |
| outfit.layers        | string          | |
| outfit.colors        | table           | |
| idea                 | string          | |
| sounds               | table<string>   | |
| teleports            | boolean         | |


SpeakerNPC type:

| Key              | Type               | Description |
| ---------------- | ------------------ | ----------- |
| name             | string             | |
| idleDir          | Direction          | |
| chatTimeout      | long               | |
| perceptionRange  | int                | |
| currentState     | ConversationStates | |
| greeting         | table              | |
| greeting.text    | string             | |
| greeting.action  | ChatAction         | |
| replies          | table              | |
| replies.quest    | string             | Reply to "quest"/"task".                      |
| replies.job      | string             | Reply to "job".                               |
| replies.help     | string             | Reply to "help".                              |
| replies.offer    | string             | Reply to "offer".                             |
| replies.bye      | string             | Reply to "bye"/"goodbye".                     |
| alternativeImage | string             | Image that will be displayed on NPCs webpage. |


Sign type:

| Key     | Type    | Description |
| ------- | ------- | ----------- |
| text    | string  | |
| visible | boolean | |


ShopSign type:

| Key     | Type    | Description |
| ------- | ------- | ----------- |
| name    | string  | |
| title   | string  | |
| caption | string  | |
| seller  | boolean | |


---
### entities:getPlayer
<span style="color:green; font-weight:bold;">entities:getPlayer</span>(name)
- Retrieves a logged in Player.
- Parameters:
  - ***name:*** (`string`) Name of player.
- Returns:
  - Logged in {@link games.stendhal.server.entity.player.Player} or `nil`.
- Usage:

        local player = entities:getPlayer("foo")


---
### entities:getNPC
<span style="color:green; font-weight:bold;">entities:getNPC</span>(name)
- Retrieves an existing SpeakerNPC.
- Parameters:
  - ***name:*** (`String`) Name of NPC.
- Returns:
  - {@link games.stendhal.server.entity.npc.SpeakerNPC} instance or `null`.

---
### entities:getItem
<span style="color:green; font-weight:bold;">entities:getItem</span>(name)
- Retrieves a registered Item.
- Parameters:
  - ***name:*** (`String`) Name of the item.
- Returns:
  - {@link games.stendhal.server.entity.item.Item} instance or `null` if not a registered item.

---
### entities:getStackableItem
<span style="color:green; font-weight:bold;">entities:getStackableItem</span>(name)
- Retrieves a registered StackableItem.
- Parameters:
  - ***name:*** (`String`) Name of the item.
- Returns:
  - {@link games.stendhal.server.entity.item.StackableItem} instance or `null` if not a registered stackable item.

---
### entities:createSpeakerNPC
<span style="color:green; font-weight:bold;">entities:createSpeakerNPC</span>(name)
- ***DEPRECATED:*** Use [entities:create](#entities:create).
- Creates an interactive NPC.
- ***name:*** (`String`) Name of new NPC.
- Returns:
  - New {@link games.stendhal.server.core.scripting.lua.LuaEntityHelper#LuaSpeakerNPC} instance.

---
### entities:createSilentNPC
<span style="color:green; font-weight:bold;">entities:createSilentNPC</span>()
- ***DEPRECATED:*** *Use [entities:create](#entities:create).*
- Creates a non-interactive NPC.
- Returns:
  - New {@link games.stendhal.server.core.scripting.lua.LuaEntityHelper#LuaSilentNPC} instance.

---
### entities:setPath
<span style="color:green; font-weight:bold;">entities:setPath</span>(entity, table, loop)
- ***DEPRECATED:*** *path can now be set by directly calling the NPC's `setPath` method*
- Helper method for setting an NPC's path.
- Parameters:
  - ***entity:*** (`RPEntity`) Then entity whose path is being set.
  - ***table:*** (`LuaTable`) Table with list of coordinates representing nodes.
  - ***loop:*** (`boolean`) If `true`, the entity's path should loop.

---
### entities:setPathAndPosition
<span style="color:green; font-weight:bold;">entities:setPathAndPosition</span>(entity, table, loop)
- ***DEPRECATED:*** *path can now be set by directly by calling the NPC's `setPath` method*
- Helper function for setting an NPC's path & starting position.
- Parameters:
  - ***entity:*** (`RPEntity`) The entity whose path is being set.
  - ***table:*** (`LuaTable`) Table with list of coordinates representing nodes.
  - ***loop:*** (`boolean`) If `true`, the entity's path should loop.

---
### entities:createSign
<span style="color:green; font-weight:bold;">entities:createSign</span>(visible)
- ***DEPRECATED:*** *Use [entities:create](#entities:create).*
- Creates a new {@link games.stendhal.server.entity.sign.Sign} entity.
- Parameters:
  - ***visible:*** (`boolean`) (optional) If `false`, the sign will not have a visual representation (default: `true`).
- Returns:
  - New {@link games.stendhal.server.entities.mapstuff.Sign} instance or {@link games.stendhal.server.entities.mapstuff.Reader} (visible=false).

---
### entities:createShopSign
<span style="color:green; font-weight:bold;">entities:createShopSign</span>(name, title, caption, seller)
- ***DEPRECATED:*** *Use [entities:create](#entities:create).*
- Creates a new {@link games.stendhal.server.entity.sign.ShopSign} entity.
- Parameters:
  - ***name:*** (`String`) The shop name.
  - ***title:*** (`String`) The sign title.
  - ***caption:*** (`String`) The caption above the table.
  - ***seller:*** (`boolean`) `true`, if this sign is for items sold by an NPC (default: `true`).
- Returns:
  - New {@link games.stendhal.server.entity.mapstuff.sign.ShopSign} instance.

---
### entities:summonCreature
<span style="color:green; font-weight:bold;">entities:summonCreature</span>(table)
- Summons a creature into the area.
- Parameters:
  - ***table:*** Key-value table containing parameters for summoning creature.
    - *keys:*
      - <span style="color:blue;">*name:*</span> (`string`) Name of creature to be summoned.
      - <span style="color:blue;">*zone:*</span> (`string`) Name of zone where creature should be summoned.
      - <span style="color:blue;">*x:*</span> (`int`) Horizontal position of summon location.
      - <span style="color:blue;">*y:*</span> (`int`) Vertical position of summon location.
      - <span style="color:blue;">*summoner:*</span> (`string`) (optional) Name of entity doing the summoning (used for logging game events).
      - <span style="color:blue;">*raid:*</span> (`bool`) (optional) Whether or not the creature should be a {@link games.stendhal.server.entity.creature.RaidCreature} instance (default: true).

## Members:

### entities.manager

This is simply the {@link games.stendhal.server.core.rule.defaultruleset.DefaultEntityManager entity manager} instance.

# LuaSpeakerNPC

- Inherits: {@link games.stendhal.server.entity.npc.SpeakerNPC}

## Public methods:

---
### LuaSpeakerNPC:add
<span style="color:darkgreen; font-weight:bold;">LuaSpeakerNPC:add</span>(states, triggers, conditions, nextState, reply, actions)
- Additional method to support passing Lua data types as parameters.
- Parameters:
  - ***states:*** The conversation state(s) the entity should be in to trigger response. Can be [ConversationStates enum](https://stendhalgame.org/wiki/StendhalAPI#ConversationStates) value or `LuaTable` of ConversationStates.
  - ***triggers:*** `String` or `LuaTable` of strings to trigger response.
  - ***conditions:*** Conditions to check for this response. Can be [[StendhalAPI#ChatCondition|ChatCondition instance]], a `LuaTable` of ChatCondition instances, or a function.
  - ***nextState:*** (`ConversationState`) Conversation state to set entity to after response.
  - ***reply:*** (`string`) The NPC's response or `null`.
  - ***actions:*** Actions to execute. Can be [ChatAction instance](https://stendhalgame.org/wiki/StendhalAPI#ChatAction), a `LuaTable` of ChatAction instances, or a function.

---
### LuaSpeakerNPC:setPath
<span style="color:darkgreen; font-weight:bold;">entities:setPath</span>(table, loop)
- Set a path for this entity to follow.
- Parameters:
  - **table:** (`table`) Table of coordinates to set as path. Example: `{{35, 79}, {35, 89}, {40, 89}}`
  - **loop:** (`boolean`) *(optional)* If `true`, entity should loop around to restart path when reaching the end.

---
### LuaSpeakerNPC:setPathAndPosition
<span style="color:darkgreen; font-weight:bold;">entities:setPathAndPosition</span>(table, loop)
- Set path & starting position for entity. The starting position is the first node in the path.
- Parameters:
  - **table:** (`table`) Table of coordinates to set as path. Example: `{{35, 79}, {35, 89}, {40, 89}}`
  - **loop:** (`boolean`) *(optional)* If `true`, entity should loop around to restart path when reaching the end.

# LuaSilentNPC

- Inherits: {@link games.stendhal.server.entity.npc.SilentNPC}

## Public methods:

---
### LuaSilentNPC:setPath
<span style="color:darkgreen; font-weight:bold;">entities:setPath</span>(table, loop)
- Set a path for this entity to follow.
- Parameters:
  - **table:** (`table`) Table of coordinates to set as path. Example: `{{35, 79}, {35, 89}, {40, 89}}`
  - **loop:** (`boolean`) *(optional)* If `true`, entity should loop around to restart path when reaching the end.

---
### LuaSilentNPC:setPathAndPosition
<span style="color:darkgreen; font-weight:bold;">entities:setPathAndPosition</span>(table, loop)
- Set path & starting position for entity. The starting position is the first node in the path.
- Parameters:
  - **table:** (`table`) Table of coordinates to set as path. Example: `{{35, 79}, {35, 89}, {40, 89}}`
  - **loop:** (`boolean`) *(optional)* If `true`, entity should loop around to restart path when reaching the end.
