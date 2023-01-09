package net.thomilist.dimensionalinventories.mixin;

import java.util.function.Predicate;

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
import net.thomilist.dimensionalinventories.DimensionalInventoriesMod;

@Mixin(AbstractCriterion.class)
public abstract class DisableAdvancementProgressMixin<T extends AbstractCriterionConditions>
implements Criterion<T> {
    @Inject(at = @At("HEAD"), method = "trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Predicate;)V", cancellable = true)
    public void trigger(ServerPlayerEntity player, Predicate<T> predicate, CallbackInfo info)
    {
        DimensionalInventoriesMod.LOGGER.debug("DisableAdvancementProgressMixin triggered.");
        
        String dimensionName = player.getWorld().getRegistryKey().getValue().toString();
        DimensionPool pool = DimensionPoolManager.getPoolWithDimension(dimensionName);

        if (!pool.canProgressAdvancements())
        {
            DimensionalInventoriesMod.LOGGER.info("Dimension " + dimensionName + " in pool " + pool.getName() + " cannot award advancements. Cancelling criterion progress...");
            info.cancel();
        }
    }
}
