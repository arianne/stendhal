/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.parser;

final class PackageInfo {

/*TODO mf - describe Conversation Parser usage and implementation


(from SF entry 1851849 - make NPC conversation more flexible)

Talking to non character players should be made more flexible, so that the
understand not only the very constrained imperative syntax.

Examples for additional sentences to be interpreted as the well known "buy
banana" phrase are:

- give me a banana
- please give me a banana
- i would like to have a banana
- can you give me a banana, please?

All this five sentences should be treated as equivalent by the NPC handling
code.

Currently there are already the ConversationParser and Sentence classes,
which parse the user input and translate it into a simple grammatical
representation of the sentence (verb/amount/object/preposition/object2). In
order to achieve the flexible conversation, this should be extended to
understand more complex sentences including subjects, verbs consisting of
more than one word and may be even questions.

Then the FSM engine should be extended to evaluate this grammatical
constructs, stored in class Sentence, instead of only looking at the verb
at the beginning of the sentence. Quest writers should be able to configure
the grammatical aliases by simple rules like:

- buy OBJECT
- give ME OBJECT
- would like to have OBJECT
- can YOU give ME OBJECT?

Filling words like "please" should be silently ignored, common words like
"could" should be replaced by their normal form like "can".


(from developer blog)

Work is going on to make conversation with virtual in game characters (NPCs) more flexible.
Previously NPCs could only understand a very limited amount of predefined commands like for example "buy banana".
They should also be capable to understand sentences, which are worded in a more natural way of language.
Currently (in release 0.65) the server code can already parse expressions containing verbal amounts with
singular and plural nouns like "buy two bananas". The next stage of conversation parser will also be able
to understand sentences like "Can you give me three bananas, please?". To achieve this, it will be based
on a list known words with associated word types. If an user speaks to a NPC, the sentences are parsed
into grammatical expressions like "SUBJECT VERB OBJECT" and matched with predefined expressions to model
the character response.

(see also the related Developer Track entry.)


...

This description can be used as base for further functionality tests.

...


FAQ:

[21:18] <kymara> i have a question about the word list
[21:19] <kymara> if someone makes a new shop selling items which aren't already sold, or a quest with new items, do we have any guarantee it will work?
[21:19] <kymara> or do we now have to add shop inventories to the word list too?
[21:20] <kymara> are there any rules for whether we'll have to add words or not?
[21:22] <martin_> it will work in most cases without any additional intervention, but it is better to add the new vocabulary to be sure the words are assigned correct types
[21:22] <martin_> it works this way:
[21:22] <martin_> at program start the item and creature lists are read from xml
[21:23] <martin_> the same time this names are registered in the word list, if not already present
[21:23] <martin_> items are treated as objects
[21:23] <martin_> and creatures are treated as subjects
[21:23] <martin_> this is the default type assignment
[21:23] <martin_> so one should from time to time into the database for still untyped word entries
[21:24] <durkham> one should <verb missing>
[21:24] <martin_> this is why i implemented the script ListUnknownWords.class
[21:24] <martin_> so one should look from time to time into the database for still untyped word entries
[21:24] <martin_> by executing /script ListUnknownWords.class
[21:25] <martin_> currently i added all words, which are present in item and creature names
[21:25] <martin_> if you add a completely new one, it will be recognized as new
[21:26] <durkham> so we do not take care when creating new items or creatures ?
[21:26] <durkham> as it is handled automatically
[21:26] <martin_> normally it's no problem
[21:27] <martin_> only for some ambiguess words
[21:27] <martin_> then we have to look into it and use some brain :)

*/

}
