
[TOC]

# Introduction

Adds helper functions for creating & manipulating quests & exposes select public methods of the {@link games.stendhal.server.core.rp.StendhalQuestSystem} class.

# quests

## Methods

---
### quests:create
<span style="color:green; font-weight:bold;">quests:create</span>(slotName, name)

- Creates a new quest.
- Parameters:
    - ***slotName:*** (optional) The string identifier for the quest.
    - ***name:*** (optional) The human-readable name that can be shown in travel log.
- Returns: New {@link games.stendhal.server.core.scripting.lua.LuaQuestHelper.LuaQuest LuaQuest} instance.

---
### quests:load
<span style="color:green; font-weight:bold;">quests:load</span>(quest)

- Adds a quest to the world.
- Parameters:
    - ***quest:*** The {@link games.stendhal.server.maps.quests.IQuest IQuest} instance to be loaded.

---
### quests:unload
<span style="color:green; font-weight:bold;">quests:unload</span>(questName)

- Removes a quest from the world.
- Parameters:
    - ***questName:*** String name of the quest to be removed.

---
### quests:cache
<span style="color:green; font-weight:bold;">quests:cache</span>(quest)

- Caches a quest for loading at server startup.
- Parameters:
    - ***quest:*** {@link games.stendhal.server.maps.quests.IQuest IQuest} instance to be loaded.
- Aliases:
    - <span style="color:green; font-style:italic;">quests:register</span>

---
### quests:isLoaded
<span style="color:green; font-weight:bold;">quests:isLoaded</span>(quest)

- Checks if a quest has been loaded.
- Parameters:
    - ***quest:*** {@link games.stendhal.server.maps.quests.IQuest IQuest} instance to check.
- Returns: <code>true</code> if the instance matches stored quests.

---
### quests:listAll
<span style="color:green; font-weight:bold;">quests:listAll</span>(player)

- List all quests the player knows about.
- Parameters:
    - ***player:*** Player to create the report for.
- Returns: String report.

---
### quests:list
<span style="color:green; font-weight:bold;">quests:list</span>(player, questName)j

- Creates a report on a specified quest for a player.
- Parameters:
    - ***player:*** Player to create the report for.
    - ***questName:*** Name of quest to be reported.
- Returns: String report.

---
### quests:listStates
<span style="color:green; font-weight:bold;">quests:listStates</span>(player)

- Dumps the internal quest states for the specified player. This is used for the {@link games.stendhal.server.action.admin.InspectAction InspectAction}.
- Parameters:
    - ***player:*** Player to create the report for.
- Returns: String report.

---
### quests:getQuest
<span style="color:green; font-weight:bold;">quests:getQuest</span>(questName)

- Retrieves the {@link games.stendhal.server.maps.quests.IQuest IQuest} object for a named quest.
- Parameters:
    - ***questName:*** Name of quest.
- Returns: <code>IQuest</code> or <code>null</code> if doesn't exist.

---
### quests:getQuestFromSlot
<span style="color:green; font-weight:bold;">quests:getQuestFromSlot</span>(questSlot)

- Retrieves the {@link games.stendhal.server.maps.quests.IQuest IQuest} object for a quest.
- Parameters:
    - ***questSlot:*** Quest identifier string.
- Returns: <code>IQuest</code> or <code>null</code> if doesn't exist.

---
### quests:getOpen
<span style="color:green; font-weight:bold;">quests:getOpen</span>(player)

- Retrieves a list of open quests from a player.
- Parameters:
    - ***player:*** {@link games.stendhal.server.entity.player.Player Player} instance to be checked.
- Returns: List of string identifiers for open quests.

---
### quests:getCompleted
<span style="color:green; font-weight:bold;">quests:getCompleted</span>(player)

- Retrieves a list of completed quests from a player.
- Parameters:
    - ***player:*** {@link games.stendhal.server.entity.player.Player Player} instance to be checked.
- Returns: List of string identifiers for completed quests.

---
### quests:getIncomplete
<span style="color:green; font-weight:bold;">quests:getIncomplete</span>(player, region)

- Retrieves a list of incomplete quests in a specified region.
- Parameters:
    - ***player:*** {@link games.stendhal.server.entity.player.Player Player} instance to be checked.
    - ***region:*** Region name/identifier.
- Returns: List of string identifiers of incomplete quests in region.

---
### quests:getRepeatable
<span style="color:green; font-weight:bold;">quests:getRepeatable</span>(player)

- Retrieves a list of quests a player has completed, and can now do again.
- Parameters:
    - ***player:*** {@link games.stendhal.server.entity.player.Player Player} instance to be checked.
- Returns:

---
### quests:getDescription
<span style="color:green; font-weight:bold;">quests:getDescription</span>(player, questName)

- Retrieves the description of a quest.
- Parameters:
    - ***player:*** {@link games.stendhal.server.entity.player.Player Player} instance to be checked.
    - ***questName:*** Name of the quest.
- Returns: <code>{@link java.lang.String String}</code> description.

---
### quests:getLevelWarning
<span style="color:green; font-weight:bold;">quests:getLevelWarning</span>(player, questName)

- If the quest is too dangerous, add a warning unless the player has already completed it.
- Parameters:
    - ***player:*** {@link games.stendhal.server.entity.player.Player Player} instance to be checked.
    - ***questName:*** Name of the quest.
- Returns: <code>{@link java.lang.String String}</code>

---
### quests:getProgressDetails
<span style="color:green; font-weight:bold;">quests:getProgressDetails</span>(player, questeName)

- Retrieves details on the progress of the quest.
- Parameters:
    - ***player:*** {@link games.stendhal.server.entity.player.Player Player} instance to be checked.
    - ***questName:*** Name of the quest.
- Returns: <code>{@link java.util.List List}<{@link java.lang.String String}></code>

---
### quests:getNPCNamesForUnstartedInRegionForLevel
<span style="color:green; font-weight:bold;">quests:getNPCNamesForUnstartedInRegionForLevel</span>(player, region)

- Retrieves a list of the unique npc names for unstarted quests in a specified region.
- Parameters:
    - ***player:*** {@link games.stendhal.server.entity.player.Player Player} instance to be checked.
    - ***region:*** Region to check in.
- Returns: <code>{@link java.util.List List}<{@link java.lang.String String}></code>

---
### quests:getDescriptionForUnstartedInRegionFromNPCName
<span style="color:green; font-weight:bold;">quests:getDescriptionForUnstartedInRegionFromNPCName</span>(player, region, name)

- Retrieves quest descriptions for unstarted quests in a specified region matching a specific NPC name.
- Parameters:
    - ***player:*** {@link games.stendhal.server.entity.player.Player Player} instance to be checked.
    - ***region:*** Region to check in.
    - ***name:*** Name of NPC.
- Returns: <code>{@link java.util.List List}<{@link java.lang.String String}></code>

# Classes

## LuaQuest

See: {@link games.stendhal.server.core.scripting.lua.LuaQuestHelper.LuaQuest LuaQuest}

### Public Members

#### LuaQuest.init
<span style="color:darkblue; font-weight:bold;">LuaQuest.init</span>

- Called by {@link games.stendhal.server.maps.quests.IQuest#addToWorld}.
- Type: function

### Public Methods

### Usage

```
local loadStep1 = function()
	...
end

local loadStep2 = function()
	...
end

local myQuest = quests:create("my_quest", "My Quest")
myQuest.init = function()
	loadStep1()
	loadStep2()
end

quests:register(myQuest)
```

## quests.simple

A special class for creating a simple collect single item quest.

### Methods:

---
#### quests.simple:create</span>
<span style="color:green; font-weight:bold;">quests.simple:create</span></span>(slotName, properName, npcName)

- Parameters:
    - ***slotName:*** <code>String</code> identifier to be used for quest.
    - ***properName:*** Human-readable name to be displayed in travel log.
    - ***npcName:*** The NPC associated with the quest.
- Returns: {@link games.stendhal.server.maps.quests.SimpleQuestCreator} instance.

## SimpleQuest Object

{@link games.stendhal.server.maps.quests.SimpleQuestCreator.SimpleQuest}

### Methods

---
#### setDescription
<span style="color:green; font-weight:bold;">setDescription</span>(descr)

- Parameters:
    - ***descr:*** (<code>String</code>)

---
#### setRepeatable
<span style="color:green; font-weight:bold;">setRepeatable</span>(delay)

- Parameters:
    - ***delay:*** (<code>Integer</code>)

---
#### setItemToCollect
<span style="color:green; font-weight:bold;">setItemToCollect</span>(itemName, quantity)

- Parameters:
    - ***itemName:*** (<code>String</code>)
    - ***quantity:*** (<code>int</code>)

---
#### setXPReward
<span style="color:green; font-weight:bold;">setXPReward</span>(xp)

- Parameters:
    - ***xp:*** (<code>int</code>)

---
#### setKarmaReward
<span style="color:green; font-weight:bold;">setKarmaReward</span>(karma)

- Parameters:
    - ***karma:*** (<code>double</code>)

---
#### setKarmaAcceptReward
<span style="color:green; font-weight:bold;">setKarmaAcceptReward</span>(karma)

- Parameters:
    - ***karma:*** (<code>double</code>)

---
#### setKarmaRejectReward
<span style="color:green; font-weight:bold;">setKarmaRejectReward</span>(karma)

- Parameters:
    - ***karma:*** (<code>double</code>)

---
#### addItemReward
<span style="color:green; font-weight:bold;">addItemReward</span>(itemName, quantity)

- Parameters:
    - ***itemName:*** (<code>String</code>)
    - ***quantity:*** (<code>int</code>) (optional)

---
#### addStatReward
<span style="color:green; font-weight:bold;">addStatReward</span>(id, amount)

- Parameters:
    - ***id:*** (<code>String</code>) See IDs below.
    - ***amount:*** (<code>int</code>)
- IDs:
    - ***xp:***
    - ***def:***
    - ***atk:***
    - ***ratk:***

---
#### setVerboseReward
<span style="color:green; font-weight:bold;">setVerboseReward</span>(verbose)

- Parameters:
    - ***verbose:*** (<code>boolean</code>)

---
#### setReply
<span style="color:green; font-weight:bold;">setReply</span>(id, reply)

- Parameters:
    - ***id:*** (<code>String</code>) See IDs below.
    - ***reply:*** (<code>String</code>)

:; ''IDs:''
:: '''''request:'''''
:: '''''accept:'''''
:: '''''reject:'''''
:: '''''reward:'''''
:: '''''verbose_reward_prefix:'''''
:: '''''already_active:'''''
:: '''''missing:'''''
:: '''''no_repeat:'''''
:: '''''cooldown_prefix:'''''

---
#### setRegion
<span style="color:green; font-weight:bold;">setRegion</span>(regionName)

- Parameters:
    - ***regionName:*** (<code>String</code>)

Also inherits methods from {@link games.stendhal.server.maps.quests.AbstractQuest}:

Example:
```
-- create SimpleQuest instance
local quest = simpleQuest:create("wood_for_lua", "Wood for Lua", "Lua")

quest:setDescription("Lua needs help gathering wood.")
quest:setRequestReply("I need help gathering some wood. Will you help me?")
quest:setAcceptReply("Great!")
quest:setRewardReply("Thank a bunch!")
quest:setRejectReply("Fine! I don't need your help anyway.")
quest:setItemToCollect("wood", 5)
quest:setRepeatable(true)
quest:setRepeatDelay(10)
quest:setXPReward(50)
quest:setKarmaReward(5.0)
quest:addItemReward("rose", 3)
quest:addItemReward("money", 100)
quest:setRegion(Region.SEMOS_CITY)

quests:register(quest)
```
