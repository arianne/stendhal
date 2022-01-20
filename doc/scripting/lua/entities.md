
Entities {#lua_entities}
========

[TOC]

## entities

See also: [StendhalAPI#Entities](https://stendhalgame.org/wiki/StendhalAPI#Entities) for public methods that can be performed on `Entity` objects.

Methods:

<span style="color:green; font-weight:bold;">entities:getPlayer</span>(name)
* Retrieves a logged in Player.
* ***name:*** (`String`) Name of player.
* *returns:* Logged in {@link games.stendhal.server.entity.player.Player} or `null`.

<span style="color:green; font-weight:bold;">entities:getNPC</span>(name)
* Retrieves an existing SpeakerNPC.
* ***name:*** (`String`) Name of NPC.
* *returns:* {@link games.stendhal.server.entity.npc.SpeakerNPC} instance or `null`.

<span style="color:green; font-weight:bold;">entities:getItem</span>(name)
* Retrieves a registered Item.
* ***name:*** (`String`) Name of the item.
* *returns:* {@link games.stendhal.server.entity.item.Item} instance or `null` if not a registered item.

<span style="color:green; font-weight:bold;">entities:getStackableItem</span>(name)
* Retrieves a registered StackableItem.
* ***name:*** (`String`) Name of the item.
* *returns:* {@link games.stendhal.server.entity.item.StackableItem} instance or `null` if not a registered stackable item.

<span style="color:green### font-style:italic;">entities:createSpeakerNPC</span>(name)
* Creates an interactive NPC.
* ***name:*** (`String`) Name of new NPC.
* *returns:* New {@link games.stendhal.server.core.scripting.lua.LuaEntityHelper#LuaSpeakerNPC} instance.

<span style="color:green; font-weight:bold;">entities:createSilentNPC</span>()
* Creates a non-interactive NPC.
* *returns:* New {@link games.stendhal.server.core.scripting.lua.LuaEntityHelper#LuaSilentNPC} instance.

<span style="color:green; font-weight:bold;">entities:setPath</span>(entity, table, loop)
* ***DEPRECATED:** path can now be set by directly calling the NPC's `setPath` method*
* Helper method for setting an NPC's path.
* ***entity:*** (`RPEntity`) Then entity whose path is being set.
* ***table:*** (`LuaTable`) Table with list of coordinates representing nodes.
* ***loop:*** (`boolean`) If `true`, the entity's path should loop.

<span style="color:green; font-weight:bold;">entities:setPathAndPosition</span>(entity, table, loop)* <nowiki>
* ***DEPRECATED:** path can now be set by directly calling the NPC's `setPath` method*
* Helper function for setting an NPC's path & starting position.
* ***entity:*** (`RPEntity`) Then entity whose path is being set.
* ***table:*** (`LuaTable`) Table with list of coordinates representing nodes.
* ***loop:*** (`boolean`) If `true`, the entity's path should loop.

<span style="color:green; font-weight:bold;">entities:createSign</span>(visible)
* Creates a new {@link games.stendhal.server.entity.sign.Sign} entity.
* ***visible:*** (`boolean`) (optional) If `false`, the sign will not have a visual representation.
* *returns:* New `Sign` instance.

<span style="color:green; font-weight:bold;">entities:createShopSign</span>(name, title, caption, seller)
* Creates a new {@link games.stendhal.server.entity.sign.ShopSign} entity.
* ***name:*** (`String`) The shop name.
* ***title:*** (`String`) The sign title.
* ***caption:*** (`String`) The caption above the table.
* ***seller:*** (`boolean`) `true`, if this sign is for items sold by an NPC (defaults to `true` if `null`).
* *returns:* New {@link games.stendhal.server.entity.mapstuff.sign.ShopSign} instance.

<span style="color:green; font-weight:bold;">entities:summonCreature</span>(table)
* Summons a creature into the area.
* ***table:*** Key-value table containing parameters for summoning creature.
* *keys:*
  * *<span style="color:blue;">name:</span>* (`string`) Name of creature to be summoned.
  * *<span style="color:blue;">zone:</span>* (`string`) Name of zone where creature should be summoned.
  * *<span style="color:blue;">x:</span>* (`int`) Horizontal position of summon location.
  * *<span style="color:blue;">y:</span>* (`int`) Vertical position of summon location.
  * *<span style="color:blue;">summoner:</span>* (`string`) (optional) Name of entity doing the summoning (used for logging game events).
  * *<span style="color:blue;">raid:</span>* (`bool`) (optional) Whether or not the creature should be a `RaidCreature` instance (default: true).

### Lua Entity Classes

#### LuaSpeakerNPC

* Inherits: {@link games.stendhal.server.entity.npc.SpeakerNPC}

Public methods:

{{PublicMethod | add | params=states, triggers, conditions, nextState, reply, actions | desc=Additional method to support passing Lua data types as parameters. | paramlist=
{{MethodParam | states | desc=The conversation state(s) the entity should be in to trigger response. Can be [[StendhalAPI#ConversationStates|ConversationStates enum]] value or `LuaTable` of ConversationStates. }}
{{MethodParam | triggers | desc=`String` or `LuaTable` of strings to trigger response. }}
{{MethodParam | conditions | desc=Conditions to check for this response. Can be [[StendhalAPI#ChatCondition|ChatCondition instance]], a `LuaTable` of ChatCondition instances, or a function. }}
{{MethodParam | nextState | type=ConversationState | desc=Conversation state to set entity to after response. }}
{{MethodParam | reply | type=String | desc=The NPC's response or `null`. }}
{{MethodParam | actions | desc=Actions to execute. Can be [[StendhalAPI#ChatAction|ChatAction instance]], a `LuaTable` of ChatAction instances, or a function. }} }}

{{PublicMethod | setPath | params=table, loop | desc=Set a path for this entity to follow. | paramlist=
{{MethodParam | table | type=table | desc=Table of coordinates to set as path. Example: `{{35, 79}, {35, 89}, {40, 89}}` }}
{{MethodParam | loop | type=boolean | desc=(optional) If `true`, entity should loop around to restart path when reaching the end. }} }}

{{PublicMethod | setPathAndPosition | params=table, loop | desc=Set path & starting position for entity. The starting position is the first node in the path. | paramlist=
{{MethodParam | table | type=table | desc=Table of coordinates to set as path. Example: `{{35, 79}, {35, 89}, {40, 89}}` }}
{{MethodParam | loop | type=boolean | desc=(optional) If `true`, entity should loop around to restart path when reaching the end. }} }}

#### LuaSilentNPC

* Inherits: {@link games.stendhal.server.entity.npc.SilentNPC}

##### Public methods:

{{PublicMethod | setPath | params=table, loop | desc=Set a path for this entity to follow. | paramlist=
{{MethodParam | table | type=table | desc=Table of coordinates to set as path. Example: `{{35, 79}, {35, 89}, {40, 89}}` }}
{{MethodParam | loop | type=boolean | desc=(optional) If `true`, entity should loop around to restart path when reaching the end. }} }}

{{PublicMethod | setPathAndPosition | params=table, loop | desc=Set path & starting position for entity. The starting position is the first node in the path. | paramlist=
{{MethodParam | table | type=table | desc=Table of coordinates to set as path. Example: `{{35, 79}, {35, 89}, {40, 89}}` }}
{{MethodParam | loop | type=boolean | desc=(optional) If `true`, entity should loop around to restart path when reaching the end. }} }}

### entities.manager

This is simply the {@link games.stendhal.server.core.rule.defaultruleset.DefaultEntityManager entity manager} instance.

Public methods:

***TODO***
