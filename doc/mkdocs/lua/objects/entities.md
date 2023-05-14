
[TOC]

---
## Introduction

Object instance: `entities`

---
### Description

Helper for managing in-game entities.

---
### See Also

For public methods that can be performed on [`Entity`][Entity] objects.

- [RPEntity][]
- [SilentNPC][]
- [SpeakerNPC][]
- [Item][]
- [StackableItem][]
- [Sign][]
- [ShopSign][]
- [Reader][]

---
## Methods

---
### entities:addBankTeller
<div class="function">
    entities:addBankTeller <span class="params">(npc)</span>
</div>

- Adds [bank teller behaviour][BankTellerBehaviour] to an NPC.
- Parameters:
    - ___npc:__ ([SpeakerNPC][])_ NPC to receive behaviour.

---
### entities:create
<div class="function">
    entities:create <span class="params">(def)</span>
</div>

- Creates a new entity.
- Parameters:
    - ___def:__ ([table][LuaTable])_ Entity definition table (see [Entity Definition Tables](#entity-definition-tables)).
- Returns: _([Entity][Entity])_
    - New entity instance.

---
### entities:createItemSpawner
<div class="function">
    entities:createItemSpawner <span class="params">(name, meanTurns)</span>
</div>
<div class="function">
    entities:createItemSpawner <span class="params">(name, meanTurns, initOnAdded)</span>
</div>

- Creates an item spawner.
- Parameters:
    - ___name:__ ([string][LuaString])_ Name of item to be spawned.
    - ___meanTurns:__ ([int][LuaInteger])_ Average number of turns for item to respawn.
    - ___initOnAdded:__ ([boolean][LuaBoolean])_ If `true` initializes spawner when added to zone.
- Returns: ([PassiveEntityRespawnPoint][])
    - New spawn point instance.

---
### entities:createShopSign
<div class="function">
    entities:createShopSign <span class="params">(name, title, caption, seller)</span>
</div>

- ___DEPRECATED:__ Use [entities:create](#entitiescreate)._
- Creates a new [ShopSign][] entity.
- Parameters:
    - ___name:__ ([string][LuaString])_ The shop name.
    - ___title:__ ([string][LuaString])_ The sign title.
    - ___caption:__ ([string][LuaString])_ The caption above the table.
    - ___seller:__ ([boolean][LuaBoolean])_ `true`, if this sign is for items sold by an NPC (default: `true`).
- Returns: _([ShopSign][])_
    - New shop sign instance.

---
### entities:createSign
<div class="function">
    entities:createSign <span class="params">()</span>
</div>
<div class="function">
    entities:createSign <span class="params">(visible)</span>
</div>

- ___DEPRECATED:__ Use [entities:create](#entitiescreate)._
- Creates a new sign.
- Parameters:
    - ___visible:__ ([boolean][LuaBoolean])_ If `false`, the sign will not have a visual
      representation (default: `true`).
- Returns: _([Sign][]|[Reader][])_
    - New sign or reader (visible=false).

---
### entities:createSilentNPC
<div class="function">
    entities:createSilentNPC <span class="params">()</span>
</div>

- ___DEPRECATED:__ Use [entities:create](#entitiescreate)._
- Creates a non-interactive NPC.
- Returns: _([LuaSilentNPC](#luasilentnpc))_
    - New silent NPC instance.

---
### entities:createSpeakerNPC
<div class="function">
    entities:createSpeakerNPC <span class="params">(name)</span>
</div>

- ___DEPRECATED:__ Use [entities:create](#entitiescreate)._
- Creates an interactive NPC.
- ___name:__ ([string][LuaString])_ Name of new NPC.
- Returns: _([LuaSpeakerNPC](#luaspeakernpc))_
    - New speaker NPC instance.

---
### entities:getItem
<div class="function">
    entities:getItem <span class="params">(name)</span>
</div>

- Retrieves a registered Item.
- Parameters:
    - ___name:__ ([string][LuaString])_ Name of the item.
- Returns: _([Item][])_
    - Item instance or [`nil`][LuaNil] if ___name___ not registered.

---
### entities:getNPC
<div class="function">
    entities:getNPC <span class="params">(name)</span>
</div>

- Retrieves an existing NPC.
- Parameters:
    - ___name:__ ([string][LuaString])_ Name of NPC.
- Returns: _([SpeakerNPC][])_
    - NPC instance or [`nil`][LuaNil].

---
### entities:getPlayer
<div class="function">
    entities:getPlayer <span class="params">(name)</span>
</div>

- Retrieves a logged in player.
- Parameters:
    - ___name:__ ([string][LuaString])_ Name of player.
- Returns: _([Player][])_
    - Logged in player or [`nil`][LuaNil].
- Usage:

        local player = entities:getPlayer("foo")

---
### entities:getStackableItem
<div class="function">
    entities:getStackableItem <span class="params">(name)</span>
</div>

- Retrieves a registered stackable item.
- Parameters:
    - ___name:__ ([string][LuaString])_ Name of the item.
- Returns: _([StackableItem][])_
    - Stackable item instance or [`nil`][LuaNil] if ___name___ not registered.

---
### entities:setEntityPath
<div class="function">
    entities:setEntityPath <span class="params">(entity, table, loop)</span>
</div>

- ___DEPRECATED:__ Path can now be set by directly calling the NPC's `setPath` method._
- Sets a guided entity's path using a table.
- Parameters:
    - ___entity__ ([LuaGuidedEntity](#luaguidedentity))_ The entity whose path is being set.
    - ___table__ ([table][LuaTable])_ Table with list of coordinates representing nodes.
    - ___loop__ ([boolean][LuaBoolean])_ If `true` entity will restart path upon completion.

---
### entities:setPath
<div class="function">
    entities:setPath <span class="params">(entity, table, loop)</span>
</div>

- ___DEPRECATED:__ Path can now be set by directly calling the NPC's `setPath` method._
- Helper method for setting an NPC's path.
- Parameters:
    - ___entity:__ ([RPEntity][])_ Then entity whose path is being set.
    - ___table:__ ([table][LuaTable])_ Table with list of coordinates representing nodes.
    - ___loop:__ ([boolean][LuaBoolean])_ If `true` entity will restart path upon completion.

---
### entities:setPathAndPosition
<div class="function">
    entities:setPathAndPosition <span class="params">(entity, table, loop)</span>
</div>

- ___DEPRECATED:__ Path can now be set by directly by calling the NPC's `setPath` method._
- Helper function for setting an NPC's path &amp; starting position.
- Parameters:
    - ___entity:__ ([RPEntity][])_ The entity whose path is being set.
    - ___table:__ ([table][LuaTable])_ Table with list of coordinates representing nodes.
    - ___loop:__ ([boolean][LuaBoolean])_ If `true`, the entity's path should loop.

---
### entities:setEntityTraits
<div class="function">
    entities:setEntityTraits <span class="params">(entity, traits)</span>
</div>

- ___DEPRECATED:__ Use [entities:create](#entitiescreate)._
- Adds attributes defined in [Entity][].
- Parameters:
    - ___entity:__ ([Entity][])_ The entity to whom attributes will be added.
    - ___traints:__ ([table][LuaTable])_ List of attributes.

---
### entities:setNPCTraits
<div class="function">
    entities:setNPCTraits <span class="params">(npc, traits)</span>
</div>

- ___DEPRECATED:__ Use [entities:create](#entitiescreate)._
- Adds attributes defined in [PassiveNPC][].
- Parameters:
    - ___npc__: ([PassiveNPC][])_ The entity to whom attributes will be added.
    - ___traits__: ([table][LuaTable])_ table of attributes.

---
### entities:summonCreature
<div class="function">
    entities:summonCreature <span class="params">(name, zone, x, y, summoner, raid)</span>
</div>
<div class="function">
    entities:summonCreature <span class="params">(def)</span>
</div>

- Summons a creature into the area.
- Parameters:
    - ___name:__ ([string][LuaString])_ Name of creature to be summoned.
    - ___zone:__ ([string][LuaString])_ Name of zone where creature should be summoned.
    - ___x:__ ([int][LuaInteger])_ Horizontal position of summon location.
    - ___y:__ ([int][LuaInteger])_ Vertical position of summon location.
    - ___summoner:__ ([string][LuaString])_ Name of entity doing the summoning.
    - ___raid:__ ([bool][LuaBoolean])_ Whether or not the creature should be a [RaidCreature][]
      instance.
    - ___def:__ ([table][LuaTable])_ Key-value table containing parameters for summoning creature
      (see [Summon Creature Table](#summon-creature-table)).

## Entity Definition Tables

### General Definition Table

| Key         | Type   | Required | Description                                                                     |
| ----------- | ------ | -------- | ------------------------------------------------------------------------------- |
| type        | string | yes      | "SpeakerNPC", "SilentNPC", "Sign", "ShopSign", or "Reader".                     |
| pos         | table  | no       | Entity position relative to zone ({x, y}).                                      |
| description | string | no       | Information for "look" command.                                                 |
| class       | string | no       | Entity class (for signs, image to be used).                                     |
| subclass    | string | no       | Entity sub-class (usually image to be used).                                    |
| resistance  | int    | no       | Amount of resistance when colliding with another entity (0-100) (default: 100). |
| size        | table  | no       | {w, h}                                                                          |
| cursor      | string | no       | Cursor to display over entity.                                                  |
| visibility  | int    | no       | Level of visibility (0-100) (default: 100).                                     |
| menu        | string | no       | Alternate text for menu.                                                        |

### [`SpeakerNPC`][SpeakerNPC] &amp; [`SilentNPC`][SilentNPC] Definition Table

Includes: [general definition](#general-definition-table)

| Key                  | Type                | Required | Description                                        |
| -------------------- | ------------------- | -------- | -------------------------------------------------- |
| dir                  | Direction           | no       | Entity's initial facing direction.                 |
| ignoresCollision     | boolean             | no       | If `false`, movement won't be halted on collision. |
| path                 | table               | no       | See [Path Table](#path-table)                      |
| speed                | double              | no       | Entity movement speed.                             |
| basehp               | int                 | no       | Entity base HP.                                    |
| hp                   | int                 | no       | Entity actual HP.                                  |
| outfit               | table               | no       | See [Outfit Table](#outfit-table)                  |
| idea                 | string              | no       | Icon shown representing entity's state.            |
| sounds               | table&lt;string&gt; | no       | Randomly played sounds emitted from entity.        |
| teleports            | boolean             | no       |                                                    |

### [`SpeakerNPC`][SpeakerNPC] Definition Table

Includes: [SpeakerNPC &amp; SilentNPC definition](#speakernpc-silentnpc-definition-table)

| Key             | Type               | Required | Description                                                                     |
| --------------- | ------------------ | -------- | ------------------------------------------------------------------------------- |
| name            | string             | yes      | Name of the NPC.                                                                |
| idleDir         | Direction          | no       | Facing direction when NPC is idle.                                              |
| chatTimeout     | long               | no       | Amount of idle time before NPC ends conversation (default: ???).                |
| perceptionRange | int                | no       | Distance at which NPC will hear players (default: ???).                         |
| currentState    | ConversationStates | no       | Converstion state NPC is initialized with (default: `ConversationStates.IDLE`). |
| greeting        | table              | no       | Responses to keywords (see [Greeting Table](#greeting-table)).                  |
| replies         | table              | no       | Responses to keywords (see [Replies Table](#replies-table)).                    |
| altImage        | string             | no       | Image that will be displayed on NPCs webpage.                                   |

### [`Sign`][Sign] Definition Table

Includes: [general definition](#general-definition-table)

| Key     | Type    | Required | Description                                                              |
| ------- | ------- | -------- | ------------------------------------------------------------------------ |
| text    | string  | no       | Text shown when sign is read.                                            |
| visible | boolean | no       | Whether or not a sprite should be drawn for this sign (default: `true`). |

### [`ShopSign`][ShopSign] Definition Table

Includes: [Sign definition](#sign-definition-table)

| Key     | Type    | Required | Description                                    |
| ------- | ------- | -------- | ---------------------------------------------- |
| name    | string  | yes      | Shop name/identfier associated with this sign. |
| title   | string  | yes      | Title displayed at top of window.              |
| caption | string  | yes      | Caption displayed at top of window.            |
| seller  | boolean | yes      | Whether sign represents a seller shop or not.  |

## Parameter Tables

### Greeting Table

| Key    | Type       | Required | Description                                   |
| ------ | ---------- | -------- | --------------------------------------------- |
| text   | string     | yes      | NPC response when player starts conversation. |
| action | ChatAction | no       | NPC action when player start conversation.    |

### Replies Table

| Key   | Type   | Required | Description               |
| ----- | ------ | -------- | ------------------------- |
| quest | string | no       | Reply to "quest"/"task".  |
| job   | string | no       | Reply to "job".           |
| help  | string | no       | Reply to "help".          |
| offer | string | no       | Reply to "offer".         |
| bye   | string | no       | Reply to "bye"/"goodbye". |

### Outfit Table

| Key    | Type                     | Required | Description             |
| -------| ------------------------ | -------- | ----------------------- |
| layers | string                   | yes      | Entity's outfit.        |
| colors | table&lt;string, int&gt; | no       | Entity's outfit colors. |

### Path Table

| Key             | Type            | Required | Description                                                       |
| --------------- | --------------- | -------- | ----------------------------------------------------------------- |
| nodes           | table           | yes      | List of positions the entity will traverse.                       |
| loop            | boolean         | no       | If `true`, entity will restart path unpon completion.             |
| retrace         | boolean         | no       | If `true`, entity will retrace path backwards upon completion.    |
| collisionAction | CollisionAction | no       | Action when entity collides (default: `CollisionAction.REVERSE`). |

## Summon Creature Table

| Key      | Type   | Required | Description                                                                          |
| -------  | ------ | -------- | ------------------------------------------------------------------------------------ |
| name     | string | yes      | Name of creature to be summoned.                                                     |
| zone     | string | yes      | Name of zone where creature should be summoned.                                      |
| x        | int    | yes      | Horizontal position of summon location.                                              |
| y        | int    | yes      | Vertical position of summon location.                                                |
| summoner | string | no       | Name of entity doing the summoning (used for logging game events).                   |
| raid     | bool   | no       | Whether or not the creature should be a [RaidCreature][] instance (default: `true`). |


## Members

### entities.manager

This is simply the [entity manager][DefaultEntityManager] instance.

# LuaSpeakerNPC

- Inherits: [SpeakerNPC][]

## Public Methods

---
### LuaSpeakerNPC:add
<div class="function">
    LuaSpeakerNPC:add <span class="params">(states, triggers, conditions, nextState, reply, actions)</span>
</div>

- Additional method to support passing Lua data types as parameters.
- Parameters:
    - ___states:___ The conversation state(s) the entity should be in to trigger response. Can be
      [ConversationStates][] enum value or [table][LuaTable] of `ConversationStates`.
    - ___triggers:___ [string][LuaString] or table of strings to trigger response.
    - ___conditions:___ Conditions to check for this response. Can be [ChatCondition][] instance, a
      table of `ChatCondition` instances, or a [function][LuaFunction].
    - ___nextState:__ ([ConversationStates][])_ Conversation state to set entity to after response.
    - ___reply:__ ([string][LuaString])_ The NPC's response or [nil][LuaNil].
    - ___actions:___ Actions to execute. Can be [ChatAction][] instance, a table of `ChatAction`
      instances, or a function.

---
### LuaSpeakerNPC:setPath
<div class="function">
    entities:setPath <span class="params">(table, loop)</span>
</div>

- Set a path for this entity to follow.
- Parameters:
    - ___table:__ ([table][LuaTable])_ Table of coordinates to set as path.
      Example: `{{35, 79}, {35, 89}, {40, 89}}`
    - ___loop:__ ([bool][LuaBoolean]) (optional)_ If `true`, entity should loop around to restart
      path when reaching the end.

---
### LuaSpeakerNPC:setPathAndPosition
<div class="function">
    entities:setPathAndPosition <span class="params">(table, loop)</span>
</div>

- Set path &amp; starting position for entity. The starting position is the first node in the path.
- Parameters:
    - ___table:__ ([table][LuaTable])_ Table of coordinates to set as path.
      Example: `{{35, 79}, {35, 89}, {40, 89}}`
    - ___loop:__ ([bool][LuaBoolean]) (optional)_ If `true`, entity should loop around to restart
      path when reaching the end.

# LuaSilentNPC

- Inherits: [SilentNPC][]

## Public Methods

---
### LuaSilentNPC:setPath
<div class="function">
    entities:setPath <span class="params">(table, loop)</span>
</div>

- Set a path for this entity to follow.
- Parameters:
    - ___table:__ ([table][LuaTable])_ Table of coordinates to set as path.
      Example: `{{35, 79}, {35, 89}, {40, 89}}`
    - ___loop:__ ([bool][LuaBoolean]) (optional)_ If `true`, entity should loop around to restart
      path when reaching the end.

---
### LuaSilentNPC:setPathAndPosition
<div class="function">
    entities:setPathAndPosition <span class="params">(table, loop)</span>
</div>

- Set path &amp; starting position for entity. The starting position is the first node in the path.
- Parameters:
    - ___table:__ ([table][LuaTable])_ Table of coordinates to set as path.
      Example: `{{35, 79}, {35, 89}, {40, 89}}`
    - ___loop:__ ([bool][LuaBoolean]) (optional)_ If `true`, entity should loop around to restart
      path when reaching the end.


[BankTellerBehaviour]: /reference/java/games/stendhal/server/entity/npc/behaviour/impl/BankTellerBehaviour.html
[ChatAction]: /reference/java/games/stendhal/server/entity/npc/ChatAction.html
[ChatCondition]: /reference/java/games/stendhal/server/entity/npc/ChatCondition.html
[ConversationStates]: /reference/java/games/stendhal/server/entity/npc/ConversationStates.html
[DefaultEntityManager]: /reference/java/games/stendhal/server/core/rule/defaultruleset/DefaultEntityManager.html
[Entity]: /reference/java/games/stendhal/server/entity/Entity.html
[Item]: /reference/java/games/stendhal/server/entity/item/Item.html
[LuaEntityHelper]: /reference/java/games/stendhal/server/core/scripting/lua/LuaEntityHelper.html
[PassiveEntityRespawnPoint]: /reference/java/games/stendhal/server/entity/mapstuff/spawner/PassiveEntityRespawnPoint.html
[PassiveNPC]: /reference/java/games/stendhal/server/entity/npc/PassiveNPC.html
[Player]: /reference/java/games/stendhal/server/entity/player/Player.html
[RaidCreature]: /reference/java/games/stendhal/server/entity/creature/RaidCreature.html
[Reader]: /reference/java/games/stendhal/server/entity/mapstuff/sign/Reader.html
[RPEntity]: /reference/java/games/stendhal/server/entity/RPEntity.html
[ShopSign]: /reference/java/games/stendhal/server/entity/mapstuff/sign/ShopSign.html
[Sign]: /reference/java/games/stendhal/server/entity/mapstuff/sign/Sign.html
[SilentNPC]: /reference/java/games/stendhal/server/entity/npc/SilentNPC.html
[SpeakerNPC]: /reference/java/games/stendhal/server/entity/npc/SpeakerNPC.html
[StackableItem]: /reference/java/games/stendhal/server/entity/item/StackableItem.html

[LuaBoolean]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaBoolean.html
[LuaFunction]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaFunction.html
[LuaInteger]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaInteger.html
[LuaNil]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaNil.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
