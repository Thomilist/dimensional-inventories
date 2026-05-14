package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;
import net.thomilist.dimensionalinventories.gametest.util.BlockPlacement;

import java.util.List;
import java.util.Set;

public class NonPlayerHandlingTests
    extends DimensionalInventoriesGameTest
{
    // When an item entity crosses dimension pools, it should be deleted
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionDeletesItemEntity( final GameTestHelper context )
    {
        BlockPlacement.PlaceFloor( context );
        final BasicModSetup setup = BasicModSetup.withDefaultModules();

        for ( final Item item : BuiltInRegistries.ITEM )
        {
            DimensionalInventoriesGameTest.LOGGER.debug(
                "transitionDeletesItemEntity: {}",
                item
            );
            final ItemEntity itemEntity = context.spawnItem( item, 0.5f, 2.5f, 0.5f );

            setup.instance.transitionHandler.handleEntityDimensionChange(
                itemEntity,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.assertEntityNotPresent( EntityType.ITEM );
        }

        context.succeed();
    }

    // When an item entity crosses dimension pools, but one or both of the dimensions are not
    // assigned to any dimension pool, the item entity should be unaffected
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void unconfiguredTransitionDoesNotDeleteItemEntity( final GameTestHelper context )
    {
        BlockPlacement.PlaceFloor( context );
        final BasicModSetup setup = BasicModSetup.withDefaultModules();

        final ItemEntity itemEntity = context.spawnItem( Items.STONE, 0.5f, 2.5f, 0.5f );

        setup.instance.transitionHandler.handleEntityDimensionChange(
            itemEntity,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.UNCONFIGURED_DIMENSION
        );

        context.assertEntityPresent( EntityType.ITEM );
        context.succeed();
    }

    // When a mob entity crosses dimension pools, it should be deleted
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionDeletesMobEntity( final GameTestHelper context )
    {
        BlockPlacement.PlaceFloor( context );
        final BasicModSetup setup = BasicModSetup.withDefaultModules();

        final Set<MobCategory> mobSpawnGroups = Set.of(
            MobCategory.AMBIENT,
            MobCategory.AXOLOTLS,
            MobCategory.CREATURE,
            MobCategory.MONSTER,
            MobCategory.UNDERGROUND_WATER_CREATURE,
            MobCategory.WATER_AMBIENT,
            MobCategory.WATER_CREATURE
        );

        for ( final EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE )
        {
            if ( !entityType.isEnabled( context.getLevel().enabledFeatures() ) )
            {
                continue;
            }

            if ( !mobSpawnGroups.contains( entityType.getCategory() ) )
            {
                continue;
            }

            DimensionalInventoriesGameTest.LOGGER.debug(
                "transitionDeletesMobEntity: {}",
                entityType.getDescription().getString()
            );
            final Entity entity = context.spawn( entityType, 4, 4, 4 );

            setup.instance.transitionHandler.handleEntityDimensionChange(
                entity,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.assertEntityNotPresent( entityType );
        }

        context.succeed();
    }

    // When a mob entity crosses dimension pools, but one or both of the dimensions are not
    // assigned to any dimension pool, the mob entity should be unaffected
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void unconfiguredTransitionDoesNotDeleteMobEntity( final GameTestHelper context )
    {
        BlockPlacement.PlaceFloor( context );
        final BasicModSetup setup = BasicModSetup.withDefaultModules();

        final EntityType<Creeper> entityType = EntityType.CREEPER;
        final Creeper entity = context.spawn( entityType, 4, 4, 4 );

        setup.instance.transitionHandler.handleEntityDimensionChange(
            entity,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.UNCONFIGURED_DIMENSION
        );

        context.assertEntityPresent( entityType );
        context.succeed();
    }

    // Ensure chest boats, chest minecarts and hopper minecarts don't drop their contents on transition,
    // i.e. not bringing back https://github.com/Thomilist/dimensional-inventories/issues/15
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionHandlesClearableEntity( final GameTestHelper context )
    {
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
            if ( !entityType.isEnabled( context.getLevel().enabledFeatures() ) )
            {
                continue;
            }

            final var entity = context.spawn( entityType, 4, 4, 4 );

            entity.getItemStacks().replaceAll( ignored -> new ItemStack( Items.STONE, Items.STONE.getDefaultMaxStackSize() ) );

            setup.instance.transitionHandler.handleEntityDimensionChange(
                entity,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.assertEntityNotPresent( EntityType.ITEM );
            context.killAllEntities();
        }

        context.succeed();
    }
}
