package net.thomilist.dimensionalinventories;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DimensionalInventoriesMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("DimensionalInventories");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Dimensional Inventories initialised.");

		ServerLifecycleEvents.SERVER_STARTING.register((server) ->
		{
			InventoryManager.onServerStart(server, LOGGER);
		});

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) ->
		{
			String originDimensionName = origin.getRegistryKey().getValue().toString();
			String destinationDimensionName = destination.getRegistryKey().getValue().toString();
			
			LOGGER.info(player.getName().getString() + " travelled from " + originDimensionName + " to " + destinationDimensionName + ".");

			InventoryManager.saveInventory(player, originDimensionName);
			InventoryManager.clearInventory(player);
			InventoryManager.loadInventory(player, destinationDimensionName);

			return;
		});
	}
}
