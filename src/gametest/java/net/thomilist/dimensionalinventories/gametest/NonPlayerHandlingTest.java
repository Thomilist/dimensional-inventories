package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.Item;
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
    extends DimensionalInventoriesGameTest
{
    // When an item entity crosses dimension pools, it should be deleted
    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE )
    public void transitionDeletesItemEntity( final TestContext context )
    {
        this.logTestStart();

        BlockPlacement.PlaceFloor( context );
        final BasicModSetup setup = BasicModSetup.withDefaultModules();

        for ( final Item item : Registries.ITEM )
        {
            DimensionalInventoriesGameTest.LOGGER.debug(
                "transitionDeletesItemEntity: {}",
                item.getName().getString()
            );
            final ItemEntity itemEntity = context.spawnItem( item, 0.5f, 2.5f, 0.5f );

            setup.instance.transitionHandler.handleEntityDimensionChange(
                itemEntity,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.dontExpectEntity( EntityType.ITEM );
        }

        context.complete();
    }

    // When an item entity crosses dimension pools, but one or both of the dimensions are not
    // assigned to any dimension pool, the item entity should be unaffected
    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE )
    public void unconfiguredTransitionDoesNotDeleteItemEntity( final TestContext context )
    {
        this.logTestStart();

        BlockPlacement.PlaceFloor( context );
        final BasicModSetup setup = BasicModSetup.withDefaultModules();

        final ItemEntity itemEntity = context.spawnItem( Items.STONE, 0.5f, 2.5f, 0.5f );

        setup.instance.transitionHandler.handleEntityDimensionChange(
            itemEntity,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.UNCONFIGURED_DIMENSION
        );

        context.expectEntity( EntityType.ITEM );
        context.complete();
    }

    // When a mob entity crosses dimension pools, it should be deleted
    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE )
    public void transitionDeletesMobEntity( final TestContext context )
    {
        this.logTestStart();

        BlockPlacement.PlaceFloor( context );
        final BasicModSetup setup = BasicModSetup.withDefaultModules();

        final Set<SpawnGroup> mobSpawnGroups = Set.of(
            SpawnGroup.AMBIENT,
            SpawnGroup.AXOLOTLS,
            SpawnGroup.CREATURE,
            SpawnGroup.MONSTER,
            SpawnGroup.UNDERGROUND_WATER_CREATURE,
            SpawnGroup.WATER_AMBIENT,
            SpawnGroup.WATER_CREATURE
        );

        for ( final EntityType<?> entityType : Registries.ENTITY_TYPE )
        {
            if ( !entityType.isEnabled( context.getWorld().getEnabledFeatures() ) )
            {
                continue;
            }

            if ( !mobSpawnGroups.contains( entityType.getSpawnGroup() ) )
            {
                continue;
            }

            DimensionalInventoriesGameTest.LOGGER.debug(
                "transitionDeletesMobEntity: {}",
                entityType.getName().getString()
            );
            final Entity entity = context.spawnEntity( entityType, 4, 4, 4 );

            setup.instance.transitionHandler.handleEntityDimensionChange(
                entity,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.dontExpectEntity( entityType );
        }

        context.complete();
    }

    // When a mob entity crosses dimension pools, but one or both of the dimensions are not
    // assigned to any dimension pool, the mob entity should be unaffected
    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE )
    public void unconfiguredTransitionDoesNotDeleteMobEntity( final TestContext context )
    {
        this.logTestStart();

        BlockPlacement.PlaceFloor( context );
        final BasicModSetup setup = BasicModSetup.withDefaultModules();

        final EntityType<CreeperEntity> entityType = EntityType.CREEPER;
        final CreeperEntity entity = context.spawnEntity( entityType, 4, 4, 4 );

        setup.instance.transitionHandler.handleEntityDimensionChange(
            entity,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.UNCONFIGURED_DIMENSION
        );

        context.expectEntity( entityType );
        context.complete();
    }

    // Ensure chest boats, chest minecarts and hopper minecarts don't drop their contents on transition,
    // i.e. not bringing back https://github.com/Thomilist/dimensional-inventories/issues/15
    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE )
    public void transitionHandlesClearableEntity( final TestContext context )
    {
        this.logTestStart();

        BlockPlacement.PlaceFloor( context );
        final BasicModSetup setup = BasicModSetup.withDefaultModules();

        final var clearableEntityTypes = List.of(
            // Minecarts
            EntityType.HOPPER_MINECART,
            EntityType.CHEST_MINECART,

            // Boats
            EntityType.ACACIA_CHEST_BOAT,
            EntityType.BIRCH_CHEST_BOAT,
            EntityType.CHERRY_CHEST_BOAT,
            EntityType.DARK_OAK_CHEST_BOAT,
            EntityType.JUNGLE_CHEST_BOAT,
            EntityType.MANGROVE_CHEST_BOAT,
            EntityType.OAK_CHEST_BOAT,
            EntityType.PALE_OAK_CHEST_BOAT,
            EntityType.SPRUCE_CHEST_BOAT
        );

        for ( final var entityType : clearableEntityTypes )
        {
            if ( !entityType.isEnabled( context.getWorld().getEnabledFeatures() ) )
            {
                continue;
            }

            final var entity = context.spawnEntity( entityType, 4, 4, 4 );

            entity.getInventory().replaceAll( ignored -> new ItemStack( Items.STONE, Items.STONE.getMaxCount() ) );

            setup.instance.transitionHandler.handleEntityDimensionChange(
                entity,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.dontExpectEntity( EntityType.ITEM );
            context.killAllEntities();
        }

        context.complete();
    }
}
