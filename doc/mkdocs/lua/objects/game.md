
---
# Contents

[TOC]


---
# Introduction

Object instance: `game`


---
## Description

The main object that handles setting zone &amp; adding entities to game.


---
# Methods


---
## game:add
<div class="function">
    game:add <span class="paramlist">obj</span>
</div>
<div class="function">
    game:add <span class="paramlist">obj, expire</span>
</div>
<div class="function">
    game:add <span class="paramlist">npc</span>
</div>
<div class="function">
    game:add <span class="paramlist">creature, x, y</span>
</div>

- Adds an object or entity instance to the current zone.
- Parameters:
    - <span class="param">obj</span>
      <span class="datatype">[RPObject]</span>
      Object to add.
    - <span class="param">expire</span>
      <span class="datatype">[bool][LuaBoolean]</span>
      `true` if the item should expire according to its normal behavior, `false` otherwise.
    - <span class="param">npc</span>
      <span class="datatype">[NPC]</span>
      NPC to add.
    - <span class="param">creature</span>
      <span class="datatype">[Creature]</span>
      Creature to add.
    - <span class="param">x</span>
      <span class="datatype">[int][LuaInteger]</span>
      Horizontal position of where to add creature.
    - <span class="param">y</span>
      <span class="datatype">[int][LuaInteger]</span>
      Vertical position of where to add creature.
- Returns:
  <span class="datatype">[Creature]</span>
  _(`game:add(creature)` only)_


---
## game:addGameEvent
<div class="function">
    game:addGameEvent <span class="paramlist">source, event, params</span>
</div>

- Raises a new [game event][GameEvent]. Same function as [game:raiseEvent](#gameraiseevent).
- Parameters:
    - <span class="param">source</span>
      <span class="datatype">[string][LuaString]</span>
      Source of the event, usually a character.
    - <span class="param">event</span>
      <span class="datatype">[string][LuaString]</span>
      Name of event.
    - <span class="param">params:</span>
      <span class="datatype">[List][java.util.List]&lt;[String][java.lang.String]&gt;</span>
      Event parameters.
- See: [ScriptingSandbox.addGameEvent]


---
## game:createEvent
<div class="function">
    game:createEvent <span class="paramlist">source, event, param, ...</span>
</div>
<div class="function">
    game:createEvent <span class="paramlist">source, event, params</span>
</div>

- Creates a new game event.
- Parameters:
    - <span class="param">source</span>
      <span class="datatype">[`string`][LuaString]</span>
      Source of the event, usually a character.
    - <span class="param">event</span>
      <span class="datatype">[`string`][LuaString]</span>
      Name of event.
    - <span class="param">param</span>
      <span class="datatype">[`string`][LuaString]</span>
      Event parameter.
    - <span class="param">params</span>
      <span class="datatype">[`table<string>`][LuaTable]</span>
      List of event parameters.
- Returns:
  <span class="datatype">[GameEvent]</span>
  New game event instance.
- See also:
    - [game:raiseEvent](#gameraiseevent)


---
## game:getCreature
<div class="function">
    game:getCreature <span class="paramlist">clazz</span>
</div>

- Retrieves a registered creature.
    - <span class="param">clazz</span>
      <span class="datatype">[string][LuaString]</span>
      Name of the creature.
- Returns: [Creature] or [nil][LuaNil] if not registered.
- See: [ScriptingSandbox.getCreature]


---
## game:getCreatures
<div class="function">
    game:getCreatures <span class="paramlist"></span>
</div>

- Retrieves all registered creatures.
- Returns:
  <span class="datatype">[Creature[]][java.util.Arrays]</span>
  An array of all available creatures.
- See: [ScriptingSandbox.getCreatures]
- TODO:
    - <span class="fixme">should return Lua table</span>


---
## game:getItem
<div class="function">
    game:getItem <span class="paramlist">name</span>
</div>

- Retrieves a registered item.
- Parameters:
    - <span class="param">name</span>
      <span class="datatype">[string][LuaString]</span>
      Name of the item.
- Returns:
  <span class="datatype">[Item]</span>
  Item or [nil][LuaNil] if not registered.


---
## game:getItems
<div class="function">
    game:getItems <span class="paramlist"></span>
</div>

- Retrieves all registered items.
- Returns:
  <span class="datatype">[Item[]][java.util.Arrays]</span>
  An array of all available items.


---
## game:getMessage
<div class="function">
    game:getMessage <span class="paramlist"></span>
</div>

- Retrieves exception message set by script.
- Returns:
  <span class="datatype">[string][LuaString]</span>


---
## game:getZone
<div class="function">
    game:getZone <span class="paramlist">name</span>
</div>
<div class="function">
    game:getZone <span class="paramlist">obj</span>
</div>

- Retrieves a registered zone.
- Parameters:
    - <span class="param">name</span>
      <span class="datatype">[string][LuaString]</span>
      Retrieve zone by name.
    - <span class="param">obj</span>
      <span class="datatype">[RPObject]</span>
      Retreive zone where ___obj___ is located.
- Returns:
  <span class="datatype">[StendhalRPZone]</span>
  Zone or [nil][LuaNil] if not registered.


---
## game:modify
<div class="function">
    game:modify <span class="paramlist">entity</span>
</div>

- Notifies the [StendhalRPWorld] that this entity's attributes have changed.
- Parameters:
    - <span class="param">entity</span>
      <span class="datatype">[RPEntity]</span>
      Entity being modified.


---
## game:playerIsInZone
<div class="function">
    game:playerIsInZone <span class="paramlist">player, zone</span>
</div>

- Checks if a player is in a specific zone.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player to check.
    - <span class="param">zone</span>
      <span class="datatype">[string][LuaString]</span>
      Name of zone.
- Returns:
  <span class="datatype">[bool][LuaBoolean]</span>
  `true` if player's zone matches ___zone___.


---
## game:privateText
<div class="function">
    game:privateText <span class="paramlist">player, text</span>
</div>

- Sends a private text to a player.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player to receive the message.
    - <span class="param">text</span>
      <span class="datatype">[string][LuaString]</span>
      Message text to send to player.


---
## game:raiseEvent
<div class="function">
    game:raiseEvent <span class="paramlist">source, event, param, ...</span>
</div>
<div class="function">
    game:raiseEvent <span class="paramlist">source, event, params</span>
</div>

- Raises a new [game event][GameEvent].
- Parameters:
    - <span class="param">source</span>
      <span class="datatype">[`string`][LuaString]</span>
      Source of the event, usually a character.
    - <span class="param">event</span>
      <span class="datatype">[`string`][LuaString]</span>
      Name of event.
    - <span class="param">param</span>
      <span class="datatype">[`string`][LuaString]</span>
      Event parameter.
    - <span class="param">params</span>
      <span class="datatype">[`table<string>`][LuaTable]</span>
      List of event parameters.
- See also:
    - [game:createEvent](#gamecreateevent)


---
## game:remove
<div class="function">
    game:remove <span class="paramlist">object</span>
</div>
<div class="function">
    game:remove <span class="paramlist">npc</span>
</div>

- Removes an object or entity from world.
- Parameters:
    - <span class="param">object</span>
      <span class="datatype">[RPObject]</span>
      Object to remove.
    - <span class="param">npc</span>
      <span class="datatype">[NPC]</span>
      NPC to remove.


---
## game:runAfter
<div class="function">
    game:runAfter <span class="paramlist">turns, func</span>
</div>

- Executes a function after a specified number of turns.
- Parameters:
    - <span class="param">turns</span>
      <span class="datatype">[int][LuaInteger]</span>
      Number of turns to wait.
    - <span class="param">func</span>
      <span class="datatype">[function][LuaFunction]</span>
      Function to be executed.
- TODO:
    - <span class="fixme">how to invoke with parameters?</span>


---
## game:setMusic
<div class="function">
    game:setMusic <span class="paramlist">filename</span>
</div>
<div class="function">
    game:setMusic <span class="paramlist">filename, args</span>
</div>

- Sets the background music for the current zone.
- Parameters:
    - <span class="param">filename</span>
      <span class="datatype">[string][LuaString]</span>
      File basename excluding .ogg extension.
    - <span class="param">args</span>
      <span class="datatype">[table][LuaTable]</span>
      Table of key=value integer values.
        - Keys:
            - <span class="table-attr">volume</span>
              Volume level (default: 100).
            - <span class="table-attr">x</span>
              The X coordinate of the sound source (default: 1).
            - <span class="table-attr">y</span>
              The Y coordinate of the sound source (default: 1).
            - <span class="table-attr">radius</span>
              The radius from which the music can be heard (default: 10000).


---
## game:setZone
<div class="function">
    game:setZone <span class="paramlist">name</span>
</div>
<div class="function">
    game:setZone <span class="paramlist">zone</span>
</div>

- Selects the zone to be configured.
- Parameters:
    - <span class="param">name</span>
      <span class="datatype">[string][LuaString]</span>
      String name identifier.
    - <span class="param">zone</span>
      <span class="datatype">[StendhalRPZone]</span>
      Zone instance.
- Returns:
  <span class="datatype">[bool][LuaBoolean]</span>
  `true` if zone was successfully set.


[Creature]: /reference/java/games/stendhal/server/entity/creature/Creature.html
[GameEvent]: /reference/java/games/stendhal/server/core/engine/GameEvent.html
[Item]: /reference/java/games/stendhal/server/entity/item/Item.html
[NPC]: /reference/java/games/stendhal/server/entity/npc/NPC.html
[Player]: /reference/java/games/stendhal/server/entity/player/Player.html
[RPEntity]: /reference/java/games/stendhal/server/entity/RPEntity.html
[RPObject]: /reference/java/marauroa/common/game/RPObject.html
[ScriptingSandbox.addGameEvent]: /reference/java/games/stendhal/server/core/scripting/ScriptingSandbox.html#addGameEvent(java.lang.String,java.lang.String,java.util.List)
[ScriptingSandbox.getCreature]: /reference/java/games/stendhal/server/core/scripting/ScriptingSandbox.html#getCreature(java.lang.String)
[ScriptingSandbox.getCreatures]: /reference/java/games/stendhal/server/core/scripting/ScriptingSandbox.html#getCreatures()
[StendhalRPWorld]: /reference/java/games/stendhal/server/core/engine/StendhalRPWorld.html
[StendhalRPZone]: /reference/java/games/stendhal/server/core/engine/StendhalRPZone.html

[java.lang.String]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html
[java.util.Arrays]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html
[java.util.List]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html

[LuaBoolean]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaBoolean.html
[LuaFunction]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaFunction.html
[LuaInteger]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaInteger.html
[LuaNil]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaNil.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
