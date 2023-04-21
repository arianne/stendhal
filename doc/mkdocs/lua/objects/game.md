
[TOC]

---
## Introduction

Object instance: `game`

---
### Description

The main object that handles setting zone &amp; adding entities to game.

---
## Methods

---
### game:add
<div class="function">
    game:add <span class="params">(obj)</span>
</div>
<div class="function">
    game:add <span class="params">(obj, expire)</span>
</div>
<div class="function">
    game:add <span class="params">(npc)</span>
</div>
<div class="function">
    game:add <span class="params">(creature, x, y)</span>
</div>

- Adds an object or entity instance to the current zone.
- Parameters:
    - ___obj:__ ([RPObject][])_ Object to add.
    - ___expire:__ ([bool][LuaBoolean])_
    - ___npc:___ NPC to add.
    - ___creature:__ ([Creature][])_ Creature to add.
    - ___x:__ ([int][LuaInteger])_ Horizontal position of where to add creature.
    - ___y:__ ([int](LuaInteger])_ Vertical position of where to add creature.
- Returns: _([Creature][]) `game:add(creature)` only_

---
### game:addGameEvent
<div class="function">
    game:addGameEvent <span class="params">(source, event, params)</span>
</div>

- Adds a new [GameEvent][].
- Parameters:
    - ___source:__ ([string][LuaString])_ Source of the event, usually a character.
    - ___event:__ ([string][LuaString])_ Name of event.
    - ___params:__ ([List][java.util.List]&lt;[String][java.lang.String]&gt;)_ Event parameters.

---
### game:getCreature
<div class="function">
    game:getCreature <span class="params">(clazz)</span>
</div>

- Retrieves a registered creature.
    - ___clazz:__ ([string][LuaString])_ Name of the creature.
- Returns: _([Creature][])_
    - Creature or [nil][LuaNil] if not registered.

---
### game:getCreatures
<div class="function">
    game:getCreatures <span class="params">()</span>
</div>

- Retrieves all registered creatures.
- Returns: _([Creature[]][java.util.Arrays])_
    - An array of all available creatures.

---
### game:getItem
<div class="function">
    game:getItem <span class="params">(name)</span>
</div>

- Retrieves a registered item.
- Parameters:
    - ___name:__ ([string][LuaString])_ Name of the item.
- Returns: _([Item][])_
    - Item or [nil][LuaNil] if not registered.

---
### game:getItems
<div class="function">
    game:getItems <span class="params">()</span>
</div>

- Retrieves all registered items.
- Returns: _([Item[]][java.util.Arrays])_
    - An array of all available items.

---
### game:getMessage
<div class="function">
    game:getMessage <span class="params">()</span>
</div>

- Retrieves exception message set by script.
- Returns: _([string][LuaString])_

---
### game:getZone
<div class="function">
    game:getZone <span class="params">(name)</span>
</div>
<div class="function">
    game:getZone <span class="params">(obj)</span>
</div>

- Retrieves a registered zone.
- Parameters:
    - ___name:__ ([string][LuaString])_ Retrieve zone by name.
    - ___obj:__ ([RPObject][])_ Retreive zone where ***obj*** is located.
- Returns: _([StendhalRPZone][])_
    - Zone or [nil][LuaNil] if not registered.

---
### game:modify
<div class="function">
    game:modify <span class="params">(entity)</span>
</div>

- Notifies the [StendhalRPWorld][] that this entity's attributes have changed.
- Parameters:
    - ___entity:__ ([RPEntity][])_ Entity being modified.

---
### game:playerIsInZone
<div class="function">
    game:playerIsInZone <span class="params">(player, zone)</span>
</div>

- Checks if a player is in a specific zone.
- Parameters:
    - ___player:__ ([Player][])_ Player to check.
    - ___zone:__ ([string][LuaString])_ Name of zone.
- Returns: _([bool][LuaBoolean])_
    - `true` if player's zone matches ___zone___.

---
### game:privateText
<div class="function">
    game:privateText <span class="params">(player, text)</span>
</div>

- Sends a private text to a player.
- Parameters:
    - ___player:__ ([Player][])_ Player to receive the message.
    - ___text:__ ([string][LuaString])_ Message text to send to player.

---
### game:remove
<div class="function">
    game:remove <span class="params">(object)</span>
</div>
<div class="function">
    game:remove <span class="params">(npc)</span>
</div>

- Removes an object or entity from world.
- Parameters:
    - ___object:__ ([RPObject][])_ Object to remove.
    - ___npc:__ ([NPC][])_ NPC to remove.

---
### game:runAfter
<div class="function">
    game:runAfter <span class="params">(turns, func)</span>
</div>

- Executes a function after a specified number of turns.
- Parameters:
    - ___turns:__ ([int][LuaInteger])_ Number of turns to wait.
    - ___func:__ ([function][LuaFunction])_ Function to be executed.

---
### game:setMusic
<div class="function">
    game:setMusic <span class="params">(filename)</span>
</div>
<div class="function">
    game:setMusic <span class="params">(filename, args)</span>
</div>

- Sets the background music for the current zone.
- Parameters:
    - ___filename:__ ([string][LuaString])_ File basename excluding .ogg extension.
    - ___args:__ ([table][LuaTable])_ Table of key=value integer values.
        - Keys:
            - <span class="table-attr">volume:</span> Volume level (default: 100).
            - <span class="table-attr">x:</span> The X coordinate of the sound source (default: 1).
            - <span class="table-attr">y:</span> The Y coordinate of the sound source (default: 1).
            - <span class="table-attr">radius:</span> The radius from which the music can be heard (default: 10000).

---
### game:setZone
<div class="function">
    game:setZone <span class="params">(name)</span>
</div>
<div class="function">
    game:setZone <span class="params">(zone)</span>
</div>

- Selects the zone to be configured.
- Parameters:
    - ___name:__ ([string][LuaString])_ String name identifier.
    - ___zone:__ ([StendhalRPZone][])_ Zone instance.
- Returns: _([bool][LuaBoolean])_
    - `true` if zone was successfully set.


[Creature]: /reference/java/games/stendhal/server/entity/creature/Creature.html
[GameEvent]: /reference/java/games/stendhal/server/core/engine/GameEvent.html
[Item]: /reference/java/games/stendhal/server/entity/item/Item.html
[NPC]: /reference/java/games/stendhal/server/entity/npc/NPC.html
[Player]: /reference/java/games/stendhal/server/entity/player/Player.html
[RPEntity]: /reference/java/games/stendhal/server/entity/RPEntity.html
[RPObject]: /reference/java/marauroa/common/game/RPObject.html
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
