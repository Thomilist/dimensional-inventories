package net.thomilist.dimensionalinventories;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.thomilist.dimensionalinventories.command.Commands;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.module.ModuleGroup;
import net.thomilist.dimensionalinventories.module.builtin.legacy.pool.DimensionPoolConfigModule_SV1;
import net.thomilist.dimensionalinventories.module.builtin.legacy.inventory.InventoryModule_SV1;
import net.thomilist.dimensionalinventories.module.builtin.legacy.status.StatusModule_SV1;
import net.thomilist.dimensionalinventories.module.builtin.shoulderentity.ShoulderEntityModule;
import net.thomilist.dimensionalinventories.module.version.StorageVersionMigration;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.config.ConfigModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.module.ModuleRegistry;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.gamemode.GameModeModule;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventoryModule;
import net.thomilist.dimensionalinventories.module.builtin.status.StatusModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolTransitionHandler;
import net.thomilist.dimensionalinventories.util.NbtConversionHelper;
import net.thomilist.dimensionalinventories.util.ModProperties;
import net.thomilist.dimensionalinventories.util.SavePaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DimensionalInventories
	implements ModInitializer
{
	private static final String MOD_ID = "dimensional-inventories";

	public static final ModProperties PROPERTIES = new ModProperties(DimensionalInventories.MOD_ID);
	public static final Logger LOGGER = LoggerFactory.getLogger(DimensionalInventories.PROPERTIES.namePascal());
	public static final DimensionalInventories INSTANCE = new DimensionalInventories();

	public final StorageVersion storageVersion;
	public final StorageVersionMigration storageVersionMigration;
	public final ModuleRegistry<ConfigModule> configModules = new ModuleRegistry<>(ConfigModule.class);
	public final ModuleRegistry<PlayerModule> playerModules = new ModuleRegistry<>(PlayerModule.class);
	public final DimensionPoolTransitionHandler transitionHandler;
	public final Commands commands = new Commands();

	public DimensionalInventories(StorageVersion storageVersion)
	{
		this.storageVersion = storageVersion;
		this.transitionHandler = new DimensionPoolTransitionHandler(this.storageVersion, this.configModules, this.playerModules);
		this.storageVersionMigration = new StorageVersionMigration(this.storageVersion, this.configModules, this.transitionHandler);
	}

	public DimensionalInventories()
	{
		this(StorageVersion.V2);
	}

	@Override
	public void onInitialize()
	{
		try (var LAF = LostAndFound.init("init", "base", DimensionalInventories.PROPERTIES.id()))
		{
			DimensionalInventories.INSTANCE.registerBuiltinModules();
			DimensionalInventories.INSTANCE.registerStartupHandlers();
			DimensionalInventories.INSTANCE.registerPlayerTravelHandler();
			DimensionalInventories.INSTANCE.registerPlayerRespawnHandler();
			DimensionalInventories.INSTANCE.registerEntityTravelHandler();
			DimensionalInventories.INSTANCE.registerCommands();
		}
	}

	public void registerModules(ModuleGroup moduleGroup)
	{
		DimensionalInventories.LOGGER.info(
			"Registering modules from module group {} ...",
			StringHelper.joinAndWrapScopes(moduleGroup.groupId())
		);

        this.configModules.register(moduleGroup);
        this.playerModules.register(moduleGroup);
	}

	private void registerStartupHandlers()
	{
		ServerLifecycleEvents.SERVER_STARTED.register((server) ->
		{
			try (var LAF = LostAndFound.init("server started"))
			{
				NbtConversionHelper.onServerStarted(server);
				SavePaths.onServerStarted(server);
				storageVersionMigration.tryMigrate(server);

				for (var config : configModules.get(StorageVersion.latest()))
				{
					config.loadWithContext();
				}

				DimensionalInventories.LOGGER.info("{} {} initialised",
					DimensionalInventories.PROPERTIES.namePretty(),
					DimensionalInventories.PROPERTIES.version());
			}
		});
	}

	private void registerPlayerTravelHandler()
	{
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) ->
		{
			try (var LAF = LostAndFound.init("player changed dimension"))
			{
				String originDimensionName = origin.getRegistryKey().getValue().toString();
				String destinationDimensionName = destination.getRegistryKey().getValue().toString();

				transitionHandler.handlePlayerDimensionChange(
					player,
					originDimensionName,
					destinationDimensionName);
			}
		});
	}

	private void registerPlayerRespawnHandler()
	{
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
		{
			try (var LAF = LostAndFound.init("player respawned"))
			{
				String originDimensionName = oldPlayer.getWorld().getRegistryKey().getValue().toString();
				String destinationDimensionName = newPlayer.getWorld().getRegistryKey().getValue().toString();

				transitionHandler.handlePlayerDimensionChange(
					newPlayer,
					originDimensionName,
					destinationDimensionName);
			}
		});
	}

	private void registerEntityTravelHandler()
	{
		ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register((originalEntity, newEntity, origin, destination) ->
		{
			try (var LAF = LostAndFound.init("entity changed dimension"))
			{
				String originDimensionName = origin.getRegistryKey().getValue().toString();
				String destinationDimensionName = destination.getRegistryKey().getValue().toString();

				transitionHandler.handleEntityDimensionChange(
					newEntity,
					originDimensionName,
					destinationDimensionName);
			}
		});
	}

	private void registerCommands()
	{
		for (var module : configModules.get(storageVersion))
		{
			if (module instanceof DimensionPoolConfigModule)
			{
				commands.register((DimensionPoolConfigModule) module);
			}
		}
	}
}
