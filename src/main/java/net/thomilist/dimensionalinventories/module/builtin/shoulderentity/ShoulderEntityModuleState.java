package net.thomilist.dimensionalinventories.module.builtin.shoulderentity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.mixin.ServerPlayerEntityAccessor;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModuleState;

import java.lang.reflect.Type;

public class ShoulderEntityModuleState
    implements PlayerModuleState
{
    public NbtCompound leftShoulderEntity = new NbtCompound();
    public NbtCompound rightShoulderEntity = new NbtCompound();
    public long shoulderEntityAddedTime = 0;

    public ShoulderEntityModuleState()
    { }

    public ShoulderEntityModuleState( final ServerPlayerEntity player )
    {
        this.loadFromPlayer( player );
    }

    @Override
    public void applyToPlayer( final ServerPlayerEntity player )
    {
        ((ServerPlayerEntityAccessor) player).invokeSetLeftShoulderNbt( this.leftShoulderEntity );
        ((ServerPlayerEntityAccessor) player).invokeSetRightShoulderNbt( this.rightShoulderEntity );
        ((ServerPlayerEntityAccessor) player).setShoulderMountTime( this.shoulderEntityAddedTime );
    }

    @Override
    public void loadFromPlayer( final ServerPlayerEntity player )
    {
        this.leftShoulderEntity = player.getLeftShoulderNbt();
        this.rightShoulderEntity = player.getRightShoulderNbt();
        this.shoulderEntityAddedTime = ((ServerPlayerEntityAccessor) player).getShoulderMountTime();
    }

    @Override
    public Type type()
    {
        return ShoulderEntityModuleState.class;
    }
}
