# Version 2.0.0: A Complete Rewrite

> [!INFO]  
> TL;DR: Backup your world before updating. Migration is automatic. No lost features.

In terms of software, version 2.0.0 is almost a complete rewrite of the mod.
However, the feature set remains largely the same. What gives?

## Background

When I first created Dimensional Inventories, it was essentially a quick and dirty proof of concept for a server I administered myself, which only ended up running for a months.
It was just about sufficient to allow the use of a data pack to add a creative dimension while keeping the creative inventory separate from the survival one.
Still, since I had been unable to find any existing Fabric mod to accomplish this, I decided to share it on the off chance that someone else might find it useful too.

It sparked *much* more interest than I ever imagined, and I quickly realised that the original design was pushed to its limits despite the mod being relatively simple in terms of features.
A few feature requests came in, but these were difficult to fit into the mod in any remotely clean fashion, so the idea of a rewrite quickly took hold, though finding the time to actually *do* it a lot took longer than I would have liked.

Well, I found the time during spring and summer of 2024, so here we are!

## What was wrong with version 1?

Besides poor code quality stemming from my inexperience with Java at the time, the implementation of the mod was fundamentally flawed.
Player data was stored as lines of plain text, with items being formatted as NBT strings, while health, experience and so on were written directly.
A valid file would have exactly 73 lines of data, which the position of a given line being tightly coupled with its meaning.
This meant that the save format was difficult to adapt to new features, and since the NBT strings were not escaped, it would break when any piece of data contained multiline text.

For most items, and indeed all the cases I originally tested, this was okay, but not so much for the Book & Quill.
Multiline text from a Book & Quill would cause the NBT parsing to fail, since only the portion up to the first `\n` would be included for a given entry.
And, to add insult to injury, the positional nature of the file meant that the rest of the data would then no longer be valid, and so the load would be aborted, and any data *after* the Book & Quill would *also* be lost.

## How is version 2 different?

On the surface, not much has changed.
The same features are still there, though with a few tweaks and quality-of-life changes here and there.
However, much has changed behind the scenes: Almost the entire mod has been rewritten from scratch with much more thought put into the design this time around.
Here are the highlights:

### More robust storage format

The 73 lines of fragile plain text data have been replaced with JSON.
This was already used for configuration, but now, player data also gets to enjoy the robustness from a more structured format.
Data is now escaped properly, and an error in one field does not necessarily cause total data loss for the following fields.

### Lost and found

While the storage format is much more robust, it would be naïve to deem errors impossible.
To minimise unrecoverable data loss, a "lost and found" system has been implemented.
When faced with an error while loading data, a comprehensive context surrounding the error, as well as the data that caused the error, is logged to a special `lost+found` directory.
While these logs cannot be accessed in-game, they do provide insights to allow a server admin to rectify the situation.

### Modular design

Most suggestions I have received are requests to support more data from vanilla Minecraft, e.g. advancements, or to support other mods, e.g. Trinkets or Linked Storage.
In principle, these suggestions are great fits for the mod, but the old implementation did not encourage such development.

To remedy this, the new version is designed from the ground up to be highly modular.
Essentially, the mod is now a framework for persistent state tied to dimension pools.
Each category of data is then handled through its own module, but only the logic specific to this data is included here, and the framework-like core takes care of the rest.

Modules are assigned groups, IDs and version numbers to better accommodate future needs and wants.
Three modules are included within the mod in the initial release, covering game mode, inventory (including armour and ender chest) and status (health, experience, status effects and so on).

### Quality-of-life changes

As alluded to in the module talk, there is at least *one* new feature:
Status effects are now saved as well.

Beyond that, however, most of the visible changes revolve around feedback.
Commands should provide more detailed messages for successful and failed invocations alike, and the logs should be more consistent and, indeed, useful.

## Migration

So how do you migrate? Well, first of all...

> [!CAUTION]
> Backup your world *before* updating.

Got it? Good.
Now, the actual migration is triggered automatically at the end of the server startup process.
It *should* happen completely seamlessly, but edge cases are (sort of by definition) difficult to test for, so while I'm fairly confident that the migration code is sound, I can never be 100% certain.

If migration fails, an exception is thrown, and the server will crash.
Causing a crash is deliberate, as after a failed migration, the mod is not in a valid state, and so further operation is nonsensical.
I do not *expect* migrations to fail, but if it happens to your server, and you are unable to resolve the issue yourself, well...
You know where to find me, but please remember to bring a relevant crash log.
