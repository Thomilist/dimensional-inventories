package net.thomilist.dimensionalinventories.mixin;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.util.LogThrottler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Predicate;

// >=1.20.3: T extends AbstractCriterion.Conditions
//  <1.20.3: T extends AbstractCriterionConditions
@LimitedCompatibility( target = "Minecraft",
                       versions = "<1.20.3" )
@Mixin( AbstractCriterion.class )
public abstract class DisableAdvancementProgressMixin<T extends AbstractCriterionConditions>
    implements Criterion<T>
{
    @Unique
    private static final LogThrottler LOG_THROTTLER = new LogThrottler( 10000 );

    @Unique
    private static DimensionPoolConfigModule DIMENSION_POOL_CONFIG;

    @Unique
    private static DimensionPoolConfigModule dimensionPoolConfig()
    {
        if ( DisableAdvancementProgressMixin.DIMENSION_POOL_CONFIG == null )
        {
            DisableAdvancementProgressMixin.DIMENSION_POOL_CONFIG = DimensionalInventories.INSTANCE.configModules.get(
                DimensionPoolConfigModule.class );
        }

        return DisableAdvancementProgressMixin.DIMENSION_POOL_CONFIG;
    }

    @Inject( at = @At( "HEAD" ),
             method = "trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Predicate;)V",
             cancellable = true )
    public void trigger( final ServerPlayerEntity player, final Predicate<T> predicate, final CallbackInfo info )
    {
        final String dimensionName = player.getWorld().getRegistryKey().getValue().toString();

        final Optional<DimensionPool> pool = DisableAdvancementProgressMixin
            .dimensionPoolConfig()
            .state()
            .poolWithDimension( dimensionName, DisableAdvancementProgressMixin.LOG_THROTTLER.get() );

        if ( pool.isPresent() && !pool.get().canProgressAdvancements() )
        {
            info.cancel();
        }
    }
}
