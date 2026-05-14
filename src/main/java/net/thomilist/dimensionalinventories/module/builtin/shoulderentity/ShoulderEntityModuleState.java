package net.thomilist.dimensionalinventories.module.builtin.shoulderentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.thomilist.dimensionalinventories.mixin.ServerPlayerAccessor;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModuleState;

import java.lang.reflect.Type;

public class ShoulderEntityModuleState
    implements PlayerModuleState
{
    public CompoundTag leftShoulderEntity = new CompoundTag();
    public CompoundTag rightShoulderEntity = new CompoundTag();
    public long shoulderEntityAddedTime = 0;

    public ShoulderEntityModuleState()
    { }

    public ShoulderEntityModuleState( final ServerPlayer player )
    {
        this.loadFromPlayer( player );
    }

    @Override
    public void applyToPlayer( final ServerPlayer player )
    {
        ((ServerPlayerAccessor) player).invokeSetShoulderEntityLeft( this.leftShoulderEntity );
        ((ServerPlayerAccessor) player).invokeSetShoulderEntityRight( this.rightShoulderEntity );
        ((ServerPlayerAccessor) player).setTimeEntitySatOnShoulder( this.shoulderEntityAddedTime );
    }

    @Override
    public void loadFromPlayer( final ServerPlayer player )
    {
        this.leftShoulderEntity = player.getShoulderEntityLeft();
        this.rightShoulderEntity = player.getShoulderEntityRight();
        this.shoulderEntityAddedTime = ((ServerPlayerAccessor) player).getTimeEntitySatOnShoulder();
    }

    @Override
    public Type type()
    {
        return ShoulderEntityModuleState.class;
    }
}
