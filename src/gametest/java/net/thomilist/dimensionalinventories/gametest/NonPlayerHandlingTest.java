package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;
import net.thomilist.dimensionalinventories.gametest.util.BlockPlacement;

import java.util.List;
import java.util.Set;

public class NonPlayerHandlingTest
{
    // When an item entity crosses dimension pools, it should be deleted
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void transitionDeletesItemEntity(TestContext context)
    {
        BlockPlacement.PlaceFloor(context);
        var setup = new BasicModSetup();

        for (var item : Registries.ITEM)
        {
            DimensionalInventoriesGameTest.LOGGER.debug("transitionDeletesItemEntity: {}", item.getName().getString());
            var itemEntity = context.spawnItem(item, 0.5f, 2.5f, 0.5f);

            setup.instance.transitionHandler.handleEntityDimensionChange(
                itemEntity,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.dontExpectEntity(EntityType.ITEM);
        }

        context.complete();
    }

    // When an item entity crosses dimension pools, but one or both of the dimensions are not
    // assigned to any dimension pool, the item entity should be unaffected
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void unconfiguredTransitionDoesNotDeleteItemEntity(TestContext context)
    {
        BlockPlacement.PlaceFloor(context);
        var setup = new BasicModSetup();

        var itemEntity = context.spawnItem(Items.STONE, 0.5f, 2.5f, 0.5f);

        setup.instance.transitionHandler.handleEntityDimensionChange(
            itemEntity,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.UNCONFIGURED_DIMENSION
        );

        context.expectEntity(EntityType.ITEM);
        context.complete();
    }

    // When a mob entity crosses dimension pools, it should be deleted
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void transitionDeletesMobEntity(TestContext context)
    {
        BlockPlacement.PlaceFloor(context);
        var setup = new BasicModSetup();

        Set<SpawnGroup> mobSpawnGroups = Set.of(
            SpawnGroup.AMBIENT,
            SpawnGroup.AXOLOTLS,
            SpawnGroup.CREATURE,
            SpawnGroup.MONSTER,
            SpawnGroup.UNDERGROUND_WATER_CREATURE,
            SpawnGroup.WATER_AMBIENT,
            SpawnGroup.WATER_CREATURE
        );

        for (var entityType : Registries.ENTITY_TYPE)
        {
            if (!mobSpawnGroups.contains(entityType.getSpawnGroup()))
            {
                continue;
            }

            DimensionalInventoriesGameTest.LOGGER.debug("transitionDeletesMobEntity: {}", entityType.getName().getString());
            var entity = context.spawnEntity(entityType, 4, 4, 4);

            setup.instance.transitionHandler.handleEntityDimensionChange(
                entity,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.dontExpectEntity(entityType);
        }

        context.complete();
    }

    // When a mob entity crosses dimension pools, but one or both of the dimensions are not
    // assigned to any dimension pool, the mob entity should be unaffected
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void unconfiguredTransitionDoesNotDeleteMobEntity(TestContext context)
    {
        BlockPlacement.PlaceFloor(context);
        var setup = new BasicModSetup();

        var entityType = EntityType.CREEPER;
        var entity = context.spawnEntity(entityType, 4, 4, 4);

        setup.instance.transitionHandler.handleEntityDimensionChange(
            entity,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.UNCONFIGURED_DIMENSION
        );

        context.expectEntity(entityType);
        context.complete();
    }

    // Ensure chest boats, chest minecarts and hopper minecarts don't drop their contents on transition,
    // i.e. not bringing back https://github.com/Thomilist/dimensional-inventories/issues/15
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void transitionHandlesClearableEntity(TestContext context)
    {
        BlockPlacement.PlaceFloor(context);
        var setup = new BasicModSetup();

        for (var entityType : List.of(EntityType.CHEST_BOAT, EntityType.CHEST_MINECART, EntityType.HOPPER_MINECART))
        {
            var entity = context.spawnEntity(entityType, 4, 4, 4);

            entity.getInventory().replaceAll(ignored -> new ItemStack(Items.STONE, Items.STONE.getMaxCount()));

            setup.instance.transitionHandler.handleEntityDimensionChange(
                entity,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.dontExpectEntity(EntityType.ITEM);
            context.killAllEntities();
        }

        context.complete();
    }
}
