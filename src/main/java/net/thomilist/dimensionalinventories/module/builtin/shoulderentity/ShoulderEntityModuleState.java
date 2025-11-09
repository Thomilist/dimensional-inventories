package net.thomilist.dimensionalinventories.module.builtin.shoulderentity;

import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.mixin.ServerPlayerEntityAccessor;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModuleState;

import java.lang.reflect.Type;
import java.util.Optional;

public class ShoulderEntityModuleState
    implements PlayerModuleState
{
    public NbtCompound leftShoulderEntity = new NbtCompound();
    public NbtCompound rightShoulderEntity = new NbtCompound();
    public Optional<ParrotEntity.Variant> leftShoulderParrotVariant = Optional.empty();
    public Optional<ParrotEntity.Variant> rightShoulderParrotVariant = Optional.empty();
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
        ((ServerPlayerEntityAccessor) player).invokeSetLeftShoulderNbt(this.leftShoulderEntity );
        ((ServerPlayerEntityAccessor) player).invokeSetRightShoulderNbt(this.rightShoulderEntity );
        player.setLeftShoulderParrotVariant( this.leftShoulderParrotVariant );
        player.setRightShoulderParrotVariant( this.rightShoulderParrotVariant );
        ((ServerPlayerEntityAccessor) player).setShoulderMountTime( this.shoulderEntityAddedTime );
    }

    @Override
    public void loadFromPlayer( final ServerPlayerEntity player )
    {
        this.leftShoulderEntity = player.getLeftShoulderNbt();
        this.rightShoulderEntity = player.getRightShoulderNbt();
        this.leftShoulderParrotVariant = player.getLeftShoulderParrotVariant();
        this.rightShoulderParrotVariant = player.getRightShoulderParrotVariant();
        this.shoulderEntityAddedTime = ((ServerPlayerEntityAccessor) player).getShoulderMountTime();
    }

    @Override
    public Type type()
    {
        return ShoulderEntityModuleState.class;
    }
}
