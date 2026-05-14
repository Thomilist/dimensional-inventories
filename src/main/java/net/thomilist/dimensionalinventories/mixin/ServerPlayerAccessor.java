package net.thomilist.dimensionalinventories.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin( ServerPlayer.class )
public interface ServerPlayerAccessor
{
    @Accessor
    long getTimeEntitySatOnShoulder();

    @Accessor( "timeEntitySatOnShoulder" )
    void setTimeEntitySatOnShoulder( long shoulderMountTime );

    @Invoker
    void invokeRemoveEntitiesOnShoulder();

    @Invoker
    void invokeSetShoulderEntityLeft( CompoundTag leftShoulderNbt );

    @Invoker
    void invokeSetShoulderEntityRight( CompoundTag rightShoulderNbt );
}
