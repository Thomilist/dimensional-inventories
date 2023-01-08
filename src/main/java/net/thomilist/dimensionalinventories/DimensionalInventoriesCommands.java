package net.thomilist.dimensionalinventories;

//import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
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
                            .then(literal("put")
                                .executes(context -> putDimensionIntoPool(context)))
                            .then(literal("remove")
                                .executes(context -> removeDimensionFromPool(context)))))
                    .then(literal("gamemode")
                        .then(argument("gameModeName", gameMode())
                            .executes(context -> setDimensionPoolGameMode(context)))))));
    }

    public static int printVersion(CommandContext<ServerCommandSource> context)
    {
        final Text text = Text.literal("Dimensional Inventories v1.0.0 by Thomilist");
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

    public static int putDimensionIntoPool(CommandContext<ServerCommandSource> context)
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
        sendFeedback(context, "Put '" + dimensionName + "' into '" + poolName + "'.");
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
        sendFeedback(context, "Gamemode '" + gameMode.asString() + "' set for dimension pool '" + poolName + "'.");
        return Command.SINGLE_SUCCESS;
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> context, String message)
    {
        Text text = Text.literal(message);
        context.getSource().sendFeedback(text, false);
        return;
    }
}
