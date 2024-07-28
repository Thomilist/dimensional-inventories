package net.thomilist.dimensionalinventories.command;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static net.minecraft.command.argument.DimensionArgumentType.dimension;
import static net.minecraft.command.argument.GameModeArgumentType.gameMode;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolOperationResult;
import net.thomilist.dimensionalinventories.util.Properties;

import java.util.Optional;

public class Commands
{
    private enum Command
    {
        ROOT ("diminv"),
        LIST_POOLS ("list"),
        POOL ("pool"),
        POOL_ID ("poolId"),
        LIST_DIMENSIONS_IN_POOL ("list"),
        CREATE_POOL ("create"),
        DELETE_POOL ("delete"),
        DIMENSION ("dimension"),
        DIMENSION_NAME ("dimensionName"),
        ASSIGN_DIMENSION_TO_POOL ("assign"),
        REMOVE_DIMENSION_FROM_POOL("remove"),
        GAME_MODE ("gameMode"),
        GAME_MODE_NAME ("gameModeName"),
        PROGRESS_ADVANCEMENTS ("progressAdvancements"),
        PROGRESS_ADVANCEMENTS_ENABLED ("progressAdvancementsEnabled"),
        INCREMENT_STATISTICS ("incrementStatistics"),
        INCREMENT_STATISTICS_ENABLED ("incrementStatisticsEnabled");

        private final String command;

        Command(String command)
        {
            this.command = command;
        }

        @Override
        public String toString()
        {
            return this.command;
        }
    }

    private static DimensionPoolConfigModule DIMENSION_POOL_CONFIG;

    private static DimensionPoolConfigModule dimensionPoolConfig()
    {
        if (Commands.DIMENSION_POOL_CONFIG == null)
        {
            Commands.DIMENSION_POOL_CONFIG = DimensionalInventories.CONFIG_MODULES
                .get(DimensionPoolConfigModule.class);
        }

        return Commands.DIMENSION_POOL_CONFIG;
    }

    public static void register()
    {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            Commands.register(dispatcher));
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(literal(Command.ROOT.toString())
            .requires(source -> source.hasPermissionLevel(4))
            .executes(Commands::printVersion)
            .then(literal(Command.LIST_POOLS.toString())
                .executes(Commands::listAllDimensionPools))
            .then(literal(Command.POOL.toString())
                .then(argument(Command.POOL_ID.toString(), word())
                    .then(literal(Command.LIST_DIMENSIONS_IN_POOL.toString())
                        .executes(Commands::listDimensionPool))
                    .then(literal(Command.CREATE_POOL.toString())
                        .executes(Commands::createDimensionPool))
                    .then(literal(Command.DELETE_POOL.toString())
                        .executes(Commands::removeDimensionPool))
                    .then(literal(Command.DIMENSION.toString())
                        .then(argument(Command.DIMENSION_NAME.toString(), dimension())
                            .then(literal(Command.ASSIGN_DIMENSION_TO_POOL.toString())
                                .executes(Commands::assignDimensionToPool))
                            .then(literal(Command.REMOVE_DIMENSION_FROM_POOL.toString())
                                .executes(Commands::removeDimensionFromPool))))
                    .then(literal(Command.GAME_MODE.toString())
                        .then(argument(Command.GAME_MODE_NAME.toString(), gameMode())
                            .executes(Commands::setDimensionPoolGameMode)))
                    .then(literal(Command.PROGRESS_ADVANCEMENTS.toString())
                        .then(argument(Command.PROGRESS_ADVANCEMENTS_ENABLED.toString(), bool())
                            .executes(Commands::setProgressAdvancementsInPool)))
                    .then(literal(Command.INCREMENT_STATISTICS.toString())
                        .then(argument(Command.INCREMENT_STATISTICS_ENABLED.toString(), bool())
                            .executes(Commands::setIncrementStatisticsInPool))))));
    }

    public static int printVersion(CommandContext<ServerCommandSource> context)
    {
        context.getSource().sendFeedback(() ->
            Text.literal(Properties.modNamePretty() + " " + Properties.modVersion() + " by Thomilist"),
            false);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public static int listAllDimensionPools(CommandContext<ServerCommandSource> context)
    {
        context.getSource().sendFeedback(() ->
            Text.literal(dimensionPoolConfig().state().asString()),
            false);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public static int listDimensionPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, Command.POOL_ID.toString());
        Optional<DimensionPool> pool = dimensionPoolConfig().state().poolWithId(dimensionPoolId);

        if (pool.isEmpty())
        {
            Commands.sendFeedback(context, "Unable to fetch pool '" + dimensionPoolId + "'");
            return -1;
        }

        Commands.sendFeedback(context, "Dimension pool:" + pool.get().asString());
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public static int createDimensionPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, Command.POOL_ID.toString());

        DimensionPoolOperationResult result = dimensionPoolConfig().state()
            .createPool(dimensionPoolId, GameMode.DEFAULT);

        if (!result.success())
        {
            Commands.sendFeedback(context, "Unable to create dimension pool: '" + dimensionPoolId + "' already exists");
            return -1;
        }

        dimensionPoolConfig().saveWithContext();
        Commands.sendFeedback(context, "Dimension pool '" + dimensionPoolId + "' created");
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public static int removeDimensionPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, Command.POOL_ID.toString());

        DimensionPoolOperationResult result = dimensionPoolConfig().state()
            .deletePool(dimensionPoolId);

        if (!result.success())
        {
            Commands.sendFeedback(context, "Unable to remove dimension pool: '" + dimensionPoolId + "' does not exist");
            return -1;
        }

        dimensionPoolConfig().saveWithContext();
        Commands.sendFeedback(context, "Dimension pool '" + dimensionPoolId + "' removed");
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public static int assignDimensionToPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, Command.POOL_ID.toString());
        ServerWorld dimension;

        try
        {
            dimension = DimensionArgumentType.getDimensionArgument(context, Command.DIMENSION_NAME.toString());
        }
        catch (CommandSyntaxException e)
        {
            Commands.sendFeedback(context, "Unable to fetch dimension");
            return -1;
        }

        String dimensionName = dimension.getRegistryKey().getValue().toString();
        DimensionPoolOperationResult result = dimensionPoolConfig().state()
            .assignDimensionToPool(dimensionName, dimensionPoolId);

        if (!result.success())
        {
            Commands.sendFeedback(context, "'" + dimensionPoolId + "' is not a valid dimension pool ID");
            return -1;
        }

        switch (result.operation())
        {
            case ADD_DIMENSION:
            {
                Commands.sendFeedback(context, "Assigned dimension '" + result.target() + "' to dimension pool '" + result.to() + "'");
                break;
            }
            case MOVE_DIMENSION:
            {
                Commands.sendFeedback(context, "Moved dimension '" + result.target() + "' from dimension pool '" + result.from() + "' to '" + result.to() + "'");
                break;
            }
            case NO_OP:
            {
                Commands.sendFeedback(context, "Dimension '" + result.target() + "' is already in dimension pool '" + result.to() + "'");
                break;
            }
        }

        dimensionPoolConfig().saveWithContext();
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public static int removeDimensionFromPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, Command.POOL_ID.toString());
        ServerWorld dimension;

        try
        {
            dimension = DimensionArgumentType.getDimensionArgument(context, Command.DIMENSION_NAME.toString());
        }
        catch (CommandSyntaxException e)
        {
            Commands.sendFeedback(context, "Unable to fetch dimension");
            return -1;
        }

        String dimensionName = dimension.getRegistryKey().getValue().toString();

        DimensionPoolOperationResult result = dimensionPoolConfig().state().removeDimensionFromPool(dimensionName, dimensionPoolId);

        if (!result.success())
        {
            Commands.sendFeedback(context, "'" + dimensionPoolId + "' is not a valid dimension pool ID");
            return -1;
        }

        switch (result.operation())
        {
            case REMOVE_DIMENSION:
            {
                Commands.sendFeedback(context, "Removed dimension '" + dimensionName + "' from dimension pool '" + dimensionPoolId + "'");
                break;
            }
            case NO_OP:
            {
                Commands.sendFeedback(context, "Dimension '" + dimensionName + "' not found in dimension pool '" + dimensionPoolId + "'");
                break;
            }
        }

        dimensionPoolConfig().saveWithContext();
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public static int setDimensionPoolGameMode(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, Command.POOL_ID.toString());
        GameMode gameMode;

        try
        {
            gameMode = GameModeArgumentType.getGameMode(context, Command.GAME_MODE_NAME.toString());
        }
        catch (CommandSyntaxException e)
        {
            Commands.sendFeedback(context, "Invalid game mode");
            return -1;
        }

        Optional<DimensionPool> pool = dimensionPoolConfig().state().poolWithId(dimensionPoolId);

        if (pool.isEmpty())
        {
            Commands.sendFeedback(context, "Unable to fetch dimension pool '" + dimensionPoolId + "'");
            return -1;
        }

        pool.get().setGameMode(gameMode);
        dimensionPoolConfig().saveWithContext();
        Commands.sendFeedback(context, "Game mode '" + gameMode.asString() + "' set for dimension pool '" + dimensionPoolId + "'");
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public static int setProgressAdvancementsInPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, Command.POOL_ID.toString());
        boolean progressAdvancements = BoolArgumentType.getBool(context, Command.PROGRESS_ADVANCEMENTS_ENABLED.toString());

        Optional<DimensionPool> pool = dimensionPoolConfig().state().poolWithId(dimensionPoolId);

        if (pool.isEmpty())
        {
            Commands.sendFeedback(context, "Unable to fetch dimension pool '" + dimensionPoolId + "'");
            return -1;
        }

        pool.get().setProgressAdvancements(progressAdvancements);
        dimensionPoolConfig().saveWithContext();

        if (progressAdvancements)
        {
            Commands.sendFeedback(context, "Players can now progress advancements while in the dimension pool '" + dimensionPoolId + "'");
        }
        else
        {
            Commands.sendFeedback(context, "Players can no longer progress advancements while in the dimension pool '" + dimensionPoolId + "'");
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public static int setIncrementStatisticsInPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, Command.POOL_ID.toString());
        boolean incrementStatistics = BoolArgumentType.getBool(context, Command.INCREMENT_STATISTICS_ENABLED.toString());

        Optional<DimensionPool> pool = dimensionPoolConfig().state().poolWithId(dimensionPoolId);

        if (pool.isEmpty())
        {
            Commands.sendFeedback(context, "Unable to fetch pool '" + dimensionPoolId + "'");
            return -1;
        }

        pool.get().setIncrementStatistics(incrementStatistics);
        dimensionPoolConfig().saveWithContext();

        if (incrementStatistics)
        {
            Commands.sendFeedback(context, "Players can now increment statistics while in the dimension pool '" + dimensionPoolId + "'");
        }
        else
        {
            Commands.sendFeedback(context, "Players can no longer increment statistics while in the dimension pool '" + dimensionPoolId + "'");
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> context, String message)
    {
        context.getSource().sendFeedback(() -> Text.literal(message), false);
    }
}
