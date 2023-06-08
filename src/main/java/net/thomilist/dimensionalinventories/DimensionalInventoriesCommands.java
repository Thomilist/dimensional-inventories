package net.thomilist.dimensionalinventories;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static net.minecraft.command.argument.DimensionArgumentType.dimension;
import static net.minecraft.command.argument.GameModeArgumentType.gameMode;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.Optional;

public final class DimensionalInventoriesCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(literal("diminv")
            .requires(source -> source.hasPermissionLevel(4))
            .executes(context -> printVersion(context))
            .then(literal("list")
                .executes(context -> listAllDimensionPools(context)))
            .then(literal("pool")
                .then(argument("poolName", word())
                    .then(literal("list")
                        .executes(context -> listDimensionPool(context)))
                    .then(literal("create")
                        .executes(context -> createDimensionPool(context)))
                    .then(literal("remove")
                        .executes(context -> removeDimensionPool(context)))
                    .then(literal("dimension")
                        .then(argument("dimensionName", dimension())
                            .then(literal("assign")
                                .executes(context -> assignDimensionToPool(context)))
                            .then(literal("remove")
                                .executes(context -> removeDimensionFromPool(context)))))
                    .then(literal("gamemode")
                        .then(argument("gameModeName", gameMode())
                            .executes(context -> setDimensionPoolGameMode(context))))
                    .then(literal("progressAdvancements")
                        .then(argument("progressAdvancements", bool())
                            .executes(context -> setProgressAdvancementsInPool(context))))
                    .then(literal("incrementStatistics")
                        .then(argument("incrementStatistics", bool())
                            .executes(context -> setIncrementStatisticsInPool(context)))))));
    }

    public static int printVersion(CommandContext<ServerCommandSource> context)
    {
        context.getSource().sendFeedback(() -> Text.literal("Dimensional Inventories 1.0.2+1.20 by Thomilist"), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int listAllDimensionPools(CommandContext<ServerCommandSource> context)
    {
        context.getSource().sendFeedback(() -> Text.literal(DimensionPoolManager.getPoolsAsString()), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int listDimensionPool(CommandContext<ServerCommandSource> context)
    {
        String poolName = StringArgumentType.getString(context, "poolName");
        Optional<DimensionPool> pool = DimensionPoolManager.getPoolWithName(poolName);

        if (pool.isEmpty())
        {
            sendFeedback(context, "Unable to fetch pool '" + poolName + "'.");
            return -1;
        }

        sendFeedback(context, "Dimension pool:" + pool.get().asString());
        return Command.SINGLE_SUCCESS;
    }

    public static int createDimensionPool(CommandContext<ServerCommandSource> context)
    {
        String poolName = StringArgumentType.getString(context, "poolName");

        if (DimensionPoolManager.createPool(poolName, GameMode.SURVIVAL))
        {
            sendFeedback(context, "Unable to create pool '" + poolName + "'.");
            return -1;
        }

        DimensionPoolManager.saveToFile();
        sendFeedback(context, "Dimension pool '" + poolName + "' created.");
        return Command.SINGLE_SUCCESS;
    }

    public static int removeDimensionPool(CommandContext<ServerCommandSource> context)
    {
        String poolName = StringArgumentType.getString(context, "poolName");

        if (DimensionPoolManager.removePool(poolName))
        {
            sendFeedback(context, "Unable to remove pool '" + poolName + "'.");
            return -1;
        }

        DimensionPoolManager.saveToFile();
        sendFeedback(context, "Dimension pool '" + poolName + "' removed.");
        return Command.SINGLE_SUCCESS;
    }

    public static int assignDimensionToPool(CommandContext<ServerCommandSource> context)
    {
        String poolName = StringArgumentType.getString(context, "poolName");
        ServerWorld dimension;

        try
        {
            dimension = DimensionArgumentType.getDimensionArgument(context, "dimensionName");
        }
        catch (CommandSyntaxException e)
        {
            sendFeedback(context, "Unable to fetch dimension.");
            return -1;
        }

        String dimensionName = dimension.getRegistryKey().getValue().toString();
        DimensionPoolManager.addDimensionToPool(poolName, dimensionName);
        DimensionPoolManager.saveToFile();
        sendFeedback(context, "Assigned '" + dimensionName + "' to '" + poolName + "'.");
        return Command.SINGLE_SUCCESS;
    }

    public static int removeDimensionFromPool(CommandContext<ServerCommandSource> context)
    {
        String poolName = StringArgumentType.getString(context, "poolName");
        ServerWorld dimension;

        try
        {
            dimension = DimensionArgumentType.getDimensionArgument(context, "dimensionName");
        }
        catch (CommandSyntaxException e)
        {
            sendFeedback(context, "Unable to fetch dimension.");
            return -1;
        }

        String dimensionName = dimension.getRegistryKey().getValue().toString();
        Optional<DimensionPool> pool = DimensionPoolManager.getPoolWithName(poolName);

        if (pool.isEmpty())
        {
            sendFeedback(context, "Unable to fetch pool '" + poolName + "'.");
            return -1;
        }

        if (pool.get().removeDimension(dimensionName))
        {
            sendFeedback(context, "'" + dimensionName + "' not found in '" + poolName + "'.");
            return 0;
        }

        DimensionPoolManager.saveToFile();
        sendFeedback(context, "Removed '" + dimensionName + "' from '" + poolName + "'.");
        return Command.SINGLE_SUCCESS;
    }

    public static int setDimensionPoolGameMode(CommandContext<ServerCommandSource> context)
    {
        String poolName = StringArgumentType.getString(context, "poolName");
        GameMode gameMode;

        try
        {
            gameMode = GameModeArgumentType.getGameMode(context, "gameModeName");
        }
        catch (CommandSyntaxException e)
        {
            sendFeedback(context, "Invalid gamemode.");
            return -1;
        }

        Optional<DimensionPool> pool = DimensionPoolManager.getPoolWithName(poolName);

        if (pool.isEmpty())
        {
            sendFeedback(context, "Unable to fetch pool '" + poolName + "'.");
            return -1;
        }

        pool.get().setGameMode(gameMode);
        DimensionPoolManager.saveToFile();
        sendFeedback(context, "Gamemode '" + gameMode.asString() + "' set for dimension pool '" + poolName + "'.");
        return Command.SINGLE_SUCCESS;
    }

    public static int setProgressAdvancementsInPool(CommandContext<ServerCommandSource> context)
    {
        String poolName = StringArgumentType.getString(context, "poolName");
        boolean progressAdvancements = BoolArgumentType.getBool(context, "progressAdvancements");

        Optional<DimensionPool> pool = DimensionPoolManager.getPoolWithName(poolName);

        if (pool.isEmpty())
        {
            sendFeedback(context, "Unable to fetch pool '" + poolName + "'.");
            return -1;
        }

        pool.get().setProgressAdvancements(progressAdvancements);
        DimensionPoolManager.saveToFile();

        if (progressAdvancements)
        {
            sendFeedback(context, "Players can now progress advancements while in the dimension pool '" + poolName + "'.");
        }
        else
        {
            sendFeedback(context, "Players can no longer progress advancements while in the dimension pool '" + poolName + "'.");
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int setIncrementStatisticsInPool(CommandContext<ServerCommandSource> context)
    {
        String poolName = StringArgumentType.getString(context, "poolName");
        boolean incrementStatistics = BoolArgumentType.getBool(context, "incrementStatistics");

        Optional<DimensionPool> pool = DimensionPoolManager.getPoolWithName(poolName);

        if (pool.isEmpty())
        {
            sendFeedback(context, "Unable to fetch pool '" + poolName + "'.");
            return -1;
        }

        pool.get().setIncrementStatistics(incrementStatistics);
        DimensionPoolManager.saveToFile();

        if (incrementStatistics)
        {
            sendFeedback(context, "Players can now increment statistics while in the dimension pool '" + poolName + "'.");
        }
        else
        {
            sendFeedback(context, "Players can no longer increment statistics while in the dimension pool '" + poolName + "'.");
        }

        return Command.SINGLE_SUCCESS;
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> context, String message)
    {
        context.getSource().sendFeedback(() -> Text.literal(message), false);
        return;
    }
}
