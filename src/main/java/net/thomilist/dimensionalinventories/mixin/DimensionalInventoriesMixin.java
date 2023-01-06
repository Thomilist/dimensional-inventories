package net.thomilist.dimensionalinventories.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import net.thomilist.dimensionalinventories.DimensionalInventoriesMod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class DimensionalInventoriesMixin {
	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
		DimensionalInventoriesMod.LOGGER.info("Dimensional Inventories mixin.");
	}
}
