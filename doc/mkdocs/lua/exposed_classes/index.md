
Static Classes &amp; Enumerations
=============================

[TOC]

---
## ConversationStates

The [ConversationStates][] enum.

Example usage:

```
local npc = entities:createSpeakerNPC("foo")
npc:setCurrentState(ConversationStates.IDLE)
```

---
## ConversationPhrases

The [ConversationPhrases][] class.

Example usage:

```
local npc = entities:createSpeakerNPC("foo")
npc:add(ConversationStates.IDLE,
	ConversationPhrases.GREETING_MESSAGES,
	nil,
	ConversationStates.ATTENDING,
	"Hello! How can I help you.",
	nil)
```

---
## CollisionAction

The [CollisionAction][] enum.

Example usage:

```
local npc = entities:createSilentNPC()
npc:setCollisionAction(CollisionAction.STOP)
```

---
## SkinColor

The [SkinColor][] enum.

Example usage:

```
local npc = entities:createSpeakerNPC("foo")
npc:setOutfit("body=0,head=0,hair=3,dress=5")
npc:setOutfitColor("skin", SkinColor.DARK)
```

---
## Direction

The [Direction][] enum.

Example usage:

```
local npc = entities:createSpeakerNPC("foo")
npc:setDirection(Direction.DOWN)
```

---
## DaylightPhase

The [DaylightPhase][] enum.

---
## Region

The [Region][] class.

---
## MathHelper

The [MathHelper][] class.

---
## Color

The [java.awt.Color][] class.

Example usage:

```
local npc = entities:createSpeakerNPC("foo")
npc:setOutfit("body=0,head=0,hair=3,dress=5")
npc:setOutfitColor("dress", Color.BLUE)
```

---
## SingletonRepository

The [SingletonRepository][] static instance.

Implemented as `singletons` object.

---
## CloneManager

The [CloneManager][] static instance.

Implemented as `clones` object.

Example usage:

```
local myClone = clones:clone(orig)
```

---
## Rand

The [Rand][] random number generator.

Implemented as `random` object.

---
## ShopType

The [ShopType][] enum.


[java.awt.Color]: https://docs.oracle.com/javase/8/docs/api/java/awt/Color.html

[CloneManager]: /reference/java/games/stendhal/server/entity/npc/CloneManager.html
[CollisionAction]: /reference/java/games/stendhal/server/entity/CollisionAction.html
[ConversationPhrases]: /reference/java/games/stendhal/server/entity/npc/ConversationPhrases.html
[ConversationStates]: /reference/java/games/stendhal/server/entity/npc/ConversationStates.html
[DaylightPhase]: /reference/java/games/stendhal/server/core/rp/DaylightPhase.html
[Direction]: /reference/java/games/stendhal/common/Direction.html
[MathHelper]: /reference/java/games/stendhal/common/MathHelper.html
[Rand]: /reference/java/games/stendhal/common/Rand.html
[Region]: /reference/java/games/stendhal/server/maps/Region.html
[ShopType]: /reference/java/games/stendhal/server/entity/npc/shop/ShopType.html
[SingletonRepository]: /reference/java/games/stendhal/server/core/engine/SingletonRepository.html
[SkinColor]: /reference/java/games/stendhal/common/constants/SkinColor.html
