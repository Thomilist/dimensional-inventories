package net.thomilist.dimensionalinventories.mixin;

import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.util.LogThrottler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class DisableStatisticIncrementMixin
    extends LivingEntity
{
    @Unique
    private static DimensionPoolConfigModule DIMENSION_POOL_CONFIG;

    @Unique
    private static DimensionPoolConfigModule dimensionPoolConfig()
    {
        if (DisableStatisticIncrementMixin.DIMENSION_POOL_CONFIG == null)
        {
            DisableStatisticIncrementMixin.DIMENSION_POOL_CONFIG =
                DimensionalInventories.CONFIG_MODULES.get(DimensionPoolConfigModule.class);
        }

        return DisableStatisticIncrementMixin.DIMENSION_POOL_CONFIG;
    }

    @Unique
    private static final LogThrottler LOG_THROTTLER = new LogThrottler(10000);

    public DisableStatisticIncrementMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super((EntityType<? extends LivingEntity>)EntityType.PLAYER, world);
    }

    @Unique
    public boolean canPoolIncrementStatistics()
    {
        String dimensionName = this.getWorld().getRegistryKey().getValue().toString();

        Optional<DimensionPool> pool = dimensionPoolConfig().state()
            .poolWithDimension(dimensionName, DisableStatisticIncrementMixin.LOG_THROTTLER.get());

        return pool.map(DimensionPool::canIncrementStatistics).orElse(true);
    }

    
    @Inject(at = @At("HEAD"), method = "incrementStat(Lnet/minecraft/util/Identifier;)V", cancellable = true)
    public void incrementStat(Identifier stat, CallbackInfo info)
    {
        if (!canPoolIncrementStatistics())
        {
            info.cancel();
        }
    }
    
    @Inject(at = @At("HEAD"), method = "increaseStat(Lnet/minecraft/util/Identifier;I)V", cancellable = true)
    public void increaseStat(Identifier stat, int amount, CallbackInfo info)
    {
        if (!canPoolIncrementStatistics())
        {
            info.cancel();
        }
    }
    
    @Inject(at = @At("HEAD"), method = "incrementStat(Lnet/minecraft/stat/Stat;)V", cancellable = true)
    public void incrementStat(Stat<?> stat, CallbackInfo info)
    {
        if (!canPoolIncrementStatistics())
        {
            info.cancel();
        }
    }


    @Inject(at = @At("HEAD"), method = "increaseStat(Lnet/minecraft/stat/Stat;I)V", cancellable = true)
    public void increaseStat(Stat<?> stat, int amount, CallbackInfo info)
    {
        if (!canPoolIncrementStatistics())
        {
            info.cancel();
        }
    }
}
