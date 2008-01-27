package games.stendhal.server.entity.npc.parser;

abstract class PackageInfo {

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

*/

}
