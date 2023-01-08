package net.thomilist.dimensionalinventories;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DimensionalInventoriesMod implements ModInitializer
{
	public static final Logger LOGGER = LoggerFactory.getLogger("DimensionalInventories");

	@Override
	public void onInitialize()
	{
		ServerLifecycleEvents.SERVER_STARTING.register((server) ->
		{
			InventoryManager.onServerStart(server, LOGGER);
			DimensionPoolManager.onServerStart(server, LOGGER);
			LOGGER.info("Dimensional Inventories initialised.");
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> DimensionalInventoriesCommands.register(dispatcher));

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) ->
		{
			String originDimensionName = origin.getRegistryKey().getValue().toString();
			String destinationDimensionName = destination.getRegistryKey().getValue().toString();
			
			handleDimensionChange(player, originDimensionName, destinationDimensionName);

			return;
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
		{
			String originDimensionName = oldPlayer.getWorld().getDimensionKey().getValue().toString();
			String destinationDimensionName = newPlayer.getWorld().getDimensionKey().getValue().toString();

			handleDimensionChange(newPlayer, originDimensionName, destinationDimensionName);

			return;
		});
	}

	public static void handleDimensionChange(ServerPlayerEntity player, String originDimensionName, String destinationDimensionName)
	{
		LOGGER.info(player.getName().getString() + " travelled from " + originDimensionName + " to " + destinationDimensionName + ".");

		if (DimensionPoolManager.samePoolContainsBoth(originDimensionName, destinationDimensionName))
		{
			LOGGER.info("The origin and destination dimensions are in the same pool. Player unaffected.");
		}
		else
		{
			LOGGER.info("The origin and destination dimensions are in different pools. Switching inventories...");
			InventoryManager.saveInventory(player, originDimensionName);
			InventoryManager.clearInventory(player);
			InventoryManager.loadInventory(player, destinationDimensionName);
			player.changeGameMode(DimensionPoolManager.getGameModeOfDimension(destinationDimensionName));
		}

		return;
	}
}
