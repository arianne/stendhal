
[TOC]

---
## Introduction

Object instance: `quests`

---
### Description

Adds helper functions for creating &amp; manipulating quests &amp; exposes select public methods of
the [StendhalQuestSystem][] class.

## Methods

---
### quests:cache
<div class="function">
    quests:cache <span class="params">(quest)</span>
</div>

- ___DEPRECATED:__ Call `register` directly from the quest instance._
- Caches a quest for loading at server startup.
- Parameters:
    - ___quest:__ ([IQuest][])_ Quest instance to be cached.

---
### quests:create
<div class="function">
    quests:create <span class="params">()</span>
</div>
<div class="function">
    quests:create <span class="params">(slotName)</span>
</div>
<div class="function">
    quests:create <span class="params">(slotName, name)</span>
</div>
<div class="function">
    quests:create <span class="params">(slotName, name, desc)</span>
</div>

- Creates a new quest.
- Parameters:
    - ___slotName:__ ([string][LuaString])_ Quest slot identifier.
    - ___name:__ ([string][LuaString])_ The reader friendly name that can be shown in
      travel log.
    - ___desc:__ ([string][LuaString]) Quest description.
- Returns: _([LuaQuest][])_
  - New quest instance.

---
### quests:createBuilder
<div class="function">
    quests:createBuilder <span class="params">(task)</span>
</div>

- Creates a quest builder to pass to `quests:createManuscript`.
- Parameters:
    - ___task:__ ([string][LuaString])_ Quest task type. Supported are "BringItemTask" &amp;
      "KillCreaturesTask".
- Returns: _([QuestBuilder][])_
  - New quest builder instance.

---
### quests:createManuscript
<div class="function">
    quests:createManuscript <span class="params">(builder)</span>
</div>

- Creates a new quest in manuscript format.
- Parameters:
    - ___builder:__ ([QuestBuilder][])_ A quest builder instance that defines quest.
- Returns: _([QuestManuscript][])_
    - New quest instance.

---
### quests:getCompleted
<div class="function">
    quests:getCompleted <span class="params">(player)</span>
</div>

- Retrieves a list of completed quests from a player.
- Parameters:
    - ___player:__ ([Player][])_ Player instance to be checked.
- Returns: _([List][java.util.List]&lt;[String][java.lang.String]&gt;)_
    - List of string identifiers for completed quests.
- TODO:
    - Return [LuaTable][] instead of Java list.

---
### quests:getDescription
<div class="function">
    quests:getDescription <span class="params">(player, questName)</span>
</div>

- Retrieves the description of a quest.
- Parameters:
    - ___player:__ ([Player][])_ Player instance to be checked.
    - ___questName:__ ([string][LuaString])_ Name of the quest.
- Returns: _([string][LuaString])_
    - Description.

---
### quests:getDescriptionForUnstartedInRegionFromNPCName
<div class="function">
    quests:getDescriptionForUnstartedInRegionFromNPCName <span class="params">(player, region, name)</span>
</div>

- Retrieves quest descriptions for unstarted quests in a specified region matching a specific NPC
  name.
- Parameters:
    - ___player:__ ([Player][])_ Player instance to be checked.
    - ___region:__ ([string][LuaString])_ Region to check in.
    - ___name:__ ([string][LuaString])_ Name of NPC.
- Returns: _([java.util.List][]<[java.lang.String][]>)_
- TODO:
    - Return [LuaTable][] instead of Java list.

---
### quests:getIncomplete
<div class="function">
    quests:getIncomplete <span class="params">(player, region)</span>
</div>

- Retrieves a list of incomplete quests in a specified region.
- Parameters:
    - ___player:__ ([Player][])_ Player instance to be checked.
    - ___region:__ ([string][LuaString])_ Region name/identifier.
- Returns: _([java.util.List][]<[java.lang.String][]>)_
    - List of string identifiers of incomplete quests in ___region___.
- TODO:
    - Return [LuaTable][] instead of Java list.

---
### quests:getLevelWarning
<div class="function">
    quests:getLevelWarning <span class="params">(player, questName)</span>
</div>

- If the quest is too dangerous, add a warning unless the player has already completed it.
- Parameters:
    - ___player:__ ([Player][])_ Player instance to be checked.
    - ___questName:__ ([string][LuaString])_ Name of the quest.
- Returns: _([string][LuaString])_

---
### quests:getNPCNamesForUnstartedInRegionForLevel
<div class="function">
    quests:getNPCNamesForUnstartedInRegionForLevel <span class="params">(player, region)</span>
</div>

- Retrieves a list of the unique npc names for unstarted quests in a specified region.
- Parameters:
    - ___player:__ ([Player][])_ Player instance to be checked.
    - ___region:__ ([string][LuaString])_ Region to check in.
- Returns: _([java.util.List][]&lt;[java.lang.String][]&gt;)_
- TODO:
    - Return [LuaTable][] instead of Java list.

---
### quests:getOpen
<div class="function">
    quests:getOpen <span class="params">(player)</span>
</div>

- Retrieves a list of open quests from a player.
- Parameters:
    - ___player:__ ([Player][])_ Player instance to be checked.
- Returns: _([java.util.List][]&lt;[java.lang.String][]&gt;)_
    - List of string identifiers for open quests.
- TODO:
    - Return [LuaTable][] instead of Java list.

---
### quests:getProgressDetails
<div class="function">
    quests:getProgressDetails <span class="params">(player, questeName)</span>
</div>

- Retrieves details on the progress of the quest.
- Parameters:
    - ___player:__ ([Player][])_ Player instance to be checked.
    - ___questName:__ ([string][LuaString])_ Name of the quest.
- Returns: _([java.util.List][]&gt;[java.lang.String][]&lt;)_
- TODO:
    - Return [LuaTable][] instead of Java list.

---
### quests:getQuest
<div class="function">
    quests:getQuest <span class="params">(questName)</span>
</div>

- Retrieves the [IQuest][] object for a named quest.
- Parameters:
    - ___questName:__ ([string][LuaString])_ Name of quest.
- Returns: _([IQuest][])_
    - Quest instance or [nil][LuaNil] if doesn't exist.

---
### quests:getQuestFromSlot
<div class="function">
    quests:getQuestFromSlot <span class="params">(questSlot)</span>
</div>

- Retrieves the [IQuest][] object for a quest.
- Parameters:
    - ___questSlot:__ ([string][LuaString])_ Quest identifier string.
- Returns: _([IQuest][])_
    - Quest instance or [nil][LuaNil] if doesn't exist.

---
### quests:getRepeatable
<div class="function">
    quests:getRepeatable <span class="params">(player)</span>
</div>

- Retrieves a list of quests a player has completed, and can now do again.
- Parameters:
    - ___player:__ ([Player][])_ Player instance to be checked.
- Returns: _([java.util.List][]&gt;[java.lang.String][]&lt;)_
- TODO:
    - Return [LuaTable][] instead of Java list.

---
### quests:isLoaded
<div class="function">
    quests:isLoaded <span class="params">(quest)</span>
</div>

- Checks if a quest has been loaded.
- Parameters:
    - ___quest:__ ([IQuest][])_ Quest instance to check.
- Returns: _([bool][LuaBoolean])_
    - `true` if the instance matches stored quests.

---
### quests:list
<div class="function">
    quests:list <span class="params">(player, questName)</span>
</div>

- Creates a report on a specified quest for a player.
- Parameters:
    - ___player:__ ([Player][])_ Player to create the report for.
    - ___questName:__ ([string][LuaString])_ Name of quest to be reported.
- Returns: _([string][LuaString])_
    - Report.

---
### quests:listAll
<div class="function">
    quests:listAll <span class="params">(player)</span>
</div>

- List all quests the player knows about.
- Parameters:
    - ___player:__ ([Player][])_ Player to create the report for.
- Returns: _([string][LuaString])_
    - Report.

---
### quests:listStates
<div class="function">
    quests:listStates <span class="params">(player)</span>
</div>

- Dumps the internal quest states for the specified player. This is used for the [InspectAction][].
- Parameters:
    - ___player:__ ([Player][])_ Player to create the report for.
- Returns: _([string][LuaString])_
    - Report.

---
### quests:load
<div class="function">
    quests:load <span class="params">(quest)</span>
</div>

- Adds a quest to the world.
- Parameters:
    - ___quest:__ ([IQuest][])_ The quest instance to be loaded.

---
### quests:register
<div class="function">
    quests:register <span class="params">(quest)</span>
</div>

- ___DEPRECATED:__ Call `register` directly from the quest instance._
- Caches a quest for loading at startup. Alias for `quests:cache`.
- Parameters:
    - ___quest__ ([IQuest][])_ Quest instance to be cached.

---
### quests:unload
<div class="function">
    quests:unload <span class="params">(questName)</span>
</div>

- Removes a quest from the world.
- Parameters:
    - ___questName:__ ([string][LuaString])_ Name of the quest to be removed.

---
## Classes

---
### LuaQuest

See: [LuaQuest][]

---
#### Public Members

---
##### LuaQuest.completedCheck
<div class="member">
    LuaQuest.completedCheck
</div>

- Called by `LuaQuest.isCompleted`.
- Type: [function][LuaFunction]

---
##### LuaQuest.formattedHistory
<div class="member">
    LuaQuest.formattedHistory
</div>

- Called by `LuaQuest.getFormattedHistory`.
- Type: [function][LuaFunction]

---
##### LuaQuest.history
<div class="member">
    LuaQuest.history
</div>

- Called by `LuaQuest.getHistory`.
- Type: [function][LuaFunction]

---
##### LuaQuest.init
<div class="member">
    LuaQuest.init
</div>

- Called by [IQuest.addToWorld](/reference/java/games/stendhal/server/maps/quests/IQuest.html#addToWorld()).
- Type: [function][LuaFunction]

---
##### LuaQuest.remove
<div class="member">
    LuaQuest.remove
</div>

- Called by [IQuest.addToWorld](/reference/java/games/stendhal/server/maps/quests/IQuest.html#removeFromWorld()).
- Type: [function][LuaFunction]

---
##### LuaQuest.repeatableCheck
<div class="member">
    LuaQuest.repeatableCheck
</div>

- Called by `LuaQuest.isRepeatable`.
- Type: [function][LuaFunction]

---
##### LuaQuest.startedCheck
<div class="member">
    LuaQuest.startedCheck
</div>

- Called by `LuaQuest.isStarted`.
- Type: [function][LuaFunction]

---
#### Public Methods

---
##### LuaQuest:addToWorld
<div class="method">
    LuaQuest:addToWorld <span class="params">()</span>
</div>

- See [IQuest.addToWorld](/reference/java/games/stendhal/server/maps/quests/IQuest.html#addToWorld())

---
##### LuaQuest:getFormattedHistory
<div class="method">
    LuaQuest:getFormattedHistory <span class="params">(player)</span>
</div>

- See [IQuest.getFormattedHistory](/reference/java/games/stendhal/server/maps/quests/IQuest.html#getFormattedHistory(games.stendhal.server.entity.player.Player))

---
##### LuaQuest:getHistory
<div class="method">
    LuaQuest:getHistory <span class="params">(player)</span>
</div>

- See [IQuest.getHistory](/reference/java/games/stendhal/server/maps/quests/IQuest.html#getHistory(games.stendhal.server.entity.player.Player))

---
##### LuaQuest:getMinLevel
<div class="method">
    LuaQuest:getMinLevel <span class="params">()</span>
</div>

- See [IQuest.getMinLevel](/reference/java/games/stendhal/server/maps/quests/IQuest.html#getMinLevel())

---
##### LuaQuest:getNPCName
<div class="method">
    LuaQuest:getNPCName <span class="params">()</span>
</div>

- See [IQuest.getNPCName](/reference/java/games/stendhal/server/maps/quests/IQuest.html#getNPCName())

---
##### LuaQuest:getName
<div class="method">
    LuaQuest:getName <span class="params">()</span>
</div>

- See [IQuest.getName](/reference/java/games/stendhal/server/maps/quests/IQuest.html#getName())

---
##### LuaQuest:getOriginalName
<div class="method">
    LuaQuest:getOriginalName <span class="params">()</span>
</div>

- Retrieves unformatted quest name.
- Returns: _([string][LuaString])_
    - Unmodified quest name string.

---
##### LuaQuest:getRegion
<div class="method">
    LuaQuest:getRegion <span class="params">()</span>
</div>

- See [IQuest.getRegion](/reference/java/games/stendhal/server/maps/quests/IQuest.html#getRegion())

---
##### LuaQuest:getSlotName
<div class="method">
    LuaQuest:getSlotName <span class="params">()</span>
</div>

- See [IQuest.getSlotName](http://stendhal.localhost/reference/java/games/stendhal/server/maps/quests/IQuest.html#getSlotName())

---
##### LuaQuest:isCompleted
<div class="method">
    LuaQuest:isCompleted <span class="params">(player)</span>
</div>

- See [IQuest.isCompleted](http://stendhal.localhost/reference/java/games/stendhal/server/maps/quests/IQuest.html#isCompleted(games.stendhal.server.entity.player.Player))

---
##### LuaQuest:isRepeatable
<div class="method">
    LuaQuest:isRepeatable <span class="params">()</span>
</div>

- See [IQuest.isRepeatable](http://stendhal.localhost/reference/java/games/stendhal/server/maps/quests/IQuest.html#isRepeatable(games.stendhal.server.entity.player.Player))

---
##### LuaQuest:isStarted
<div class="method">
    LuaQuest:isStarted <span class="params">(player)</span>
</div>

- See [IQuest.isStarted](http://stendhal.localhost/reference/java/games/stendhal/server/maps/quests/IQuest.html#isStarted(games.stendhal.server.entity.player.Player))

---
##### LuaQuest:isVisibleOnQuestStatus
<div class="method">
    LuaQuest:isVisibleOnQuestStatus <span class="params">()</span>
</div>

- See [IQuest.isVisibleOnQuestStatus](http://stendhal.localhost/reference/java/games/stendhal/server/maps/quests/IQuest.html#isVisibleOnQuestStatus())

---
##### LuaQuest:register
<div class="method">
    LuaQuest:register <span class="params">()</span>
</div>
<div class="method">
    LuaQuest:register <span class="params">(func)</span>
</div>

- Registers quest to be added to world at server startup. `LuaQuest.init` must be set before this is
  called.
- Parameters:
    - ___func:__ ([function][LuaFunction])_ Function to execute when `quests:loadQuest` is called.

---
##### LuaQuest:removeFromWorld
<div class="method">
    LuaQuest:removeFromWorld <span class="params">()</span>
</div>

- See [IQuest.removeFromWorld](http://stendhal.localhost/reference/java/games/stendhal/server/maps/quests/IQuest.html#removeFromWorld())

---
##### LuaQuest:setAddFunction
<div class="method">
    LuaQuest:setAddFunction <span class="params">(func)</span>
</div>

- ___DEPRECATED:__ Set `LuaQuest.init` directly._
- Sets the function for adding the quest to the game.
- Parameters:
    - ___func:__ ([function][LuaFunction])_ Function to invoke when `LuaQuest.addToWorld` is called.

---
##### LuaQuest:setCompletedCheckFunction
<div class="method">
    LuaQuest:setCompletedCheckFunction <span class="params">(func)</span>
</div>

- Sets the function for checking if the quest is started.
- Parameters:
    - ___func:__ ([function][LuaFunction])_ Function to invoke when `LuaQuest.isCompleted` is called.

---
##### LuaQuest:setDescription
<div class="method">
    LuaQuest:setDescription <span class="params">(desc)</span>
</div>

- Sets the quest description string.
- Parameters:
    - ___desc:__ ([string][LuaString])_ Quest description string.

---
##### LuaQuest:setFormattedHistoryFunction
<div class="method">
    LuaQuest:setFormattedHistoryFunction <span class="params">(func)</span>
</div>

- Sets the function for retrieving formatted history of quest state.
- Parameters:
    - ___func:__ ([function][LuaFunction])_ Function to invoke when `LuaQuest.getFormattedHistory`
      is called.

---
##### LuaQuest:setHistoryFunction
<div class="method">
    LuaQuest:setHistoryFunction <span class="params">(func)</span>
</div>

- Sets the function for retrieving history of quest state.
- Parameters:
    - ___func:__ ([function][LuaFunction])_ Function to invoke when `LuaQuest.getHistory` is called.

---
##### LuaQuest:setMinLevel
<div class="method">
    LuaQuest:setMinLevel <span class="params">(level)</span>
</div>

- Sets the recommended minimum level.
- Parameters:
    - ___level:__ ([int][LuaInteger])_ Level to return when `LuaQuest.getMinLevel` is called.

---
##### LuaQuest:setNPCName
<div class="method">
    LuaQuest:setNPCName <span class="params">(name)</span>
</div>

- Sets the NPC name.
- Parameters:
    - ___name:__ ([string][LuaString])_ NPC name to return when `LuaQuest.getNPCName` is called.

---
##### LuaQuest:setName
<div class="method">
    LuaQuest:setName <span class="params">(name)</span>
</div>

- Sets the quest name string.
- Parameters:
    - ___name:__ ([string][LuaString])_ Quest name string to be returned when `LuaQuest.getName` is
      called.

---
##### LuaQuest:setRegion
<div class="method">
    LuaQuest:setRegion <span class="params">(region)</span>
</div>

- Sets the quest region.
- Parameters:
    - ___region:__ ([string][LuaString])_ Region string to be returned when `LuaQuest.getRegion` is
      called.

---
##### LuaQuest:setRemoveFunction
<div class="method">
    LuaQuest:setRemoveFunction <span class="params">(func)</span>
</div>

- ___DEPRECATED:__ Set `LuaQuest.remove` directly._
- Sets the function for removing the quest from the game.
- Parameters:
    - ___func:__ ([function][LuaFunction])_ Function to invoke when `LuaQuest.removeFromWorld` is
      called.

---
##### LuaQuest:setRepeatable
<div class="method">
    LuaQuest:setRepeatable <span class="params">()</span>
</div>

- Sets the repeatable status of the quest (overrides `LuaQuest.setCompletedCheckFunction`).
- Parameters:
    - ___repeatable:__ ([bool][LuaBoolean])_ If `true`, the quest is repeatable.

---
##### LuaQuest:setRepeatableCheckFunction
<div class="method">
    LuaQuest:setRepeatableCheckFunction <span class="params">(func)</span>
</div>

- Sets the function for checking if the quest is repeatable.
- Parameters:
    - ___func:__ ([function][LuaFunction])_ Function to invoke when `LuaQuest.isRepeatable` is
      called.

---
##### LuaQuest:setSlotName
<div class="method">
    LuaQuest:setSlotName <span class="params">(slot)</span>
</div>

- Sets the quest identifier string.
- Parameters:
    - ___slot:__ ([string][LuaString])_ Slot identifier string to be returned when
      `LuaQuest.getSlotName` is called.

---
##### LuaQuest:setStartedCheckFunction
<div class="method">
    LuaQuest:setStartedCheckFunction <span class="params">(func)</span>
</div>

- Sets the function for checking if the quest is started.
- Parameters:
    - ___func:__ ([function][LuaFunction])_ Function to invoke when `LuaQuest.isStarted` is called.

---
##### LuaQuest:setVisibleOnQuestStatus
<div class="method">
    LuaQuest:setVisibleOnQuestStatus <span class="params">(visible)</span>
</div>

- Sets whether or not the quest should be shown in the travel log.
- Parameters:
    - ___visible:__ ([bool][LuaBoolean])_ If `true`, quest will be displayed in travel log.

---
## Usage Examples

Standard quest example:

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

Example of [CoalForHaunchy][] quest manuscript converted to Lua:

```
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

entities:getNPC("Haunchy Meatoch"):addReply("coal", "Sometime you could do me a #favour ...")

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
[LuaNil]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaNil.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html

[InspectAction]: /reference/java/games/stendhal/server/actions/admin/InspectAction.html
[IQuest]: /reference/java/games/stendhal/server/maps/quests/IQuest.html
[LuaQuest]: /reference/java/games/stendhal/server/core/scripting/lua/LuaQuestHelper.LuaQuest.html
[Player]: /reference/java/games/stendhal/server/entity/player/Player.html
[QuestBuilder]: /reference/java/games/stendhal/server/entity/npc/quest/QuestBuilder.html
[QuestManuscript]: /reference/java/games/stendhal/server/entity/npc/quest/QuestManuscript.html
[StendhalQuestSystem]: /reference/java/games/stendhal/server/core/rp/StendhalQuestSystem.html

[CoalForHaunchy]: https://github.com/arianne/stendhal/blob/master/src/games/stendhal/server/maps/quests/CoalForHaunchy.java
