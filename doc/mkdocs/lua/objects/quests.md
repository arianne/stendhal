
[TOC]

# Introduction

Adds helper functions for creating & manipulating quests & exposes select public methods of the [StendhalQuestSystem][] class.

# quests

## Methods

---
### quests:create
<span style="color:green; font-weight:bold;">quests:create</span>(slotName, name)

- Creates a new quest.
- Parameters:
    - ***slotName:*** (optional) The string identifier for the quest.
    - ***name:*** (optional) The human-readable name that can be shown in travel log.
- Returns: New [LuaQuest][] instance.

---
### quests:createManuscript
<span style="color:green; font-weight:bold;">quests:createManuscript</span>(builder)

- Creates a new quest in manuscript format.
- Parameters:
    - ***builder:*** A [QuestBuilder](../../../java/games/stendhal/server/entity/npc/quest/QuestBuilder.html) instance that defines quest.
- Returns: New [QuestManuscript](../../../java/games/stendhal/server/entity/npc/quest/QuestManuscript.html) instance.

---
### quests:createBuilder
<span style="color:green; font-weight:bold;">quests:createBuilder</span>(task)

- Creates a quest builder to pass to `quests:createManuscript`.
- Parameters:
    - ***task:*** Task type identifier. Supported are "BringItemTask" & "KillCreaturesTask".
- Returns: New [QuestBuilder](../../../java/games/stendhal/server/entity/npc/quest/QuestBuilder.html) instance.

---
### quests:load
<span style="color:green; font-weight:bold;">quests:load</span>(quest)

- Adds a quest to the world.
- Parameters:
    - ***quest:*** The [IQuest][] instance to be loaded.

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
    - ***quest:*** [IQuest][] instance to be loaded.
- Aliases:
    - <span style="color:green; font-style:italic;">quests:register</span>

---
### quests:isLoaded
<span style="color:green; font-weight:bold;">quests:isLoaded</span>(quest)

- Checks if a quest has been loaded.
- Parameters:
    - ***quest:*** [IQuest][] instance to check.
- Returns: `true` if the instance matches stored quests.

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

- Dumps the internal quest states for the specified player. This is used for the [InspectAction][].
- Parameters:
    - ***player:*** Player to create the report for.
- Returns: String report.

---
### quests:getQuest
<span style="color:green; font-weight:bold;">quests:getQuest</span>(questName)

- Retrieves the [IQuest][] object for a named quest.
- Parameters:
    - ***questName:*** Name of quest.
- Returns: `IQuest` or `null` if doesn't exist.

---
### quests:getQuestFromSlot
<span style="color:green; font-weight:bold;">quests:getQuestFromSlot</span>(questSlot)

- Retrieves the [IQuest][] object for a quest.
- Parameters:
    - ***questSlot:*** Quest identifier string.
- Returns: `IQuest` or `null` if doesn't exist.

---
### quests:getOpen
<span style="color:green; font-weight:bold;">quests:getOpen</span>(player)

- Retrieves a list of open quests from a player.
- Parameters:
    - ***player:*** [Player][] instance to be checked.
- Returns: List of string identifiers for open quests.

---
### quests:getCompleted
<span style="color:green; font-weight:bold;">quests:getCompleted</span>(player)

- Retrieves a list of completed quests from a player.
- Parameters:
    - ***player:*** [Player][] instance to be checked.
- Returns: List of string identifiers for completed quests.

---
### quests:getIncomplete
<span style="color:green; font-weight:bold;">quests:getIncomplete</span>(player, region)

- Retrieves a list of incomplete quests in a specified region.
- Parameters:
    - ***player:*** [Player][] instance to be checked.
    - ***region:*** Region name/identifier.
- Returns: List of string identifiers of incomplete quests in region.

---
### quests:getRepeatable
<span style="color:green; font-weight:bold;">quests:getRepeatable</span>(player)

- Retrieves a list of quests a player has completed, and can now do again.
- Parameters:
    - ***player:*** [Player][] instance to be checked.
- Returns:

---
### quests:getDescription
<span style="color:green; font-weight:bold;">quests:getDescription</span>(player, questName)

- Retrieves the description of a quest.
- Parameters:
    - ***player:*** [Player][] instance to be checked.
    - ***questName:*** Name of the quest.
- Returns: String description.

---
### quests:getLevelWarning
<span style="color:green; font-weight:bold;">quests:getLevelWarning</span>(player, questName)

- If the quest is too dangerous, add a warning unless the player has already completed it.
- Parameters:
    - ***player:*** [Player][] instance to be checked.
    - ***questName:*** Name of the quest.
- Returns: String

---
### quests:getProgressDetails
<span style="color:green; font-weight:bold;">quests:getProgressDetails</span>(player, questeName)

- Retrieves details on the progress of the quest.
- Parameters:
    - ***player:*** [Player][] instance to be checked.
    - ***questName:*** Name of the quest.
- Returns: [java.util.List][]<[java.lang.String][]>

---
### quests:getNPCNamesForUnstartedInRegionForLevel
<span style="color:green; font-weight:bold;">quests:getNPCNamesForUnstartedInRegionForLevel</span>(player, region)

- Retrieves a list of the unique npc names for unstarted quests in a specified region.
- Parameters:
    - ***player:*** [Player][] instance to be checked.
    - ***region:*** Region to check in.
- Returns: [java.util.List][]<[java.lang.String][]>

---
### quests:getDescriptionForUnstartedInRegionFromNPCName
<span style="color:green; font-weight:bold;">quests:getDescriptionForUnstartedInRegionFromNPCName</span>(player, region, name)

- Retrieves quest descriptions for unstarted quests in a specified region matching a specific NPC name.
- Parameters:
    - ***player:*** [Player][] instance to be checked.
    - ***region:*** Region to check in.
    - ***name:*** Name of NPC.
- Returns: [java.util.List][]<[java.lang.String][]>

# Classes

## LuaQuest

See: [LuaQuest][]

### Public Members

#### LuaQuest.init
<span style="color:darkblue; font-weight:bold;">LuaQuest.init</span>

- Called by [IQuest.addToWorld](../../../java/games/stendhal/server/maps/quests/IQuest.html#addToWorld()).
- Type: function

### Public Methods

### Usage

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


[java.lang.String]: https://docs.oracle.com/javase/8/docs/api/java/lang/String.html
[java.util.List]: https://docs.oracle.com/javase/8/docs/api/java/util/List.html

[CoalForHaunchy]: https://github.com/arianne/stendhal/blob/master/src/games/stendhal/server/maps/quests/CoalForHaunchy.java
[InspectAction]: ../../../java/games/stendhal/server/actions/admin/InspectAction.html
[IQuest]: ../../../java/games/stendhal/server/maps/quests/IQuest.html
[LuaQuest]: ../../../java/games/stendhal/server/core/scripting/lua/LuaQuestHelper.LuaQuest.html
[Player]: ../../../java/games/stendhal/server/entity/player/Player.html
[StendhalQuestSystem]: ../../../java/games/stendhal/server/core/rp/StendhalQuestSystem.html
