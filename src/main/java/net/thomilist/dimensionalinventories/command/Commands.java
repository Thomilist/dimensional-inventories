package net.thomilist.dimensionalinventories.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolOperationResult;

import java.util.Optional;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.argument.DimensionArgumentType.dimension;
import static net.minecraft.command.argument.GameModeArgumentType.gameMode;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands
{
    private DimensionPoolConfigModule dimensionPoolConfig;

    public Commands()
    { }

    private static String versionString()
    {
        return "%s %s by %s".formatted(
            DimensionalInventories.PROPERTIES.namePretty(),
            DimensionalInventories.PROPERTIES.version(),
            DimensionalInventories.PROPERTIES.authorsPretty()
        );
    }

    public void register( final DimensionPoolConfigModule dimensionPoolConfig )
    {
        this.dimensionPoolConfig = dimensionPoolConfig;

        CommandRegistrationCallback.EVENT.register( ( dispatcher, registryAccess, environment ) -> this.register(
            dispatcher ) );
    }

    public void register( final CommandDispatcher<ServerCommandSource> dispatcher )
    {
        dispatcher.register(literal(DimensionalInventoriesCommand.ROOT.toString())
            .requires( CommandManager.requirePermissionLevel( CommandManager.OWNERS_CHECK))
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

    public int printVersion( final CommandContext<ServerCommandSource> context )
    {
        context.getSource().sendFeedback( () -> Text.literal( Commands.versionString() ), false );
        return Command.SINGLE_SUCCESS;
    }

    public int listAllDimensionPools( final CommandContext<ServerCommandSource> context )
    {
        context.getSource().sendFeedback( () -> Text.literal( this.dimensionPoolConfig.state().asString() ), false );
        return Command.SINGLE_SUCCESS;
    }

    public int listDimensionPool( final CommandContext<ServerCommandSource> context )
    {
        final String dimensionPoolId = StringArgumentType.getString(
            context,
            DimensionalInventoriesCommand.POOL_ID.toString()
        );
        final Optional<DimensionPool> pool = this.dimensionPoolConfig.state().poolWithId( dimensionPoolId );

        if ( pool.isEmpty() )
        {
            this.sendFeedback( context, "Unable to fetch pool '" + dimensionPoolId + '\'' );
            return -1;
        }

        this.sendFeedback( context, "Dimension pool:" + pool.get().asString() );
        return Command.SINGLE_SUCCESS;
    }

    public void sendFeedback( final CommandContext<ServerCommandSource> context, final String message )
    {
        context.getSource().sendFeedback( () -> Text.literal( message ), false );
    }

    public int createDimensionPool( final CommandContext<ServerCommandSource> context )
    {
        final String dimensionPoolId = StringArgumentType.getString(
            context,
            DimensionalInventoriesCommand.POOL_ID.toString()
        );

        final DimensionPoolOperationResult result = this.dimensionPoolConfig
            .state()
            .createPool( dimensionPoolId, GameMode.DEFAULT );

        if ( !result.success() )
        {
            this.sendFeedback( context, "Unable to create dimension pool: '" + dimensionPoolId + "' already exists" );
            return -1;
        }

        this.dimensionPoolConfig.saveWithContext();
        this.sendFeedback( context, "Dimension pool '" + dimensionPoolId + "' created" );
        return Command.SINGLE_SUCCESS;
    }

    public int removeDimensionPool( final CommandContext<ServerCommandSource> context )
    {
        final String dimensionPoolId = StringArgumentType.getString(
            context,
            DimensionalInventoriesCommand.POOL_ID.toString()
        );

        final DimensionPoolOperationResult result = this.dimensionPoolConfig.state().deletePool( dimensionPoolId );

        if ( !result.success() )
        {
            this.sendFeedback( context, "Unable to remove dimension pool: '" + dimensionPoolId + "' does not exist" );
            return -1;
        }

        this.dimensionPoolConfig.saveWithContext();
        this.sendFeedback( context, "Dimension pool '" + dimensionPoolId + "' removed" );
        return Command.SINGLE_SUCCESS;
    }

    public int assignDimensionToPool( final CommandContext<ServerCommandSource> context )
    {
        final String dimensionPoolId = StringArgumentType.getString(
            context,
            DimensionalInventoriesCommand.POOL_ID.toString()
        );
        final ServerWorld dimension;

        try
        {
            dimension = DimensionArgumentType.getDimensionArgument(
                context,
                DimensionalInventoriesCommand.DIMENSION_NAME.toString()
            );
        }
        catch ( final CommandSyntaxException e )
        {
            this.sendFeedback( context, "Unable to fetch dimension" );
            return -1;
        }

        final String dimensionName = dimension.getRegistryKey().getValue().toString();
        final DimensionPoolOperationResult result = this.dimensionPoolConfig
            .state()
            .assignDimensionToPool( dimensionName, dimensionPoolId );

        if ( !result.success() )
        {
            this.sendFeedback( context, '\'' + dimensionPoolId + "' is not a valid dimension pool ID" );
            return -1;
        }

        switch ( result.operation() )
        {
            case ADD_DIMENSION:
            {
                this.sendFeedback(
                    context,
                    "Assigned dimension '" + result.target() + "' to dimension pool '" + result.to() +
                    '\''
                );
                break;
            }
            case MOVE_DIMENSION:
            {
                this.sendFeedback(
                    context,
                    "Moved dimension '" + result.target() + "' from dimension pool '" + result.from() + "' to '" +
                    result.to() + '\''
                );
                break;
            }
            case NO_OP:
            {
                this.sendFeedback(
                    context,
                    "Dimension '" + result.target() + "' is already in dimension pool '" + result.to() +
                    '\''
                );
                break;
            }
        }

        this.dimensionPoolConfig.saveWithContext();
        return Command.SINGLE_SUCCESS;
    }

    public int removeDimensionFromPool( final CommandContext<ServerCommandSource> context )
    {
        final String dimensionPoolId = StringArgumentType.getString(
            context,
            DimensionalInventoriesCommand.POOL_ID.toString()
        );
        final ServerWorld dimension;

        try
        {
            dimension = DimensionArgumentType.getDimensionArgument(
                context,
                DimensionalInventoriesCommand.DIMENSION_NAME.toString()
            );
        }
        catch ( final CommandSyntaxException e )
        {
            this.sendFeedback( context, "Unable to fetch dimension" );
            return -1;
        }

        final String dimensionName = dimension.getRegistryKey().getValue().toString();

        final DimensionPoolOperationResult result = this.dimensionPoolConfig
            .state()
            .removeDimensionFromPool( dimensionName, dimensionPoolId );

        if ( !result.success() )
        {
            this.sendFeedback( context, '\'' + dimensionPoolId + "' is not a valid dimension pool ID" );
            return -1;
        }

        switch ( result.operation() )
        {
            case REMOVE_DIMENSION:
            {
                this.sendFeedback(
                    context,
                    "Removed dimension '" + dimensionName + "' from dimension pool '" + dimensionPoolId +
                    '\''
                );
                break;
            }
            case NO_OP:
            {
                this.sendFeedback(
                    context,
                    "Dimension '" + dimensionName + "' not found in dimension pool '" + dimensionPoolId +
                    '\''
                );
                break;
            }
        }

        this.dimensionPoolConfig.saveWithContext();
        return Command.SINGLE_SUCCESS;
    }

    public int setDimensionPoolGameMode( final CommandContext<ServerCommandSource> context )
    {
        final String dimensionPoolId = StringArgumentType.getString(
            context,
            DimensionalInventoriesCommand.POOL_ID.toString()
        );
        final GameMode gameMode;

        try
        {
            gameMode = GameModeArgumentType.getGameMode(
                context,
                DimensionalInventoriesCommand.GAME_MODE_NAME.toString()
            );
        }
        catch ( final CommandSyntaxException e )
        {
            this.sendFeedback( context, "Invalid game mode" );
            return -1;
        }

        final Optional<DimensionPool> pool = this.dimensionPoolConfig.state().poolWithId( dimensionPoolId );

        if ( pool.isEmpty() )
        {
            this.sendFeedback( context, "Unable to fetch dimension pool '" + dimensionPoolId + '\'' );
            return -1;
        }

        pool.get().setGameMode( gameMode );
        this.dimensionPoolConfig.saveWithContext();
        this.sendFeedback(
            context,
            "Game mode '" + gameMode.asString() + "' set for dimension pool '" + dimensionPoolId + '\''
        );
        return Command.SINGLE_SUCCESS;
    }

    public int setProgressAdvancementsInPool( final CommandContext<ServerCommandSource> context )
    {
        final String dimensionPoolId = StringArgumentType.getString(
            context,
            DimensionalInventoriesCommand.POOL_ID.toString()
        );
        final boolean progressAdvancements = BoolArgumentType.getBool(
            context,
            DimensionalInventoriesCommand.PROGRESS_ADVANCEMENTS_ENABLED.toString()
        );

        final Optional<DimensionPool> pool = this.dimensionPoolConfig.state().poolWithId( dimensionPoolId );

        if ( pool.isEmpty() )
        {
            this.sendFeedback( context, "Unable to fetch dimension pool '" + dimensionPoolId + '\'' );
            return -1;
        }

        pool.get().setProgressAdvancements( progressAdvancements );
        this.dimensionPoolConfig.saveWithContext();

        if ( progressAdvancements )
        {
            this.sendFeedback(
                context,
                "Players can now progress advancements while in the dimension pool '" + dimensionPoolId +
                '\''
            );
        }
        else
        {
            this.sendFeedback(
                context,
                "Players can no longer progress advancements while in the dimension pool '" +
                dimensionPoolId + '\''
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    public int setIncrementStatisticsInPool( final CommandContext<ServerCommandSource> context )
    {
        final String dimensionPoolId = StringArgumentType.getString(
            context,
            DimensionalInventoriesCommand.POOL_ID.toString()
        );
        final boolean incrementStatistics = BoolArgumentType.getBool(
            context,
            DimensionalInventoriesCommand.INCREMENT_STATISTICS_ENABLED.toString()
        );

        final Optional<DimensionPool> pool = this.dimensionPoolConfig.state().poolWithId( dimensionPoolId );

        if ( pool.isEmpty() )
        {
            this.sendFeedback( context, "Unable to fetch pool '" + dimensionPoolId + '\'' );
            return -1;
        }

        pool.get().setIncrementStatistics( incrementStatistics );
        this.dimensionPoolConfig.saveWithContext();

        if ( incrementStatistics )
        {
            this.sendFeedback(
                context,
                "Players can now increment statistics while in the dimension pool '" + dimensionPoolId +
                '\''
            );
        }
        else
        {
            this.sendFeedback(
                context,
                "Players can no longer increment statistics while in the dimension pool '" +
                dimensionPoolId + '\''
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    private enum DimensionalInventoriesCommand
    {
        ROOT( "diminv" ),
        LIST_POOLS( "list" ),
        POOL( "pool" ),
        POOL_ID( "poolId" ),
        LIST_DIMENSIONS_IN_POOL( "list" ),
        CREATE_POOL( "create" ),
        DELETE_POOL( "delete" ),
        DIMENSION( "dimension" ),
        DIMENSION_NAME( "dimensionName" ),
        ASSIGN_DIMENSION_TO_POOL( "assign" ),
        REMOVE_DIMENSION_FROM_POOL( "remove" ),
        GAME_MODE( "gameMode" ),
        GAME_MODE_NAME( "gameModeName" ),
        PROGRESS_ADVANCEMENTS( "progressAdvancements" ),
        PROGRESS_ADVANCEMENTS_ENABLED( "progressAdvancementsEnabled" ),
        INCREMENT_STATISTICS( "incrementStatistics" ),
        INCREMENT_STATISTICS_ENABLED( "incrementStatisticsEnabled" );

        private final String command;

        DimensionalInventoriesCommand( final String command )
        {
            this.command = command;
        }

        @Override
        public String toString()
        {
            return this.command;
        }
    }
}
