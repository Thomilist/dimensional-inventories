# Dimensional Inventories

A Minecraft Fabric mod for keeping separate inventories across pools of dimensions. Originally developed to add a separate creative world to a survival server using a custom dimension datapack (example datapack attached to releases).

## Basic Functionality

The mod operates with dimension pools. A dimension pool can contain any number of dimensions, but a dimension can only be present in one dimension pool. A player's inventory (including the regular inventory, armour, offhand, ender chest, experience, score, food, saturation, exhaustion and health) and gamemode is the same within the same dimension pool. 

When a player travels to a dimension in a different dimension pool, the player's inventory is saved to a file corresponding to the origin dimension pool. The player's inventory and status effects are then cleared, their inventory is loaded from their file in the destination dimension pool (if present), and their gamemode is switched to the dimension pool's gamemode. On the other hand, when a non-player entity travels from one dimension pool to another, it is simply deleted.

Advancement progress can be enabled or disabled for a dimension pool. Players can view their advancements in any dimension pool, but only when in a pool with advancement progress enabled will they be able to progress or complete advancements, as well as unlock recipes. A separate setting also applies this principle to statistics.

Dimension pools are loaded from and saved to a JSON file. If the file is not found, a pool named `default` will be created containing the dimensions `minecraft:overworld`, `minecraft:the_nether` and `minecraft:the_end` with the gamemode set to survival, and with advancement progress and incrementation of statistics enabled. Thus, the mod will have no effect out of the box in a regular survival world.

If the origin or destination dimension is not assigned to a dimension pool, the player's inventory and gamemode will be unaffected. This means a player can preserve their inventory while travelling across dimension pools if they travel to an unassigned dimension in between.

## Usage

The mod is used via commands starting with `diminv`. The most basic command simply shows the mod version and is accessible to anyone:

```
diminv
```

All other commands require permission level 4 (operator).

### Managing Dimension Pools

List all dimension pools, their gamemodes and the dimensions they contain:
```
diminv list
```

List a specific dimension pool, its gamemode and the dimensions it contains:
```
diminv pool <pool_name> list
```

Create a new dimension pool:
```
diminv pool <pool_name> create
```

Remove an existing dimension pool:
```
diminv pool <pool_name> remove
```

Assign a dimension to a dimension pool (also removing it from all other pools):
```
diminv pool <pool_name> dimension <dimension_name> assign
```

Remove a dimension from a dimension pool:
```
diminv pool <pool_name> dimension <dimension_name> remove
```

Set the gamemode for a dimension pool:
```
diminv pool <pool_name> gamemode <gamemode>
```

Enable or disable advancement progress for a dimension pool:
```
diminv pool <pool_name> progressAdvancements <boolean>
```

Enable or disable incrementation of statistics for a dimension pool:
```
diminv pool <pool_name> incrementStatistics <boolean>
```

## Tips

Custom dimensions do not support travel via portals, so to actually get to one, a teleport of some sort is required. One way is to use the vanilla `teleport` command in conjunction with the `execute` command:
```
execute in <dimension_name> run teleport <coordinates>
```

Accessing custom dimensions can be made easier with mods that provide warps, homes and other such teleports. For more convenient dimension hopping, players can also be allowed to teleport to their previous location - typically implemented with a `/back` command. An example of a mod that provides such functionality is [Essential Commands](https://modrinth.com/mod/essential-commands).

On a survival server with a creative dimension, the creative dimension could be accessed with a warp. Players could then set a home in the creative dimension for easy access to their current project there, and they could quickly return to the survival world using a `/back` command. With this, players could easily design their builds in creative and use them as a reference when building them in survival.

To manage access to these and other commands, it can be useful to assign permissions for different dimensions independently. This can be achieved with a permissions mod such as [LuckPerms](https://modrinth.com/mod/luckperms) - see their [wiki entry for contexts](https://luckperms.net/wiki/Context). Expanding on the teleport example, the `essentialcommands.warp.tp` and `essentialcommands.home.tp` permissions could be granted globally, while the `essentialcommands.home.set` and  `essentialcommands.back` permission could be granted only in the creative dimension.

Use of [Vanilla Permissions](https://modrinth.com/mod/vanilla-permissions) or [Universal Perms](https://modrinth.com/mod/universal-perms) to expose additional permission nodes may also be useful in this context.

Adding portal support to custom dimensions using another mod alongside Dimensional Inventories may be of interest. An example of such a mod could be [Dimension Portal Linker](https://github.com/Encrypted-Thoughts/DimensionPortalLinker/).

## Mod Compatibility

Broadly speaking, there are (at least) two interesting cases when it comes to mod compatibility:

1. Mods that use vanilla Minecraft systems to add/remove/modify game content (items, status effects, dimensions and so on). These should be compatible with Dimensional Inventories "out of the box".
2. Mods that affect the *types* of player state available (extra inventory slots, currency systems and so on). These require additional effort to integrate with Dimensional Inventories.

For the second case, Dimensional Inventories features a preliminary extension API, which allows *another* mod to register additional player state handlers (see the *Extensions* section below).

## Extensions

Dimensional Inventories features a preliminary extension API, which can be used to improve compatibility with other mods (as described in the *Mod Compatibility* section above), or to add or change features in the core Dimensional Inventories mod. The extension API may change, even across minor versions, so be sure to use the compatible mod versions listed on a particular extension release.

Note that extension mods may be developed by different people than the "main" Dimensional Inventories mod or the mod they provide support for. **You should not expect mod authors to resolve issues caused by an extension mod developed by a different author**.

Below is a list of extension mods:

| Extension                                                                                                              | Description                                                      | Author                                                                                          |
|------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| [Dimensional Inventories Extension: Trinkets](https://github.com/Thomilist/dimensional-inventories-extension-trinkets) | Compatibility with [Trinkets](https://modrinth.com/mod/trinkets) | Same as Dimensional Inventories                                                                 |
| [Dimensional Inventories Extension: Warp](https://modrinth.com/mod/dimensional-inventories-extension-warp)             | Warp command to teleport players across dimension pools          | `rofumer` ([Modrinth](https://modrinth.com/user/rofumer); [GitHub](https://github.com/Rofumer)) |
