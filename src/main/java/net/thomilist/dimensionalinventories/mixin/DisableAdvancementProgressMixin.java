package net.thomilist.dimensionalinventories.mixin;

import java.util.Optional;
import java.util.function.Predicate;

import net.thomilist.dimensionalinventories.LogThrottler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.DimensionPool;
import net.thomilist.dimensionalinventories.DimensionPoolManager;

@Mixin(AbstractCriterion.class)
public abstract class DisableAdvancementProgressMixin<T extends AbstractCriterionConditions>
implements Criterion<T> {
    private static LogThrottler logThrottler = new LogThrottler(10000);

    @Inject(at = @At("HEAD"), method = "trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Predicate;)V", cancellable = true)
    public void trigger(ServerPlayerEntity player, Predicate<T> predicate, CallbackInfo info)
    {
        String dimensionName = player.getWorld().getRegistryKey().getValue().toString();
        Optional<DimensionPool> pool = DimensionPoolManager.getPoolWithDimension(dimensionName, logThrottler.get());

        if (pool.isPresent() && !pool.get().canProgressAdvancements())
        {
            info.cancel();
        }
    }
}
