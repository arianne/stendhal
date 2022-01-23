
quests {#lua_quests}
======

[TOC]

## Introduction

Adds helper functions for creating & manipulating quests & exposes select public methods of the {{StendhalFile|master|src/games/stendhal/server/core/rp/StendhalQuestSystem.java|games.stendhal.server.core.rp.StendhalQuestSystem}} class.

Methods:

; ''<span style="color:green">quests:create</span>(slotName, name)''
: Creates a new quest.
: '''''slotName:''''' (optional) The string identifier for the quest.
: '''''name:''''' (optional) The human-readable name that can be shown in travel log.
: ''returns:'' New {{StendhalFile|master|src/games/stendhal/server/core/scripting/lua/QuestHelper.java|games.stendhal.server.core.scripting.lua.QuestHelper.LuaQuest}} instance.

; ''<span style="color:green">quests:load</span>(quest)''
: Adds a quest to the world.
: '''''quest:''''' The {{StendhalFile|master|src/games/stendhal/server/maps/quests/IQuest.java|IQuest}} instance to be loaded.

; ''<span style="color:green">quests:unload</span>(questName)''
: Removes a quest from the world.
: '''''questName:''''' String name of the quest to be removed.

; ''<span style="color:green">quests:cache</span>(quest)''
: Caches a quest for loading at server startup.
: '''''quest:''''' {{StendhalFile|master|src/games/stendhal/server/maps/quests/IQuest.java|IQuest}} instance to be loaded.
:; ''aliases:''
:: ''<span style="color:green">quests:register</span>''

; ''<span style="color:green">quests:isLoaded</span>(quest)''
: Checks if a quest has been loaded.
: '''''quest:''''' {{StendhalFile|master|src/games/stendhal/server/maps/quests/IQuest.java|IQuest}} instance to check.
: ''returns:'' <code>true</code> if the instance matches stored quests.

; ''<span style="color:green">quests:listAll</span>(player)''
: List all quests the player knows about.
: '''''player:''''' Player to create the report for.
: ''returns:'' String report.

; ''<span style="color:green">quests:list</span>(player, questName)''
: Creates a report on a specified quest for a player.
: '''''player:''''' Player to create the report for.
: '''''questName:''''' Name of quest to be reported.
: ''returns:'' String report.

; ''<span style="color:green">quests:listStates</span>(player)''
: Dumps the internal quest states for the specified player. This is used for the {{StendhalFile|master|src/games/stendhal/server/action/admin/InspectAction.java|InspectAction}}.
: '''''player:''''' Player to create the report for.
: ''returns:'' String report.

; ''<span style="color:green">quests:getQuest</span>(questName)''
: Retrieves the {{StendhalFile|master|src/games/stendhal/server/maps/quests/IQuest.java|IQuest}} object for a named quest.
: '''''questName:''''' Name of quest.
: ''returns:'' <code>IQuest</code> or <code>null</code> if doesn't exist.

; ''<span style="color:green">quests:getQuestFromSlot</span>(questSlot)''
: Retrieves the {{StendhalFile|master|src/games/stendhal/server/maps/quests/IQuest.java|IQuest}} object for a quest.
: '''''questSlot:''''' Quest identifier string.
: ''returns:'' <code>IQuest</code> or <code>null</code> if doesn't exist.

; ''<span style="color:green">quests:getOpen</span>(player)''
: Retrieves a list of open quests from a player.
: '''''player:''''' {{StendhalFile|master|src/games/stendhal/server/entity/player/Player.java|Player}} instance to be checked.
: ''returns:'' List of string identifiers for open quests.

; ''<span style="color:green">quests:getCompleted</span>(player)''
: Retrieves a list of completed quests from a player.
: '''''player:''''' {{StendhalFile|master|src/games/stendhal/server/entity/player/Player.java|Player}} instance to be checked.
: ''returns:'' List of string identifiers for completed quests.

; ''<span style="color:green">quests:getIncomplete</span>(player, region)''
: Retrieves a list of incomplete quests in a specified region.
: '''''player:''''' {{StendhalFile|master|src/games/stendhal/server/entity/player/Player.java|Player}} instance to be checked.
: '''''region:''''' Region name/identifier.
: ''returns:'' List of string identifiers of incomplete quests in region.

; ''<span style="color:green">quests:getRepeatable</span>(player)''
: Retrieves a list of quests a player has completed, and can now do again.
: '''''player:''''' {{StendhalFile|master|src/games/stendhal/server/entity/player/Player.java|Player}} instance to be checked.
: ''returns:''

; ''<span style="color:green">quests:getDescription</span>(player, questName)''
: Retrieves the description of a quest.
: '''''player:''''' {{StendhalFile|master|src/games/stendhal/server/entity/player/Player.java|Player}} instance to be checked.
: '''''questName:''''' Name of the quest.
: ''returns:'' <code>String</code> description.

; ''<span style="color:green">quests:getLevelWarning</span>(player, questName)''
: If the quest is too dangerous, add a warning unless the player has already completed it.
: '''''player:''''' {{StendhalFile|master|src/games/stendhal/server/entity/player/Player.java|Player}} instance to be checked.
: '''''questName:''''' Name of the quest.
: ''returns:'' <code>String</code>

; ''<span style="color:green">quests:getProgressDetails</span>(player, questeName)''
: Retrieves details on the progress of the quest.
: '''''player:''''' {{StendhalFile|master|src/games/stendhal/server/entity/player/Player.java|Player}} instance to be checked.
: '''''questName:''''' Name of the quest.
: ''returns:'' <code>List<String></code>

; ''<span style="color:green">quests:getNPCNamesForUnstartedInRegionForLevel</span>(player, region)''
: Retrieves a list of the unique npc names for unstarted quests in a specified region.
: '''''player:''''' {{StendhalFile|master|src/games/stendhal/server/entity/player/Player.java|Player}} instance to be checked.
: '''''region:''''' Region to check in.
: ''returns:'' <code>List<String></code>

; ''<span style="color:green">quests:getDescriptionForUnstartedInRegionFromNPCName</span>(player, region, name)''
: Retrieves quest descriptions for unstarted quests in a specified region matching a specific NPC name.
: '''''player:''''' {{StendhalFile|master|src/games/stendhal/server/entity/player/Player.java|Player}} instance to be checked.
: '''''region:''''' Region to check in.
: '''''name:''''' Name of NPC.
: ''returns:'' <code>List<String></code>

=== quests.simple ===

A special class for creating a simple collect single item quest.

Methods:

; ''<span style="color:green">quests.simple:create</span></span>(slotName, properName, npcName)''
: '''''slotName:''''' <code>String</code> identifier to be used for quest.
: '''''properName:''''' Human-readable name to be displayed in travel log.
: '''''npcName:''''' The NPC associated with the quest.
: ''returns:'' {{StendhalFile|master|src/games/stendhal/server/maps/quests/SimpleQuestCreator.java|SimpleQuest}} instance.

==== SimpleQuest Object ====

{{StendhalFile|master|src/games/stendhal/server/maps/quests/SimpleQuestCreator.java|games.stendhal.server.maps.quests.SimpleQuestCreator.SimpleQuest}}

Methods:

; ''<span style="color:green">setDescription</span>(descr)''
:
: '''''descr:''''' (<code>String</code>)

; ''<span style="color:green">setRepeatable</span>(delay)''
:
: '''''delay:''''' (<code>Integer</code>)

; ''<span style="color:green">setItemToCollect</span>(itemName, quantity)''
:
: '''''itemName:''''' (<code>String</code>)
: '''''quantity:''''' (<code>int</code>)

; ''<span style="color:green">setXPReward</span>(xp)''
:
: '''''xp:''''' (<code>int</code>)

; ''<span style="color:green">setKarmaReward</span>(karma)''
:
: '''''karma:''''' (<code>double</code>)

; ''<span style="color:green">setKarmaAcceptReward</span>(karma)''
:
: '''''karma:''''' (<code>double</code>)

; ''<span style="color:green">setKarmaRejectReward</span>(karma)''
:
: '''''karma:''''' (<code>double</code>)

; ''<span style="color:green">addItemReward</span>(itemName, quantity)''
:
: '''''itemName:''''' (<code>String</code>)
: '''''quantity:''''' (<code>int</code>) (optional)

; ''<span style="color:green">addStatReward</span>(id, amount)''
:
: '''''id:''''' (<code>String</code>) See IDs below.
: '''''amount:''''' (<code>int</code>)
:; ''IDs:''
:: '''''xp:'''''
:: '''''def:'''''
:: '''''atk:'''''
:: '''''ratk:'''''

; ''<span style="color:green">setVerboseReward</span>(verbose)''
:
: '''''verbose:''''' (<code>boolean</code>)

; ''<span style="color:green">setReply</span>(id, reply)''
:
: '''''id:''''' (<code>String</code>) See IDs below.
: '''''reply:''''' (<code>String</code>)
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

; ''<span style="color:green">setRegion</span>(regionName)''
:
: '''''regionName:''''' (<code>String</code>)

Also inherits methods from {{StendhalFile|master|src/games/stendhal/server/maps/quests/AbstractQuest.java|AbstractQuest}}:

Example:
<pre>
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
</pre>
