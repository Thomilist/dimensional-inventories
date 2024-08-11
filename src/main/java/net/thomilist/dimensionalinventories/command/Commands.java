package net.thomilist.dimensionalinventories.command;

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

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolOperationResult;
import net.thomilist.dimensionalinventories.util.Properties;

import java.util.Optional;

public class Commands
{
    private enum DimensionalInventoriesCommand
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

        DimensionalInventoriesCommand(String command)
        {
            this.command = command;
        }

        @Override
        public String toString()
        {
            return this.command;
        }
    }

    private DimensionPoolConfigModule dimensionPoolConfig;

    public void register(DimensionPoolConfigModule dimensionPoolConfig)
    {
        this.dimensionPoolConfig = dimensionPoolConfig;

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            register(dispatcher));
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(literal(DimensionalInventoriesCommand.ROOT.toString())
            .requires(source -> source.hasPermissionLevel(4))
            .executes(this::printVersion)
            .then(literal(DimensionalInventoriesCommand.LIST_POOLS.toString())
                .executes(this::listAllDimensionPools))
            .then(literal(DimensionalInventoriesCommand.POOL.toString())
                .then(argument(DimensionalInventoriesCommand.POOL_ID.toString(), word())
                    .then(literal(DimensionalInventoriesCommand.LIST_DIMENSIONS_IN_POOL.toString())
                        .executes(this::listDimensionPool))
                    .then(literal(DimensionalInventoriesCommand.CREATE_POOL.toString())
                        .executes(this::createDimensionPool))
                    .then(literal(DimensionalInventoriesCommand.DELETE_POOL.toString())
                        .executes(this::removeDimensionPool))
                    .then(literal(DimensionalInventoriesCommand.DIMENSION.toString())
                        .then(argument(DimensionalInventoriesCommand.DIMENSION_NAME.toString(), dimension())
                            .then(literal(DimensionalInventoriesCommand.ASSIGN_DIMENSION_TO_POOL.toString())
                                .executes(this::assignDimensionToPool))
                            .then(literal(DimensionalInventoriesCommand.REMOVE_DIMENSION_FROM_POOL.toString())
                                .executes(this::removeDimensionFromPool))))
                    .then(literal(DimensionalInventoriesCommand.GAME_MODE.toString())
                        .then(argument(DimensionalInventoriesCommand.GAME_MODE_NAME.toString(), gameMode())
                            .executes(this::setDimensionPoolGameMode)))
                    .then(literal(DimensionalInventoriesCommand.PROGRESS_ADVANCEMENTS.toString())
                        .then(argument(DimensionalInventoriesCommand.PROGRESS_ADVANCEMENTS_ENABLED.toString(), bool())
                            .executes(this::setProgressAdvancementsInPool)))
                    .then(literal(DimensionalInventoriesCommand.INCREMENT_STATISTICS.toString())
                        .then(argument(DimensionalInventoriesCommand.INCREMENT_STATISTICS_ENABLED.toString(), bool())
                            .executes(this::setIncrementStatisticsInPool))))));
    }

    public int printVersion(CommandContext<ServerCommandSource> context)
    {
        context.getSource().sendFeedback(() ->
            Text.literal(Properties.modNamePretty() + " " + Properties.modVersion() + " by Thomilist"),
            false);
        return Command.SINGLE_SUCCESS;
    }

    public int listAllDimensionPools(CommandContext<ServerCommandSource> context)
    {
        context.getSource().sendFeedback(() ->
            Text.literal(dimensionPoolConfig.state().asString()),
            false);
        return Command.SINGLE_SUCCESS;
    }

    public int listDimensionPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, DimensionalInventoriesCommand.POOL_ID.toString());
        Optional<DimensionPool> pool = dimensionPoolConfig.state().poolWithId(dimensionPoolId);

        if (pool.isEmpty())
        {
            sendFeedback(context, "Unable to fetch pool '" + dimensionPoolId + "'");
            return -1;
        }

        sendFeedback(context, "Dimension pool:" + pool.get().asString());
        return Command.SINGLE_SUCCESS;
    }

    public int createDimensionPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, DimensionalInventoriesCommand.POOL_ID.toString());

        DimensionPoolOperationResult result = dimensionPoolConfig.state()
            .createPool(dimensionPoolId, GameMode.DEFAULT);

        if (!result.success())
        {
            sendFeedback(context, "Unable to create dimension pool: '" + dimensionPoolId + "' already exists");
            return -1;
        }

        dimensionPoolConfig.saveWithContext();
        sendFeedback(context, "Dimension pool '" + dimensionPoolId + "' created");
        return Command.SINGLE_SUCCESS;
    }

    public int removeDimensionPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, DimensionalInventoriesCommand.POOL_ID.toString());

        DimensionPoolOperationResult result = dimensionPoolConfig.state()
            .deletePool(dimensionPoolId);

        if (!result.success())
        {
            sendFeedback(context, "Unable to remove dimension pool: '" + dimensionPoolId + "' does not exist");
            return -1;
        }

        dimensionPoolConfig.saveWithContext();
        sendFeedback(context, "Dimension pool '" + dimensionPoolId + "' removed");
        return Command.SINGLE_SUCCESS;
    }

    public int assignDimensionToPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, DimensionalInventoriesCommand.POOL_ID.toString());
        ServerWorld dimension;

        try
        {
            dimension = DimensionArgumentType.getDimensionArgument(context, DimensionalInventoriesCommand.DIMENSION_NAME.toString());
        }
        catch (CommandSyntaxException e)
        {
            sendFeedback(context, "Unable to fetch dimension");
            return -1;
        }

        String dimensionName = dimension.getRegistryKey().getValue().toString();
        DimensionPoolOperationResult result = dimensionPoolConfig.state()
            .assignDimensionToPool(dimensionName, dimensionPoolId);

        if (!result.success())
        {
            sendFeedback(context, "'" + dimensionPoolId + "' is not a valid dimension pool ID");
            return -1;
        }

        switch (result.operation())
        {
            case ADD_DIMENSION:
            {
                sendFeedback(context, "Assigned dimension '" + result.target() + "' to dimension pool '" + result.to() + "'");
                break;
            }
            case MOVE_DIMENSION:
            {
                sendFeedback(context, "Moved dimension '" + result.target() + "' from dimension pool '" + result.from() + "' to '" + result.to() + "'");
                break;
            }
            case NO_OP:
            {
                sendFeedback(context, "Dimension '" + result.target() + "' is already in dimension pool '" + result.to() + "'");
                break;
            }
        }

        dimensionPoolConfig.saveWithContext();
        return Command.SINGLE_SUCCESS;
    }

    public int removeDimensionFromPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, DimensionalInventoriesCommand.POOL_ID.toString());
        ServerWorld dimension;

        try
        {
            dimension = DimensionArgumentType.getDimensionArgument(context, DimensionalInventoriesCommand.DIMENSION_NAME.toString());
        }
        catch (CommandSyntaxException e)
        {
            sendFeedback(context, "Unable to fetch dimension");
            return -1;
        }

        String dimensionName = dimension.getRegistryKey().getValue().toString();

        DimensionPoolOperationResult result = dimensionPoolConfig.state().removeDimensionFromPool(dimensionName, dimensionPoolId);

        if (!result.success())
        {
            sendFeedback(context, "'" + dimensionPoolId + "' is not a valid dimension pool ID");
            return -1;
        }

        switch (result.operation())
        {
            case REMOVE_DIMENSION:
            {
                sendFeedback(context, "Removed dimension '" + dimensionName + "' from dimension pool '" + dimensionPoolId + "'");
                break;
            }
            case NO_OP:
            {
                sendFeedback(context, "Dimension '" + dimensionName + "' not found in dimension pool '" + dimensionPoolId + "'");
                break;
            }
        }

        dimensionPoolConfig.saveWithContext();
        return Command.SINGLE_SUCCESS;
    }

    public int setDimensionPoolGameMode(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, DimensionalInventoriesCommand.POOL_ID.toString());
        GameMode gameMode;

        try
        {
            gameMode = GameModeArgumentType.getGameMode(context, DimensionalInventoriesCommand.GAME_MODE_NAME.toString());
        }
        catch (CommandSyntaxException e)
        {
            sendFeedback(context, "Invalid game mode");
            return -1;
        }

        Optional<DimensionPool> pool = dimensionPoolConfig.state().poolWithId(dimensionPoolId);

        if (pool.isEmpty())
        {
            sendFeedback(context, "Unable to fetch dimension pool '" + dimensionPoolId + "'");
            return -1;
        }

        pool.get().setGameMode(gameMode);
        dimensionPoolConfig.saveWithContext();
        sendFeedback(context, "Game mode '" + gameMode.asString() + "' set for dimension pool '" + dimensionPoolId + "'");
        return Command.SINGLE_SUCCESS;
    }

    public int setProgressAdvancementsInPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, DimensionalInventoriesCommand.POOL_ID.toString());
        boolean progressAdvancements = BoolArgumentType.getBool(context, DimensionalInventoriesCommand.PROGRESS_ADVANCEMENTS_ENABLED.toString());

        Optional<DimensionPool> pool = dimensionPoolConfig.state().poolWithId(dimensionPoolId);

        if (pool.isEmpty())
        {
            sendFeedback(context, "Unable to fetch dimension pool '" + dimensionPoolId + "'");
            return -1;
        }

        pool.get().setProgressAdvancements(progressAdvancements);
        dimensionPoolConfig.saveWithContext();

        if (progressAdvancements)
        {
            sendFeedback(context, "Players can now progress advancements while in the dimension pool '" + dimensionPoolId + "'");
        }
        else
        {
            sendFeedback(context, "Players can no longer progress advancements while in the dimension pool '" + dimensionPoolId + "'");
        }

        return Command.SINGLE_SUCCESS;
    }

    public int setIncrementStatisticsInPool(CommandContext<ServerCommandSource> context)
    {
        String dimensionPoolId = StringArgumentType.getString(context, DimensionalInventoriesCommand.POOL_ID.toString());
        boolean incrementStatistics = BoolArgumentType.getBool(context, DimensionalInventoriesCommand.INCREMENT_STATISTICS_ENABLED.toString());

        Optional<DimensionPool> pool = dimensionPoolConfig.state().poolWithId(dimensionPoolId);

        if (pool.isEmpty())
        {
            sendFeedback(context, "Unable to fetch pool '" + dimensionPoolId + "'");
            return -1;
        }

        pool.get().setIncrementStatistics(incrementStatistics);
        dimensionPoolConfig.saveWithContext();

        if (incrementStatistics)
        {
            sendFeedback(context, "Players can now increment statistics while in the dimension pool '" + dimensionPoolId + "'");
        }
        else
        {
            sendFeedback(context, "Players can no longer increment statistics while in the dimension pool '" + dimensionPoolId + "'");
        }

        return Command.SINGLE_SUCCESS;
    }

    public void sendFeedback(CommandContext<ServerCommandSource> context, String message)
    {
        context.getSource().sendFeedback(() -> Text.literal(message), false);
    }
}
