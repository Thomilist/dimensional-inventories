package net.thomilist.dimensionalinventories.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerEntity.class)
public interface PlayerEntityAccessor
{
    @Accessor
    long getShoulderEntityAddedTime();

    @Accessor("shoulderEntityAddedTime")
    void setShoulderEntityAddedTime(long shoulderEntityAddedTime);

    @Accessor("LEFT_SHOULDER_ENTITY")
    static TrackedData<NbtCompound> getLeftShoulderEntity()
    {
        throw new AssertionError();
    }

    @Accessor("RIGHT_SHOULDER_ENTITY")
    static TrackedData<NbtCompound> getRightShoulderEntity()
    {
        throw new AssertionError();
    }

    @Invoker
    void invokeDropShoulderEntities();
}
