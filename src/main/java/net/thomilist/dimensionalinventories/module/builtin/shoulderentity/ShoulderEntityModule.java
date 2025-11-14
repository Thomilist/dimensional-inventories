package net.thomilist.dimensionalinventories.module.builtin.shoulderentity;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.base.player.JsonPlayerModule;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.version.VersionedJsonData;
import net.thomilist.dimensionalinventories.util.NbtUtils;

public final class ShoulderEntityModule
    extends ModuleBase
    implements JsonPlayerModule<ShoulderEntityModuleState>
{
    private static final String MODULE_ID = "shoulder-entity";
    private static final String DESCRIPTION = "Shoulder entities - just parrots, at least for now.";

    private static final StorageVersion[] STORAGE_VERSIONS = {
        StorageVersion.V2
    };

    private static final Gson GSON = JsonModule.GSON_BUILDER.create();

    private final ShoulderEntityModuleState state = new ShoulderEntityModuleState();

    public ShoulderEntityModule( final String groupId )
    {
        super(
            ShoulderEntityModule.STORAGE_VERSIONS,
            groupId,
            ShoulderEntityModule.MODULE_ID,
            ShoulderEntityModule.DESCRIPTION
        );
    }

    private static void preprocessOldShoulderEntityNbt( final NbtCompound nbtCompound )
    {
        if ( NbtUtils.isEffectivelyEmpty( nbtCompound ) || nbtCompound.contains( "id" ) )
        {
            return;
        }

        nbtCompound.putString( "id", "minecraft:parrot" );
    }

    @Override
    public Gson gson()
    {
        return ShoulderEntityModule.GSON;
    }

    @Override
    public ShoulderEntityModuleState loadVersionedData( final VersionedJsonData versionedData )
        throws JsonParseException
    {
        final ShoulderEntityModuleState loadedState = JsonPlayerModule.super.loadVersionedData( versionedData );

        ShoulderEntityModule.preprocessOldShoulderEntityNbt( loadedState.leftShoulderEntity );
        ShoulderEntityModule.preprocessOldShoulderEntityNbt( loadedState.rightShoulderEntity );

        return loadedState;
    }

    @Override
    public ShoulderEntityModuleState newInstance( final ServerPlayerEntity player )
    {
        return new ShoulderEntityModuleState( player );
    }

    @Override
    public ShoulderEntityModuleState state()
    {
        return this.state;
    }

    @Override
    public ShoulderEntityModuleState defaultState()
    {
        return new ShoulderEntityModuleState();
    }
}
