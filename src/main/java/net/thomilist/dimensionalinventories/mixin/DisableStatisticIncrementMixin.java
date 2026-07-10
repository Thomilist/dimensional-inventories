package net.thomilist.dimensionalinventories.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.compatibility.Compat;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.util.LogThrottler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin( Player.class )
public abstract class DisableStatisticIncrementMixin
    extends LivingEntity
{
    @Unique
    private static final LogThrottler LOG_THROTTLER = new LogThrottler( 10000 );

    @Unique
    private static DimensionPoolConfigModule DIMENSION_POOL_CONFIG;

    protected DisableStatisticIncrementMixin( final Level world,
                                              final BlockPos pos,
                                              final float yaw,
                                              final GameProfile gameProfile )
    {
        super( EntityTypes.PLAYER, world );
    }

    @Unique
    private static DimensionPoolConfigModule dimensionPoolConfig()
    {
        if ( DisableStatisticIncrementMixin.DIMENSION_POOL_CONFIG == null )
        {
            DisableStatisticIncrementMixin.DIMENSION_POOL_CONFIG = DimensionalInventories.INSTANCE.configModules.get(
                DimensionPoolConfigModule.class );
        }

        return DisableStatisticIncrementMixin.DIMENSION_POOL_CONFIG;
    }

    @Inject( at = @At( "HEAD" ),
             method = "awardStat(Lnet/minecraft/resources/Identifier;)V",
             cancellable = true )
    public void incrementStat( final Identifier stat, final CallbackInfo info )
    {
        if ( !this.canPoolIncrementStatistics() )
        {
            info.cancel();
        }
    }

    @Unique
    public boolean canPoolIncrementStatistics()
    {
        final String dimensionName = Compat.ENTITY.getWorld( this ).dimension().identifier().toString();

        final Optional<DimensionPool> pool = DisableStatisticIncrementMixin
            .dimensionPoolConfig()
            .state()
            .poolWithDimension( dimensionName, DisableStatisticIncrementMixin.LOG_THROTTLER.get() );

        return pool.map( DimensionPool::canIncrementStatistics ).orElse( true );
    }

    @Inject( at = @At( "HEAD" ),
             method = "awardStat(Lnet/minecraft/resources/Identifier;I)V",
             cancellable = true )
    public void increaseStat( final Identifier stat, final int amount, final CallbackInfo info )
    {
        if ( !this.canPoolIncrementStatistics() )
        {
            info.cancel();
        }
    }

    @Inject( at = @At( "HEAD" ),
             method = "awardStat(Lnet/minecraft/stats/Stat;)V",
             cancellable = true )
    public void incrementStat( final Stat<?> stat, final CallbackInfo info )
    {
        if ( !this.canPoolIncrementStatistics() )
        {
            info.cancel();
        }
    }

    @Inject( at = @At( "HEAD" ),
             method = "awardStat(Lnet/minecraft/stats/Stat;I)V",
             cancellable = true )
    public void increaseStat( final Stat<?> stat, final int amount, final CallbackInfo info )
    {
        if ( !this.canPoolIncrementStatistics() )
        {
            info.cancel();
        }
    }
}
