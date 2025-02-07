package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.WorldSavePath;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.gametest.util.TestState;
import net.thomilist.dimensionalinventories.gametest.util.assertion.InventoryAsserter;
import net.thomilist.dimensionalinventories.gametest.util.assertion.StatusAsserter;
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

    @BeforeBatch( batchId = StorageVersionMigrationTest.MIGRATION_BATCH )
    public void stashExistingData( final ServerWorld unused )
    {
        TestState.stashModData( StorageVersionMigrationTest.MIGRATION_BATCH );
    }

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

        instance.configModules.get( StorageVersion.latest() ).forEach( ConfigModule::loadWithContext );

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

        final DimensionalInventories instanceWithoutGameMode = this.getInstanceWithoutGameModeModule();

        // Player data migrated correctly?

        final DummyServerPlayerEntity player = new DummyServerPlayerEntity( context.getWorld(), playerUuids.get( 4 ) );
        final InventoryAsserter combinedInventory = new InventoryAsserter( context, player.getInventory() );
        final InventoryAsserter enderChest = new InventoryAsserter( context, player.getEnderChestInventory() );
        final StatusAsserter status = new StatusAsserter( context, player );

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
            combinedInventory.assertItemTypeAt( index, expectedItems.get( index ) );
        }

        status.assertTotalExperience( 1712 );
        status.assertScore( 120754 );
        status.assertFoodLevel( 20 );
        status.assertSaturationLevel( 9.8f );
        status.assertExhaustion( 0.2372941f );
        status.assertHealth( 20.0f );

        // ... for dimension pool 'default'?

        instanceWithoutGameMode.transitionHandler.loadToPlayer( StorageVersion.V2, dimensionPoolDefault, player );

        combinedInventory.assertItemTypeAt( 0, Items.DIAMOND_SWORD );
        combinedInventory.assertItemTypeAt( 1, Items.TRIDENT );
        combinedInventory.assertItemTypeAt( 2, Items.COOKED_PORKCHOP );
        combinedInventory.assertItemTypeAt( 3, Items.DIAMOND_PICKAXE );
        combinedInventory.assertItemTypeAt( 4, Items.DIAMOND_HOE );
        combinedInventory.assertItemTypeAt( 5, Items.AIR );
        combinedInventory.assertItemTypeAt( 6, Items.ENDER_PEARL );
        combinedInventory.assertItemTypeAt( 7, Items.FIREWORK_ROCKET );
        combinedInventory.assertItemTypeAt( 8, Items.WATER_BUCKET );

        combinedInventory.assertItemTypeAt( 9, Items.CHEST );
        combinedInventory.assertItemTypeAt( 10, Items.DIAMOND_SHOVEL );
        combinedInventory.assertItemTypeAt( 11, Items.ENDER_CHEST );
        combinedInventory.assertItemTypeAt( 12, Items.OAK_BOAT );
        combinedInventory.assertItemTypeAt( 13, Items.SHULKER_SHELL );
        combinedInventory.assertItemTypeAt( 14, Items.TORCH );

        for ( int index = 15; index < 36; ++index )
        {
            combinedInventory.assertItemTypeAt( index, Items.AIR );
        }

        combinedInventory.assertItemTypeAt( 36, Items.DIAMOND_BOOTS );
        combinedInventory.assertItemTypeAt( 37, Items.DIAMOND_LEGGINGS );
        combinedInventory.assertItemTypeAt( 38, Items.ELYTRA );
        combinedInventory.assertItemTypeAt( 39, Items.DIAMOND_HELMET );

        enderChest.assertItemTypeAt( 0, Items.BLUE_SHULKER_BOX );
        enderChest.assertItemTypeAt( 1, Items.BROWN_SHULKER_BOX );
        enderChest.assertItemTypeAt( 2, Items.CRAFTING_TABLE );
        enderChest.assertItemTypeAt( 3, Items.CYAN_SHULKER_BOX );
        enderChest.assertItemTypeAt( 4, Items.DIAMOND_AXE );
        enderChest.assertItemTypeAt( 5, Items.DIAMOND_PICKAXE );
        enderChest.assertItemTypeAt( 6, Items.DIAMOND_SWORD );
        enderChest.assertItemTypeAt( 7, Items.DIAMOND_SWORD );
        enderChest.assertItemTypeAt( 8, Items.GRAY_SHULKER_BOX );
        enderChest.assertItemTypeAt( 9, Items.LIGHT_GRAY_SHULKER_BOX );
        enderChest.assertItemTypeAt( 10, Items.ORANGE_SHULKER_BOX );
        enderChest.assertItemTypeAt( 11, Items.SHULKER_BOX );
        enderChest.assertItemTypeAt( 12, Items.WHITE_BED );

        for ( int index = 13; index < 20; ++index )
        {
            enderChest.assertItemTypeAt( index, Items.WHITE_SHULKER_BOX );
        }

        for ( int index = 20; index < 27; ++index )
        {
            enderChest.assertItemTypeAt( index, Items.AIR );
        }

        status.assertTotalExperience( 64098 );
        status.assertScore( 159947 );
        status.assertFoodLevel( 20 );
        status.assertSaturationLevel( 0.0f );
        status.assertExhaustion( 0.45421875f );
        status.assertHealth( 20.0f );

        // ... including damage, enchantments, custom names etc. (the stuff that was moved to item components)?

        // Pre-24w09a (1.20.5)
        if ( MinecraftVersion.CURRENT.getSaveVersion().getId() <= 3819 )
        {
            combinedInventory.assertCountAt( 38, 1 );
            combinedInventory.assertDamage( 38, 6 );
            combinedInventory.assertName( 38, "Can I put a wang on this?" );
            combinedInventory.assertEnchantment( 38, Enchantments.UNBREAKING, 3 );
            combinedInventory.assertEnchantment( 38, Enchantments.MENDING, 1 );
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

        final File sampleDataDirectory = TestState.toFileExtractZip( sampleDataPath );
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

    private DimensionalInventories getInstanceWithoutGameModeModule()
    {
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

        return instanceWithoutGameMode;
    }
}
