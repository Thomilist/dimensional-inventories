package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.WorldSavePath;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.gametest.util.TestState;
import net.thomilist.dimensionalinventories.module.ModuleGroup;
import net.thomilist.dimensionalinventories.module.base.config.ConfigModule;
import net.thomilist.dimensionalinventories.module.base.player.JsonPlayerModule;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.MainModuleGroup;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventoryModule;
import net.thomilist.dimensionalinventories.module.builtin.legacy.inventory.InventoryModule_SV1;
import net.thomilist.dimensionalinventories.module.builtin.legacy.pool.DimensionPoolConfigModule_SV1;
import net.thomilist.dimensionalinventories.module.builtin.legacy.status.StatusModule_SV1;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModuleState;
import net.thomilist.dimensionalinventories.module.builtin.shoulderentity.ShoulderEntityModule;
import net.thomilist.dimensionalinventories.module.builtin.status.StatusModule;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.util.DummyServerPlayerEntity;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

public class StorageVersionMigrationTest
{
    public static final String MIGRATION_BATCH = "migration";

    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE,
               batchId = StorageVersionMigrationTest.MIGRATION_BATCH )
    public void migrateLegacyToV2( final TestContext context )
    {
        // Prepare legacy data to migrate from

        final String legacySaveDirectoryName = "dimensionalinventories";
        final String resourcePath = "samples/legacy/" + legacySaveDirectoryName;
        this.initializeSampleData( context, resourcePath, legacySaveDirectoryName );

        // Initialise mod instance

        final DimensionalInventories instance = new DimensionalInventories();
        instance.registerModules( new MainModuleGroup() );

        // Migrate

        instance.storageVersionMigration.tryMigrate( context.getWorld().getServer() );

        // Load config modules

        for ( final ConfigModule config : instance.configModules.get( StorageVersion.latest() ) )
        {
            config.loadWithContext();
        }

        // Verify success...

        // All dimension pools migrated?

        final DimensionPoolConfigModuleState dimensionPoolConfig = instance.configModules
            .get( DimensionPoolConfigModule.class )
            .state();

        context.assertTrue( dimensionPoolConfig.poolExists( "default" ), "Dimension pool 'default' missing" );
        context.assertTrue( dimensionPoolConfig.poolExists( "creative" ), "Dimension pool 'creative' missing" );

        final DimensionPool dimensionPoolDefault = dimensionPoolConfig.poolWithId( "default" ).orElseThrow();
        final DimensionPool dimensionPoolCreative = dimensionPoolConfig.poolWithId( "creative" ).orElseThrow();

        // Dimension pools have the expected dimensions?

        context.assertTrue(
            dimensionPoolDefault.hasDimensions( "minecraft:overworld", "minecraft:the_nether", "minecraft:the_end" ),
            "Dimension pool 'default' missing dimension(s)"
        );

        context.assertTrue(
            dimensionPoolCreative.hasDimensions( "custom:creative" ),
            "Dimension pool 'creative' missing dimension(s)"
        );

        // Data for all players migrated?

        final List<String> playerUuids = List.of(
            "2acd07d7-9ed9-4faa-b077-f6c153f69db6",
            "5c18ac70-1771-48f4-91a9-67654c3df9cc",
            "8a29ab7d-83f0-4fa4-b3f0-d93bd6355493",
            "09fbece1-4631-4dea-85be-75a18ccd4ccc",
            "54ea5ebe-3b45-497f-9e9c-ecb100e7ebe1"
        );

        for ( final String playerUuid : playerUuids )
        {
            final DummyServerPlayerEntity player = new DummyServerPlayerEntity( context.getWorld(), playerUuid );

            for ( final DimensionPool dimensionPool : dimensionPoolConfig.dimensionPools.values() )
            {
                for ( final PlayerModule playerModule : instance.playerModules.get( StorageVersion.V2 ) )
                {
                    if ( playerModule instanceof final JsonPlayerModule<?> jsonPlayerModule )
                    {
                        context.assertTrue(
                            jsonPlayerModule.saveFile( player, dimensionPool ).toFile().exists(),
                            "Dimension pool '" + dimensionPool.getId() + "' missing " + playerModule.moduleId() +
                            " data for " + "player '" + playerUuid + '\''
                        );
                    }
                }
            }
        }

        // Switching game mode requires the player to have a non-null network handler, so create a mod instance
        // without the game mode module:

        class MainModuleGroupWithoutGameMode
            extends ModuleGroup
        {
            @SuppressWarnings( "deprecation" )
            public MainModuleGroupWithoutGameMode()
            {
                super( "main" );

                this.register(
                    DimensionPoolConfigModule.class,
                    InventoryModule.class,
                    StatusModule.class,
                    ShoulderEntityModule.class,
                    DimensionPoolConfigModule_SV1.class,
                    InventoryModule_SV1.class,
                    StatusModule_SV1.class
                );
            }
        }

        final DimensionalInventories instanceWithoutGameMode = new DimensionalInventories();
        instanceWithoutGameMode.registerModules( new MainModuleGroupWithoutGameMode() );

        // Player data migrated correctly?

        final DummyServerPlayerEntity player = new DummyServerPlayerEntity( context.getWorld(), playerUuids.get( 4 ) );

        @FunctionalInterface
        interface ExpectItemStack
        {
            void expectItem( int index, Item item );
        }

        final ExpectItemStack combinedInventory = ( final int index, final Item item ) -> {
            final ItemStack itemStack = player.getInventory().getStack( index );
            context.assertTrue(
                itemStack.isOf( item ),
                "Expected %s; found %s".formatted( item, itemStack.getItem() )
            );
        };

        final ExpectItemStack enderChest = ( final int index, final Item item ) -> {
            final ItemStack itemStack = player.getEnderChestInventory().getStack( index );
            context.assertTrue(
                itemStack.isOf( item ),
                "Expected %s; found %s".formatted( item, itemStack.getItem() )
            );
        };

        // ... for dimension pool 'creative'?

        instanceWithoutGameMode.transitionHandler.loadToPlayer( StorageVersion.V2, dimensionPoolCreative, player );

        final List<@NotNull Item> expectedItems = List.of(
            Items.WHITE_CONCRETE,
            Items.OBSERVER,
            Items.REDSTONE,
            Items.REPEATER,
            Items.COMPARATOR,
            Items.REDSTONE_TORCH,
            Items.TARGET,
            Items.REDSTONE_BLOCK,
            Items.SMOOTH_STONE_SLAB
        );

        for ( int index = 0; index < expectedItems.size(); ++index )
        {
            combinedInventory.expectItem( index, expectedItems.get( index ) );
        }

        context.assertTrue( player.totalExperience == 1712, "total experience" );

        context.assertTrue( player.getScore() == 120754, "score" );

        context.assertTrue( player.getHungerManager().getFoodLevel() == 20, "food level" );

        context.assertTrue( player.getHungerManager().getSaturationLevel() == 9.8f, "saturation level" );

        context.assertTrue( player.getHungerManager().getExhaustion() == 0.2372941f, "exhaustion" );

        context.assertTrue( player.getHealth() == 20.0f, "health" );

        // ... for dimension pool 'default'?

        instanceWithoutGameMode.transitionHandler.loadToPlayer( StorageVersion.V2, dimensionPoolDefault, player );

        combinedInventory.expectItem( 0, Items.DIAMOND_SWORD );
        combinedInventory.expectItem( 1, Items.TRIDENT );
        combinedInventory.expectItem( 2, Items.COOKED_PORKCHOP );
        combinedInventory.expectItem( 3, Items.DIAMOND_PICKAXE );
        combinedInventory.expectItem( 4, Items.DIAMOND_HOE );
        combinedInventory.expectItem( 5, Items.AIR );
        combinedInventory.expectItem( 6, Items.ENDER_PEARL );
        combinedInventory.expectItem( 7, Items.FIREWORK_ROCKET );
        combinedInventory.expectItem( 8, Items.WATER_BUCKET );

        combinedInventory.expectItem( 9, Items.CHEST );
        combinedInventory.expectItem( 10, Items.DIAMOND_SHOVEL );
        combinedInventory.expectItem( 11, Items.ENDER_CHEST );
        combinedInventory.expectItem( 12, Items.OAK_BOAT );
        combinedInventory.expectItem( 13, Items.SHULKER_SHELL );
        combinedInventory.expectItem( 14, Items.TORCH );

        for ( int index = 15; index < 36; ++index )
        {
            combinedInventory.expectItem( index, Items.AIR );
        }

        combinedInventory.expectItem( 36, Items.DIAMOND_BOOTS );
        combinedInventory.expectItem( 37, Items.DIAMOND_LEGGINGS );
        combinedInventory.expectItem( 38, Items.ELYTRA );
        combinedInventory.expectItem( 39, Items.DIAMOND_HELMET );

        enderChest.expectItem( 0, Items.BLUE_SHULKER_BOX );
        enderChest.expectItem( 1, Items.BROWN_SHULKER_BOX );
        enderChest.expectItem( 2, Items.CRAFTING_TABLE );
        enderChest.expectItem( 3, Items.CYAN_SHULKER_BOX );
        enderChest.expectItem( 4, Items.DIAMOND_AXE );
        enderChest.expectItem( 5, Items.DIAMOND_PICKAXE );
        enderChest.expectItem( 6, Items.DIAMOND_SWORD );
        enderChest.expectItem( 7, Items.DIAMOND_SWORD );
        enderChest.expectItem( 8, Items.GRAY_SHULKER_BOX );
        enderChest.expectItem( 9, Items.LIGHT_GRAY_SHULKER_BOX );
        enderChest.expectItem( 10, Items.ORANGE_SHULKER_BOX );
        enderChest.expectItem( 11, Items.SHULKER_BOX );
        enderChest.expectItem( 12, Items.WHITE_BED );

        for ( int index = 13; index < 20; ++index )
        {
            enderChest.expectItem( index, Items.WHITE_SHULKER_BOX );
        }

        for ( int index = 20; index < 27; ++index )
        {
            enderChest.expectItem( index, Items.AIR );
        }

        context.assertTrue( player.totalExperience == 64098, "total experience" );

        context.assertTrue( player.getScore() == 159947, "score" );

        context.assertTrue( player.getHungerManager().getFoodLevel() == 20, "food level" );

        context.assertTrue( player.getHungerManager().getSaturationLevel() == 0.0f, "saturation level" );

        context.assertTrue( player.getHungerManager().getExhaustion() == 0.45421875f, "exhaustion" );

        context.assertTrue( player.getHealth() == 20.0f, "health" );

        // ... including damage, enchantments, custom names etc. (the stuff that was moved to item components)?

        // Pre-24w09a (1.20.5)
        if ( MinecraftVersion.CURRENT.getSaveVersion().getId() <= 3819 )
        {
            final ItemStack chest = player.getInventory().armor.get( 2 );

            context.assertTrue( chest.getCount() == 1, "%s count".formatted( Items.ELYTRA ) );
            context.assertTrue( chest.getCount() == 1, "Elytra count" );
            context.assertTrue( chest.getDamage() == 6, "Elytra damage" );
            context.assertTrue( EnchantmentHelper.getLevel( Enchantments.UNBREAKING, chest ) == 3, "Unbreaking level" );
            context.assertTrue( EnchantmentHelper.getLevel( Enchantments.MENDING, chest ) == 1, "Mending level" );
            context.assertTrue( chest.getName().getString().equals( "Can I put a wang on this?" ), "custom item name" );
        }

        context.complete();
    }

    private void initializeSampleData( final TestContext context,
                                       final String resourcePath,
                                       final String worldDestinationPath )
    {
        final Path sampleDataPath = FabricLoader
            .getInstance()
            .getModContainer( "dimensional-inventories-gametest" )
            .orElseThrow()
            .findPath( resourcePath )
            .orElseThrow();

        final Path legacyDataPath = context
            .getWorld()
            .getServer()
            .getSavePath( WorldSavePath.ROOT )
            .resolve( worldDestinationPath );

        final File sampleDataDirectory = sampleDataPath.toFile();
        final File legacyDataDirectory = legacyDataPath.toFile();

        try
        {
            if ( legacyDataDirectory.exists() )
            {
                FileUtils.deleteDirectory( legacyDataDirectory );
            }

            FileUtils.copyDirectory( sampleDataDirectory, legacyDataDirectory );
        }
        catch ( final IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @BeforeBatch( batchId = StorageVersionMigrationTest.MIGRATION_BATCH )
    public void stashExistingData( final ServerWorld unused )
    {
        TestState.stashModData( StorageVersionMigrationTest.MIGRATION_BATCH );
    }
}
