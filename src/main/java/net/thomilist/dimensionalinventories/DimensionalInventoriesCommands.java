package net.thomilist.dimensionalinventories;

//import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static net.minecraft.command.argument.DimensionArgumentType.dimension;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

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
                    .then(literal("progressAdvancements")
                        .then(argument("progressAdvancements", bool())
                            .executes(context -> setProgressAdvancementsInPool(context))))
                    .then(literal("incrementStatistics")
                        .then(argument("incrementStatistics", bool())
                            .executes(context -> setIncrementStatisticsInPool(context)))))));

        for (GameMode gameMode : GameMode.values())
        {
            dispatcher.register(literal("diminv")
                .requires(source -> source.hasPermissionLevel(4))
                .then(literal("pool")
                    .then(argument("poolName", word())
                        .then(literal("gamemode")
                            .then(literal(gameMode.getName())
                                .executes(context -> setDimensionPoolGameMode(context, gameMode)))))));
        }


    }

    public static int printVersion(CommandContext<ServerCommandSource> context)
    {
        final Text text = Text.literal("Dimensional Inventories 1.0.1+1.19.2 by Thomilist");
        context.getSource().sendFeedback(text, false);
        return Command.SINGLE_SUCCESS;
    }

    public static int listAllDimensionPools(CommandContext<ServerCommandSource> context)
    {
        final Text text = Text.literal(DimensionPoolManager.getPoolsAsString());
        context.getSource().sendFeedback(text, false);
        return Command.SINGLE_SUCCESS;
    }

    public static int listDimensionPool(CommandContext<ServerCommandSource> context)
    {
        String poolName = StringArgumentType.getString(context, "poolName");
        Text text;
        
        try
        {
            text = Text.literal("Dimension pool:" + DimensionPoolManager.getPoolWithName(poolName).asString());
        }
        catch (NullPointerException e)
        {
            text = Text.literal(e.getMessage());
            context.getSource().sendFeedback(text, false);
            return -1;
        }
        
        context.getSource().sendFeedback(text, false);
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
        DimensionPool pool;

        try
        {
            pool = DimensionPoolManager.getPoolWithName(poolName);
        }
        catch (NullPointerException e)
        {
            sendFeedback(context, "Unable to fetch pool '" + poolName + "'.");
            return -1;
        }

        if (pool.removeDimension(dimensionName))
        {
            sendFeedback(context, "'" + dimensionName + "' not found in '" + poolName + "'.");
            return 0;
        }

        DimensionPoolManager.saveToFile();
        sendFeedback(context, "Removed '" + dimensionName + "' from '" + poolName + "'.");
        return Command.SINGLE_SUCCESS;
    }

    public static int setDimensionPoolGameMode(CommandContext<ServerCommandSource> context, GameMode gameMode)
    {
        String poolName = StringArgumentType.getString(context, "poolName");
        DimensionPool pool;

        try
        {
            pool = DimensionPoolManager.getPoolWithName(poolName);
        }
        catch (NullPointerException e)
        {
            sendFeedback(context, "Unable to fetch pool '" + poolName + "'.");
            return -1;
        }

        pool.setGameMode(gameMode);
        DimensionPoolManager.saveToFile();
        sendFeedback(context, "Gamemode '" + gameMode.getName() + "' set for dimension pool '" + poolName + "'.");
        return Command.SINGLE_SUCCESS;
    }

    public static int setProgressAdvancementsInPool(CommandContext<ServerCommandSource> context)
    {
        String poolName = StringArgumentType.getString(context, "poolName");
        boolean progressAdvancements = BoolArgumentType.getBool(context, "progressAdvancements");

        DimensionPool pool;

        try
        {
            pool = DimensionPoolManager.getPoolWithName(poolName);
        }
        catch (NullPointerException e)
        {
            sendFeedback(context, "Unable to fetch pool '" + poolName + "'.");
            return -1;
        }

        pool.setProgressAdvancements(progressAdvancements);
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

        DimensionPool pool;

        try
        {
            pool = DimensionPoolManager.getPoolWithName(poolName);
        }
        catch (NullPointerException e)
        {
            sendFeedback(context, "Unable to fetch pool '" + poolName + "'.");
            return -1;
        }

        pool.setIncrementStatistics(incrementStatistics);
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
        Text text = Text.literal(message);
        context.getSource().sendFeedback(text, false);
        return;
    }
}
