# Dimensional Inventories

A Minecraft Fabric mod for keeping separate inventories across pools of dimensions. Originally developed to add a separate creative world to a survival server using a custom dimension datapack.

## Basic Functionality

The mod operators with dimension pools. A dimension pool can contain any number of dimensions, but a dimension can only be present in one dimension pool. A player's inventory (including the regular inventory, armour, offhand, ender chest, experience, score, food, saturation, exhaustion and health) and gamemode is the same within the same dimension pool. 

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