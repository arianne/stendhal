
---
# Contents

[TOC]


---
# Introduction

Object instance: `quests`


---
## Description

Adds helper functions for creating &amp; manipulating quests &amp; exposes select public methods of
the [StendhalQuestSystem] class.

# Methods


---
## quests:cache
<div class="function">
    quests:cache <span class="paramlist">quest</span>
</div>

- <span class="deprecated">Call `register` directly from the quest instance.</span>
- Caches a quest for loading at server startup.
- Parameters:
    - <span class="param">quest</span>
      <span class="datatype">[IQuest]</span>
      Quest instance to be cached.


---
## quests:create
<div class="function">
    quests:create <span class="paramlist"></span>
</div>
<div class="function">
    quests:create <span class="paramlist">slotName</span>
</div>
<div class="function">
    quests:create <span class="paramlist">slotName, name</span>
</div>
<div class="function">
    quests:create <span class="paramlist">slotName, name, desc</span>
</div>

- Creates a new quest.
- Parameters:
    - <span class="param">slotName</span>
      <span class="datatype">[string][LuaString]</span>
      Quest slot identifier.
    - <span class="param">name</span>
      <span class="datatype">[string][LuaString]</span>
      The reader friendly name that can be shown in travel log.
    - <span class="param">desc</span>
      <span class="datatype">[string][LuaString]</span>
      Quest description.
- Returns:
  <span class="datatype">[LuaQuest]</span>
  New quest instance.


---
## quests:createBuilder
<div class="function">
    quests:createBuilder <span class="paramlist">task</span>
</div>

- Creates a quest builder to pass to `quests:createManuscript`.
- Parameters:
    - <span class="param">task</span>
      <span class="datatype">[string][LuaString]</span>
      Quest task type. Supported are "BringItemTask" &amp; "KillCreaturesTask".
- Returns:
  <span class="datatype">[QuestBuilder]</span>
  New quest builder instance.


---
## quests:createManuscript
<div class="function">
    quests:createManuscript <span class="paramlist">builder</span>
</div>

- Creates a new quest in manuscript format.
- Parameters:
    - <span class="param">builder</span>
      <span class="datatype">[QuestBuilder]</span>
      A quest builder instance that defines quest.
- Returns:
  <span class="datatype">[QuestManuscript]</span>
  New quest instance.


---
## quests:getCompleted
<div class="function">
    quests:getCompleted <span class="paramlist">player</span>
</div>

- Retrieves a list of completed quests from a player.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player instance to be checked.
- Returns:
  <span class="datatype">[List][java.util.List]&lt;[String][java.lang.String]&gt;</span>
  List of string identifiers for completed quests.
- TODO:
    - Return [table][LuaTable] instead of Java list.


---
## quests:getDescription
<div class="function">
    quests:getDescription <span class="paramlist">player, questName</span>
</div>

- Retrieves the description of a quest.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player instance to be checked.
    - <span class="param">questName</span>
      <span class="datatype">[string][LuaString]</span>
      Name of the quest.
- Returns:
  <span class="datatype">[string][LuaString]</span>
  Description.


---
## quests:getDescriptionForUnstartedInRegionFromNPCName
<div class="function">
    quests:getDescriptionForUnstartedInRegionFromNPCName <span class="paramlist">player, region,
    name</span>
</div>

- Retrieves quest descriptions for unstarted quests in a specified region matching a specific NPC
  name.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player instance to be checked.
    - <span class="param">region</span>
      <span class="datatype">[string][LuaString]</span>
      Region to check in.
    - <span class="param">name</span>
      <span class="datatype">[string][LuaString]</span>
      Name of NPC.
- Returns:
  <span class="datatype">[java.util.List]<[java.lang.String]></span>
- TODO:
    - Return [table][LuaTable] instead of Java list.


---
## quests:getIncomplete
<div class="function">
    quests:getIncomplete <span class="paramlist">player, region</span>
</div>

- Retrieves a list of incomplete quests in a specified region.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player instance to be checked.
    - <span class="param">region</span>
      <span class="datatype">[string][LuaString]</span>
      Region name/identifier.
- Returns:
  <span class="datatype">[java.util.List]<[java.lang.String]></span>
  List of string identifiers of incomplete quests in ___region___.
- TODO:
    - Return [table][LuaTable] instead of Java list.


---
## quests:getLevelWarning
<div class="function">
    quests:getLevelWarning <span class="paramlist">player, questName</span>
</div>

- If the quest is too dangerous, add a warning unless the player has already completed it.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player instance to be checked.
    - <span class="param">questName</span>
      <span class="datatype">[string][LuaString]</span>
      Name of the quest.
- Returns:
  <span class="datatype">[string][LuaString]</span>


---
## quests:getNPCNamesForUnstartedInRegionForLevel
<div class="function">
    quests:getNPCNamesForUnstartedInRegionForLevel <span class="paramlist">player, region</span>
</div>

- Retrieves a list of the unique npc names for unstarted quests in a specified region.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player instance to be checked.
    - <span class="param">region</span>
      <span class="datatype">[string][LuaString]</span>
      Region to check in.
- Returns:
  <span class="datatype">[java.util.List]&lt;[java.lang.String]&gt;</span>
- TODO:
    - Return [table][LuaTable] instead of Java list.


---
## quests:getOpen
<div class="function">
    quests:getOpen <span class="paramlist">player</span>
</div>

- Retrieves a list of open quests from a player.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player instance to be checked.
- Returns:
  <span class="datatype">[java.util.List]&lt;[java.lang.String]&gt;</span>
  List of string identifiers for open quests.
- TODO:
    - Return [table][LuaTable] instead of Java list.


---
## quests:getProgressDetails
<div class="function">
    quests:getProgressDetails <span class="paramlist">player, questeName</span>
</div>

- Retrieves details on the progress of the quest.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player instance to be checked.
    - <span class="param">questName</span>
      <span class="datatype">[string][LuaString]</span>
      Name of the quest.
- Returns:
  <span class="datatype">[java.util.List]&gt;[java.lang.String]&lt;</span>
- TODO:
    - Return [table][LuaTable] instead of Java list.


---
## quests:getQuest
<div class="function">
    quests:getQuest <span class="paramlist">questName</span>
</div>

- Retrieves the [IQuest] object for a named quest.
- Parameters:
    - <span class="param">questName</span>
      <span class="datatype">[string][LuaString]</span>
      Name of quest.
- Returns:
  <span class="datatype">[IQuest]</span>
  Quest instance or [`nil`][LuaNil] if doesn't exist.


---
## quests:getQuestFromSlot
<div class="function">
    quests:getQuestFromSlot <span class="paramlist">questSlot</span>
</div>

- Retrieves the [`IQuest`][IQuest] object for a quest.
- Parameters:
    - <span class="param">questSlot</span>
      <span class="datatype">[string][LuaString]</span>
      Quest identifier string.
- Returns:
  <span class="datatype">[IQuest]</span>
  Quest instance or [`nil`][LuaNil] if doesn't exist.


---
## quests:getRepeatable
<div class="function">
    quests:getRepeatable <span class="paramlist">player</span>
</div>

- Retrieves a list of quests a player has completed, and can now do again.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player instance to be checked.
- Returns:
  <span class="datatype">[java.util.List]&gt;[java.lang.String]&lt;</span>
- TODO:
    - Return [table][LuaTable] instead of Java list.


---
## quests:isLoaded
<div class="function">
    quests:isLoaded <span class="paramlist">quest</span>
</div>

- Checks if a quest has been loaded.
- Parameters:
    - <span class="param">quest</span>
      <span class="datatype">[IQuest]</span>
      Quest instance to check.
- Returns:
  <span class="datatype">[bool][LuaBoolean]</span>
  `true` if the instance matches stored quests.


---
## quests:list
<div class="function">
    quests:list <span class="paramlist">player, questName</span>
</div>

- Creates a report on a specified quest for a player.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player to create the report for.
    - <span class="param">questName</span>
      <span class="datatype">[string][LuaString]</span>
      Name of quest to be reported.
- Returns:
  <span class="datatype">[string][LuaString]</span>
  Report.


---
## quests:listAll
<div class="function">
    quests:listAll <span class="paramlist">player</span>
</div>

- List all quests the player knows about.
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player to create the report for.
- Returns:
  <span class="datatype">[string][LuaString]</span>
  Report.


---
## quests:listStates
<div class="function">
    quests:listStates <span class="paramlist">player</span>
</div>

- Dumps the internal quest states for the specified player. This is used for the [InspectAction].
- Parameters:
    - <span class="param">player</span>
      <span class="datatype">[Player]</span>
      Player to create the report for.
- Returns:
  <span class="datatype">[string][LuaString]</span>
  Report.


---
## quests:load
<div class="function">
    quests:load <span class="paramlist">quest</span>
</div>

- Adds a quest to the world.
- Parameters:
    - <span class="param">quest</span>
      <span class="datatype">[IQuest]</span>
      The quest instance to be loaded.


---
## quests:register
<div class="function">
    quests:register <span class="paramlist">quest</span>
</div>

- ___DEPRECATED:__ Call `register` directly from the quest instance._
- Caches a quest for loading at startup. Alias for `quests:cache`.
- Parameters:
    - <span class="param">quest</span>
      <span class="datatype">[IQuest]</span>
      Quest instance to be cached.


---
## quests:unload
<div class="function">
    quests:unload <span class="paramlist">questName</span>
</div>

- Removes a quest from the world.
- Parameters:
    - <span class="param">questName</span>
      <span class="datatype">[string][LuaString]</span>
      Name of the quest to be removed.


---
# Classes


---
## LuaQuest

See: [LuaQuest]


---
### Public Members


---
#### LuaQuest.completedCheck
<div class="member">
    LuaQuest.completedCheck
</div>

- Called by `LuaQuest.isCompleted`.
- Type: [function][LuaFunction]


---
#### LuaQuest.formattedHistory
<div class="member">
    LuaQuest.formattedHistory
</div>

- Called by `LuaQuest.getFormattedHistory`.
- Type: [function][LuaFunction]


---
#### LuaQuest.history
<div class="member">
    LuaQuest.history
</div>

- Called by `LuaQuest.getHistory`.
- Type: [function][LuaFunction]


---
#### LuaQuest.init
<div class="member">
    LuaQuest.init
</div>

- Called by [IQuest.addToWorld].
- Type: [function][LuaFunction]


---
#### LuaQuest.remove
<div class="member">
    LuaQuest.remove
</div>

- Called by [IQuest.removeFromWorld].
- Type: [function][LuaFunction]


---
#### LuaQuest.repeatableCheck
<div class="member">
    LuaQuest.repeatableCheck
</div>

- Called by `LuaQuest.isRepeatable`.
- Type: [function][LuaFunction]


---
#### LuaQuest.startedCheck
<div class="member">
    LuaQuest.startedCheck
</div>

- Called by `LuaQuest.isStarted`.
- Type: [function][LuaFunction]


---
### Public Methods


---
#### LuaQuest:addToWorld
<div class="method">
    LuaQuest:addToWorld <span class="paramlist"></span>
</div>

- See [IQuest.addToWorld].


---
#### LuaQuest:getFormattedHistory
<div class="method">
    LuaQuest:getFormattedHistory <span class="paramlist">player</span>
</div>

- See [IQuest.getFormattedHistory].


---
#### LuaQuest:getHistory
<div class="method">
    LuaQuest:getHistory <span class="paramlist">player</span>
</div>

- See [IQuest.getHistory]


---
#### LuaQuest:getMinLevel
<div class="method">
    LuaQuest:getMinLevel <span class="paramlist"></span>
</div>

- See [IQuest.getMinLevel]


---
#### LuaQuest:getNPCName
<div class="method">
    LuaQuest:getNPCName <span class="paramlist"></span>
</div>

- See [IQuest.getNPCName]


---
#### LuaQuest:getName
<div class="method">
    LuaQuest:getName <span class="paramlist"></span>
</div>

- See [IQuest.getName]


---
#### LuaQuest:getOriginalName
<div class="method">
    LuaQuest:getOriginalName <span class="paramlist"></span>
</div>

- Retrieves unformatted quest name.
- Returns:
  <span class="datatype">[string][LuaString]</span>
  Unmodified quest name string.


---
#### LuaQuest:getRegion
<div class="method">
    LuaQuest:getRegion <span class="paramlist"></span>
</div>

- See [IQuest.getRegion]


---
#### LuaQuest:getSlotName
<div class="method">
    LuaQuest:getSlotName <span class="paramlist"></span>
</div>

- See [IQuest.getSlotName]


---
#### LuaQuest:isCompleted
<div class="method">
    LuaQuest:isCompleted <span class="paramlist">player</span>
</div>

- See [IQuest.isCompleted]


---
#### LuaQuest:isRepeatable
<div class="method">
    LuaQuest:isRepeatable <span class="paramlist"></span>
</div>

- See [IQuest.isRepeatable]


---
#### LuaQuest:isStarted
<div class="method">
    LuaQuest:isStarted <span class="paramlist">player</span>
</div>

- See [IQuest.isStarted]


---
#### LuaQuest:isVisibleOnQuestStatus
<div class="method">
    LuaQuest:isVisibleOnQuestStatus <span class="paramlist"></span>
</div>

- See [IQuest.isVisibleOnQuestStatus]


---
#### LuaQuest:register
<div class="method">
    LuaQuest:register <span class="paramlist"></span>
</div>
<div class="method">
    LuaQuest:register <span class="paramlist">func</span>
</div>

- Registers quest to be added to world at server startup. If called without parameters
`LuaQuest.init` must be set first.
- Parameters:
    - <span class="param">func</span>
      <span class="datatype">[function][LuaFunction]</span>
      Function to execute when `quests:loadQuest` is called.


---
#### LuaQuest:removeFromWorld
<div class="method">
    LuaQuest:removeFromWorld <span class="paramlist"></span>
</div>

- See [IQuest.removeFromWorld]


---
#### LuaQuest:setAddFunction
<div class="method">
    LuaQuest:setAddFunction <span class="paramlist">func</span>
</div>

- ___DEPRECATED:__ Set `LuaQuest.init` directly._
- Sets the function for adding the quest to the game.
- Parameters:
    - <span class="param">func</span>
      <span class="datatype">[function][LuaFunction]</span>
      Function to invoke when `LuaQuest.addToWorld` is called.


---
#### LuaQuest:setCompletedCheckFunction
<div class="method">
    LuaQuest:setCompletedCheckFunction <span class="paramlist">func</span>
</div>

- Sets the function for checking if the quest is started.
- Parameters:
    - <span class="param">func</span>
      <span class="datatype">[function][LuaFunction]</span>
      Function to invoke when `LuaQuest.isCompleted` is called.


---
#### LuaQuest:setDescription
<div class="method">
    LuaQuest:setDescription <span class="paramlist">desc</span>
</div>

- Sets the quest description string.
- Parameters:
    - <span class="param">desc</span>
      <span class="datatype">[string][LuaString]</span>
      Quest description string.


---
#### LuaQuest:setFormattedHistoryFunction
<div class="method">
    LuaQuest:setFormattedHistoryFunction <span class="paramlist">func</span>
</div>

- Sets the function for retrieving formatted history of quest state.
- Parameters:
    - <span class="param">func</span>
      <span class="datatype">[function][LuaFunction]</span>
      Function to invoke when `LuaQuest.getFormattedHistory` is called.


---
#### LuaQuest:setHistoryFunction
<div class="method">
    LuaQuest:setHistoryFunction <span class="paramlist">func</span>
</div>

- Sets the function for retrieving history of quest state.
- Parameters:
    - <span class="param">func</span>
      <span class="datatype">[function][LuaFunction]</span>
      Function to invoke when `LuaQuest.getHistory` is called.


---
#### LuaQuest:setMinLevel
<div class="method">
    LuaQuest:setMinLevel <span class="paramlist">level</span>
</div>

- Sets the recommended minimum level.
- Parameters:
    - <span class="param">level</span>
      <span class="datatype">[int][LuaInteger]</span>
      Level to return when `LuaQuest.getMinLevel` is called.


---
#### LuaQuest:setNPCName
<div class="method">
    LuaQuest:setNPCName <span class="paramlist">name</span>
</div>

- Sets the NPC name.
- Parameters:
    - <span class="param">name</span>
      <span class="datatype">[string][LuaString]</span>
      NPC name to return when `LuaQuest.getNPCName` is called.


---
#### LuaQuest:setName
<div class="method">
    LuaQuest:setName <span class="paramlist">name</span>
</div>

- Sets the quest name string.
- Parameters:
    - <span class="param">name</span>
      <span class="datatype">[string][LuaString]</span>
      Quest name string to be returned when `LuaQuest.getName` is called.


---
#### LuaQuest:setRegion
<div class="method">
    LuaQuest:setRegion <span class="paramlist">region</span>
</div>

- Sets the quest region.
- Parameters:
    - <span class="param">region</span>
      <span class="datatype">[string][LuaString]</span>
      Region string to be returned when `LuaQuest.getRegion` is called.


---
#### LuaQuest:setRemoveFunction
<div class="method">
    LuaQuest:setRemoveFunction <span class="paramlist">func</span>
</div>

- ___DEPRECATED:__ Set `LuaQuest.remove` directly._
- Sets the function for removing the quest from the game.
- Parameters:
    - <span class="param">func</span>
      <span class="datatype">[function][LuaFunction]</span>
      Function to invoke when `LuaQuest.removeFromWorld` is called.


---
#### LuaQuest:setRepeatable
<div class="method">
    LuaQuest:setRepeatable <span class="paramlist"></span>
</div>

- Sets the repeatable status of the quest (overrides `LuaQuest.setCompletedCheckFunction`).
- Parameters:
    - <span class="param">repeatable</span>
      <span class="datatype">[bool][LuaBoolean]</span>
      If `true`, the quest is repeatable.


---
#### LuaQuest:setRepeatableCheckFunction
<div class="method">
    LuaQuest:setRepeatableCheckFunction <span class="paramlist">func</span>
</div>

- Sets the function for checking if the quest is repeatable.
- Parameters:
    - <span class="param">func</span>
      <span class="datatype">[function][LuaFunction]</span>
      Function to invoke when `LuaQuest.isRepeatable` is called.


---
#### LuaQuest:setSlotName
<div class="method">
    LuaQuest:setSlotName <span class="paramlist">slot</span>
</div>

- Sets the quest identifier string.
- Parameters:
    - <span class="param">slot</span>
      <span class="datatype">[string][LuaString]</span>
      Slot identifier string to be returned when `LuaQuest.getSlotName` is called.


---
#### LuaQuest:setStartedCheckFunction
<div class="method">
    LuaQuest:setStartedCheckFunction <span class="paramlist">func</span>
</div>

- Sets the function for checking if the quest is started.
- Parameters:
    - <span class="param">func</span>
      <span class="datatype">[function][LuaFunction]</span>
      Function to invoke when `LuaQuest.isStarted` is called.


---
#### LuaQuest:setVisibleOnQuestStatus
<div class="method">
    LuaQuest:setVisibleOnQuestStatus <span class="paramlist">visible</span>
</div>

- Sets whether or not the quest should be shown in the travel log.
- Parameters:
    - <span class="param">visible</span>
      <span class="datatype">[bool][LuaBoolean]</span>
      If `true`, quest will be displayed in travel log.


---
# Usage Examples

Standard quest example:

```lua
local loadStep1 = function()
    ...
end

local loadStep2 = function()
    ...
end

local myQuest = quests:create("my_quest", "My Quest")
myQuest:register(function()
    loadStep1()
    loadStep2()
end)
```

Example of [CoalForHaunchy] quest manuscript converted to Lua:

```lua
local itemBuilder = quests:createBuilder("BringItemTask")

itemBuilder:info()
    :name("Coal for Haunchy")
    :description("Haunchy Meatoch is afraid of his BBQ grillfire. Will his coal last till the steaks are ready or will he need some more?")
    :internalName("coal_for_haunchy")
    :repeatableAfterMinutes(2 * 24 * 60)
    :minLevel(0)
    :region(Region.ADOS_CITY)
    :questGiverNpc("Haunchy Meatoch")

itemBuilder:history()
    :whenNpcWasMet("Haunchy Meatoch welcomed me to the Ados market.")
    :whenQuestWasRejected("He asked me to fetch him some pieces of coal but I don't have time to collect some.")
    :whenQuestWasAccepted("The BBQ grill-heat is low and I promised Haunchy to help him out with 25 pieces of coal.")
    :whenTaskWasCompleted("I found 25 pieces of coal for the Haunchy and think he will be happy.")
    :whenQuestWasCompleted("Haunchy Meatoch was really happy when I gave him the coal, he has enough for now. He gave me some of the best steaks which I ever ate!")
    :whenQuestCanBeRepeated("But I'd bet his amount is low again and needs more. Maybe I'll get more grilled tasty steaks.")

itemBuilder:offer()
    :respondToRequest("I cannot use wood for this huge BBQ. To keep the heat I need some really old stone coal but there isn't much left. The problem is, that I can't fetch it myself because my steaks would burn then so I have to stay here. Can you bring me 25 pieces of #coal for my BBQ please?")
    :respondToUnrepeatableRequest("The coal amount behind my counter is still high enough. I will not need more for some time.")
    :respondToRepeatedRequest("The last coal you brought me is mostly gone again. Will you bring me some more?")
    :respondToAccept("Thank you! I'll be sure to give you a nice and tasty reward.")
    :respondTo({"coal"}):saying("Coal isn't easy to find. You normally can find it somewhere in the ground but perhaps you are lucky and find some in the old Semos Mine tunnels... Will you help me?")
    :respondToReject("Oh, never mind. I thought you love BBQs like I do. Bye then.")
    :rejectionKarmaPenalty(10.0)
    :remind("Luckily my BBQ is still going. But please hurry up to bring me 25 coal as you promised.")

entities:getNPC("Haunchy Meatoch"):addReply("coal", "Sometime you could do me a #favor ...")

itemBuilder:task()
    :requestItem(25, "coal")

itemBuilder:complete()
    :greet("Ah, I see, you have enough coal to keep my BBQ on! Is it for me?")
    :respondToReject("Well then, hopefully someone else will help me before my BBQ goes out.")
    :respondToAccept(nil)
    :rewardWith(actions:create("IncreaseXPAction", {200}))
    :rewardWith(actions:create("IncreaseKarmaAction", {20}))
    :rewardWith(actions:create("EquipRandomAmountOfItemAction", {"grilled steak", 1, 4, 1,
        "Thank you! Take [this_these] [number_item] from my grill!"}))

quests:register(quests:createManuscript(itemBuilder))
```


[java.lang.String]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html
[java.util.List]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html

[LuaBoolean]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaBoolean.html
[LuaFunction]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaFunction.html
[LuaInteger]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaInteger.html
[LuaNil]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaNil.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html

[InspectAction]: /reference/java/games/stendhal/server/actions/admin/InspectAction.html
[IQuest]: /reference/java/games/stendhal/server/maps/quests/IQuest.html
[IQuest.addToWorld]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#addToWorld()
[IQuest.getFormattedHistory]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#getFormattedHistory(games.stendhal.server.entity.player.Player)
[IQuest.getHistory]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#getHistory(games.stendhal.server.entity.player.Player)
[IQuest.getMinLevel]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#getMinLevel()
[IQuest.getName]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#getName()
[IQuest.getNPCName]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#getNPCName()
[IQuest.getRegion]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#getRegion()
[IQuest.getSlotName]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#getSlotName()
[IQuest.isCompleted]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#isCompleted(games.stendhal.server.entity.player.Player)
[IQuest.isRepeatable]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#isRepeatable(games.stendhal.server.entity.player.Player)
[IQuest.isStarted]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#isStarted(games.stendhal.server.entity.player.Player)
[IQuest.isVisibleOnQuestStatus]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#isVisibleOnQuestStatus()
[IQuest.removeFromWorld]: /reference/java/games/stendhal/server/maps/quests/IQuest.html#removeFromWorld()
[LuaQuest]: /reference/java/games/stendhal/server/core/scripting/lua/LuaQuestHelper.LuaQuest.html
[Player]: /reference/java/games/stendhal/server/entity/player/Player.html
[QuestBuilder]: /reference/java/games/stendhal/server/entity/npc/quest/QuestBuilder.html
[QuestManuscript]: /reference/java/games/stendhal/server/entity/npc/quest/QuestManuscript.html
[StendhalQuestSystem]: /reference/java/games/stendhal/server/core/rp/StendhalQuestSystem.html

[CoalForHaunchy]: https://github.com/arianne/stendhal/blob/master/src/games/stendhal/server/maps/quests/CoalForHaunchy.java
